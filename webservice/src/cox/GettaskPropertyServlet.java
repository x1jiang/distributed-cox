package cox;

import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;
//import org.joda.time.DateTime;
//import org.joda.time.DateTimeComparator;
//import org.joda.time.format.DateTimeFormat;
//import org.joda.time.format.DateTimeFormatter;
import java.util.*;
//import javax.mail.*;
//import javax.mail.internet.*;
import java.util.regex.*;
//061213
//import javax.naming.*;
//import java.net.URL;

public class GettaskPropertyServlet extends HttpServlet
{
	private final double DEFAULT_SMOOTHNESS_ACCURACY = 0.05;
	private final double DEFAULT_TOLERANCE = 0.001;
	private final double DEFAULT_EPSILON = 0.001;
	private final double DEFAULT_SIGMA_SQUARED = 1;
	private final boolean DEFAULT_IS_LINEAR_KERNEL = true;
	private final boolean DEFAULT_IS_SPARSE_DATA = false;
	private final boolean DEFAULT_IS_BINARY = false;
	private final boolean DEFAULT_TEST_ONLY = false;
	private String dbconnection_property = null;
	private String dbusername_property = null;
	private String dbpassword_property = null;
	private String root_property = null;
	
	public void init(ServletConfig conf) throws ServletException {
		try
		{
			Properties properties = new Properties();
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("/config.properties");
			
			properties.load(is);
				
			
			dbconnection_property = properties.getProperty("dbconnection");
			dbusername_property = properties.getProperty("dbusername");
			dbpassword_property = properties.getProperty("dbpassword");
			root_property = properties.getProperty("root");
		}
		catch(IOException e)
		{
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException
	{
	
		HashMap<String, String> errors = null;
		String taskName, email;
		if (req.getSession().getAttribute("errors")!= null)
		{
			errors = (HashMap)req.getSession().getAttribute("errors");
		}
		else
		{
			errors = new HashMap<String, String>();
		}
		if(req.getParameter("taskName")!=null && req.getParameter("taskName")!= "" && req.getParameter("taskName")!= "undefined"){
			taskName = req.getParameter("taskName");
			System.out.println("taskName1: " + taskName);
			
		}
		else{
			taskName = (String)req.getSession().getAttribute("taskName");
			req.getSession().setAttribute("taskName", taskName);
			System.out.println("taskName: " + taskName);
		}
//		res.setContentType("text/html");
		
		
		try
		{	
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(dbconnection_property, dbusername_property, dbpassword_property);
			Statement stat = conn.createStatement();
			//---------added by lph-----------------//
			String sql= "select gtask.property, gtask.owner_email from gtask where gtask.name='" + taskName + "'";
			String taskproperty=null;
			String ownerEmail= null;
			ResultSet rs = stat.executeQuery(sql);
			if(rs.next()){
				taskproperty=rs.getString(1);
				ownerEmail=rs.getString(2);
			}
//			String taskproperty_xml = taskproperty.replace("\t", "&#x0009;");
			res.setContentType("text/xml");
			PrintWriter pw = res.getWriter();
			System.out.println("taskproperty is " + taskproperty);
			pw.println("<?xml version='1.0' encoding='ISO-8859-1'?>");
			pw.println("<root>");
			pw.println("<taskproperty>" + taskproperty + "</taskproperty>");
			pw.println("<ownerEmail>" + ownerEmail + "</ownerEmail>");
			pw.println("</root>");
			pw.close();
			

	//		String sql = "insert into GTASK (name, description, exp_date, parameters, owner_email, property) values('" + taskName + "', '" 
	//				+ description + "', STR_TO_DATE('" + dtf.print(expDate) + "', '%Y-%m-%d %H:%i:%s'), '" + paramString + "', '" 
	//				+ ownerEmail + "', '"+property+"')";
	//		stat.executeUpdate (sql);
			//out.println("Task created!");
			
			// now create users
			//String sql =  "insert into USER(email, datapath, task_id) select '" + email + "', '" + userdatapath + "', GTASK.id from GTASK where name='" + taskName + "'";
			//stat.executeUpdate(sql);
			
			//now set user's ready status to 1 by jwc 10.5
			stat.close(); 
			conn.close();

	
			req.getSession().setAttribute("taskName", taskName);
		}	
		catch(SQLException e)
		{
			e.printStackTrace();
		}catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException{
		doPost(req, res);
	}
	public boolean validEmail(String email)
	{
		String EMAIL_PATTERN = "[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);;
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
		
				   
	}
	
}

