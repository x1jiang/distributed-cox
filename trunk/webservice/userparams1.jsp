<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*,java.util.*,java.io.*,org.joda.time.DateTime,org.joda.time.DateTimeComparator,org.joda.time.format.DateTimeFormatter,org.joda.time.format.DateTimeFormat,java.sql.*,java.text.SimpleDateFormat;" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<!--  <head>
<title>Step 2: Client Data Process Applet</title>
<link rel="stylesheet" type="text/css" href="css/fonts.css" />
</head>
-->
<script type="text/javascript">
// added by jwc 10.4
function checkReadyContinuous(task,useremail){
	function checkReady(){
		var xmlhttp;    
		var taskName = task;
		var email=useremail;
		if (taskName==""){
		  document.getElementById("readyStatus").innerHTML="";
		  return;
		  }
		if (window.XMLHttpRequest)
		  {// code for IE7+, Firefox, Chrome, Opera, Safari
		  xmlhttp=new XMLHttpRequest();
		  }
		else
		  {// code for IE6, IE5
		  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
		  }
		xmlhttp.onreadystatechange=function()
		  {
		  if (xmlhttp.readyState==4 && xmlhttp.status==200)
		    {
		  	document.getElementById("readyStatus").innerHTML=xmlhttp.responseText;
			var xmlDoc = xmlhttp.responseXML;
		  	var table = "";
		  	table = table + "<table>";
		  	table = table + "<tr> <td> Email </td> <td> Participant Status </td> <td> TaskStatus </td> </tr>";
		  	for( var i=0; i < xmlDoc.getElementsByTagName("TaskStatus").length; i++){
		  		table = table + "<tr> <td> "+ xmlDoc.getElementsByTagName("Email")[i].childNodes[0].nodeValue +" </td> <td> "+ xmlDoc.getElementsByTagName("Status")[i].childNodes[0].nodeValue +" </td> <td> "+ xmlDoc.getElementsByTagName("TaskStatus")[i].childNodes[0].nodeValue+" </td> </tr>";
		  	}
		  	table = table + "</table>";
		  	document.getElementById("readyStatus").innerHTML = table;
			//  document.getElementById("readyStatus").innerHTML = "Just for test";
		    }
		  }
		//document.write("checkready?'taskName'=taskName");
		xmlhttp.open("GET","checkready?email=" + email+"&taskName="+taskName, true);
		xmlhttp.send();		
	}
	setInterval(checkReady, 2000);
}
function checkReadyStart(){
	//var task = "hello8";
	<%
	String task = null;
                 if (request.getParameter("taskName") != null)
			     {
					task = request.getParameter("taskName");
					request.getSession().setAttribute("taskName", task);
			     }
			     else if (session.getAttribute("taskName") != null)
			     {
			          task = (String)session.getAttribute("taskName");
				
			     }
	%>
	var task="<%=task %>";
	<% String email = null;
     if (request.getParameter("ownerEmail") != null)
     {
		email = request.getParameter("ownerEmail");
		request.getSession().setAttribute("ownerEmail", email);
     }
     else if (session.getAttribute("ownerEmail") != null)
     {
          email = (String)session.getAttribute("ownerEmail");
	
     }
     %>
     var email="<%=email %>";
	checkReadyContinuous(task, email);
}
</script> 


<!--  <body onLoad="displayQuote(), checkReady('hello8');" topmargin="0" leftmargin="0" rightmargin="0" bottommargin="0" id="step2"> -->
<body onLoad="checkReadyStart();" topmargin="0" leftmargin="0" rightmargin="0" bottommargin="0" id="step2"> 
<%!
	//return 0: is valid
	// 	  1: expired
	//	  2: task not exists
	int isValidTaskUser(String task, String user, String connection, String dbuser, String pwd)
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		}		
		catch(ClassNotFoundException e)
		{
			System.out.println(e.toString());
			return -1;
		}
		try
		{
			
			Connection conn = DriverManager.getConnection(connection, dbuser, pwd);
			Statement stat = conn.createStatement();

			String sql = "select u.task_id, u.email, t.exp_date from USER u, gtask t where t.name='" + task + "' and u.email='" + user 
						+ "' and t.id=u.task_id";
			ResultSet rs = stat.executeQuery(sql);
			if (rs.first())
			{
				SimpleDateFormat sdtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String expDateString = sdtf.format(rs.getDate(3));
				DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
				DateTime expDate = dtf.parseDateTime(expDateString);
				Calendar cal = Calendar.getInstance();
				DateTime curDate = dtf.parseDateTime(sdtf.format(cal.getTime()));
				if (DateTimeComparator.getInstance().compare(curDate, expDate)<=0)
				{
					stat.close();
					conn.close();
					return 0;
				}
				stat.close();
				conn.close();
				return 1;
			}
			stat.close();
			conn.close();
			return 2;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return -1;	
	}
	
	String getTaskParameters(String task, String email, String connection, String dbuser, String pwd)
	{
		String param = "";
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		}		
		catch(ClassNotFoundException e)
		{
			param+=	e.toString();
		}
		try
		{
			Connection conn = DriverManager.getConnection(connection, dbuser, pwd);
			Statement stat = conn.createStatement();

			String sql = "select t.parameters from USER u, gtask t where t.name='" + task + "' and u.email='" + email 
						+ "' and t.id=u.task_id";
			stat.executeQuery(sql);
			ResultSet rs = stat.getResultSet();
			if (rs.next())
			{
				param = rs.getString(1);
				stat.close();
				conn.close();
				return  param;
			}
			
			stat.close();
			conn.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			param += e.toString();		
		}
		return param;
	
	}
%>
<table width="1024" align="center" valign="middle" bgcolor="">

<tr><td>
<table align="center" width="800">
<tr>
<td>
<form action="beforecalservlet" method = "post" enctype="application/x-www-form-urlencoded">
<table width=600>
<tr><td class="header2">Welcome to participate the following task. All participants have been sent a link to this page.
</td></tr>
<tr><td><HR></td></tr>
<tr><td>
	<table width=400>
	<tr><td class="header2" colspan=4>Task Information:</td></tr>
	<tr>
		<td width=100>Task Name: </td>
		<td width=100>
			<%
			
				Properties properties = new Properties();
				InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("/config.properties");
				properties.load(is);
			
				String dbconnection = properties.getProperty("dbconnection");
				request.getSession().setAttribute("dbconnection_property", dbconnection);
				String dbuser = properties.getProperty("dbusername");
				request.getSession().setAttribute("dbusername_property", dbuser);
				String dbpwd = properties.getProperty("dbpassword");
				request.getSession().setAttribute("dbpassword_property", dbpwd);
				String root = properties.getProperty("root");
				request.getSession().setAttribute("root_property", root);
				
			     /*String task = null;
                 if (request.getParameter("taskName") != null)
			     {
					task = request.getParameter("taskName");
					request.getSession().setAttribute("taskName", task);
			     }
			     else if (session.getAttribute("taskName") != null)
			     {
			          task = (String)session.getAttribute("taskName");
				
			     }*/	//put it into javascript by jwc 10.4
			     out.print(task);	 
			%></td>
		<td width=100>Sponsor Email: </td>
		<td width=100>
			<%
//			     String email = null;
//                 if (request.getParameter("ownerEmail") != null)
//			     {
//					email = request.getParameter("ownerEmail");
//					request.getSession().setAttribute("ownerEmail", email);
//			     }
//			     else if (session.getAttribute("ownerEmail") != null)
//			     {
//			          email = (String)session.getAttribute("ownerEmail");
//				
//			     }
			     out.print(email);
			%></td>

	</tr>
	<tr>
	</table>
	

	</td>
</td></tr>
<% int isValid = isValidTaskUser(task, email, dbconnection, dbuser, dbpwd); 
	if (isValid == 0)
	{
 %>
<tr>
	<td>
		<HR>
	</td>
</tr>
<tr><td>
	<table width=400>
	<tr>
	<td colspan=2 class="header2">Parameters Used in the Task:
	</td>
	</tr>
	<tr>
		<td width=200 > <% String parameters = getTaskParameters(task, email, dbconnection, dbuser, dbpwd);
	        String[] paramArray = parameters.split("#");
			session.setAttribute("param", parameters);%> Allowed maximum iteration number :
	</td>
	<td width=100>
		<% out.println(paramArray[1]); %>
	</td>
	</tr>
	<tr>
	<td>Epsilon:</td>
	<td><% out.println(paramArray[3]); %></td>
	</tr>
	</table>
	</td>
	
</tr>
<tr>
	<td>
		<HR>
	</td>
</tr>
<tr><td>
<table width="600">
<tr><td width="600" colspan=2 class="header2">Participant Status:</td></tr>
</table>
<!-- add by jwc 10.4 show the result of participant result -->
<div id="readyStatus">show the status of participants here </div>
</td></tr>
<tr>
	<td>
		<HR>
	</td>
</tr>

<tr>
	<td>
	<table colspan=4 width=400>
	<% //HashMap<String, String> errors = (HashMap)request.getSession().getAttribute("errors");
	%>
	<tr>
	<td colspan=2 class="header2">User Data Properties:</td>
	</tr>
	<tr>
	<td width=200>
<%	   String property = null;
                 if (request.getParameter("showProperty") != null)
			     {
					property = request.getParameter("showProperty");
					request.getSession().setAttribute("showProperty", property);
			     }
			     else if (session.getAttribute("showProperty") != null)
			     {
			          property = (String)session.getAttribute("showProperty");
				
			     }
	 out.print(property);
%>	
	</td>
	</tr>
	</table>
	</td>
</tr>
<tr>
	<td>
		<HR>
	</td>
</tr>
<tr>
<td>
<table width=600 >
<tr>
<!-- <td class="content" align="center">
<input type="button" value="Come back later" onclick="window.open('', '_self', '');window.close();">
</td>
 -->
<td class="content" align="center"><input type="submit" value="Start modeling" /></td>
</tr>
</table>
</td>
</tr>
<% }
			else if (isValid==2)
				{
				%>
				
				<tr><td>One of following reasons caused you cannot process and upload your data:</td></tr>
				<tr><td>&#42 No such task created;</td></tr>
				<tr><td>&#42 Wrong user email address;</td></tr>
			<%
			   }
			else if (isValid==1)
			{
			%>
			<tr><td>The following reason caused you cannot process and upload your data:</td></tr>
			<tr><td>&#42 The task has been expired.</td></tr>
			<% }
			%>
</table>
</form></td>

</tr>
</table>

</td></tr>
</table>
</body>
</html>
