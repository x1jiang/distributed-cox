$(document).ready(function(){
	var Request = GetRequest();
//	$("#step4b_taskName") = Request['taskName'];
//	$("#step4b_email") = Request['email'];
	$('#step4b_initiatorEmail')[0].innerHTML = Request['email'];
	$('#step4b_taskName')[0].innerHTML = Request['taskName'];
	var taskName = Request['taskName'];
	var email = Request['email'];
//	checkReadyContinuous(taskName, email);
	
	$('button#step4b_submit').click(function(event){
		//--------added by lph 12/26---------------//
		if ( comfirmProperty())
		{
//			alert(comfirmProperty());
			var xmlhttp;
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
//				var xmlDoc = xmlhttp.responseXML;
//				var CheckPropertyresult=xmlDoc.getElementsByTagName("CheckPropertyresult")[0].childNodes[0].nodeValue;
//			    if(CheckPropertyresult==1) 
//			    {
//			    	$('#choosefile').removeClass('wrong').addClass('check');//data property is identical to initiator's
//			    }
//			    else $('#choosefile').removeClass('check').removeClass('wrong').addClass('wrong');
//				
			    }
			  }
			//document.write("checkready?'taskName'=taskName");
			var userFilePath= $("#userFilePath")[0].value;
			var userProperty = $("#userProperty")[0].value;
			xmlhttp.open("GET","./useruploadservlet?email=" + Request['email'] +"&taskName="+ Request['taskName'] + 
					"&userFilePath=" + userFilePath + "&userProperty=" + userProperty, true);
			xmlhttp.send();	
		}
		});
		//----------------------------------//
});

//-----------added by lph---------------//
/* User's data property confirmation */
function comfirmProperty ()
{
	var $form = $('form#joinTask');
	var $taskProperty;
	var $userProperty;
	var flag = 1;
	$form.find('input').each(function(){
		var id = this.id;
		var $value = $('#'+id).val(); 
		switch (id)
		{
			case "taskProperty": $taskProperty = $value; break;
			case "userProperty": $userProperty = $value; break;
		}
	});
	//if(checkTaskname())
	//{
	//	$('#createTask_title').removeClass('check').addClass('exist');
	//	flag = 0;
	//}
	if ( $userProperty == $taskProperty )
	{
		$('#choosefile').removeClass('wrong').addClass('check');
		flag = 1;
	}
	else
	{
		$('#choosefile').removeClass('check').addClass('wrong');
		flag=0;
	}
	return flag;
}

function checkReadyContinuous(task,useremail){
	function checkReady(){
		var xmlhttp;    
		var taskName = task;
		var email=useremail;
		if (taskName==""){
		  document.getElementById("readyStatus").innerHTML="";
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
		  	document.getElementById("readyStatus").innerHTML=xmlhttp.responseText;
			var xmlDoc = xmlhttp.responseXML;
		  	var table = "";
		  	table = table + "<table>";
		  	table = table + "<tr> <td> Email </td> <td> Participant Status </td> <td> TaskStatus </td> </tr>";
		  	for( var i=0; i < xmlDoc.getElementsByTagName("TaskStatus").length; i++){
		  		table = table + "<tr> <td> "+ xmlDoc.getElementsByTagName("Email")[i].childNodes[0].nodeValue 
		  		+" </td> <td> "+ xmlDoc.getElementsByTagName("Status")[i].childNodes[0].nodeValue +" </td> <td> "
		  		+ xmlDoc.getElementsByTagName("TaskStatus")[i].childNodes[0].nodeValue+" </td> </tr>";
		  	}
		  	table = table + "</table>";
		  	document.getElementById("readyStatus").innerHTML = table;
			//  check if computation start
		  	var isUserReady;
		  	for( var i=0;i< xmlDoc.getElementsByTagName("Status").length; i++)
		  	{
		  		if (email==xmlDoc.getElementsByTagName("Email")[i].childNodes[0].nodeValue)
		  		{
		  			isUserReady=xmlDoc.getElementsByTagName("Status")[i].childNodes[0].nodeValue;
		  			break;
		  		}
		  	}
			if(xmlDoc.getElementsByTagName("TaskStatus").length> 0 && xmlDoc.getElementsByTagName("Status").length> 0)
			{
				var isTaskBegin = xmlDoc.getElementsByTagName("TaskStatus")[0].childNodes[0].nodeValue;
			//	var isUserReady = xmlDoc.getElementsByTagName("SelfStatus")[0].childNodes[0].nodeValue;
				if(isTaskBegin == "1" &&isUserReady=="1"){//
					
					//var email = "<%=email %>";
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
		//document.write("checkready?'taskName'=taskName");
		xmlhttp.open("GET","checkready?email=" + email+"&taskName="+taskName, true);
		xmlhttp.send();		
	}
	setInterval(checkReady, 2000);
}
function checkPropertyContinuous(task){
	function checkPropertyReady(){
		var xmlhttp;    
		var taskName = task;
		if (taskName==""){
		  document.getElementById("taskdata_property").innerHTML="";
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
		  	
			var xmlDoc = xmlhttp.responseXML;
			var taskproperty=xmlDoc.getElementsByTagName("taskproperty")[0].childNodes[0].nodeValue;			
			document.getElementById("taskdata_property").innerHTML=taskproperty;
			document.getElementById('taskProperty').value=taskproperty;
			 var ownerEmail=xmlDoc.getElementsByTagName("ownerEmail")[0].childNodes[0].nodeValue;
			 $('#step4b_initiatorEmail')[0].innerHTML = ownerEmail;
		    }
		  }
		//document.write("checkready?'taskName'=taskName");
		xmlhttp.open("GET","gettaskpropertyservlet?taskName="+taskName, true);
		xmlhttp.send();		
	}
	setInterval(checkPropertyReady, 2000);
}
function checkReadyStart(){
	//var task = "hello8";
	var Request = GetRequest();
	var task=Request['taskName'];
	var email = Request['email'];
	checkReadyContinuous(task, email);
	checkPropertyContinuous(task);
}
	
function GetRequest()

{
var url = location.search; //get information after "?"
var theRequest = new Object();
if(url.indexOf("?") != -1)
{
  var str = url.substr(1);
    strs = str.split("&");
  for(var i = 0; i < strs.length; i ++)
    {
     theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
    }
}
return theRequest;

}

function accessAppletMethod()
{
	var fn = "fn";
    fn = document.applets[0].getAttributes();
    document.getElementById('userProperty').value = fn;
    document.getElementById('step4_properties').innerHTML = fn;
    fn = document.applets[0].getFilename();
    document.getElementById('userFilePath').value = fn;
    comfirmProperty ();
}

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
//	alert("in accessAppletMethod2");
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