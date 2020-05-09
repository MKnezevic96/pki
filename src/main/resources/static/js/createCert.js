window.onload = function () {
    loadIssuers();

    $('#afterNext').hide()


    $('#btnNext').click(function(e){
        e.preventDefault()

        // valid
            var empty = false;
                    $('#forma1 input').each(function() {
                        if ($(this).val() == '') {
                            empty = true;
                        }
                    });

                    if (empty) {
                        alert("Please fill empty fields.");
                    }
                    else {

        $('#btnNext').hide()
        $('#afterNext').show()
        $('#issuerCA').prop('disabled', true)
        let certType = $('#certType option:selected').val();
        // if(certType === 'server')
        //     $('#checkboxCA').hide();
        if(certType === 'client')
            $('#checkboxCA').hide();

        let issuerAlias = $('#issuerCA option:selected').val()
        $.ajax({
            type: 'GET',
            url: 'api/keyStore/getCert/'+issuerAlias,
            contentType: "application/json",
            complete: function(data)
            {
                console.log(data.responseJSON);
                value = data.responseJSON
                if(value.extendedKeyUsageDTO.serverAuth === true){
                    $('#checkboxes').append('<label for="server_auth">\n' +
                        '                                                            <input type="checkbox" id="server_auth" />Server Auth</label>')
                }
                if(value.extendedKeyUsageDTO.clientAuth === true){
                    $('#checkboxes').append('<label for="client_auth">\n' +
                        '                                                            <input type="checkbox" id="client_auth" />Client Auth</label>')
                }
                if(value.extendedKeyUsageDTO.codeSigning === true){
                    $('#checkboxes').append('<label for="code_signing">\n' +
                        '                                                            <input type="checkbox" id="code_signing" />Code signing</label>')
                }
                if(value.extendedKeyUsageDTO.emailProtection === true){
                    $('#checkboxes').append('<label for="email_protection">\n' +
                        '                                                            <input type="checkbox" id="email_protection" />Email protection</label>')
                }
                if(value.extendedKeyUsageDTO.timeStamping === true){
                    $('#checkboxes').append('<label for="time_stamping">\n' +
                        '                                                            <input type="checkbox" id="time_stamping" />Time stamping</label>')
                }
                if(value.extendedKeyUsageDTO.ocspSigning === true){
                    $('#checkboxes').append('<label for="OCSP_signing">\n' +
                        '                                                            <input type="checkbox" id="OCSP_signing" />OCSP signing</label>')
                }
            }

        })    // ajax
        }     // else deo
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
    		let alias = $('#alias').val()
    		let template = $('#template option:selected').val()
            let isCA = false;
            isCA = $('#CA').is(":checked");
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
            let certType = $('#certType option:selected').val();


        if($('#server_auth').is(":checked")){
            keyUsage = true;
            digitalSignature = true;
            keyEncipherment = true;
            keyAgreement = true;
            extendedKeyUsage = true;
            serverAuth = true;
        }
        if($('#client_auth').is(":checked")){
            keyUsage = true;
            digitalSignature = true;
            keyAgreement = true;
            extendedKeyUsage = true;
            clientAuth = true;
        }
        if($('#code_signing').is(":checked")){
            keyUsage = true;
            digitalSignature = true;
            extendedKeyUsage = true;
            codeSigning = true;
        }
        if($('#email_protection').is(":checked")){
            keyUsage = true;
            digitalSignature = true;
            keyEncipherment = true;
            nonRepudiation = true;
            extendedKeyUsage = true;
            emailProtection = true;
        }
        if($('#time_stamping').is(":checked")){
            keyUsage = true;
            digitalSignature = true;
            nonRepudiation = true;
            extendedKeyUsage = true;
            timeStamping = true;
        }
        if($('#OCSP_signing').is(":checked")){
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


        // if(template ==="Server Auth"){
        //         keyUsageDTO = { "keyUsage": true,"digitalSignature": true, "keyEncipherment":true,  "keyAgreement":true}
        //         extendedKeyUsageDTO = { "extendedKeyUsage": true, "serverAuth": true}
        //     }else if(template ==="Client Auth"){
        //         keyUsageDTO = { "keyUsage": true,"digitalSignature": true, "keyAgreement":true}
        //         extendedKeyUsageDTO = { "extendedKeyUsage": true, "clientAuth": true}
        //     }else if(template ==="Code signing"){
        //         keyUsageDTO = { "keyUsage": true,"digitalSignature": true}
        //         extendedKeyUsageDTO = { "extendedKeyUsage": true, "codeSigning": true}
        //     } else if(template==="Email protection") {
        //         keyUsageDTO = { "keyUsage": true,"digitalSignature": true, "nonRepudiation":true,  "keyEncipherment":true}
        //         extendedKeyUsageDTO = { "extendedKeyUsage": true, "emailProtection":true }
        //     } else if(template==="Time stamping"){
        //         keyUsageDTO = { "keyUsage": true,"digitalSignature": true, "nonRepudiation":true}
        //         extendedKeyUsageDTO = { "extendedKeyUsage": true, "timeStamping":true }
        //     }else if(template === "OCSP signing"){
        //         keyUsageDTO = { "keyUsage": true,"digitalSignature": true, "nonRepudiation":true}
        //         extendedKeyUsageDTO = { "extendedKeyUsage": true, "ocspSigning":true }
        //     } else if(template==="CA"){
        //         isCA = true
        //         keyUsageDTO = { "keyUsage": true,"keyCertSign": true}
        //         extendedKeyUsageDTO = { "extendedKeyUsage": true, "ocspSigning":true }
        //     }

            let issuerAlias = $('#issuerCA option:selected').val()
            console.log(issuerAlias)

            //console.log("templejt", template)



            $.ajax({
                type: 'GET',
                url: 'api/keyStore/getIssuerData/'+issuerAlias,
                contentType: "application/json",
                complete: function(data)
                {
                    issuerData = data.responseJSON
                    console.log(issuerData)


                    let issuerDataDTO = {"commonName":issuerData.commonName,"localityName":issuerData.localityName,"stateName":issuerData.stateName,
                    "countryName":issuerData.countryName,"organisationName":issuerData.organisationName, "organisationUnitName":issuerData.organisationUnitName,"givenName":"",
                    "surname":"", "uid":"", "serialNumber":"","email":issuerData.email};

                    let subjectDataDTO = {"commonName":commonName,"localityName":localityName,"stateName":stateName,
                                                    "countryName":countryName,"organisationName":organisationName, "organisationUnitName":organisationUnitName,"givenName":"",
                                                    "surname":"", "uid":"", "serialNumber":"","email":email};

                    let certDto = JSON.stringify({"issuerData":issuerDataDTO, "subjectData":subjectDataDTO,
                                                    "basicConstrains":isCA, "extendedKeyUsageDTO":extendedKeyUsageDTO,
                                                    "keyUsageDTO":keyUsageDTO, "alias":alias, "issuerAlias":issuerAlias,
                                                    "notAfter": issuerData.notAfter, "certType": certType});


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
                                                alert('Certificate made successfully')
                                                window.location.href = "certificates.html"
                                           }
                                           else {
                                                alert('Cannot make certificate')
                                           }
                                    }

                            })

    	        }

    	 })

})

    function loadIssuers() {
        $.ajax({
            type: 'GET',
            url: 'api/keyStore/getAliases',
            contentType: "application/json",
            complete: function(data)
            {
                aliases = data.responseJSON
                $.each(aliases, function(key, value) {
                    $('#issuerCA').append('<option value="'+value+'">'+value+'</option>');
                });
            }

        })

    }
}


