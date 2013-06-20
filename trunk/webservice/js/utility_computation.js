$(document).ready(function(){

	/* Global Variables */
	/* Mouse Click Listeners */

});
/*
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
function getLocalReport(task){
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
		  	document.getElementById("LocalreportURL").innerHTML= url;
		    window.location.href = url;
		}
	}
	xmlhttp.open("GET","getlocalreportservlet?taskName=" + taskName, true);
	xmlhttp.send();		
}

function accessAppletMethod2()
{
	var fn = "fn";
	alert("in accessAppletMethod2");
    fn = document.applets[1].getURL();
    document.getElementById('LocalreportURL').value = fn;
}

function enableButton()
{
	var gB = document.getElementById("globalButton");	
	gB.disabled=false;
	var gB2 = document.getElementById("TestButton");	
	gB2.disabled=false;
	 document.applets[1].setEnable();
}
*/
