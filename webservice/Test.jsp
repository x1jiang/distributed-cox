<html>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" %>
<%@ page import="java.sql.*,java.util.*,java.io.*,org.joda.time.DateTime,org.joda.time.DateTimeComparator,org.joda.time.format.DateTimeFormatter,org.joda.time.format.DateTimeFormat,java.sql.*,java.text.SimpleDateFormat;" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<head>

		<title>WebCox</title>
		<link href="./css/styles.css" rel="stylesheet" type="text/css">
		<script src="./js/jquery-1.8.2.min.js" language="JavaScript" type="text/javascript"></script>
		<script src="./js/utility.js" language="JavaScript" type="text/javascript"></script>
		<script src="./js/utility_Draw.js" language="JavaScript" type="text/javascript"></script>
		<script src="http://code.highcharts.com/highcharts.js"></script>
		<script src="http://code.highcharts.com/modules/exporting.js"></script>
</head>

<body topmargin="0" leftmargin="0" rightmargin="0" bottommargin="0">
		<div id="main_container">

			<div id="left_container">
				<!-- Logo Start -->
				<div id="title">

					<div id="logo">
						<h1>COX</h1>
					</div>

					<div id="name">
						<h3>Web-based Distributed Cox Regression Model</h3>
					</div>

				</div>
				<!-- Logo End -->

				<!-- Navigation Start-->
				<div id="navigation" class="border rounded-upper">

					<div id="nav_header" class="bottom-border">
						<h3>Navigation</h3></div>

					<a id="nav_login" class="nav bottom-border" href="./login.html">
						<div class="nav_icon">
						</div>
						<div class="nav_text">
							<h2 class="">Login</h2>
							<p>Log into the COX system</p>
						</div>
					</a>
					
					<a id="nav_home" class="nav bottom-border" href="./index.html">
						<div class="nav_icon">
						</div>
						<div class="nav_text">
							<h2 >Home</h2>
							<p>View your COX profile page</p>
						</div>
					</a>

					<a id="nav_instructions" class="nav bottom-border" href="./instructions.html">
						<div class="nav_icon">
						</div>
						<div class="nav_text">
							<h2 class="">Instructions</h2>
							<p>Learn the fundamentals of using COX</p>
						</div>
					</a>

					<a id="nav_registration" class="nav bottom-border" href="./registration.html">
						<div class="nav_icon">
						</div>
						<div class="nav_text">
							<h2>Registration</h2>
							<p>Register an account in COX</p>
						</div>
					</a>

					<a id="nav_createTask" class="nav bottom-border" href="./createTask.html">
						<div class="nav_icon">
						</div>
						<div class="nav_text">
							<h2>Create Task</h2>
							<p>Create a new COX task</p>
						</div>
					</a>
					
					<a id="nav_wait" class="nav bottom-border" href="./WaitForParticipants.html">
						<div class="nav_icon">
						</div>
						<div class="nav_text">
							<h2>WaitForParticipants</h2>
							<p>Wait for other participants</p>
						</div>
					</a>

					<a id="nav_compute" class="nav bottom-border" href="./computation.jsp">
						<div class="nav_icon">
						</div>
						<div class="nav_text">
							<h2 class="hightlighted">Computation</h2>
							<p>Computation process</p>
						</div>
					</a>

					<a id="team" class="nav bottom-border" href="./team.html">
						<div class="nav_icon">
						</div>
						<div class="nav_text">
							<h2>Team</h2>
							<p>Team members</p>
						</div>
					</a>
					
				</div>
				<!-- Navigation End-->
			</div>	
			<div id="right_container" class="border rounded-upper">

				<div id="header" class="bottom-border">
					<h2>Test Data</h2>
				</div>

				<!-- <div id="error_display">

				</div>-->

				<div id="full_content">

				<table width="750" align="center" valign="middle" bgcolor="">
	 				<tr><td><table align="center" width="700">
<!--							<tr><td><table width="700" align="left" valign="left" >
								<tr><td align="center"></td>
									<tr><td><table width = 680 class="content">
 -->										<tr><td>Test COX using local data</td></tr>
											 <tr><td>Delay may happen when records>50</td></tr>
<%
	Properties properties = new Properties();
	InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("/config.properties");
	properties.load(is);
	String taskName = (String)request.getSession().getAttribute("taskName");
	String betaString = null;
	String surString = null;
	String fnString = null;
	//String Y_axis = "[1,2,3]";
	//String X_axis = "[4,5,6]";
	
	String dbconnection = properties.getProperty("dbconnection");
	request.getSession().setAttribute("dbconnection_property", dbconnection);
	String dbuser = properties.getProperty("dbusername");
	request.getSession().setAttribute("dbusername_property", dbuser);
	String dbpwd = properties.getProperty("dbpassword");
	request.getSession().setAttribute("dbpassword_property", dbpwd);
	String root = properties.getProperty("root");
	request.getSession().setAttribute("root_property", root);
	
	Class.forName("com.mysql.jdbc.Driver");
    Connection conn = DriverManager.getConnection(dbconnection, dbuser, dbpwd);
    Statement stat = conn.createStatement();
	String sql = "select beta from tempresult where taskName='" + taskName + "';";
  	ResultSet rs = stat.executeQuery(sql);
  	if(rs.first()){
		betaString = rs.getString(1);
		System.out.println("betaString" + betaString);
	}
  	String sql2 = "select sur from tempresult where taskName='" + taskName + "';";
  	ResultSet rs2 = stat.executeQuery(sql2);
	if(rs2.first()){
  		surString = rs2.getString(1);
  		System.out.println("surString " + surString);
	}
	String sql3 = "select fn from tempresult where taskName='" + taskName + "';";
  	ResultSet rs3 = stat.executeQuery(sql3);
	if(rs3.first()){
  		fnString = rs3.getString(1);
  		System.out.println("fnString " + fnString);
	}
	String showFilePath = "showFilePath";
	if (request.getSession().getAttribute("showFilePath") != null)
	{
		showFilePath = (String)request.getSession().getAttribute("showFilePath");
	}

%>

<tr>	
<td>
<applet code="cox.LocalTestApplet.class" archive="WebCox.jar" width=500 height=72" MAYSCRIPT>
<!-- The parameter of applet is added by jwc -->
<param name="showFilePath" value="<%=showFilePath %>">
<param name="betaString" value="<%=betaString %>">
<param name="surString" value="<%=surString %>">
<param name="fnString" value="<%=fnString %>">
</applet>
</td>
</tr>
</table>

<script language="Javascript">
function accessAppletResult()
{
	//var fn = "TestResult";
	//alert("in accessAppletMethod");
    var Y_axis = document.applets[0].getYaxis();
    var X_axis = document.applets[0].getXaxis();
    var stepVal = document.applets[0].getStep();
	DrawCurve(eval(X_axis), eval(Y_axis), eval(stepVal));
    //DrawCurve(X_axis,Y_axis);
    //document.getElementById('Y_axis').innerHTML = Y_axis;
    //document.getElementById('X_axis').innerHTML = X_axis;
}
</script>


</td></tr>
 
<tr>
<td><input type="hidden" name="showFilePath" id='showFilePath'  value='<%=showFilePath %>' size="50"/></td>
</tr>

 <!-- show axis 
 <tr>
 <td><div id="Y_axis" align="center" valign="middle">Show Y_axis below:</div></td>
</tr>
 <tr>
 <td><div id="X_axis" align="center" valign="middle">Show X_axis below:</div></td>
</tr>
  -->
 <!-- draw figure button
 <tr><td>
 <input type="button" value="Draw Curve" id="DrawCurveButton"  onClick="DrawCurve()">
</td></tr>
-->
</table>
	<div id="container" style="min-width: 400px; height: 400px; margin: 0 auto"></div>			
								
			</div>

		</div>
</body>
</html>
