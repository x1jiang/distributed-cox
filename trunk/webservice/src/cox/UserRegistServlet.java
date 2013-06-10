package cox;
import java.sql.*;
import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;



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
		String userName = "";
		String email = "";
		String passWord = "";
//		try{
//			res.setContentType("application/x-java-serialized-object");
//			InputStream in = req.getInputStream();
//			ObjectInputStream ois = new ObjectInputStream(in);
//			DataIn dataIn = (DataIn) ois.readObject();
//			ois.close();
//			userName = dataIn.getName();
//			passWord = dataIn.getPassWord();
//		}catch(ClassNotFoundException e){
//			e.printStackTrace();
//		}
		userName = req.getParameter("User");
		req.getSession().setAttribute("userName", userName);
		email = req.getParameter("email");
		req.getSession().setAttribute("email", email);
		passWord = req.getParameter("pwd");
		req.getSession().setAttribute("passWord", passWord);
		
		
		//compare information of user with user stored in db, set the flag of ture of false;
		String sql = "" ;
		String isAdded = "NO";
		String err = "";
		req.getSession().setAttribute("isAdded", isAdded);
		try{
			Class.forName("com.mysql.jdbc.Driver");					
			Connection conn = DriverManager.getConnection(dbconnection_property, dbusername_property, dbpassword_property);
			Statement stat = conn.createStatement();
			
		//	sql = "insert into user(TASK_ID, name, EMAIL, password) values('0', '" + userName + "', '" + email + "', '" + passWord + "'); ";
		//	stat.executeUpdate(sql);//æ‰§è¡Œsqlè¯­å�¥

			isAdded = "Yes";
//			if(rs.getString("password").equals(passWord)){
//				isAdded = "YES";
//				System.out.println("User in DB");
//			}
			
			//close rs and connection by jwc 10.5
			if(stat!=null){
				stat.close();
			}
//			if(rs!=null){
//				rs.close();
//			}
			if(conn!=null){
				conn.close();
			}
			
			req.getSession().setAttribute("isAdded", isAdded);
			res.sendRedirect("regist.jsp");
			//redirect to the request result page

			
			//construct the response according to isAdded
//			DataOut statusObj = new DataOut("userisAdded", isAdded);
//			OutputStream out = res.getOutputStream();
//			ObjectOutputStream oos = new ObjectOutputStream(out);
//			oos.writeObject(statusObj);
//			oos.flush();
//			oos.close();
		}catch(SQLException e){
			e.printStackTrace();
			err = e.getMessage();
			req.getSession().setAttribute("error", err);
		}catch(ClassNotFoundException e){
			err = e.getMessage();
			req.getSession().setAttribute("error", err);
		}finally{
			

		}
	}
	
	
}
