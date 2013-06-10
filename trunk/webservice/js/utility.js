$(document).ready(function(){

	/* Global Variables */
    testflag=1;//added by lph
    registerflag=1;
	participants = new Array();

	$('#frontPage_step1').hide();
	$('#frontPage_step2').hide();
	$('#frontPage_step3').hide();
	$('#frontPage_step4').hide();
	$('#frontPage_step5').hide();
    $("p").hide();
    
	/* Mouse Click Listeners */

	$('button#addParticipant').click(function(event){
		participants = addEmails( participants );
	});

	$('button#resetParticipants').click(function(event){
		participants = resetEmails( );
	});

	$('button#register_submit').click(function(event){
		/* check registration information */
//		.ajax('/userlogin');
//		$.ajax({type:'POST', url: './userlogin', data:$('#register').serialize() });
		if ( comfirmRegistration() )
		{
			/* submit data to UserLoginServlet */
			$.ajax({type:'POST', url: './userlogin', data:$('#register').serialize() });
			
			/* hide step 1 */
			$('#frontPage_step1').slideUp('fast').delay(100).queue(function(){ 
				/* display step 2 */
				$('#frontPage_step2').slideDown('fast');
				$('#frontPage_step1').dequeue();
			});
		}
	});
    $('button#back_step12').click(function(event){
        /* hide step 1 */
			$('#frontPage_step1').slideUp('fast').delay(100).queue(function(){ 
				/* display step 2 */
				$('#frontPage_step2').slideDown('fast');
				$('#frontPage_step1').dequeue();
			});
	});
    $('button#back_step13').click(function(event){
        /* hide step 1 */
			$('#frontPage_step1').slideUp('fast').delay(100).queue(function(){ 
				/* display step 2 */
				$('#frontPage_step3').slideDown('fast');
				$('#frontPage_step1').dequeue();
			});
	});
	
	//added by lph 12/17	
	
	$('button#chooseregister').click(function(event){
		//if(comfirmChoose()){	
			$('#choosePage_step0').slideUp('fast').delay(100).queue(function(){ 
				/* display step 1 */
				$('#frontPage_step1').slideDown('fast');
				$('choosePage_step0').dequeue();
			});
		//}
	});
	
	$('button#chooseinstruction').click(function(event){
		//if(comfirmChoose()){	
			$('#choosePage_step0').slideUp('fast').delay(100).queue(function(){ 
				/* display step 2 */
				$('#frontPage_step2').slideDown('fast');
				$('choosePage_step0').dequeue();
			});
		//}
	});
	
	$('button#choosecreate').click(function(event){
		//if(comfirmChoose()){	
			$('#choosePage_step0').slideUp('fast').delay(100).queue(function(){ 
				/* display step 3 */
				/* add emails in step3 first */
				getEmailList();
				$('#frontPage_step3').slideDown('fast');
				$('choosePage_step0').dequeue();
			});
		//}
	});
	
	$('button#createTask_Submit').click(function(event){
		if ( comfirmCreatetask())
	{
		//	alert(testflag);
		$.ajax({type:'POST', url: './createcoxtaskservlet', data:$('#createTask').serialize() });
		$('#step4_taskName')[0].innerHTML = $('#taskName')[0].value;
		$('#step4_initiatorEmail')[0].innerHTML = $('#ownerEmail')[0].value;
		$('#step4_properties')[0].innerHTML = $('#showProperty')[0].value;
		checkReadyStart();
		
		$('#frontPage_step3').slideUp('fast').delay(100).queue(function(){ 
			/* display step 3 */
			/* add emails in step3 first */
			//getEmailList();
			$('#frontPage_step4').slideDown('fast');
			$('#frontPage_step3').dequeue();
		});
	}
	});

	/* Key Press Listeners */

	$('form#register').find('input').each(function(){
		$(this).keyup(function(e){
			console.log(this);
			console.log(comfirmRegistration());
		});
	});
	//----added by lph------------//
	$('form#createTask').find('input').each(function(){
		$(this).keyup(function(e){
			console.log(this);
			console.log(comfirmCreatetask());
		});
	});
	
	$('button#instructions_display').click(function(event){
		/* check registration information */
		/* hide step 2 */
		$('#frontPage_step2').slideUp('fast').delay(100).queue(function(){ 
			/* display step 3 */
			/* add emails in step3 first */
			getEmailList();
			$('#frontPage_step3').slideDown('fast');
			$('#frontPage_step2').dequeue();
		});
	});
	$('button#step4_compute').click(function(event){
		/*check status of all users*/
		/*hide previous step*/
		/*start computation*/
		var dataObj = {};
		dataObj['taskName'] = $('#taskName').val();
		dataObj['email'] = $('#ownerEmail').val();
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
//		    	testflag=0;
//		    	flag.name="exist";
//		    	$('#createTask_title').removeClass('wrong').removeClass('check').addClass('exist');//task name exist
		    }
			
		    }
		  }
		//document.write("checkready?'taskName'=taskName");
		xmlhttp.open("GET","./beforecalservlet?email=" + $('#ownerEmail').val() +"&taskName="+ $('#taskName').val(), true);
		xmlhttp.send();		
	});
	

});

/* get participants email list */
function getEmailList ()
{
   $.get(
	   "./beforeCreateCoxTaskServlet",
//	   dataType: 'json',
	   function (data){
		   //var emailSelect = $('#emailSelect');
		   //emailSelect.clearAll();
		   var emailList = eval(data);
		   $.each(emailList,
			   function(i)
			   {
		   			//emailSelect.addOption(emailList[i].xxx, emailList[i].yyy);
//			   		$('#emailSelect').append('<option value= "1"> for test 1</option>');
			   		$('#emailSelect').append('<option value="' + i + '">' + emailList[i].email + '</option>');
			   }
		   );
	   }
	   );
//   $('#emailSelect').append('<option value= "1"> for test 2</option>');
   
}

/* adds participants to task */
function addEmails( participants )
{
	var addition = document.getElementById('emailSelect');
	var text = "";
	var count = 1;
	for( i=0; i<addition.options.length; i++)
	{
		if(addition.options[i].selected){
			participants.push(addition.options[i].text);
		}
	}
	for(i=0;i<participants.length;i++)
	{
		if ( i )
		{
			text += ", \n";
		} 
		text += participants[i];
		console.log(participants[i]);
		console.log(i);
	}
	if ( participants.length )
	{
		count = participants.length;
	}
	
	document.getElementById('participants').value = text;
	document.getElementById('participants').style.height = (count * 1.5) + "em";
	return participants;
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

/* Resets participants of tasks to none */
function resetEmails()
{
	var participants = new Array();
	document.getElementById('participants').value = "";
	document.getElementById('participants').style.height = 1.5 + "em";
	return participants;
}

/* Registration correct input comfirmation */
function comfirmRegistration ()
{
	var $form = $('form#register');
	var $firstName;
	var $lastName;
	var $email;
	var $comfirmEmail;
	var $password;
	var $comfirmPassword;
	var flag = 1;
	$form.find('input').each(function(){
		var id = this.id;
		var $value = $('#'+id).val(); 
		switch (id)
		{
			case "firstName": $firstName = $value; break;
			case "lastName": $lastName = $value; break;
			case "email": $email = $value; break;
			case "comfirmEmail": $comfirmEmail = $value; break;
			case "password": $password = $value; break;
			case "comfirmPassword": $comfirmPassword = $value; break;
		}
	});
	//---------------added by lph-----------------//
	checkRegisteremail(flag);
	if ( !isNonblank( $firstName ) )
	{
		$('#register_firstName').removeClass('check').addClass('wrong');
		flag = 0;
	}
	else
	{
		$('#register_firstName').removeClass('wrong').addClass('check');
	}
	if ( !isNonblank( $lastName ) )
	{
		$('#register_lastName').removeClass('check').addClass('wrong');
		flag = 0;
	}
	else
	{
		$('#register_lastName').removeClass('wrong').addClass('check');
	}
	if ( !isNonblank( $email ) || !isEmail($email) )
	{
		$('#register_email').removeClass('check').removeClass('exist').addClass('wrong');
		flag = 0;
	}
	else
	{
		$('#register_email').removeClass('wrong').removeClass('exist').addClass('check');
	}
	if (!isNonblank( $comfirmEmail ) || !isEmail($comfirmEmail) || $email != $comfirmEmail )
	{
		$('#register_comfirmEmail').removeClass('check').addClass('wrong');
		flag = 0;
	}
	else
	{
		$('#register_comfirmEmail').removeClass('wrong').addClass('check');
	}
	if ( !isNonblank($password) )
	{
		$('#register_password').removeClass('check').addClass('wrong');
		flag = 0;
	}
	else
	{
		$('#register_password').removeClass('wrong').addClass('check');
	}
	if ( !isNonblank($comfirmPassword) || $comfirmPassword != $password )
	{
		$('#register_comfirmPassword').removeClass('check').addClass('wrong');
		flag = 0;
	}
	else
	{
		$('#register_comfirmPassword').removeClass('wrong').addClass('check');
	}
	if(registerflag==0) 
	{
	  flag=0;
	  $("p").show();
	 }
	return flag;
}

//-----------added by lph---------------//
/* Createtask correct input comfirmation */
function comfirmCreatetask ()
{
	var $form = $('form#createtask');
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
	//if(checkTaskname())
	//{
	//	$('#createTask_title').removeClass('check').addClass('exist');
	//	flag = 0;
	//}
	if ( !isNonblank( $taskName ) )
	{
		$('#createTask_title').removeClass('check').removeClass('exist').addClass('wrong');
		flag = 0;
	}
	else
	{
		$('#createTask_title').removeClass('wrong').removeClass('exist').addClass('check');
	}
	checkTaskname(flag1);
	//if (testflag==0) 
		
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
	//alert(testflag);
	if(testflag==0) flag=0;
	return flag;
}

// checks that an input string looks like a valid email address.
var isEmail_re       = /^\s*[\w\-\+_]+(\.[\w\-\+_]+)*\@[\w\-\+_]+\.[\w\-\+_]+(\.[\w\-\+_]+)*\s*$/;
function isEmail (s) {
   return String(s).search (isEmail_re) != -1;
}
// Check if string is non-blank
var isNonblank_re    = /\S/;
function isNonblank (s) {
   return (String (s).search (isNonblank_re) != -1);
}

//---------added by lph-------------------//
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

// show the status of participants
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
		  		table = table + "<tr> <td> "+ xmlDoc.getElementsByTagName("Email")[i].childNodes[0].nodeValue +" </td> <td> "+ xmlDoc.getElementsByTagName("Status")[i].childNodes[0].nodeValue +" </td> <td> "+ xmlDoc.getElementsByTagName("TaskStatus")[i].childNodes[0].nodeValue+" </td> </tr>";
		  	}
		  	table = table + "</table>";
		  	document.getElementById("readyStatus").innerHTML = table;
			//  document.getElementById("readyStatus").innerHTML = "Just for test";
		    }
		  }
		//document.write("checkready?'taskName'=taskName");
		xmlhttp.open("GET","checkready?email=" + email+"&taskName="+taskName, true);
		xmlhttp.send();		
	}
	setInterval(checkReady, 2000);
}

function checkReadyStart(){
	//var task = "hello8";
	var task= $("#taskName")[0].value;
	var email= $("#ownerEmail")[0].value;
	checkReadyContinuous(task, email);
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

//-------------added by lph---------------------------//
function checkTaskname(flag){
	var task= $("#taskName")[0].value;
	var xmlhttp;    
	var taskName = task;
	var checkresult;

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
		checkresult=xmlDoc.getElementsByTagName("Checkresult")[0].childNodes[0].nodeValue;
	    if(checkresult==1) 
	    	{
	    	testflag=0;
	    	flag.name="exist";
	    	$('#createTask_title').removeClass('wrong').removeClass('check').addClass('exist');//task name exist
	    	}
	    else testflag=1;
	   // else alert('pass');
	    }
	  }
	//document.write("checkready?'taskName'=taskName");
	xmlhttp.open("GET","checktasknameservlet?taskName="+taskName, true);
	xmlhttp.send();	
}
//-------------added by lph---------------------------//
function checkRegisteremail(flag){
	var email= $("#email")[0].value;
	var firstname= $("#firstName")[0].value;
	var lastname= $("#lastName")[0].value;
	var xmlhttp;    
	var Registeremail = email;
	var firstName=firstname;
	var lastName=lastname;
	var checkresult;

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
		checkresult=xmlDoc.getElementsByTagName("Checkresult")[0].childNodes[0].nodeValue;
	    if(checkresult==1) 
	    	{
	    	flag=0;
	    	registerflag=0;
	    	$('#register_email').removeClass('wrong').removeClass('check').addClass('exist');//task name exist
	    	}
	    else registerflag=1;
	   // else alert('pass');
	    }
	  }
	//document.write("checkready?'taskName'=taskName");
	xmlhttp.open("GET","checkregisteremailservlet?email="+ Registeremail+"&firstName="+firstName+"&lastName="+lastName, true);
	xmlhttp.send();	
}
