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
public class UpdateRegist extends HttpServlet{

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
		//get user information from req
		System.out.println("In the update regist servlet");
		String userName = "";
		String email= (String)req.getSession().getAttribute("email");
		String password = "";
		String err = "";
		if(email.equals("anonymous")){
			err += "This is anonymous user";
			res.getWriter().write("[{'status':'FAILURE', 'error':'" + err + "'}]" );
		}
		else{
			userName = req.getParameter("firstName") + "#" + req.getParameter("lastName");
			password = req.getParameter("password");
			
			System.out.println("In update regist, I have got parameters: " + userName + "\t" + email + "\t" + password);
			//compare information of user with user stored in db, set the flag of ture of false;
			String sql = "" ;
			try{
				Class.forName("com.mysql.jdbc.Driver");					
				Connection conn = DriverManager.getConnection(dbconnection_property, dbusername_property, dbpassword_property);
				Statement stat = conn.createStatement();
				sql = "Update registeduser set name='" + userName + "', password='" + password + "' where email='" + email + "'; ";
				stat.executeUpdate(sql);

				//close rs and connection 
				if(stat!=null){
					stat.close();
				}
				if(conn!=null){
					conn.close();
				}

			}catch(SQLException e){
				e.printStackTrace();
				err += e.getMessage();
			}catch(ClassNotFoundException e){
				err += e.getMessage();
			}finally{
				res.setContentType("text/html");
				if(err.equals("")){
					err += "Update profile successfully";
					res.getWriter().write("[{'status':'SUCCESS', 'error':'" + err + "'}]" );
				}
				else{
					res.getWriter().write("[{'status':'FAILURE', 'error':'" + err + "'}]" );
				}
			}
		}
		
		

	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		doGet(req, res);
	}
	
	
}
