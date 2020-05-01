package service;

import model.CertificateSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.CertificateSummaryRepository;

import java.util.Calendar;
import java.util.List;


@Service
public class RevocationService {

        @Autowired
        private CertificateSummaryRepository certificateSummaryRepository;

        public void recursiveRevocation(CertificateSummary certSum){

                List<CertificateSummary> issuedCerts = certificateSummaryRepository.findByIssuerAlias(certSum.getAlias());

                System.out.println(issuedCerts.size());

                for(CertificateSummary cert : issuedCerts){
                        certSum = certificateSummaryRepository.findByAlias(cert.getAlias());
                        certSum.setRevoked(true);
                        certSum.setRevocationDate(Calendar.getInstance().getTime());

                        certificateSummaryRepository.save(certSum);
                        recursiveRevocation(certSum);
                }

        }


}
