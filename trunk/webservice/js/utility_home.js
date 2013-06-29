$(document).ready(function(){

	/* Global Variables */

	/* Mouse Click Listeners */
	$('button#update_submit').click(function(event){
		/* check registration information */
		if ( comfirmRegistration() )
		{
			/* submit data to UserLoginServlet */
			$.ajax({type:'POST', async: false, url: './UpdateRegist', data:$('#update').serialize(),
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
	});

	$('button#update_reset').click(function(event){
		resetProfile();
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

	$('form#update').find('input').each(function(){
		$(this).keyup(function(e){
			console.log(this);
			console.log(comfirmRegistration());
		});
	});

});

/* Slides down and Displays the given Collaborater's collaborated tasks */
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
		case "tabs-2": tab = $('#content-2'); getProfile(); comfirmRegistration(); break;
		case "tabs-3": tab = $('#content-3'); getTaskNameList(); break;
		case "tabs-4": tab = $('#content-4'); getCollaborator(); break;
	}
	tab.toggleClass('hide');
}


/* Registration correct input comfirmation */
function comfirmRegistration ()
{
	var $form = $('form#update');
	var $firstName;
	var $lastName;
	var $email;
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
			case "password": $password = $value; break;
			case "comfirmPassword": $comfirmPassword = $value; break;
		}
	});
	if ( !isNonblank( $firstName ) )
	{
		$('#update_firstName').removeClass('check').addClass('wrong');
		flag = 0;
	}
	else
	{
		$('#update_firstName').removeClass('wrong').addClass('check');
	}
	if ( !isNonblank( $lastName ) )
	{
		$('#update_lastName').removeClass('check').addClass('wrong');
		flag = 0;
	}
	else
	{
		$('#update_lastName').removeClass('wrong').addClass('check');
	}
	if ( !isNonblank( $email ) || !isEmail($email) )
	{
		$('#update_email').removeClass('check').addClass('wrong');
		flag = 0;
	}
	else
	{
		$('#update_email').removeClass('wrong').addClass('check');
	}
	if ( !isNonblank($password) )
	{
		$('#update_password').removeClass('check').addClass('wrong');
		flag = 0;
	}
	else
	{
		$('#update_password').removeClass('wrong').addClass('check');
	}
	if ( !isNonblank($comfirmPassword) || $comfirmPassword != $password )
	{
		$('#update_comfirmPassword').removeClass('check').addClass('wrong');
		flag = 0;
	}
	else
	{
		$('#update_comfirmPassword').removeClass('wrong').addClass('check');
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

function getTaskNameList ()
{
   $.get(
	   "./getTaskNameList",
	   function (data){
		   $('#ul1').empty();
		   var infoList = eval("(" + data + ")");
		   $.each(infoList,
			   function(i)
			   {	
			   		if(infoList[i].taskID != 'undefined'){
			   			$('#ul1').append('<li>' + infoList[i].taskName + '</li>');
			   		}
			   }
		   );
	   }
	);
   
}

function getProfile()
{
	$.ajax({type:'POST', url: './getProfile',
		success: function(data){
			/*check if status = FAILURE*/
			var profileInfo = eval("(" + data + ")");
			var anonymous = profileInfo[0].anonymous;
			var err = profileInfo[0].error;
			if(anonymous == 'TRUE'){
				/*prevent from changing profile*/
//				alert("This is anonymous user");
				$('#firstName').attr('disabled', 'true');
				$('#lastName').attr('disabled', 'true');
				$('#password').attr('disabled', 'true');
				$('#email').attr('disabled', 'true');
				$('#comfirmPassword').attr('disabled', 'true');
				$('#update_submit').attr('disabled', 'true');
				$('#update_reset').attr('disabled', 'true');
			}
			else{
				$('#firstName').val(profileInfo[0].firstName);
				$('#lastName').val(profileInfo[0].lastName);
				$('#password').val(profileInfo[0].password);
				$('#email').val(profileInfo[0].email);
				$('#email').attr('readOnly', 'true');
			}
		}
	});	
}

function resetProfile(){
	$('#firstName').val("");
	$('#lastName').val("");
	$('#password').val("");
	$('#comfirmPassword').val("");
}

function getCollaborator(){
	$.ajax({type:'POST', url: './getCollaborator',
		success: function(data){
			var collabList = eval("(" + data + ")");
			console.log(collabList);
			for(var i=0; i<collabList.length; i++){
				if($('#collaborator_' + i).hasClass('hide')){
					$('#collaborator_' + i).removeClass('hide');
				}
				var collaborator = $('#collaborator_' + i).find('.collaborator p');
				collaborator.html(collabList[i].name + ' &lt;' + collabList[i].email + '&gt;');
				setCollabStat('collaborator_' + i, collabList[i].percent * 100);
				$taskNameList = $('#collaborator_' + i).find('.collab_tasks');
				$taskNameList.empty();
				for(var j=0; j<collabList[i].taskNameList.length; j++){
					$taskNameList.append('<li><p>' + collabList[i].taskNameList[j] + '</p></li>')
				}
			}
		}
	});
}	