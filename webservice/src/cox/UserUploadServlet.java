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

public class UserUploadServlet extends HttpServlet
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
		if(req.getParameter("taskName")!=null){
			taskName = req.getParameter("taskName");
			System.out.println("taskName1: " + taskName);
			
		}
		else{
			taskName = (String)req.getSession().getAttribute("taskName");
			req.getSession().setAttribute("taskName", taskName);
			System.out.println("taskName: " + taskName);
		}

		if(req.getParameter("email")!=null){
			email = (String)req.getParameter("email");
			System.out.println("email1 :" + email);
		}
		else{
			email = (String)req.getSession().getAttribute("email");
			req.getSession().setAttribute("email", email);
			System.out.println("email :" + email);
		
		}
//		String description = req.getParameter("description");
	//	req.getSession().setAttribute("description", description);
	//	System.out.println(description);
		
//		String ownerEmail = req.getParameter("ownerEmail").trim();
//		req.getSession().setAttribute("ownerEmail", ownerEmail);
//		System.out.println(ownerEmail);
//		if (!validEmail(ownerEmail))
//    	{
//		  errors.put("ownerEmail", "The owner email address seems not valid");
//		}
		
		String userdatapath = req.getParameter("userFilePath");
		System.out.println("userFilePath is " + userdatapath);
		req.getSession().setAttribute("userFilePath", userdatapath);
		userdatapath = userdatapath.replaceAll("\\\\", "/");
		
		String userproperty = req.getParameter("userProperty");
		req.getSession().setAttribute("userProperty", userproperty);
//		String property = (String)req.getSession().getAttribute("showProperty");
//		req.getSession().setAttribute("showProperty", property);
//		if(!userproperty.equals(property))
//		{
//			errors.put("userpropertyError","Your data attributes don't comply with the task!");
//		}
		//String partnerEmails = req.getParameter("emails");
		//req.getSession().setAttribute("emails", partnerEmails);
		//String[] partnerEmailList = null;
		//if (partnerEmails.length() > 0)
		//{
		//	String[] rawPartnerEmailList = partnerEmails.trim().split(",");
	//		partnerEmailList = new String[rawPartnerEmailList.length];
	//		for (int i=0; i<rawPartnerEmailList.length; i++)
	//		{
	//			String partnerEmail = rawPartnerEmailList[i].trim();
	//			if (!validEmail(partnerEmail))
	//			{
	//				errors.put("partnerEmails", "One of the partner email address seems not valid.");
	//				break;
	//			}
	//			partnerEmailList[i] = partnerEmail;
	//		}
	//	}
		
		double tolerance = DEFAULT_TOLERANCE;
		double epsilon = DEFAULT_EPSILON;
		double sigmaSquared = DEFAULT_SIGMA_SQUARED;
		
		
		Boolean isLinearKernel = DEFAULT_IS_LINEAR_KERNEL;
		Boolean isSparseData = DEFAULT_IS_SPARSE_DATA;
		Boolean isBinary = DEFAULT_IS_BINARY;
		Boolean testOnly = DEFAULT_TEST_ONLY;

		if (!errors.isEmpty())
		{
			req.getSession().setAttribute("errors", errors);
//			res.sendRedirect("userparams2.jsp");
//			return;
		}
		
		String paramString = "";
	//	paramString = paramString + "-c#" + smoothnessAccuracy + "#-t#" + tolerance + "#-e#" 
	//			+ epsilon + "#-p#" + sigmaSquared;
				
		
		res.setContentType("text/html");
		//ServletOutputStream out = res.getOutputStream();
		
		//out.println("<html><head><title>Privacy Preserved SVM</title></head>");
		//out.println("<body><h2>Parameters read in.</h2>");
		//out.println("<h3>Java servlet using JDBC</h3>");
		//out.println(taskName + " " + taskExists(taskName));
		//String messageString = "";
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		}		
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		try
		{
			 
			Connection conn = DriverManager.getConnection(dbconnection_property, dbusername_property, dbpassword_property);
			Statement stat = conn.createStatement();
			//---------added by lph-----------------//
			String sql= "select gtask.property from gtask where gtask.name='" + taskName + "'";
			String property="";
			ResultSet rs = stat.executeQuery(sql);
			if(rs.next()){
				property=rs.getString(1);
			}
			PrintWriter pw = res.getWriter();
//			int propertystatus=0;
//			if(userproperty.equals(property))
//				propertystatus=1;//
//			System.out.println("propertystatus is " + propertystatus);
//			pw.println("<?xml version='1.0' encoding='ISO-8859-1'?>");
//			pw.println("<root>");
//			pw.println("<CheckPropertyresult>" + propertystatus + "</CheckPropertyresult>");
//			pw.println("</root>");
			

			//now set user's ready status to 1 by jwc 10.5
//			if(propertystatus==1)
//			{
			sql = "update user, gtask set ready=1 where user.task_id=gtask.id and gtask.name='" + taskName +"' and user.email='" + email +"';";
			stat.executeUpdate(sql);
			
			//now add userFilePath to table user
			sql = "update user, gtask set datapath='" + userdatapath +"' where user.task_id=gtask.id and gtask.name='" + taskName +"' and user.email='" + email +"';";
			stat.executeUpdate(sql);
//			}
			stat.close(); 
			conn.close();

	
			req.getSession().setAttribute("taskName", taskName);
//			req.getSession().setAttribute("showProperty", property);
			req.getSession().setAttribute("userFilePath", userdatapath);
			req.getSession().setAttribute("userProperty", userproperty);

//			res.sendRedirect("computation.jsp?taskName=" + taskName + "&email=" + email);
			
			
		}	
		catch(SQLException e)
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

