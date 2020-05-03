window.onload = function () {

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
		// 	let keyUsageDTO
		// 	let extendedKeyUsageDTO
		// 	keyUsageDTO = { "keyUsage": true,"digitalSignature": true, "keyEncipherment":true,  "keyAgreement":true, "nonRepudiation":true}
		// 	extendedKeyUsageDTO = { "extendedKeyUsage": true, "serverAuth": true, "clientAuth": true, "codeSigning": true, "emailProtection":true, "timeStamping":true, "ocspSigning":true}

    		let issuerDataDTO = {"commonName":commonName,"localityName":localityName,"stateName":stateName,
    		"countryName":countryName,"organisationName":organisationName, "organisationUnitName":organisationUnitName,"givenName":"",
    		"surname":"", "uid":"", "serialNumber":"","email":email}

    		let subjectDataDTO = {"commonName":commonName,"localityName":localityName,"stateName":stateName,
                                     		"countryName":countryName,"organisationName":organisationName, "organisationUnitName":organisationUnitName,"givenName":"",
                                     		"surname":"", "uid":"", "serialNumber":"","email":email}

            let certDto = JSON.stringify({"issuerData":issuerDataDTO, "subjectData":subjectDataDTO, "keyStorePassword":keyStorePassword, "keyPassword": keyPassword,
                                            "basicConstrains":isCA, "extendedKeyUsageDTO":extendedKeyUsageDTO, "keyUsageDTO":keyUsageDTO, "alias":alias})

    		console.log(certDto)
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
                                        alert('hoce')
                                    	//window.location.href = "centreAdminPage.html"
                                   }
                                   else {
                                        alert('nece')
                                   }
                            }

    		})

    	})



}

