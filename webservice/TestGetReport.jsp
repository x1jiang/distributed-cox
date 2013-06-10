<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*,java.util.*,java.io.*,org.joda.time.DateTime,org.joda.time.DateTimeComparator,org.joda.time.format.DateTimeFormatter,org.joda.time.format.DateTimeFormat,java.sql.*,java.text.SimpleDateFormat, java.net.*;" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript">
function getReport(task){
	var xmlhttp;    
	var taskName = task;
	if (taskName==""){
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
			//send taskName to the server and get the URL to redirect
			var url = xmlhttp.responseText;
		  	document.getElementById("reportURL").innerHTML= url;
		    window.location.href = url;
		}
	}
	xmlhttp.open("GET","getreportservlet?taskName=" + taskName, true);
	xmlhttp.send();		
}
</script>
</head>
<body>
<% String taskName = "h86"; %>

<input type="button" value="getReport!" onClick="getReport('<%=taskName %>')"/>
<div id="reportURL"></div>
</body>
</html>