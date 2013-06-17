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
public class CheckDupTaskName extends HttpServlet{

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
		System.out.println("In the check dup task name");
		String taskName = "";
		taskName = req.getParameter("taskName");
		System.out.println("I have got parameters: " + taskName );
		//compare information of user with user stored in db, set the flag of ture of false;
		String sql = "" ;
		String status = "FAILURE";
		String err = "";
		try{
			Class.forName("com.mysql.jdbc.Driver");					
			Connection conn = DriverManager.getConnection(dbconnection_property, dbusername_property, dbpassword_property);
			Statement stat = conn.createStatement();
			sql = "select name from GTASK where name='" + taskName + "'";
			ResultSet rs = stat.executeQuery(sql);
			while(rs.next()) {
				if(rs.getString("name").equals(taskName)){
					status = "FAILURE";
					System.out.println("find duplicate task name");
					throw new DuplicateException();
				}
			}
			System.out.println("Not duplicate");
//			err += "Task name available";
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
			err += "Duplicate task name";
			System.out.println("duplicate task name");
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
