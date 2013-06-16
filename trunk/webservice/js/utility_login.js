$(document).ready(function(){

	/* Global Variables */

	/* Mouse Click Listeners */
	$('button#login_submit').click(function(event){
		UserLogin();
	});
	$('button#Anonymous_submit').click(function(event){
		AnonymousLogin();
	});
});

function UserLogin(){
	$.ajax({type:'POST', url: './userLoginServlet?type=authentication', 
		data:$('#login').serialize(),
		success: function(data){
			/*check if status = FAILURE*/
			var statusInfo = eval("(" + data + ")");
			var status = statusInfo[0].status;
			var err = statusInfo[0].error;
			if(status == 'SUCCESS'){
				$('#error_display').empty();
				$('#error_display').append('<p>' + err + '<br><br><br></p>');
			}
			else{
				/*get error information from data and show in UI*/
				$('#error_display').empty();
				$('#error_display').append('<p>' + err + '<br><br><br></p>');
			}
		}
	});	
}

function AnonymousLogin(){
	$.ajax({type:'POST', url: './userLoginServlet?type=anonymous', 
		data:'email=anonymous&password=anonymous',
		success: function(data){
			/*check if status = FAILURE*/
//			alert("'" + data + "'");
//			var statusInfo = eval('({"name":"jwc"})');
//			console.log(statusInfo.name);
			statusInfo = eval("(" + data + ")");
//			var statusInfo = data;
			var status = statusInfo[0].status;
			var err = statusInfo[0].error;
			if(status == 'SUCCESS'){
				$('#error_display').empty();
				$('#error_display').append('<p>' + err + '<br><br><br></p>');
			}
			else{
				/*get error information from data and show in UI*/
				$('#error_display').empty();
				$('#error_display').append('<p>' + err + '<br><br><br></p>');
			}
		},
	});	
}
	