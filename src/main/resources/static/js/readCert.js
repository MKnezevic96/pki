window.onload = function () {


            $.ajax({
        		type: 'GET',
        		url: 'api/keyStore/getCerts',
        		contentType: "application/json",
        		complete: function(data)
        		{
        			certs = data.responseJSON

                  if(data.status == "200")
                   {
                        $('#certs tbody').html('');
                        for(let cert of certs)
                        {
                            addCertTr(cert);
                        }
                        $('#certs').show()
                   }
                   else {
                        alert('Trenutno nema postojecih sertifikata')
                   }
                           		}

        	})

}




function addCertTr(cert)
{

        let issuerDataDTO = {"commonName":cert.issuerData.commonName}
        let subjectDataDTO = {"commonName":cert.subjectData.commonName}
        let certDto = JSON.stringify({"issuerData":issuerDataDTO, "subjectData":subjectDataDTO, "type":cert.type, "alias":cert.alias})

            $.ajax({
                type: 'POST',
                url:'/api/ocsp/ocspResponse',
                data: certDto,
                dataType : "json",
                contentType : "application/json; charset=utf-8",
                complete: function(data)
                    {
                        status = data.responseText

                        let tr=$('<tr></tr>')
                        let tdAlias=$('<td>'+cert.alias +'</td>')
                        let tdSubjectCN=$('<td>'+cert.subjectData.commonName +'</td>')
                        let tdIssuerCN=$('<td>'+cert.issuerData.commonName +'</td>')
                        let tdType=$('<td>'+ cert.type + '</td>')
                        let tdStatus=$('<td>' + status + '</td>')
                        let tdDownload = $('<td><button class="btn btn-default waves-effect waves-light" id="btnDownload">Download</button></td>')
                        let tdRevoke = $('<td><button class="btn btn-default waves-effect waves-light" id="btnRevoke">Revoke</button></td>')


                        tdDownload.click(download(cert))
                        tdRevoke.click(revoke(cert))

                        tr.append(tdAlias).append(tdSubjectCN).append(tdIssuerCN).append(tdType).append(tdStatus).append(tdDownload)
                        if(status==="GOOD"){
                            tr.append(tdRevoke)
                        }

                        $('#certs tbody').append(tr);

                    }
            })

}

function download(cert){

    return function(){
        let issuerDataDTO = {"commonName":cert.issuerData.commonName}
        let subjectDataDTO = {"commonName":cert.subjectData.commonName}
        let certDto = JSON.stringify({"issuerData":issuerDataDTO, "subjectData":subjectDataDTO, "type":cert.type, "alias":cert.alias, "serialNumber":cert.serialNumber})

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
                                alert('Download successful')
                           }

                    }

    		})
    }
}


function revoke(cert){

    return function(){
        let issuerDataDTO = {"commonName":cert.issuerData.commonName}
        let subjectDataDTO = {"commonName":cert.subjectData.commonName}
        let certDto = JSON.stringify({"issuerData":issuerDataDTO, "subjectData":subjectDataDTO, "type":cert.type, "alias":cert.alias, "serialNumber":cert.serialNumber})

            console.log(certDto)
            $.ajax({
                type: 'POST',
                url:'/api/revocation/revoke',
                data: certDto,
                dataType : "json",
                contentType : "application/json; charset=utf-8",
                complete: function(data)
                    {
                      if(data.status == "200")
                       {
                            window.location.href = 'readCert.html'
                       }

                    }

            })

}


}