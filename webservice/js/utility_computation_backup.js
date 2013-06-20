$(document).ready(function(){

	/* Global Variables */
	getComputerParam();
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

	$('button#globalButton').click(function(event){
		// show the submit data plane
		alert("globalButton");
	});
	$('button#TestButton').click(function(event){
		//start computation
		alert("TestButton");
	});
});

function getComputerParam(){
	$.ajax({type:'POST', async:false, url: './getComputeParam', 
		success: function(data){
			/*check if status = FAILURE*/
			alert(data);
			var statusInfo = eval("(" + data + ")");
//			alert($('applet param[name="epsilon"]').val());
//			$('applet param[name="taskStatus"]').val("0");
//			alert($('applet param:eq(5)').val());
			
//			var status = statusInfo[0].status;
//			var err = statusInfo[0].error;
//			if(status == 'SUCCESS'){
//				$('#error_display').empty();
//				$('#error_display').append('<p>' + err + '<br><br><br></p>');
//			}
//			else{
//				/*get error information from data and show in UI*/
//				$('#error_display').empty();
//				$('#error_display').append('<p>' + err + '<br><br><br></p>');
//			}
		}
	});	
}

function jsFunc(){
	return "100";
}

function setAppletParameter(){
	alert("in set applet param");

	var dataPath, taskName, root_property, maxIteration, epsilon, taskStatus;
		$.ajax({type:'POST', async:false, url: './getComputeParam', 
		success: function(data){
			/*check if status = FAILURE*/
			alert(data);
			var statusInfo = eval("(" + data + ")");
			dataPath = statusInfo[0].dataPath;
			taskName = statusInfo[0].taskName;
			root_property = statusInfo[0].root_property;
			maxIteration = statusInfo[0].maxIteration;
			epsilon = statusInfo[0].epsilon;
			taskStatus = statusInfo[0].taskStatus;
//			alert($('applet param[name="epsilon"]').val());
//			$('applet param[name="taskStatus"]').val("0");
//			alert($('applet param:eq(5)').val());
			
//			var status = statusInfo[0].status;
//			var err = statusInfo[0].error;
//			if(status == 'SUCCESS'){
//				$('#error_display').empty();
//				$('#error_display').append('<p>' + err + '<br><br><br></p>');
//			}
//			else{
//				/*get error information from data and show in UI*/
//				$('#error_display').empty();
//				$('#error_display').append('<p>' + err + '<br><br><br></p>');
//			}
		}
	});	
		document.applets[0].setParameter(dataPath, taskName, root_property,
		 maxIteration, epsilon, 1);
}