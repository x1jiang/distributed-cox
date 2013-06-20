$(document).ready(function(){

	/* Global Variables */
	var participants = new Array();
	/* Mouse Click Listeners */
	$('button#addParticipant').click(function(event){
		participants = addEmails( participants );
	});

	$('button#resetParticipants').click(function(event){
		participants = resetEmails( );
	});
	
	$('button#createTask_Submit').click(function(event){
//		var w2 = new WaitingTip({innerHTML:"<img src='images/waiting.gif' />wait several seconds..."});  
//        var div1El = document.getElementById("createTask_Submit");  
//        w2.show(div1El,"center"); 
		
//		$('#error_display').append('<p>Please allow a few seconds for the system to create a task<br><br><br><br><br><br><br><br><br></p>');
//		setTimeout( function(){$('#error_display').empty();}, 2000); 
		confirmCreateTaskNow();
	
	});

	$('form#createTask').find('input').each(function(){
		$(this).keyup(function(e){
			console.log(this);
			console.log(comfirmCreatetask());
		});
	});
});


// confirmCreateTaskNow
function confirmCreateTaskNow(){
	if ( comfirmCreatetask())
	{
		
		//$('#error_display').append('<p>Please allow a few seconds for the system to create a task<br><br><br><br><br><br><br><br><br></p>');
		//setTimeout( function() { $('#error_display').append('<p>Please allow a few seconds for the system to create a task<br><br><br><br><br><br><br><br><br></p>');}, 5000); 
		//	setTimeout( function(){$('#error_display').empty();}, 2000);
		//	alert(testflag);
		$.ajax({type:'POST', async:false, url: './createcoxtaskservlet', data:$('#createTask').serialize(),
			success: function(data){
				var statusInfo = eval("(" + data + ")");
				var status = statusInfo[0].status;
				var err = statusInfo[0].error;
				if(status == "FAILURE"){
					$('#error_display').empty();
					$('#error_display').append('<p>' + err + '<br><br><br><br><br><br><br><br><br></p>');
				}
				if(status == "SUCCESS"){
					$('#error_display').empty();
					$('#error_display').append('<p>' + err + '<br><br><br><br><br><br><br><br><br></p>');
				}
			}});
	}
	}


/* adds participants to task */
function addEmails( participants )
{
	var addition = document.getElementById('emailSelect');
	var text = $('#participants').val();
	var count = 1;
	if(text.split(',').length > count){
		count = text.split(',').length;
	}
	participants = new Array();
	for( i=0; i<addition.options.length; i++)
	{
		if(addition.options[i].selected){
			participants.push(addition.options[i].text);
		}
	}
	for(i=0;i<participants.length;i++)
	{
		if ( text!="" )
		{
			text += ", \n";
		} 
		text += participants[i];
		console.log(participants[i]);
		console.log(i);
	}
	if ( participants.length )
	{
		count += participants.length;
	}
	
	document.getElementById('participants').value = text;
	document.getElementById('participants').style.height = (count * 1.5) + "em";
	return participants;
}

/* Resets participants of tasks to none */
function resetEmails()
{
	participants = new Array();
	document.getElementById('participants').value = "";
	document.getElementById('participants').style.height = 1.5 + "em";
	return participants;
}

/* get participants email list */
function getEmailList ()
{
   $.get(
	   "./beforeCreateCoxTaskServlet",
	   function (data){
		   $('#emailSelect').empty();
		   var emailList = eval("(" + data + ")");
		   var err = emailList[0].error;
		   var initiator = emailList[0].initiator;
		   for(var i=0; i<emailList[0].email.length; i++){
			   $('#emailSelect').append('<option value="' + i + '">' + emailList[0].email[i] + '</option>');
		   }
		   if(initiator == 'anonymous' || initiator == '' || initiator == null){
			   $('#ownerEmail').val('administrator@dbmi-ucsd.org');
		   }
		   else{
			   $('#ownerEmail').val(initiator);
		   }
		   $('#ownerEmail').attr('readOnly', 'true');
	   }
	);
   
}
/* Createtask correct input comfirmation */

function comfirmCreatetask ()
{
	var $form = $('form#createTask');
	var $taskName;
	var $expiration;
	var $ownerEmail;
	var $iterationMax;
	var $epsilon;
	var $showFilePath;
	var flag = 1;
	var flag1=new Object();
	//testflag=1;
	flag1.name="0";
	$form.find('input').each(function(){
		var id = this.id;
		var $value = $('#'+id).val(); 
		switch (id)
		{
			case "taskName": $taskName = $value; break;
			case "expiration": $expiration = $value; break;
			case "ownerEmail": $ownerEmail = $value; break;
			case "iterationMax": $iterationMax = $value; break;
			case "epsilon": $epsilon = $value; break;
			case "showFilePath": $showFilePath = $value; break;
		}
	});
	if ( !isNonblank( $taskName ) || DuplicateTaskName($taskName) || !isAlphaNumber($taskName) )
	{
		$('#createTask_title').removeClass('check').removeClass('exist').addClass('wrong');
		flag = 0;
	}
	else
	{
		$('#createTask_title').removeClass('wrong').removeClass('exist').addClass('check');
	}
		
	if ( !isNonblank( $expiration )||!isInteger($expiration) )
	{
		$('#createTask_expiration').removeClass('check').addClass('wrong');
		flag = 0;
	}
	else
	{
		$('#createTask_expiration').removeClass('wrong').addClass('check');
	}
	if ( !isNonblank( $ownerEmail ) || !isEmail($ownerEmail) )
	{
		$('#createTask_ownerEmail').removeClass('check').addClass('wrong');
		flag = 0;
	}
	else
	{
		$('#createTask_ownerEmail').removeClass('wrong').addClass('check');
	}
	if (!isNonblank( $iterationMax ) || !isInteger($iterationMax) )
	{
		$('#createTask_iterationMax').removeClass('check').addClass('wrong');
		flag = 0;
	}
	else
	{
		$('#createTask_iterationMax').removeClass('wrong').addClass('check');
	}
	if ( !isNonblank($epsilon) || !isDecimal($epsilon) )
	{
		$('#createTask_epsilon').removeClass('check').addClass('wrong');
		flag = 0;
	}
	else
	{
		$('#createTask_epsilon').removeClass('wrong').addClass('check');
	}
	if ( !isNonblank($showFilePath))
	{
		$('#choosefile').removeClass('check').addClass('wrong');
		flag = 0;
	}
	else
	{
		$('#choosefile').removeClass('wrong').addClass('check');
	}
	return flag;
}

//checks that an input string looks like a valid email address.
var isEmail_re       = /^\s*[\w\-\+_]+(\.[\w\-\+_]+)*\@[\w\-\+_]+\.[\w\-\+_]+(\.[\w\-\+_]+)*\s*$/;
function isEmail (s) {
 return String(s).search (isEmail_re) != -1;
}
//Check if string is non-blank
var isNonblank_re    = /\S/;
function isNonblank (s) {
 return (String (s).search (isNonblank_re) != -1);
}
//check that an input string only includes positive integer.
var isInteger_re =/^\d+(\.\d{2})?$/  ;
function isInteger(s){
	return String(s).search(isInteger_re)!=-1;
}
//check that an input string only includes positive decimal.
var isDecimal_re= /^[0]{1}[.]{1}[0-9]{1,10}$/; 
function isDecimal(s){
	return isDecimal_re.exec(s);
	//String(s).search(isDecimal_re)!=-1;
}

var isAlphaNumber_re=/^\w+$/;
function isAlphaNumber(s){
	if (isAlphaNumber_re.test(s)) return true;
	else{
		var err = "Use digit, alphabet and underline as title"
		$('#error_display').append('<p>' + err + '<br><br></p>');
		return false;
	}
}


function DuplicateTaskName(s){
	var result = true;
	$.ajax({type:'POST', async:false, url: "./checkDupTaskName", data:$("#taskName").serialize(),
		success: function(data){
			var statusInfo = eval("(" + data + ")");
			var status = statusInfo[0].status;
			var err = statusInfo[0].error;
			if(status == "FAILURE"){
				$('#error_display').empty();
				$('#error_display').append('<p>' + err + '<br><br><br></p>');
				result = true;
			}
			if(status == "SUCCESS"){
				$('#error_display').empty();
				$('#error_display').append('<p>' + err + '<br><br><br></p>');
				result = false;
			}
		}});
	return result;
}

/* extract information from applet to html*/
function accessAppletMethod()
{
	var fn = "";
    fn = document.applets[0].getAttributes();
    document.getElementById('showProperty').value = fn;
    fn = document.applets[0].getFilename();
    document.getElementById('showFilePath').value = fn;
    comfirmCreatetask();
}
