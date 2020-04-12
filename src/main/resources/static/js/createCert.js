window.onload = function () {


    $('#afterNext').hide()

    $('#btnNext').click(function(e){
        e.preventDefault()
        $('#btnNext').hide()
        $('#afterNext').show()

        let password = $('#keyStorePassword').val()

        $.ajax({
        		type: 'GET',
        		url: 'api/keyStore/getAliases/'+password,
        		contentType: "application/json",
        		complete: function(data)
        		{
        			aliases = data.responseJSON
        			  $.each(aliases, function(key, value) {
                            $('#issuerCA').append('<option value="'+value+'">'+value+'</option>');
                      });
        		}

        	})

    })





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
    		let template = $('#template option:selected').val()
            let isCA = false;
            let keyPasswordIssuer = $("#keyPasswordIssuer").val()
            let keyUsageDTO
            let extendedKeyUsageDTO


            if(template ==="Server Auth"){
                keyUsageDTO = { "keyUsage": true,"digitalSignature": true, "keyEncipherment":true,  "keyAgreement":true}
                extendedKeyUsageDTO = { "extendedKeyUsage": true, "serverAuth": true}
            }else if(template ==="Client Auth"){
                keyUsageDTO = { "keyUsage": true,"digitalSignature": true, "keyAgreement":true}
                extendedKeyUsageDTO = { "extendedKeyUsage": true, "clientAuth": true}
            }else if(template ==="Code signing"){
                keyUsageDTO = { "keyUsage": true,"digitalSignature": true}
                extendedKeyUsageDTO = { "extendedKeyUsage": true, "codeSigning": true}
            } else if(template==="Email protection") {
                keyUsageDTO = { "keyUsage": true,"digitalSignature": true, "nonRepudiation":true,  "keyEncipherment":true}
                extendedKeyUsageDTO = { "extendedKeyUsage": true, "emailProtection":true }
            } else if(template==="Time stamping"){
                keyUsageDTO = { "keyUsage": true,"digitalSignature": true, "nonRepudiation":true}
                extendedKeyUsageDTO = { "extendedKeyUsage": true, "timeStamping":true }
            }else if(template === "OCSP signing"){
                keyUsageDTO = { "keyUsage": true,"digitalSignature": true, "nonRepudiation":true}
                extendedKeyUsageDTO = { "extendedKeyUsage": true, "ocspSigning":true }
            } else if(template==="CA"){
                isCA = true
                keyUsageDTO = { "keyUsage": true,"keyCertSign": true}
                extendedKeyUsageDTO = { "extendedKeyUsage": true, "ocspSigning":true }
            }

            let issuerAlias = $('#issuerCA option:selected').val()
            console.log(issuerAlias)

            console.log("templejt", template)



            $.ajax({
                type: 'GET',
                url: 'api/keyStore/getIssuerData/'+issuerAlias+'/'+keyStorePassword+'/'+keyPasswordIssuer,
                contentType: "application/json",
                complete: function(data)
                {
                    issuerData = data.responseJSON
                    console.log(issuerData)


                    let issuerDataDTO = {"commonName":issuerData.commonName,"localityName":issuerData.localityName,"stateName":issuerData.stateName,
                    "countryName":issuerData.countryName,"organisationName":issuerData.organisationName, "organisationUnitName":issuerData.organisationUnitName,"givenName":"",
                    "surname":"", "uid":"", "serialNumber":"","email":issuerData.email}

                    let subjectDataDTO = {"commonName":commonName,"localityName":localityName,"stateName":stateName,
                                                    "countryName":countryName,"organisationName":organisationName, "organisationUnitName":organisationUnitName,"givenName":"",
                                                    "surname":"", "uid":"", "serialNumber":"","email":email}

                    let certDto = JSON.stringify({"issuerData":issuerDataDTO, "subjectData":subjectDataDTO, "keyStorePassword":keyStorePassword, "keyPassword": keyPassword,
                                                    "basicConstrains":isCA, "extendedKeyUsageDTO":extendedKeyUsageDTO, "keyUsageDTO":keyUsageDTO, "alias":alias, "issuerAlias":issuerAlias})


                    console.log(certDto)
                    $.ajax({
                        type: 'POST',
                        url:'/api/certificates/generateCertificate',
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

    	        }

    	 })

})
}


