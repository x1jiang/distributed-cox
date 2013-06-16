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
//import javax.naming.*;
//import java.net.URL;
//import net.sf.json.*;

public class CheckTasknameServlet extends HttpServlet
{
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
		PrintWriter pw = res.getWriter();
		HashMap<String, String> errors = null;
		if (req.getSession().getAttribute("errors")!= null)
		{
			errors = (HashMap)req.getSession().getAttribute("errors");
		}
		else
		{
			errors = new HashMap<String, String>();
		}

		String taskName = req.getParameter("taskName");
		System.out.println("taskName is " + taskName);
		req.getSession().setAttribute("taskName", taskName);		
		int taskStatus = isValidTask(taskName);
		System.out.println("taskStatus is " + taskStatus);
//---------added by lph-----------------//
		res.setContentType("text/xml");
		pw.println("<?xml version='1.0' encoding='ISO-8859-1'?>");
		pw.println("<root>");
		pw.println("<Checkresult>" + taskStatus + "</Checkresult>");
		pw.println("</root>");
		
		if (taskStatus == 2)
		{
			errors.put("task", "The task name has to only contain word characters: [a-zA-Z0-9]");
		}
		else if (taskStatus == 1)
		{
			errors.put("task", "The task name has already been used.");
		}
		else if (taskStatus == -1)
		{
			errors.put("task", "There is something wrong in the database when creating the task.");
			
		}
		else if (taskStatus == -2)
		{
			errors.put("task", "Jdbc class not found exception.");
			
		}
		pw.close();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException{
		doPost(req, res);
	}
	
	/**
	return 0: task valid
		   1: task exists
		   -1: other error
		   2: task name not valid
	*/
	public int isValidTask(String task)
	{
		String task_pattern = "[a-zA-Z0-9]+";
		Pattern pattern = Pattern.compile(task_pattern);;
		Matcher matcher = pattern.matcher(task);
		if (!matcher.matches())
		{
			return 2;
		}
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		}		
		catch(ClassNotFoundException e)
		{
			return -2;
		}
		try
		{
			Connection conn = DriverManager.getConnection(dbconnection_property, dbusername_property, dbpassword_property);
			Statement stat = conn.createStatement();
			String sql = "select name from GTASK where name='" + task + "'";
			ResultSet rs = stat.executeQuery(sql);
			if (rs.first())
			{
				rs.close();
				conn.close();
				return 1;
			}
			rs.close();
			conn.close();
			return 0;
			
		}
		catch(SQLException e)
		{
			return -1;
		}
		
	}
}

