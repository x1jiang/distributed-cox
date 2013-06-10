<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*,java.util.*,java.io.*,org.joda.time.DateTime,org.joda.time.DateTimeComparator,org.joda.time.format.DateTimeFormatter,org.joda.time.format.DateTimeFormat,java.sql.*,java.text.SimpleDateFormat;" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>The result of login</title>
</head>

<body>

<table width=600 align="left" class="content">
	<tr>
		<td> Name </td>
		<%
			String userName = "";
			if(request.getSession().getAttribute("userName")!= null){
				userName =(String) request.getSession().getAttribute("userName");
			}
			
		%>
		<td> <input type="text" name="User" value="<%=userName %>" size="15"/> </td>
	</tr>
	<tr>
		<td> Already registed </td>
		<%
			String inDB = "";
		   
			if(request.getSession().getAttribute("inDB")!= null){
				inDB = (String)request.getSession().getAttribute("inDB");
			}
		%>
		<td> <input type="text" name="inDB" value="<%=inDB %>"size="15"/> </td>
	</tr>
<%-- 	<tr>
		<td> Error </td>
		<%
			String err = "";
			if(request.getSession().getAttribute("error")!= null){
				err = (String)request.getSession().getAttribute("error");
			}
		<td> <input type="text" name="error" value="<%=err %>"size="30"/> </td>
	</tr>
--%>
<tr>
		<td> Status </td>
		<%
		    String Status = "";
		if(request.getSession().getAttribute("Status")!= null){
				Status = (String)request.getSession().getAttribute("Status");
			}
		%>
		<td> <input type="text" name="Status" value="<%=Status %>"size="30"/> </td>
	</tr>
<%-- <td bgcolor="#ACBC"> </td>--%>
<tr>
<td 
height="50" align="center"><input type="button" value="Return Home" class="content" onClick="window.location='index.html'">
</td>
</tr>
<%-- <td bgcolor="#BCBCBC"> </td> --%>

</table>

</body>

</html>
