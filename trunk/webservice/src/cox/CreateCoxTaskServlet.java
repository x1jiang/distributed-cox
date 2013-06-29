package cox;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.joda.time.DateTime;
//import org.joda.time.DateTimeComparator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.*;
//import javax.mail.*;
//import javax.mail.internet.*;
import java.util.regex.*;
//import javax.naming.*;
//import java.net.URL;
//import net.sf.json.*;

//061213

public class CreateCoxTaskServlet extends HttpServlet
{
	private final int DEFAULT_MAX_ITERATION = 100;
	private final double DEFAULT_EPSILON = 0.000001;

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
		String error = "";
		String status = "";
		if (req.getSession().getAttribute("errors")!= null)
		{
			errors = (HashMap)req.getSession().getAttribute("errors");
		}
		else
		{
			errors = new HashMap<String, String>();
		}

		String taskName = req.getParameter("taskName");
		System.out.println("In createTaskServlet, taskName is " + taskName + "ownerEmail is " + req.getParameter("ownerEmail") + "emails is " + req.getParameter("emails"));
		req.getSession().setAttribute("taskName", taskName);		
		int taskStatus = isValidTask(taskName);

		if (taskStatus == 2)
		{
			errors.put("task", "The task name has to only contain word characters: [a-zA-Z0-9]");
			error += "The task name has to only contain word characters and underline";
			status = "FAILURE";
		}
		else if (taskStatus == 1)
		{
			errors.put("task", "The task name has already been used.");
			error += "The task name has already been used";
			status = "FAILURE";
		}
		else if (taskStatus == -1)
		{
			errors.put("task", "There is something wrong in the database when creating the task.");
			error += "There is something wrong in the database when creating the task.";
			status = "FAILURE";
		}
		else if (taskStatus == -2)
		{
			errors.put("task", "Jdbc class not found exception.");
			error += "Jdbc class not found exception.";
			status = "FAILURE";
		}
		int expDays = 3;
		try
		{
			expDays = Integer.parseInt(req.getParameter("expDays"));
			req.getSession().setAttribute("expDays", req.getParameter("expDays"));
			if (expDays <=0)
			{
				errors.put("expDays", "Please enter a positive interger.");
			}
		}
		catch(NumberFormatException e)
		{
			errors.put("expDays", "Please enter a positive integer.");
		}
		String description = req.getParameter("description");
		req.getSession().setAttribute("description", description);
		String ownerEmail = req.getParameter("ownerEmail").trim();
		req.getSession().setAttribute("ownerEmail", ownerEmail);
		if (!validEmail(ownerEmail))
		{
			errors.put("ownerEmail", "The owner email address seems not valid");
		}
	
		
		String partnerEmails = req.getParameter("emails");
		req.getSession().setAttribute("emails", partnerEmails);
		String[] partnerEmailList = null;
		if (partnerEmails.length() > 0)
		{
			String[] rawPartnerEmailList = partnerEmails.trim().split(",");
			partnerEmailList = new String[rawPartnerEmailList.length];
			for (int i=0; i<rawPartnerEmailList.length; i++)
			{
				String partnerEmail = rawPartnerEmailList[i].trim();
				if (!validEmail(partnerEmail))
				{
					errors.put("partnerEmails", "One of the partner email address seems not valid.");
					break;
				}
				partnerEmailList[i] = partnerEmail;
			}
		}
		
		//String ownerEmail = partnerEmailList[0];
		req.getSession().setAttribute("ownerEmail", ownerEmail);
		if (!validEmail(ownerEmail))
		{
			errors.put("ownerEmail", "The owner email address seems not valid");
		}
		
		int maxIteration = DEFAULT_MAX_ITERATION;
		try
		{
			maxIteration = Integer.parseInt(req.getParameter("iterationMax"));
			req.getSession().setAttribute("maxIteration", req.getParameter("iterationMax"));
			if (maxIteration < 0)
			{
				errors.put("iterationMax", "\"Maximum iteration number \": please enter a positive int value or use the default value " 
					+ DEFAULT_MAX_ITERATION + ".");
			}
		}
		catch(NumberFormatException e)
		{
			errors.put("iterationMax", "\"Maximum iteration number \": please enter a positive int value or use the default value " 
					+ DEFAULT_MAX_ITERATION + ".");
		}
		double epsilon = DEFAULT_EPSILON;
		try
		{
			epsilon = Double.valueOf(req.getParameter("epsilon"));
			if (epsilon < 0)
			{
				errors.put("epsilon", "\"Epsilon\": please enter a positive double vlaue or use the default value " + DEFAULT_EPSILON + ".");
			}
			
		}
		catch(NumberFormatException e)
		{
			errors.put("epsilon", "\"Epsilon\": please enter a positive double vlaue or use the default value " + DEFAULT_EPSILON + ".");
		}
		
		String datapath = req.getParameter("showFilePath");
		req.getSession().setAttribute("showFilePath", datapath);
		//added by jwc 10.5 change the form of datapath
		datapath = datapath.replaceAll("\\\\", "/");
		
		String property = req.getParameter("showProperty");
		System.out.println("The property is " + property);
		req.getSession().setAttribute("showProperty", property);
		if(property.equals("showProperty"))
		{
			errors.put("property","Please specify your data file location to check the data format!");
		}
		
		//set the attributes before the redirect of page
		req.getSession().setAttribute("taskName", taskName);
		req.getSession().setAttribute("ownerEmail", ownerEmail);	
		req.getSession().setAttribute("showProperty", property);
		System.out.println("property after setAttribute " + (String)req.getSession().getAttribute("showProperty"));
		req.getSession().setAttribute("datapath", datapath);

		
		
		
		String paramString = "";
		
		/*
		paramString = paramString + "-c#" + maxIteration + "#-t#" + tolerance + "#-e#" 
				+ epsilon + "#-p#" + sigmaSquared 
				+ "#-l#" + isLinearKernel + "#-s#" + isSparseData + "#-b#" + isBinary + "#-a#" + testOnly;
		*/
				
		paramString = paramString + "-c#" + maxIteration + "#-e#" + epsilon;

		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(dbconnection_property, dbusername_property, dbpassword_property);
			Statement stat = conn.createStatement();

			DateTime expDate = new DateTime();
			expDate = expDate.plusDays(expDays);
			DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

			String sql = "insert into GTASK (name, description, exp_date, parameters, owner_email, property) values('" + taskName + "', '" 
					+ description + "', STR_TO_DATE('" + dtf.print(expDate) + "', '%Y-%m-%d %H:%i:%s'), '" + paramString + "', '" 
					+ ownerEmail + "', '"+property+"')";
			stat.executeUpdate (sql);
			//out.println("Task created!");
			// now create users
			sql =  "insert into USER(email, datapath, task_id) select '" + ownerEmail + "', '" + datapath + "', GTASK.id from GTASK where name='" + taskName + "'";
			stat.executeUpdate(sql);
			//now set owner's ready status to 1 by jwc 10.5
			sql = "update user, gtask set ready=1 where user.task_id=gtask.id and gtask.name='" + taskName +"' and user.email='" + ownerEmail +"';";
			stat.executeUpdate(sql);
			
			invitePartners(ownerEmail, ownerEmail, taskName, expDate);
			if (partnerEmailList != null)
			{
				for (String email:partnerEmailList)
				{
					email=email.trim();
					if (email.length() > 0 && email.indexOf("@") >=0)
					{
						sql = "insert into USER(email, task_id) select '" + email + "', GTASK.id from GTASK where name='" + taskName + "'";
						stat.executeUpdate(sql);
						invitePartners(ownerEmail, email, taskName, expDate);
					}
				}
				
			}
			stat.close(); 
			conn.close();
			
			req.getSession().setAttribute("taskName", taskName);
			req.getSession().setAttribute("ownerEmail", ownerEmail);	
			req.getSession().setAttribute("showProperty", property);
			System.out.println("property after setAttribute " + (String)req.getSession().getAttribute("showProperty"));
			req.getSession().setAttribute("datapath", datapath);
			
			if(status.equals("")){
				status = "SUCCESS";
				error = "You have created a new task";
			}
		}	
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}finally{
			res.setContentType("text/html");
			res.getWriter().write("[{\"status\":\"" + status + "\", \"error\":\"" + error + "\"}]");
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException{
		doPost(req, res);
	}
	public String invitePartners(String from, String to, String taskName, DateTime expDate)
	{
		String subject = "Invitation to the Distributed Cox Regression project";
		String text = "You are invited to join the task under the Distributed Cox Regression project, " 
					+ taskName + ", created by " + from + ".\n"; 
		text +="Please click the link below to process your partial data OR check the task status.\n";
			
		DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

		text +="Please note that the expiration time of the task is " + dtf.print(expDate) + ".\n\n";
//		text += root_property + "participant.html?email="+ to + "&taskName=" + taskName + "\n";
		text += root_property + "WaitForParticipants.html?email="+ to + "&taskName=" + taskName + "\n";
//		http://localhost:8080/webglore2/WaitForParticipants.html
		EmailSender es = new EmailSender(from, to, subject, text);
//		AuthEmailSender es = new AuthEmailSender("jiangwenchao88@gmail.com", to, subject, text);
		System.out.println("An email is sent to" + to);
		return es.send(); 
	} 
	
	/**
	return 0: task valid
		   1: task exists
		   -1: other error
		   2: task name not valid
	*/
	public int isValidTask(String task)
	{
		String task_pattern = "\\w+";
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
	
	public boolean validEmail(String email)
	{
		String EMAIL_PATTERN = "[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);;
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
		
				   
	}
	
}

