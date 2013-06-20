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
public class BeforeCalculateServlet extends HttpServlet{

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
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		String sql = "" ;
		String err = "";
		String taskName = null;
		String dataPath = null;
		String ownerEmail = null;
		int numClient = 0;
		if (req.getParameter("taskName") != null)
		{
			taskName = req.getParameter("taskName");
			req.getSession().setAttribute("taskName", taskName);
		}
		else if (req.getSession().getAttribute("taskName") != null)
		{
		     taskName = (String)req.getSession().getAttribute("taskName");
		
		}
		if (req.getParameter("ownerEmail") != null)
		{
			ownerEmail = req.getParameter("ownerEmail");
			req.getSession().setAttribute("ownerEmail", ownerEmail);
		}
		else if (req.getSession().getAttribute("ownerEmail") != null)
		{
		     ownerEmail = (String)req.getSession().getAttribute("ownerEmail");
		
		}
//		req.getSession().setAttribute("taskName", taskName);

		try{
			Class.forName("com.mysql.jdbc.Driver");					
			Connection conn = DriverManager.getConnection(dbconnection_property, dbusername_property, dbpassword_property);
			Statement stat = conn.createStatement();
//			sql = "select * from registeduser where name = '" + userName + "';";
			//set Task Status
			sql = "select taskStatus from gtask where gtask.name='" + taskName + "'";
			stat.execute(sql);
			ResultSet rs = stat.getResultSet();
			rs.next();
			if(rs.getInt(1) == 0){
				sql = "update gtask set gtask.Taskstatus=1 where gtask.name='" + taskName +"'";
				stat.executeUpdate (sql);
			}
			//set ClientNumber
			sql = "select u.email from user u, gtask g where u.task_id=g.id and u.ready=1 and g.Name='" + taskName + "';";
			stat.executeQuery(sql);
			rs = stat.getResultSet();
			while(rs.next()){
				numClient ++;
			}
			sql = "update gtask set NumClient=" + numClient + " where name='" + taskName + "';";
			stat.executeUpdate(sql);
		//	String sql2 = "select gtask.Taskstatus from gtask where gtask.name='" + taskName + "'";
		//	stat.executeQuery(sql2);
		//	ResultSet rs = stat.getResultSet();
		//	System.out.println("Taskstatus'" + rs.getInt(1) + "'.");
			//close rs and connection by jwc 10.5
			if(stat!=null){
				stat.close();
			}
			if(conn!=null){
				conn.close();
			}
			if(rs!=null){
				rs.close();
			}
		
		PrintWriter pw = res.getWriter();
		res.setContentType("text/xml");
		pw.println("<?xml version='1.0' encoding='ISO-8859-1'?>");
		pw.println("<root>");
		pw.println("<Checkresult>" + 1 + "</Checkresult>");
		pw.println("<Redirect>" +  "computation.jsp?taskName=" + taskName + "&amp;email=" + ownerEmail + "</Redirect>");
//		pw.println("<Redirect>" +  "computation.jsp </Redirect>");
		pw.println("</root>");
		pw.close();

//			res.sendRedirect("computation.jsp?taskName=" + taskName + "&email=" + ownerEmail);
		}catch(SQLException e){
			e.printStackTrace();
			err = e.getMessage();
			req.getSession().setAttribute("error", err);
		}catch(ClassNotFoundException e){
			err = e.getMessage();
			req.getSession().setAttribute("error", err);
		}
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		doPost(req, res);
	}
}
