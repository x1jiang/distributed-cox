<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*;" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>User Login</title>
</head>
<body topmargin="0" leftmargin="0" rightmargin="0" bottommargin="0">

<form  action="./register" method="get" enctype="application/x-www-form-urlencoded">	
<table width=600 align="left" class="content">
	<tr>
		<td> Name </td>
		<td> <input type="text" name="User" size="15"/> </td>
	</tr>
	<tr>
		<td> Email </td>
		<td> <input type="text" name="email" size="15"/> </td>
	</tr>
	<tr>
		<td> Password </td>
		<td> <input type="password" name="pwd" size="16"/> </td>
	</tr>
	
	<tr >
		<td> 
			<input type="submit" value="Register" class="content" />
		</td>
	</tr>	

</table>
</form>
</body>
</html>
