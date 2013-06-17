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
public class GetTaskNameList extends HttpServlet{

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
		List<String> taskNameList = new ArrayList<String>();
		String sql = "" ;
		String err = "";
		String currentUserEmail = (String)req.getSession().getAttribute("email");
		System.out.println("in the before create cox task servlet");
		try{
			Class.forName("com.mysql.jdbc.Driver");					
			Connection conn = DriverManager.getConnection(dbconnection_property, dbusername_property, dbpassword_property);
			Statement stat = conn.createStatement();
			if(currentUserEmail != null){
				System.out.println("I am a registed user");
				sql = "select distinct task_id from user where email ='" + currentUserEmail + "';";
			}
			else{
				sql = "select distinct task_id from user;";
			}
			ResultSet rs = stat.executeQuery(sql);//
			while(rs.next()) {
				taskList.add(rs.getInt("task_id"));
			}
			req.getSession().setAttribute("taskList", taskList);
			
			/*get task name list according to the task id*/
			for( int i=0; i < taskList.size(); i++){
				sql = "select name from gtask where id=" + taskList.get(i) + ";";
				rs = stat.executeQuery(sql);
				if(rs.next()){// because one id has only one task name
					taskNameList.add(rs.getString("name"));
				}
			}
			
			/*return data in json form like [{'email' : 'aa@163.com'}, {'email' : 'bb@gmail.com'}]*/
			res.setContentType("text/html");
			PrintWriter out = res.getWriter();
			String str_json = "[";

			/*return task_id and task name in json form */
			for(int i=0; i<taskList.size(); i++) // 
            {
				str_json += "{'taskID' : '" + taskList.get(i) + "', 'taskName' : '" + taskNameList.get(i) + "'},";	
           }
						
			str_json = str_json.substring(0, str_json.length() -1) + "]";
			out.write(str_json);
			
			for(Iterator i = taskList.iterator(); i.hasNext();) // 
            {
				System.out.println(i.next()); 	
           }
			for(Iterator i = taskNameList.iterator(); i.hasNext();) // 
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
