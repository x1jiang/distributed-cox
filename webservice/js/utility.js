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

	$('button#register_submit').click(function(event){
		/* check registration information */
		if ( comfirmRegistration() )
		{
			/* TODO:: Something */
		}
	});

	/* hide step 1 */
	$('.collaborator').click(function(event){
		/* Display Collaborated Tasks */
		displayCollabTasks(this);
	});

	/* Key Press Listeners */

	$('#tabs li').click(function(event){
		/* Display different home tab */
		switchHomeTab(this);
	});

	$('form#register').find('input').each(function(){
		$(this).keyup(function(e){
			console.log(this);
			console.log(comfirmRegistration());
		});
	});

});

/* Slides down and Dislays the given Collaborater's collaborated tasks */
function displayCollabTasks(collaborator)
{
	var $collab = $(collaborator);
	var $tasks = $($collab).parent().parent().parent().parent().find('.collab_tasks');
	if ( $tasks.hasClass('hide') )
	{
		$tasks.slideDown('fast');
		$tasks.removeClass('hide');
	}
	else
	{
		$tasks.slideUp('fast').queue(function(){ 
			$tasks.addClass('hide');
			$tasks.dequeue();
		});
	}
}
/* Sets Collaborator's collaboration percentage */
function setCollabStat(collaboratorId, percentage)
{
	var $collaborator = $('#'+collaboratorId);
	var $stat = $collaborator.find('.progress-inner');
	$stat.css( 'width', percentage + '%' );
}


/* Switches tabs on the home profile page to 
display the content of the clicked tab*/
function switchHomeTab(tab)
{
	var $tab = $(tab);
	var $prevTarget = $('#tabs li.target');
	if ( !$tab.hasClass('target') )
	{
		$prevTarget.removeClass('target');
		$tab.addClass('target');
		tabDisplay( $prevTarget.attr('id') );
		tabDisplay( $tab.attr('id') );
	}
}
/* Displays or hides the home profile tabs content*/
function tabDisplay (id)
{
	var tab;
	switch (id)
	{
		case "tabs-1": tab = $('#content-1'); break;
		case "tabs-2": tab = $('#content-2'); break;
		case "tabs-3": tab = $('#content-3'); break;
		case "tabs-4": tab = $('#content-4'); break;
	}
	tab.toggleClass('hide');
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
		$('#register_email').removeClass('check').addClass('wrong');
		flag = 0;
	}
	else
	{
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

