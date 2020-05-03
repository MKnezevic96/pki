window.onload = function () {

    $('#certs').hide()
    $('#second').hide()


    $('#btnConfirm').click(function(e){
    		e.preventDefault()

            let keyStorePassword = $('#keyStorePassword').val()
            $('#passwordDiv').hide()

            $.ajax({
        		type: 'GET',
        		url: 'api/keyStore/getCerts/' + keyStorePassword,
        		contentType: "application/json",
        		complete: function(data)
        		{
        			certs = data.responseJSON

                  if(data.status == "200")
                   {
                        $('#certs tbody').html('');
                        for(let cert of certs)
                        {
                            addCertTr(cert, keyStorePassword);
                        }
                        $('#certs').show()
                   }
                   else {
                        alert('Trenutno nema postojecih sertifikata')
                   }
                           		}

        	})

    })

}




function addCertTr(cert, keyStorePassword)
{


//let keyPassword ="a"



        let issuerDataDTO = {"commonName":cert.issuerData.commonName}
                let subjectDataDTO = {"commonName":cert.subjectData.commonName}
                let certDto = JSON.stringify({"issuerData":issuerDataDTO, "subjectData":subjectDataDTO, "keyStorePassword":keyStorePassword,
                                              "type":cert.type, "alias":cert.alias, "keyPassword":keyPassword})

            		console.log(certDto)
            		$.ajax({
            			type: 'POST',
            			url:'/api/ocsp/ocspResponse',
            			data: certDto,
            			dataType : "json",
            			contentType : "application/json; charset=utf-8",
            			complete: function(data)
                            {

                                console.log(data.responseText)
                                status = data.responseText




	let tr=$('<tr></tr>')
	let tdAlias=$('<td>'+cert.alias +'</td>')
	let tdSubjectCN=$('<td>'+cert.subjectData.commonName +'</td>')
	let tdIssuerCN=$('<td>'+cert.issuerData.commonName +'</td>')
	let tdType=$('<td>'+ cert.type + '</td>')
	let tdStatus=$('<td>' + status + '</td>')
    let tdDownload = $('<td><button class="btn btn-default waves-effect waves-light" id="btnDownload">Download</button></td>')
    let tdRevoke = $('<td><button class="btn btn-default waves-effect waves-light" id="btnRevoke">Revoke</button></td>')


	tdDownload.click(download(cert, keyStorePassword))
	tdRevoke.click(revoke(cert, keyStorePassword))


	tr.append(tdAlias).append(tdSubjectCN).append(tdIssuerCN).append(tdType).append(tdStatus).append(tdDownload).append(tdRevoke)
	$('#certs tbody').append(tr);

	}

                		})

}

function download(cert, keyStorePassword){

    return function(){
        let issuerDataDTO = {"commonName":cert.issuerData.commonName}
        let subjectDataDTO = {"commonName":cert.subjectData.commonName}
        let certDto = JSON.stringify({"issuerData":issuerDataDTO, "subjectData":subjectDataDTO, "keyStorePassword":keyStorePassword,
                                      "type":cert.type, "alias":cert.alias, "serialNumber":cert.serialNumber})

    		console.log(certDto)
    		$.ajax({
    			type: 'POST',
    			url:'/api/keyStore/download',
    			data: certDto,
    			dataType : "json",
    			contentType : "application/json; charset=utf-8",
    			complete: function(data)
                    {
                         console.log(data.status)

                          if(data.status == "200")
                           {
                                alert('hoce')
                           }
                           else {
                                alert('nece')
                           }
                    }

    		})
    }
}


function revoke(cert, keyStorePassword){

return function(){

        keyPassword ="a"



        let issuerDataDTO = {"commonName":cert.issuerData.commonName}
                let subjectDataDTO = {"commonName":cert.subjectData.commonName}
                let certDto = JSON.stringify({"issuerData":issuerDataDTO, "subjectData":subjectDataDTO, "keyStorePassword":keyStorePassword,
                                              "type":cert.type, "alias":cert.alias, "keyPassword":keyPassword, "serialNumber":cert.serialNumber})

            		console.log(certDto)
            		$.ajax({
            			type: 'POST',
            			url:'/api/revocation/revoke',
            			data: certDto,
            			dataType : "json",
            			contentType : "application/json; charset=utf-8",
            			complete: function(data)
                            {
                                 console.log(data.status)
                                 console.log(data.responseText)
                                  if(data.status == "200")
                                   {
                                        alert('hoce')
                                   }
                                   else {
                                        alert('nece')
                                   }
                            }

            		})


}


}