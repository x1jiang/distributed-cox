$(document).ready(function(){

	/* Global Variables */

	/* Mouse Click Listeners */

	$('button#register_submit').click(function(event){
		/* check registration information */
		if ( comfirmRegistration() )
		{
			/* TODO:: Something */
			$.ajax({type:'POST', async:false, url: './userRegistServlet?isCheck=FALSE', data:$('#register').serialize(),
				success: function(data){
					var statusInfo = eval("(" + data + ")");
					var status = statusInfo[0].status;
					var err = statusInfo[0].error;
					if(status == "FAILURE"){
						$('#error_display').empty();
						$('#error_display').append('<p>' + err + '<br><br><br></p>');
					}
					if(status == "SUCCESS"){
						$('#error_display').empty();
						$('#error_display').append('<p>' + err + '<br><br><br></p>');
					}
				}});
		}
	});
	
	$('form#register').find('input').each(function(){
		$(this).keyup(function(e){
			console.log(this);
			console.log(comfirmRegistration());
		});
	});
});

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
	if ( !isNonblank( $email ) || !isEmail($email) || Duplicate($email) )
	{
//		console.log('duplicate is ' + Duplicate($email));
		$('#register_email').removeClass('check').addClass('wrong');
		flag = 0;
	}
	else
	{
//		console.log('duplicate is ' + Duplicate($email));
		$('#register_email').removeClass('wrong').addClass('check');
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

function Duplicate(s){
	var result;
	$.ajax({type:'POST', async:false, url: './userRegistServlet?isCheck=TRUE', data:$('#register').serialize(),
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


