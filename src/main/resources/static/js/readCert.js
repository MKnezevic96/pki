window.onload = function () {



}




function addCertTr(certs)
{
	let tr=$('<tr></tr>');
	let tdAlias=$('<td>'+certs.alias +'</td>');
	let tdSubjectCN=$('<td>'+racuni.subjectData.commonName +'</td>');
	let tdIssuerCN=$('<td>'+certs.issuerData.commonName +'</td>');
    let tdDownload = $('<td>' + '<a href="#">Download</a>' + '</td>');
    let tdRevoke = $('<td>' + '<a href="#">Revoke</a>' + '</td>');


	let tdObrisi = $('<td>' + '<a href="#">Obrisi</a>' + '</td>');
	tdObrisi.click(clickBrisi(racuni.brojRacuna));

	let tdAkDe = $('<td>' + '<a href="#">'+tipic+'</a>' + '</td>');
	tdAkDe.click(clickAD(racuni.brojRacuna));

	tr.append(tdBroj).append(tdTip).append(tdRaspolozivo).append(tdRezervisano).append(tdUkupno).append(tdOnline).append(tdAktivan).append(tdObrisi).append(tdAkDe);
	$('#tabelaracuna tbody').append(tr); //prvo smo napravili red, i onda ga kacimo na tbody

	if(racuni.aktivan===1)
	{
		dodajSelect(racuni);
	}

}