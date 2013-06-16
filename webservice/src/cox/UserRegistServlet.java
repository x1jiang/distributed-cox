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
public class UserRegistServlet extends HttpServlet{

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
//			InputStream is = new FileInputStream(f);
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
		//get user information from req
		System.out.println("In the userRegist servlet");
		String userName = "";
		String email="";
		String passWord = "";
		String isCheck = "";

		userName = req.getParameter("firstName") + "#" + req.getParameter("lastName");
		req.getSession().setAttribute("userName", userName);
		passWord = req.getParameter("password");
		req.getSession().setAttribute("password", passWord);
		email=req.getParameter("email");
		isCheck = req.getParameter("isCheck");
		
		System.out.println("I have got parameters: " + userName + "\t" + email + "\t" + passWord + "\t" + isCheck);
		//compare information of user with user stored in db, set the flag of ture of false;
		String sql = "" ;
		String status = "FAILURE";
		String err = "";
		try{
			Class.forName("com.mysql.jdbc.Driver");					
			Connection conn = DriverManager.getConnection(dbconnection_property, dbusername_property, dbpassword_property);
			Statement stat = conn.createStatement();
			sql = "select * from registeduser where email = '" + email +"';";
			ResultSet rs = stat.executeQuery(sql);
			while(rs.next()) {
				if(rs.getString("EMAIL").equals(email)){
					status = "FAILURE";
					System.out.println("find duplicate email");
					throw new DuplicateException();
				}
			}
			System.out.println("Not duplicate");
			if(isCheck.equals("FALSE")){
				sql = "insert into registeduser(name, EMAIL, password) values('" + userName + "', '" + email + "', '" + passWord + "'); ";
				stat.executeUpdate(sql);
				status = "SUCCESS";
				err += "Regist success";
				req.getSession().setAttribute("email", email);
			}
			//if just check the blank, no sql operation is needed
			status = "SUCCESS";
			//close rs and connection
			if(stat!=null){
				stat.close();
			}
			if(rs!=null){
				rs.close();
			}
			if(conn!=null){
				conn.close();
			}

		}catch(SQLException e){
			e.printStackTrace();
			err += e.getMessage();
		}catch(ClassNotFoundException e){
			err += e.getMessage();
		}catch(DuplicateException e){
			err += "Duplicate email";
			System.out.println("duplicate email");
		}finally{
			res.setContentType("text/html");
			res.getWriter().write("[{\"status\":\"" + status + "\", \"error\":\"" + err + "\"}]");
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		doGet(req, res);
	}
	
	
}
