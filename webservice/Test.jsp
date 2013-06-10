<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" %>
<%@ page import="java.sql.*,java.util.*,java.io.*,org.joda.time.DateTime,org.joda.time.DateTimeComparator,org.joda.time.format.DateTimeFormatter,org.joda.time.format.DateTimeFormat,java.sql.*,java.text.SimpleDateFormat;" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Test COX Performance</title>
<link rel="stylesheet" type="text/css" href="css/fonts.css" />
</head>
<body topmargin="0" leftmargin="0" rightmargin="0" bottommargin="0">
<table width="1024" align="center" valign="middle" bgcolor="">

<tr><td>
<table align="center" width="800">
<tr>
<td>

<table width="800" align="left" valign="left" >
<tr>

<td align="center"> </td>
<tr>
	<td>
		<table width = 800 class="content">
			<tr>
				<td>Test COX performance using local data</td>
			</tr>
<%
	Properties properties = new Properties();
	InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("/config.properties");
	properties.load(is);
	String taskName = (String)request.getSession().getAttribute("taskName");
	
	String dbconnection = properties.getProperty("dbconnection");
	request.getSession().setAttribute("dbconnection_property", dbconnection);
	String dbuser = properties.getProperty("dbusername");
	request.getSession().setAttribute("dbusername_property", dbuser);
	String dbpwd = properties.getProperty("dbpassword");
	request.getSession().setAttribute("dbpassword_property", dbpwd);
	String root = properties.getProperty("root");
	request.getSession().setAttribute("root_property", root);
	
    Connection conn = DriverManager.getConnection(dbconnection, dbuser, dbpwd);
    Statement stat = conn.createStatement();
	String sql = "select beta from tempresult where taskName='" + taskName + "';";
  	ResultSet rs = stat.executeQuery(sql);
  	rs.first();
  	String betaString = rs.getString(1);
  	System.out.println("betaString " + betaString);
  	
	String sql2 = "select sur from tempresult where taskName='" + taskName + "';";
  	ResultSet rs2 = stat.executeQuery(sql2);
  	rs2.first();
  	String surString = rs2.getString(1);
  	System.out.println("surString " + surString);

	String sql3 = "select fn from tempresult where taskName='" + taskName + "';";
  	ResultSet rs3 = stat.executeQuery(sql3);
  	rs3.first();
  	String fnString = rs3.getString(1);
  	System.out.println("fnString " + fnString);

	String showFilePath = "showFilePath";
	if (request.getSession().getAttribute("showFilePath") != null)
	{
		showFilePath = (String)request.getSession().getAttribute("showFilePath");
	}
	
	
%>
	
<td>
<applet code="cox.LocalTestApplet.class" archive="cox.jar, Jama-1.0.2.jar" width=500 height=70" MAYSCRIPT>
<!-- The parameter of applet is added by jwc -->
<param name="showFilePath" value="<%=showFilePath %>">
<param name="betaString" value="<%=betaString %>">
<param name="surString" value="<%=surString %>">
<param name="fnString" value="<%=fnString %>">
</applet>
</td>
</tr>
</table>

		</table>
	</td>
</tr>
<script language="Javascript">

function accessAppletResult()
{
	var fn = "TestResult";
	//alert("in accessAppletMethod");
 //   fn = document.applets[0].getAttributes();
 //   document.getElementById('showProperty').value = fn;
    fn = document.applets[0].getResults();
    document.getElementById('TestResult').innerHTML = fn;
    //document.getElementById('sameAsApplet').style.display='none';
}
</script>
<tr>
<td><input type="hidden" name="showFilePath" id='showFilePath'  value='<%=showFilePath %>' size="50"/></td>
</tr>


 <tr>
 <td>
<div id="TestResult" align="center" valign="middle">Here to show the test result!</div></td>
</tr>

</tr>
</table>
</form>
</td>

</table>
</body>
</html>
