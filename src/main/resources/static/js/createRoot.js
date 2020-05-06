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


    		e.preventDefault();
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

	 //-----------------------------------------------------------------
	 let keyUsage = false;
	 let digitalSignature = false;
	 let keyEncipherment = false;
	 let keyAgreement = false;
	 let nonRepudiation = false;
	 let extendedKeyUsage = false;
	 let serverAuth = false;
	 let clientAuth = false;
	 let codeSigning = false;
	 let emailProtection = false;
	 let timeStamping = false;
	 let ocspSigning = false;
	 let keyCertSign = false;

	 if($('#server_auth').is(":checked")){
		 console.log('server');
		 keyUsage = true;
		 digitalSignature = true;
		 keyEncipherment = true;
		 keyAgreement = true;
		 extendedKeyUsage = true;
		 serverAuth = true;
	 }
	 if($('#client_auth').is(":checked")){
		 console.log('client');
		 keyUsage = true;
		 digitalSignature = true;
		 keyAgreement = true;
		 extendedKeyUsage = true;
		 clientAuth = true;
	 }
	 if($('#code_signing').is(":checked")){
		 console.log('code');
		 keyUsage = true;
		 digitalSignature = true;
		 extendedKeyUsage = true;
		 codeSigning = true;
	 }
	 if($('#email_protection').is(":checked")){
		 console.log('email');
		 keyUsage = true;
		 digitalSignature = true;
		 keyEncipherment = true;
		 nonRepudiation = true;
		 extendedKeyUsage = true;
		 emailProtection = true;
	 }
	 if($('#time_stamping').is(":checked")){
		 console.log('time');
		 keyUsage = true;
		 digitalSignature = true;
		 nonRepudiation = true;
		 extendedKeyUsage = true;
		 timeStamping = true;
	 }
	 if($('#OCSP_signing').is(":checked")){
		 console.log('ocsp');
		 keyUsage = true;
		 digitalSignature = true;
		 nonRepudiation = true;
		 extendedKeyUsage = true;
		 ocspSigning = true;
	 }
	 if(isCA){
		 keyUsage = true;
		 keyCertSign = true;
		 extendedKeyUsage = true;
		 ocspSigning = true;
	 }
	 let keyUsageDTO;
	 let extendedKeyUsageDTO;
	 keyUsageDTO = { "keyUsage": keyUsage, "digitalSignature": digitalSignature, "keyEncipherment": keyEncipherment, "keyAgreement": keyAgreement,  "nonRepudiation": nonRepudiation, "keyCertSign": keyCertSign}
	 extendedKeyUsageDTO = { "extendedKeyUsage": extendedKeyUsage, "serverAuth": serverAuth, "clientAuth": clientAuth, "codeSigning": codeSigning, "emailProtection": emailProtection, "timeStamping": timeStamping, "ocspSigning": ocspSigning }
	 console.log(keyUsageDTO);
	 console.log(extendedKeyUsageDTO);

	 //-----------------------------------------------------------------


    		let issuerDataDTO = {"commonName":commonName,"localityName":localityName,"stateName":stateName,
    		"countryName":countryName,"organisationName":organisationName, "organisationUnitName":organisationUnitName,"givenName":"",
    		"surname":"", "uid":"", "serialNumber":"","email":email}

    		let subjectDataDTO = {"commonName":commonName,"localityName":localityName,"stateName":stateName,
                                     		"countryName":countryName,"organisationName":organisationName, "organisationUnitName":organisationUnitName,"givenName":"",
                                     		"surname":"", "uid":"", "serialNumber":"","email":email}

	 		let certDto = JSON.stringify({"issuerData":issuerDataDTO, "subjectData":subjectDataDTO, "keyStorePassword":keyStorePassword, "keyPassword": keyPassword,
		 									"basicConstrains":isCA, "extendedKeyUsageDTO":extendedKeyUsageDTO, "keyUsageDTO":keyUsageDTO, "alias":alias})

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
                                    	window.location.href = "certificates.html"
                                   }
                                   else {
                                        e.preventDefault();
                                        alert('Cannot make root certificate. ')
                                   }
                            }
                   })


    	})
})



