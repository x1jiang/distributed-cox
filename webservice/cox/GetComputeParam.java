package cox;

import java.sql.*;
import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.sf.json.JSONSerializer;

public class GetComputeParam  extends HttpServlet{

	private String dbconnection_property = null;
	private String dbusername_property = null;
	private String dbpassword_property = null;
	private String root_property = null;
	
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
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		Map<String, String> params = new HashMap<String, String>();
		String dataPath = null;
		String param=null;//added by lph
		String email = null;
		String taskName = null;
		int numClient = 0;
		if (req.getParameter("email") != null)
		{
			email = req.getParameter("email");
			req.getSession().setAttribute("email", email);
		}
		
	    if (req.getParameter("taskName") != null)
	    {
			taskName = req.getParameter("taskName");
			req.getSession().setAttribute("taskName", taskName);
	    }
	    System.out.println("taskName is " + taskName + "email is :" + email);
	    try{
		  Connection conn = DriverManager.getConnection(dbconnection_property, dbusername_property, dbpassword_property);
		  Statement stat = conn.createStatement();
		  String sql = "select u.datapath, g.parameters, g.taskStatus, g.property from user u, gtask g where u.task_id=g.id and g.name='" + taskName + "' and u.email='" + email + "';";
		  ResultSet rs = stat.executeQuery(sql);
		  if(rs.first())
		  {
		 	 dataPath=rs.getString(1);
		 	 param=rs.getString(2);//added by lph 
		  }
		  System.out.println("param is " + param);
		  String[] paramArray = param.split("#");//added by lph
		  int maxIteration=Integer.parseInt(paramArray[1]);
		  double epsilon= Double.parseDouble(paramArray[3]);
		  
		  /*conn = DriverManager.getConnection(dbconnection, dbuser, dbpwd);
		  stat = conn.createStatement();
		  sql = "select g.taskStatus from gtask g where g.name='" + taskName + "';";
		  rs = stat.executeQuery(sql);
		  int taskStatus = rs.getInt(1);*/
		  int taskStatus = rs.getInt(3);
		  String property = rs.getString(4);
		  String[] property1 = property.split("\t");
		  property = "";
		  for(int i=0; i<property1.length -1; i++){
			  property = property + property1[i] + "#"; 
		  }
		  property = property + property1[property1.length -1]; 
		  
		 // System.out.println("property is " + property);
			rs.close();
			conn.close();
			stat.close();
			
			params.put("dataPath", dataPath);
			params.put("taskName", taskName);
			params.put("root_property", root_property);
			params.put("maxIteration", new Integer(maxIteration).toString());
			params.put("epsilon", new Double(epsilon).toString());
			params.put("taskStatus", new Integer(taskStatus).toString());
			res.setContentType("application/x-json");
			PrintWriter out = res.getWriter();
			out.write(JSONSerializer.toJSON(params).toString());
			
	    }
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		doPost(req, res);
	}

}
