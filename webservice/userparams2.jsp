<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*,java.util.*,java.io.*,org.joda.time.DateTime,org.joda.time.DateTimeComparator,org.joda.time.format.DateTimeFormatter,org.joda.time.format.DateTimeFormat,java.sql.*,java.text.SimpleDateFormat;" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Step 2: Client Data Process Applet</title>
<link rel="stylesheet" type="text/css" href="css/fonts.css" />
</head>

<script type="text/javascript">
// added by jwc 10.4
function checkReadyContinuous(task){
	function checkReady(){
		var xmlhttp;    
		var taskName = task;
		if (taskName==""){
		  document.getElementById("readyStatus").innerHTML="";
		  document.getElementById("taskreadyStatus").innerHTML="";//added by lph
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
		<%
		String email = null;
	                 if (request.getParameter("email") != null)
				     {
						email = request.getParameter("email");
						request.getSession().setAttribute("email", email);
				     }
				     else if (session.getAttribute("email") != null)
				     {
				          email = (String)session.getAttribute("email");
					
				     }
		%>
		var useremail="<%=email %>";
		xmlhttp.onreadystatechange=function()
		  {
		  if (xmlhttp.readyState==4 && xmlhttp.status==200)
		    {
		  	//document.getElementById("readyStatus").innerHTML=xmlhttp.responseText;

			//  document.getElementById("readyStatus").innerHTML = "Just for test";
			var xmlDoc = xmlhttp.responseXML;
		  	var table = "";
		  	table = table + "<table>";
		  	table = table + "<tr> <td> Email </td> <td> Participant Status </td> <td> TaskStatus </td> </tr>";
		  	for( var i=0; i < xmlDoc.getElementsByTagName("TaskStatus").length; i++){
		  		table = table + "<tr> <td> "+ xmlDoc.getElementsByTagName("Email")[i].childNodes[0].nodeValue +" </td> <td> "+ xmlDoc.getElementsByTagName("Status")[i].childNodes[0].nodeValue +" </td> <td> "+ xmlDoc.getElementsByTagName("TaskStatus")[i].childNodes[0].nodeValue+" </td> </tr>";
		  	}
		  	table = table + "</table>";
		  	document.getElementById("readyStatus").innerHTML = table;
		  	document.getElementById("yourStatus").innerHTML = xmlDoc.getElementsByTagName("SelfStatus")[0].childNodes[0].nodeValue;
			if(xmlDoc.getElementsByTagName("TaskStatus").length> 0 && xmlDoc.getElementsByTagName("SelfStatus").length> 0)
			{
				var isTaskBegin = xmlDoc.getElementsByTagName("TaskStatus")[0].childNodes[0].nodeValue;
				var isUserReady = xmlDoc.getElementsByTagName("SelfStatus")[0].childNodes[0].nodeValue;
				document.getElementById("taskreadyStatus").innerHTML=isTaskBegin;
				if(isTaskBegin == "1" &&isUserReady=="1"){//
					
					var email = "<%=email %>";
					var url = "computation.jsp?taskName=" + taskName + "&email=" + email;
					window.location.href=url;
				}
				else if (isTaskBegin != "0" && isUserReady=="0"){						
					alert("Task has began. You missed this task!");
					var url = "index.html";
					window.location.href=url;
				}
			}
			else
			{
				alert("XML data fortmat is wrong and the original text content is:"+xmlhttp.responseText);
			}
		}

		}
		xmlhttp.open("GET","checkready?taskName=" + taskName + "&email=" + useremail, true);
		xmlhttp.send();		
	}

	setInterval(checkReady, 3000);
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
	checkReadyContinuous(task);
}
</script> 

<!-- This file added script, body, div named readyStatus and the store of input content similar to userparam1 by jwc at 10.5 -->
<body onLoad="checkReadyStart();" topmargin="0" leftmargin="0" rightmargin="0" bottommargin="0" id="step2">
<%!
	//return 0: is valid
	// 	  1: expired
	//	  2: task not exists
	//    3: kernel already submitted
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
<tr><td>
  <form action="useruploadservlet" method = "post" enctype="application/x-www-form-urlencoded"> 
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
                 if (request.getParameter("task") != null)
			     {
					task = request.getParameter("task");
					request.getSession().setAttribute("task", task);
			     }
			     else if (session.getAttribute("task") != null)
			     {
			          task = (String)session.getAttribute("task");
				
			     }*/
			     out.print(task);	 
			%></td>
		<td width=100>User Email: </td>
		<td width=100>
			<%
			     String useremail = null;
                 if (request.getParameter("email") != null)
			     {
					useremail = request.getParameter("email");
					request.getSession().setAttribute("email", useremail);
					out.print(useremail);
			     }
			     else if (session.getAttribute("email") != null)
			     {
			          useremail = (String)session.getAttribute("email");
			          session.setAttribute("email", useremail);
			         
				
			     }
			%></td>

	</tr>
	<tr>
	</table>
	

	</td>
</td></tr>
<% int isValid = isValidTaskUser(task, useremail, dbconnection, dbuser, dbpwd); 
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
		<td width=200 > <% String parameters = getTaskParameters(task, useremail, dbconnection, dbuser, dbpwd);
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
<!-- add by jwc 10.5 show the result of participant result -->
<div id="readyStatus">show the status of participants here </div>
</td></tr>
<tr>
	<td>
		<HR>
	</td>
</tr>

<tr><td>
<table width="600">
<tr><td width="600" colspan=2 class="header2">Task Status:</td></tr>
</table>
<!-- add by jwc 10.5 show the result of participant result -->
<div id="taskreadyStatus">show the status of the task here </div>

<table width="600">
<tr><td width="600" colspan=2 class="header2">Your Status:</td></tr>
</table>
<div id="yourStatus"> Show your status here</div>
</td></tr>
<tr>
	<td>
		<HR>
	</td>
</tr>

<tr>
	<td>
	<table colspan=4 width=400>
	<% HashMap<String, String> errors = (HashMap)request.getSession().getAttribute("errors");
	%>
	<tr>
	<td colspan=2 class="header2">User Data Properties:</td>
	</tr>
	<tr>
	<td width=200>
<%	   String property = null;
      Connection conn = DriverManager.getConnection(dbconnection, dbuser, dbpwd);
       Statement stat = conn.createStatement();
     String sql = "select t.property from gtask t where t.name='" + task + "'";
     ResultSet rs = stat.executeQuery(sql);
     if(rs.first())
     {
    	 property=rs.getString(1);
    	 conn.close();
    	 stat.close();
     }
            //    if (request.getParameter("showProperty") != null)
			//     {
			//		property = request.getParameter("showProperty");
	request.getSession().setAttribute("showProperty", property);
			//     }
			//     else if (session.getAttribute("showProperty") != null)
			//     {
			//          property = (String)session.getAttribute("showProperty");			
			//     }
	 out.print(property);
%>	
	</td>
	</tr>
	</table>
	</td>
</tr>
<tr>
<table width="800" align="left" valign="left" >
<tr>

<% 
	String userFilePath = "userFilePath";
	if (request.getSession().getAttribute("userFilePath") != null)
	{
		userFilePath = (String)request.getSession().getAttribute("userFilePath");
	}
	
	String userProperty = "userProperty";
	if (request.getSession().getAttribute("userProperty") != null)
	{
		userProperty = (String)request.getSession().getAttribute("userProperty");
	}
	String propertyNameError = null;
	if (errors!= null)
	{
	propertyNameError = errors.get("userpropertyError");
	}
	if (propertyNameError!=null) 
	{
	errors.remove("userpropertyError");
	%>
	<span><font color="red"><%=propertyNameError %></font></span>
 <% } %>

<td>
<applet code="glore.ChooseFileApplet.class" archive="ChooseFileApplet.jar" width=500 height=55>
<!-- The parameter of applet is added by jwc -->
<param name="userFilePath" value="<%=userFilePath %>">
</applet>
</td>
</tr>
</table>
<td></td>

<!--<tr><td>
  <input type="button" onclick="accessAppletMethod()" value="ClickMe">
</td></tr>-->
<!-- <div id="showFileName"> Here show file name </div>-->
<!--  <div id="showProperty">Here show the property read</div>-->
<tr>
<td><input type="text" name="userFilePath" id='userFilePath'  value='<%=userFilePath %>' size="50"/></td>
</tr>
<tr>
<td><input type="text" name="userProperty" id='userProperty'  value='<%=userProperty %>' size="50"/></td>
</tr>


<script language="Javascript">
function accessAppletMethod()
{
	var fn = "fn";
    fn = document.applets[0].getAttributes();
    document.getElementById('userProperty').value = fn;
    fn = document.applets[0].getFilename();
    document.getElementById('userFilePath').value = fn;
    //document.getElementById('sameAsApplet').style.display='none';
}
</script>

</tr>
<tr>
<td>
<table width=600 >
<tr>
<!-- <td> 
  <html>
  <head>
	<script type="text/javascript">
	function to(){
		window.location.href="userparams1.jsp";
		}
	</script> 
  </head> 
  <body>
  <input type="button" value="Submit" Onclick="to()"/>
  </body>
 </html> 
</td>-->
<!-- <td class="content" align="center">
<input type="button" value="Come back later" onclick="window.open('', '_self', '');window.close();">
</td>
 -->
 <td class="content" align="center"><input type="submit" value="Submit" /> </td>
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
