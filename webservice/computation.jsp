<html>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*,java.util.*,java.io.*,org.joda.time.DateTime,org.joda.time.DateTimeComparator,org.joda.time.format.DateTimeFormatter,org.joda.time.format.DateTimeFormat,java.sql.*,java.text.SimpleDateFormat;" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<head>

		<title>WebCox</title>
		<link href="./css/styles.css" rel="stylesheet" type="text/css">
		</script><script src="./js/jquery-1.8.2.min.js" language="JavaScript" type="text/javascript"></script>
		<script src="./js/utility.js" language="JavaScript" type="text/javascript"></script>

		 <script src="./js/utility_computation.js" language="JavaScript" type="text/javascript"></script> 
</head>

<body  topmargin="0" leftmargin="0" rightmargin="0" bottommargin="0" > 
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
					<h2>Computation</h2>
				</div>

				<div id="error_display">

				</div>

				<div id="content">
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
					
					String dataPath = null;
					String param=null;//added by lph
					String email = null;
					String taskName = null;
					 int maxIteration=20;
					 double epsilon= 0.000001;
					 String[] paramArray;
					  int taskStatus = -1 ;
					  String property = null;
					  String[] property1 = null;
					 
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
				    Class.forName("com.mysql.jdbc.Driver");
				    Connection conn = DriverManager.getConnection(dbconnection, dbuser, dbpwd);
				    Statement stat = conn.createStatement();
				  String sql = "select u.datapath, g.parameters, g.taskStatus, g.property from user u, gtask g where u.task_id=g.id and g.name='" + taskName + "' and u.email='" + email + "';";
				  ResultSet rs = stat.executeQuery(sql);
				  if(rs.first())
				  {
				 	 dataPath=rs.getString(1);
				 	 param=rs.getString(2);//added by lph 
					  if(param!=null){
						  paramArray = param.split("#");//added by lph
						  maxIteration=Integer.parseInt(paramArray[1]);
						  epsilon= Double.parseDouble(paramArray[3]);
					  }
					  taskStatus = rs.getInt(3);
					  property = rs.getString(4);
					  property1 = property.split("\t");
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
				  }
				%>
				
				<table>
				<tr>
				<td>
				<!-- The parameter of applet is added by jwc -->
				<applet code="cox.Procedure2Applet.class" archive="WebCox.jar" width=500 height=450>			
				<param name="dataPath" value="<%=dataPath %>">
				<param name="taskName" value="<%=taskName %>">
				<param name="root_property" value="<%=root %>">
				<param name="maxIteration" value="<%=maxIteration %>">
				<param name="epsilon" value="<%=epsilon %>">
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
				
				</div>

			</div>

		</div>
</body>
</html>
