package cox;
import java.sql.*;
import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
//061213
/**
 * This class is similar to ClientServlet.java in ppsvm
 * This servlet receive username and passwd as imput, compare with items in table registeduser, return DataOut as response.
 * @author Wenchao
 *
 */
public class UserLoginServlet extends HttpServlet{

	private String dbconnection_property = null;
	private String dbusername_property = null;
	private String dbpassword_property = null;
	private String root_property = null;
	/**
	 * read parameters from file config.properties to initialize db: 
	 * dbconnection;
	 * dbusername;
	 * dbpassword;
	 */
	public void init(ServletConfig conf) throws ServletException {
		File f = new File("config.properties");
		Properties properties = new Properties();
		try{
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("/config.properties");
			properties.load(is);
			properties.list(System.out);
			dbconnection_property = properties.getProperty("dbconnection");
			dbusername_property = properties.getProperty("dbusername");
			dbpassword_property = properties.getProperty("dbpassword");
			root_property = properties.getProperty("root");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/** Compare .
	 * @param
	 * req DataIn as input class, which includes name, password
	 * @param
	 * res DataOut as output class, which is the state of  
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		//get email, password, and login type from req
		System.out.println("In the userlogin servlet");
		String email="";
		String password = "";
		String loginType = (String)req.getParameter("type");
		String status = "FAILURE";
		String err = "";

		if(loginType.equals("authentication")){
			email = req.getParameter("email");
			password = req.getParameter("password");
			System.out.println("I have got parameters: " + email + "\t" + password);
			
			//compare information of user with user stored in table registeduser, set the flag to ture of false;
			String sql = "" ;
			try{
				Class.forName("com.mysql.jdbc.Driver");					
				Connection conn = DriverManager.getConnection(dbconnection_property, dbusername_property, dbpassword_property);
				Statement stat = conn.createStatement();
				sql = "select * from registeduser where email = '" + email +"';";
				ResultSet rs = stat.executeQuery(sql);//
				while(rs.next()) {
					if(rs.getString("password").equals(password)){
						status = "SUCCESS";
					}
				}
				//close database
				if(stat!=null){
					stat.close();
				}
				if(rs!=null){
					rs.close();
				}
				if(conn!=null){
					conn.close();
				}
				//check if user is registered
				if(status.equals("SUCCESS")){
					System.out.println("User passes authentication");
					err += "Login success!";
					req.getSession().setAttribute("email", email);
					req.getSession().setAttribute("status", status);
				}
				else {
					req.getSession().setAttribute("status", status);
					throw new  UserNotRegistException("User has not registed");
				}
			}catch(SQLException e){
				e.printStackTrace();
				err += e.getMessage();
				req.getSession().setAttribute("error", err);
			}catch(ClassNotFoundException e){
				err += e.getMessage();
				req.getSession().setAttribute("error", err);
			}catch(UserNotRegistException e){
				err += e.getMessage();
				req.getSession().setAttribute("error", err);
			}finally{
				res.setContentType("text/html");
				res.getWriter().write("[{\"status\":\"" + status + "\", \"error\":\"" + err + "\"}]");
			}
		}
		else{
			email = "anonymous";
			password = "anonymous";
			System.out.println(req.getParameter("email") + " " + req.getParameter("password"));
			status = "SUCCESS";
			err += "Anonymous login success";
			req.getSession().setAttribute("email", email);
			req.getSession().setAttribute("status", status);
			req.getSession().setAttribute("error", err);
			res.setContentType("text/html");
			res.getWriter().write("[{\"status\":\"" + status + "\", \"error\":\"" + err + "\"}]");
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		doGet(req, res);
	}
}

