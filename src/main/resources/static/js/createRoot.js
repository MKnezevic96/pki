$(document).ready(function() {


 $('#btnCreate').click(function(e){
/*
 $("#contact").validate({
    rules: {

    commonName: {
            required: true,
            lettersonly: true
          },
    countryName: {
             required: true,
             lettersonly: true
           },
    stateName: {
             required: true,
             lettersonly: true
           },
    localityName: {
                  required: true,
                  lettersonly: true
                },
    organisationName: {
                   required: true,
                   lettersonly: true
                 },
    organisationUnitName: {
                    required: true,
                    lettersonly: true
                  },
    email: {
        required: true,
        email: true,
        //remote: "http://localhost:3000/inputValidator"
      },

    alias: {
        required: true,
        nowhitespace: true,
      }
   }
  })


*/


    	//	e.preventDefault()








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

    		let issuerDataDTO = {"commonName":commonName,"localityName":localityName,"stateName":stateName,
    		"countryName":countryName,"organisationName":organisationName, "organisationUnitName":organisationUnitName,"givenName":"",
    		"surname":"", "uid":"", "serialNumber":"","email":email}

    		let subjectDataDTO = {"commonName":commonName,"localityName":localityName,"stateName":stateName,
                                     		"countryName":countryName,"organisationName":organisationName, "organisationUnitName":organisationUnitName,"givenName":"",
                                     		"surname":"", "uid":"", "serialNumber":"","email":email}

            let certDto = JSON.stringify({"issuerData":issuerDataDTO, "subjectData":subjectDataDTO, "keyStorePassword":keyStorePassword, "keyPassword": keyPassword,
                                            "basicConstrains":isCA, "alias":alias})

    		console.log(certDto)     // samo provera






    		$.ajax({
    			type: 'POST',
    			url:'/api/certificates/generateSelfSigned',
    			data: certDto,
    			dataType : "json",
    			contentType : "application/json; charset=utf-8",
    			complete: function(data)
                            {
                                 console.log(data.status)

                                  if(data.status == "200")
                                   {
                                        alert('Root certificate made successfully. ')
                                    	//window.location.href = "centreAdminPage.html"
                                   }
                                   else {
                                        e.preventDefault();
                                        alert('Cannot make root certificate. ')
                                   }
                            }
                   })


    	})
})



