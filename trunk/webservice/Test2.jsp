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
		<script src="./js/utility_Test.js" language="JavaScript" type="text/javascript"></script>
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
<%
	Properties properties = new Properties();
	InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("/config.properties");
	properties.load(is);
	String taskName = (String)request.getSession().getAttribute("taskName");
	String betaString = null;
	String surString = null;
	String fnString = null;
	String propertyString = null;
	String[] ps = null;
	
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
	String sql4 = "select property from gtask where name='" + taskName + "';";
  	ResultSet rs4 = stat.executeQuery(sql4);
	if(rs4.first()){
  		propertyString = rs4.getString(1);
  		System.out.println("propertyString " + propertyString);
  		ps = propertyString.split("\t");
	}
	String showFilePath = "showFilePath";
	if (request.getSession().getAttribute("showFilePath") != null)
	{
		showFilePath = (String)request.getSession().getAttribute("showFilePath");
	}
%>
				<div id="header" class="bottom-border">
					<h2>Test Cox using local data</h2>
				</div>
					
				<div id="error_display">
				</div>

				<div id="content">
					<form id = 'createTest' name = 'createTest'>
							<div class='top'>
								<h3>To test data by records or by features:</h3>
							</div>
							<div class = 'container alignleft'> 
								<table>
								<tr>
									<td>
								<div class = 'alignleft'><span>By records</span></div>
								<div class = 'alignleft'><input type='radio' value='byRecords' name='radio' checked="checked"></div>
									</td>
									<td>
								<div class = 'alignleft'><span>By features</span></div>
								<div class = 'alignleft'><input type='radio' value='byFeatures' name='radio'></div>
									</td>
								</tr>
								</table>
								<div class='clear'></div>
							</div>
							<div class = 'clear'></div>
							
		                    <div class="top">
								<h3>If by features, choose features to compare:</h3>
							</div>
							 
							<div class = 'container alignleft'> 
							<table>
							<%
							for(int i=0;i<(ps.length-2);i++){		
								out.print("<tr>");
								out.print("<td>");
								out.print("<div class = 'alignleft'><span>"+ps[i]+"</span></div>"); 
								out.print("<div class = 'alignleft'><input type='checkbox' value='"+i+"' name='box"+i+"'></div>"); 
								out.print("</td>");
								out.print("<td>");
								out.print("<div class = 'alignleft'><span>cutoff</span></div>"); 
								out.print("<div class = 'alignleft'><input type='text' name='text"+i+"'></div>"); 
								out.print("</td>");
								out.print("</tr>");
								out.print("<div class='clear'></div>"); 
							}
							%>
							</table>
							</div> 	
							<div class = 'clear'></div>

							<div class = 'container alignleft'> 			      
							<input TYPE="button" VALUE="Submit Form" onClick="info_input('<%=ps.length-2%>')"> 
							</div> 	
							<div class = 'clear'></div>
						</form>
					<div class='top'>
						<h3>Input test file:</h3>
					</div>
					<applet name=LTA code="cox.LocalTestApplet2.class" archive="WebCox.jar" width=500 height=72" MAYSCRIPT>
  					<param name="showFilePath" value="<%=showFilePath %>">
  					<param name="betaString" value="<%=betaString %>">
					<param name="surString" value="<%=surString %>">
					<param name="fnString" value="<%=fnString %>">
  					</applet>
						
					<div id="container" style="min-width: 400px; height: 400px; margin: 0 auto"></div>	
				</div>				
			</div>

		</div>
</body>
</html>
