window.onload = function () {


    $(document).ready(function() {

// uneto ime
     $('#commonName').on('input', function() {
                var input=$(this);
                var is_name=input.val();
                if(is_name){input.removeClass("invalid").addClass("valid");}
                else{input.removeClass("valid").addClass("invalid");}
               });
// uneto localityName
     $('#localityName').on('input', function() {
                var input=$(this);
                var l_name=input.val();
                if(l_name){input.removeClass("invalid").addClass("valid");}
                else{input.removeClass("valid").addClass("invalid");}
               });
// uneto stateName
     $('#stateName').on('input', function() {
                var input=$(this);
                var s_name=input.val();
                if(s_name){input.removeClass("invalid").addClass("valid");}
                else{input.removeClass("valid").addClass("invalid");}
               });
// uneto countryName
     $('#countryName').on('input', function() {
                var input=$(this);
                var c_name=input.val();
                if(c_name){input.removeClass("invalid").addClass("valid");}
                else{input.removeClass("valid").addClass("invalid");}
               });
// uneto organisationName
     $('#organisationName').on('input', function() {
                var input=$(this);
                var o_name=input.val();
                if(o_name){input.removeClass("invalid").addClass("valid");}
                else{input.removeClass("valid").addClass("invalid");}
               });

 // uneto organisationUnitName
      $('#organisationUnitName').on('input', function() {
                 var input=$(this);
                 var ou_name=input.val();
                 if(ou_name){input.removeClass("invalid").addClass("valid");}
                 else{input.removeClass("valid").addClass("invalid");}
                });
   // uneto email
        $('#email').on('input', function() {
                  var input=$(this);
                  	var re =  /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
                  	var is_email=re.test(input.val());
                  	if(is_email){input.removeClass("invalid").addClass("valid");}
                  	else{input.removeClass("valid").addClass("invalid");}
                  });
     // uneto alias
          $('#countryName').on('input', function() {
                     var input=$(this);
                     var ali=input.val();
                     if(ali){input.removeClass("invalid").addClass("valid");}
                     else{input.removeClass("valid").addClass("invalid");}
                    });


    });


	$('#btnCreate').click(function(e){
    		e.preventDefault()

            let commonName = $('#commonName').val()
    		let localityName = $('#localityName').val()
    		let stateName = $('#stateName').val()
    		let countryName = $('#countryName').val()
    		let organisationName = $('#organisationName').val()
    		let organisationUnitName = $('#organisationUnitName').val()
    		let email = $('#email').val()
    		let keyStorePassword = $('#keyStorePassword').val()
    		let keyPassword = $('#keyPassword').val()
    		let alias = $('#alias').val()
            let isCA = false;

/* verifikacija

            var error_free=true;

  var valid=commonName.hasClass("valid");
  		var error_element=$("span", commonName.parent());
  		if (!valid){error_element.removeClass("error").addClass("error_show"); error_free=false;}
  		else{error_element.removeClass("error_show").addClass("error");}

var valid=localityName.hasClass("valid");
  		var error_element=$("span", localityName.parent());
  		if (!valid){error_element.removeClass("error").addClass("error_show"); error_free=false;}
  		else{error_element.removeClass("error_show").addClass("error");}

 var valid=stateName.hasClass("valid");
      	var error_element=$("span", stateName.parent());
      	if (!valid){error_element.removeClass("error").addClass("error_show"); error_free=false;}
      	else{error_element.removeClass("error_show").addClass("error");}

 var valid=countryName.hasClass("valid");
   		var error_element=$("span", countryName.parent());
   		if (!valid){error_element.removeClass("error").addClass("error_show"); error_free=false;}
   		else{error_element.removeClass("error_show").addClass("error");}

 var valid=organisationName.hasClass("valid");
   		var error_element=$("span", organisationName.parent());
   		if (!valid){error_element.removeClass("error").addClass("error_show"); error_free=false;}
   		else{error_element.removeClass("error_show").addClass("error");}

var valid=organisationUnitName.hasClass("valid");
  		var error_element=$("span", organisationUnitName.parent());
  		if (!valid){error_element.removeClass("error").addClass("error_show"); error_free=false;}
  		else{error_element.removeClass("error_show").addClass("error");}

var valid=email.hasClass("valid");
  		var error_element=$("span", email.parent());
  		if (!valid){error_element.removeClass("error").addClass("error_show"); error_free=false;}
  		else{error_element.removeClass("error_show").addClass("error");}

var valid=alias.hasClass("valid");
  		var error_element=$("span", alias.parent());
  		if (!valid){error_element.removeClass("error").addClass("error_show"); error_free=false;}
  		else{error_element.removeClass("error_show").addClass("error");}


*/
    		let issuerDataDTO = {"commonName":commonName,"localityName":localityName,"stateName":stateName,
    		"countryName":countryName,"organisationName":organisationName, "organisationUnitName":organisationUnitName,"givenName":"",
    		"surname":"", "uid":"", "serialNumber":"","email":email}

    		let subjectDataDTO = {"commonName":commonName,"localityName":localityName,"stateName":stateName,
                                     		"countryName":countryName,"organisationName":organisationName, "organisationUnitName":organisationUnitName,"givenName":"",
                                     		"surname":"", "uid":"", "serialNumber":"","email":email}

            let certDto = JSON.stringify({"issuerData":issuerDataDTO, "subjectData":subjectDataDTO, "keyStorePassword":keyStorePassword, "keyPassword": keyPassword,
                                            "basicConstrains":isCA, "alias":alias})

    		console.log(certDto)     // samo provera

    			var form_data=$("#contact").serializeArray();
            	var error_free=true;
            	for (var input in form_data){
            		var element=$(form_data[input]['name']);
            		var valid=element.hasClass("valid");
            		var error_element=$("span", element.parent());
            		if (!valid){error_element.removeClass("error").addClass("error_show"); error_free=false;}
            		else{error_element.removeClass("error_show").addClass("error");}
            	}



    		$.ajax({
    			type: 'POST',
    			url:'/api/certificates/generateSelfSigned',
    			data: certDto,
    			dataType : "json",
    			contentType : "application/json; charset=utf-8",
    			complete: function(data)
                            {
                                 console.log(data.status)

                                  if(data.status == "200" && error_free)
                                   {
                                        alert('Root certificate made successfully. ')
                                    	//window.location.href = "centreAdminPage.html"
                                   }
                                   else {
                                        alert('Cannot make root certificate. ')
                                   }
                            }

    		})

    	})



}

