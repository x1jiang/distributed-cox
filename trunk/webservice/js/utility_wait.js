$(document).ready(function(){

	/* Global Variables */
	//in participants.js, data is got from url, however, I want to get data from session.
	getTaskInfo();
	checkReadyStart();
	/* Mouse Click Listeners */
	$('button#submit').click(function(event){
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
			    }
			  }
			//document.write("checkready?'taskName'=taskName");
			var Request = GetRequest();
			var userFilePath= $("#userFilePath")[0].value;
			var userProperty = $("#userProperty")[0].value;
			xmlhttp.open("GET","./useruploadservlet?email=" + Request['email'] +"&taskName="+ Request['taskName'] + 
					"&userFilePath=" + userFilePath + "&userProperty=" + userProperty, true);
			xmlhttp.send();	
		}
		});
		//----------------------------------//

	$('button#show_submit').click(function(event){
		// show the submit data plane
//		alert("show submit");
		if($('.submitData').hasClass('hide')){
			$('.submitData').slideDown('fast');
			$('.submitData').removeClass('hide');
			$('#show_submit').html('Hide submission');
		}else{
			$('.submitData').addClass('hide');
			$('.submitData').hide();
			$('#show_submit').html('Check local data');
		}
	});

	$('button#begin_computation').click(function(event){
		/*check status of all users*/
		/*hide previous step*/
		/*start computation*/
		var dataObj = {};
		dataObj['taskName'] = $('#taskName')[0].innerHTML;
		dataObj['email'] = $('#initiatorEmail')[0].innerHTML;

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
			var xmlDoc = xmlhttp.responseXML;
			var checkresult=xmlDoc.getElementsByTagName("Checkresult")[0].childNodes[0].nodeValue;
		    if(checkresult==1) 
		    {
		    	window.location.href=xmlDoc.getElementsByTagName("Redirect")[0].childNodes[0].nodeValue;
	    	$('#createTask_title').removeClass('wrong').removeClass('check').addClass('exist');//task name exist
		    }
			
		    }
		  }
		//document.write("checkready?'taskName'=taskName");
		xmlhttp.open("GET","./beforecalservlet?email=" + $('#initiatorEmail')[0].innerHTML +"&taskName="+ $('#taskName')[0].innerHTML, true);
		xmlhttp.send();		
	});
});
//get information from url.
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
    return theRequest;
}else{
	return null;
}


}
//get attribute and file path from applet
function accessAppletMethod()
{
	var fn = "fn";
    fn = document.applets[0].getAttributes();
    $('#userProperty').val(fn);
//    alert(document.getElementById("taskdata_property").innerHTML);
    document.getElementById("properties").innerHTML = fn;
    fn = document.applets[0].getFilename();
    $('#userFilePath').val(fn);
    comfirmProperty ();
}

function getTaskInfo(){
//	alert(GetRequest()['taskName']);
//	if(GetRequest()['taskName'] & GetRequest()['email']){
	if(GetRequest()!=null){
		var Request = GetRequest();
		var taskName=Request['taskName'];
		var partEmail = Request['email'];
		//to get initiator email
//		$('#initiatorEmail')[0].innerHTML = initEmail;
		$('#taskName')[0].innerHTML = taskName;
		$('#begin_computation').hide();
	}
//	var Request = GetRequest();
//	var task=Request['taskName'];
//	var email = Request['email'];
	else{
		$('#show_submit').hide();
		$.ajax({type:'POST', async:false, url: './getTaskInfo', 
		success: function(data){
			var statusInfo = eval("(" + data + ")");
			var taskName = statusInfo[0].taskName;
			var initEmail = statusInfo[0].initEmail;
			var err = statusInfo[0].error;
			$('#initiatorEmail')[0].innerHTML = initEmail;
			$('#taskName')[0].innerHTML = taskName;
			alert("task Name is " + taskName + " initEmail is " + initEmail);
		}
		});	
	}
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
				var isUserReady = xmlDoc.getElementsByTagName("SelfStatus")[0].childNodes[0].nodeValue;
				if(isTaskBegin == "1" &&isUserReady=="1"){//
					
					//var email = "<%=email %>";
					var url = "computation.jsp?taskName=" + taskName + "&email=" + email;
					window.location.href=url;
				}
				else if (isTaskBegin != "0" && isUserReady=="0"){						
					alert("Task has began. You missed this task!");
					var url = "home.html";
					window.location.href=url;
				}
			}
			else
			{
//				alert("XML data fortmat is wrong and the original text content is:"+xmlhttp.responseText);
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
//			document.getElementById('taskProperty').value=taskproperty;
			 var ownerEmail=xmlDoc.getElementsByTagName("ownerEmail")[0].childNodes[0].nodeValue;
			 $('#initiatorEmail')[0].innerHTML = ownerEmail;
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
//	var Request = GetRequest();
//	var task=Request['taskName'];
//	var email = Request['email'];
	var task, Request, email;
	if(GetRequest() !=null){
		 Request = GetRequest();
		 task=Request['taskName'];
		 email = Request['email'];
	}
	else{
		email = $('#initiatorEmail')[0].innerHTML;
		 task = $('#taskName')[0].innerHTML;
	}
//	var email = $('#initiatorEmail')[0].innerHTML;
//	var task = $('#taskName')[0].innerHTML;
	checkReadyContinuous(task, email);
	checkPropertyContinuous(task);
}

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
