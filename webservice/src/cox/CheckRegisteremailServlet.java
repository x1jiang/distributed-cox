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
public class CheckRegisteremailServlet extends HttpServlet{

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
		System.out.println("In the userlogin servlet");
		String userName = "";
		String email="";
		PrintWriter pw = res.getWriter();
		int checkstatus=0;
		userName = req.getParameter("firstName") + "#" + req.getParameter("lastName");
		req.getSession().setAttribute("userName", userName); 
		email=req.getParameter("email");
		req.getSession().setAttribute("email", email);
		System.out.println("I have got parameters: " + userName + "\t" + email);
		//compare information of user with user stored in db, set the flag of ture of false;
		String sql = "" ;
		String inDB = "NO";
		String Status = "SUCCESS";
		String err = "";
		req.getSession().setAttribute("inDB", inDB);
		req.getSession().setAttribute("Status", Status);
		try{
			Class.forName("com.mysql.jdbc.Driver");					
			Connection conn = DriverManager.getConnection(dbconnection_property, dbusername_property, dbpassword_property);
			Statement stat = conn.createStatement();
//			sql = "select * from registeduser where name = '" + userName + "';";
			sql = "select * from registeduser where name = '" + userName +"';";
			ResultSet rs = stat.executeQuery(sql);//æ‰§è¡Œsqlè¯­å�¥
			rs.next();
			while(rs.next()) {
				System.out.println(rs.getString("EMAIL"));
				if(rs.getString("EMAIL").equals(email)){
					inDB = "YES";
					Status = "FAILURE";
				}
			}
           if(inDB.equals("YES")) checkstatus=1;
           res.setContentType("text/xml");
           pw.println("<?xml version='1.0' encoding='ISO-8859-1'?>");
   		   pw.println("<root>");
   		   pw.println("<Checkresult>" + checkstatus + "</Checkresult>");
   		   pw.println("</root>");
   		System.out.println("checkstatus is " + checkstatus);
			//close rs and connection by jwc 10.5
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
			err = e.getMessage();
			req.getSession().setAttribute("error", err);
		}catch(ClassNotFoundException e){
			err = e.getMessage();
			req.getSession().setAttribute("error", err);
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		doGet(req, res);
	}
	
	
}
