package cox;
import java.sql.*;
import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * This class is similar to ClientServlet.java in ppsvm
 * This servlet receive username and passwd as imput, compare with items in table registeduser, return DataOut as response.
 * @author  Wenchao, Challen
 *
 */
public class BeforeCreateCoxTaskServlet extends HttpServlet{

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
		List<Integer> taskList = new ArrayList<Integer>();
		List<String> emailList = new ArrayList<String>();
		String sql = "" ;
		String err = "";
		try{
			Class.forName("com.mysql.jdbc.Driver");					
			Connection conn = DriverManager.getConnection(dbconnection_property, dbusername_property, dbpassword_property);
			Statement stat = conn.createStatement();
//			sql = "select * from registeduser where name = '" + userName + "';";
			sql = "select distinct task_id from user;";
			ResultSet rs = stat.executeQuery(sql);//
			while(rs.next()) {
				taskList.add(rs.getInt("task_id"));
			}
			req.getSession().setAttribute("taskList", taskList);

			sql = "select distinct email from registeduser;";
			rs = stat.executeQuery(sql);//
			while(rs.next()) {
				emailList.add(rs.getString("email"));
			}
//			req.getSession().setAttribute("emailList", emailList);
			
			/*return data in json form like [{'email' : 'aa@163.com'}, {'email' : 'bb@gmail.com'}]*/
			res.setContentType("text/html");
			PrintWriter out = res.getWriter();
			String str_json = "[";
			for(Iterator i = emailList.iterator(); i.hasNext();) // 
            {
				str_json += "{'email' : '" + i.next() + "'},";	
           }
			str_json = str_json.substring(0, str_json.length() -1) + "]";
			out.write(str_json);
			
			for(Iterator i = emailList.iterator(); i.hasNext();) // 
            {
				System.out.println(i.next()); 	
           }
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
			System.out.println("Here finish BeforeCreateCoxTaskServlet");

		}catch(SQLException e){
			e.printStackTrace();
			err = e.getMessage();
//			req.getSession().setAttribute("error", err);
		}catch(ClassNotFoundException e){
			err = e.getMessage();
//			req.getSession().setAttribute("error", err);
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		doGet(req, res);
	}
	
	
}
