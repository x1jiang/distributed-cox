package cox;

import java.sql.*;
import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
//061213


/**
 * This servlet checks the ready status of all participants
 * This servlet is used in userparams.jsp
 * This servlet is callled by polling in userparams.jsp
 * @author Wenchao
 *
 */
public class checkReadyServlet extends HttpServlet{

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
		String taskName = req.getParameter("taskName"); 	//change by jwc 10.24
		String email = req.getParameter("email"); 	//change by lph 11.08
//		String taskName = "hello8";
		//taskName = (String)req.getSession().getAttribute("taskName");
		PrintWriter pw = res.getWriter();
		List<String> emailList = new ArrayList<String>();
		String sql = "" ;
		String err = "";
		StringBuffer sb=new StringBuffer();//added by lph
		ResultSet rs;
		Connection conn;
		Statement stat;
		try{
			Class.forName("com.mysql.jdbc.Driver");					
			conn = DriverManager.getConnection(dbconnection_property, dbusername_property, dbpassword_property);
			stat = conn.createStatement();

			sql = "select user.email, user.ready, gtask.Taskstatus from user, gtask where user.task_id=gtask.id and gtask.name='"+ taskName + "';";
			rs = stat.executeQuery(sql);//æ‰§è¡Œsqlè¯­å�¥
			//print result as a table
////------------------use XML instead of text--------------------------------------------------------------------
			res.setContentType("text/xml");
			String xmlOutput = "";
			xmlOutput += "<?xml version='1.0' encoding='ISO-8859-1'?>";
			xmlOutput += "<root>";
//			pw.println("<?xml version='1.0' encoding='ISO-8859-1'?>");
//			pw.println("<root>");
			while(rs.next()) {
				xmlOutput += "<Email>" + rs.getString(1) + "</Email>";
//				pw.println("<Email>" + rs.getString(1) + "</Email>");
				xmlOutput += "<Status>" + rs.getString(2) + "</Status>";
//				pw.println("<Status>" + rs.getString(2) + "</Status>");
				xmlOutput += "<TaskStatus>" + rs.getString(3) + "</TaskStatus>";
//				pw.println("<TaskStatus>" + rs.getString(3) + "</TaskStatus>");
				emailList.add(rs.getString("email"));
			}
			sql = "select user.ready, gtask.Taskstatus from user, gtask where user.task_id=gtask.id and user.email='"+email+"' and gtask.name='"+ taskName + "';";
			rs = stat.executeQuery(sql);
			while(rs.next()) {
//				pw.println("<SelfStatus>" + rs.getString(1) + "</SelfStatus>");
				xmlOutput += "<SelfStatus>" + rs.getString(1) + "</SelfStatus>";
			}
//			pw.println("</root>");
			xmlOutput += "</root>";
			System.out.println("xmlOutput is " + xmlOutput);
			pw.println(xmlOutput);
//------------------------------------------------------------------------------------------------------------
			//close rs and connection by jwc 10.5
			if(rs!=null){
				rs.close();
			}
			if(conn!=null){
				conn.close();
			}
			if(stat!=null){
				stat.close();
			}

//			for(Iterator i = emailList.iterator(); i.hasNext();) // /é¡ºåº�å�–å‡ºç»“æžœé›†ä¸­çš„æ•°æ�®
//            {
//				System.out.println(i.next()); 	
//           }

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
