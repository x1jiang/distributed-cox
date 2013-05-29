<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*,java.util.*,java.io.*,org.joda.time.DateTime,org.joda.time.DateTimeComparator,org.joda.time.format.DateTimeFormatter,org.joda.time.format.DateTimeFormat,java.sql.*,java.text.SimpleDateFormat;" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript">

function accessAppletMethod2()
{
	var fn = "fn";
	alert("in accessAppletMethod2");
    fn = document.applets[1].getURL();
    document.getElementById('LocalreportURL').value = fn;
}

</script>
<title>Step 2: Client Data Process Applet</title>
</head>

<!--  <body onLoad="displayQuote(), checkReady('hello8');" topmargin="0" leftmargin="0" rightmargin="0" bottommargin="0" id="step2"> -->
<body  topmargin="0" leftmargin="0" rightmargin="0" bottommargin="0" > 
<table width="1024" align="center" valign="middle" bgcolor="">

</table>
<%
	System.out.println("in computation.jsp");
	Properties confProperty = new Properties();
	InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("/config.properties");
	confProperty.load(is);
	
	String dbconnection = confProperty.getProperty("dbconnection");
	request.getSession().setAttribute("dbconnection_property", dbconnection);
	String dbuser = confProperty.getProperty("dbusername");
	request.getSession().setAttribute("dbusername_property", dbuser);
	String dbpwd = confProperty.getProperty("dbpassword");
	request.getSession().setAttribute("dbpassword_property", dbpwd);
	String root = confProperty.getProperty("root");
	request.getSession().setAttribute("root_property", root);
	String outAddress = confProperty.getProperty("outAddress");
	request.getSession().setAttribute("outAddress_property", outAddress);	

	String dataPath = null;
	String param=null;//added by lph
	String email = null;
	String taskName = null;
	int numClient = 0;
	if (request.getParameter("email") != null)
	{
		email = request.getParameter("email");
		request.getSession().setAttribute("email", email);
	}
	else if (session.getAttribute("email") != null)
	{
	     email = (String)session.getAttribute("email");
	
	}
	
    if (request.getParameter("taskName") != null)
    {
		taskName = request.getParameter("taskName");
		request.getSession().setAttribute("taskName", taskName);
    }
    else if (session.getAttribute("taskName") != null)
    {
         taskName = (String)session.getAttribute("taskName");
	
    }
    
    Connection conn = DriverManager.getConnection(dbconnection, dbuser, dbpwd);
    Statement stat = conn.createStatement();
  String sql = "select u.datapath, g.parameters, g.taskStatus, g.property from user u, gtask g where u.task_id=g.id and g.name='" + taskName + "' and u.email='" + email + "';";
  ResultSet rs = stat.executeQuery(sql);
  if(rs.first())
  {
 	 dataPath=rs.getString(1);
 	 param=rs.getString(2);//added by lph 
  }
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
  
  System.out.println("params: " + email + " " + taskName + " " + taskStatus + " " + property + " " + dataPath);
  
 // System.out.println("property is " + property);
	rs.close();
	conn.close();
	stat.close();
%>
<!--  <script type="text/javascript">
<%
//if(taskStatus==2)
//{
%>
      var taskName="<%=taskName%>";
      var email = "<%=email %>";
	  var url = "computation.jsp?email=" + email + "&taskName=" + taskName;
	  window.location.href=url;
<%//}%>
</script> -->
<%
// if(taskStatus==1)
//{
%>
<table>
<tr>
<td>
<applet code="cox.Procedure2Applet.class" archive="cox.jar, Jama-1.0.2.jar" width=500 height=450>
<!-- The parameter of applet is added by jwc -->
<param name="dataPath" value="<%=dataPath %>">
<param name="taskName" value="<%=taskName %>">
<param name="root_property" value="<%=root %>">
<param name="maxIteration" value="<%=paramArray[1] %>">
<param name="epsilon" value="<%=paramArray[3] %>">
<param name="taskStatus" value="<%=taskStatus %>">
</applet>
</td>
</tr>
</table>
<%//}
%>

<td bgcolor="#BCBCBC"> </td>
<td height="50" align="center"><input type="button" id="TestButton" value="Test" class="content" onClick="window.location='Test.jsp'">
</td>

</tr></td>
</body>
</html>
