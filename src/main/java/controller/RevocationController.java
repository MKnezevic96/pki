package controller;

import dto.CertificateDTO;
import dto.CertificateSummaryDTO;
import model.CertificateSummary;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import repository.CertificateSummaryRepository;
import service.KeyStoreService;
import service.RevocationService;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping(value="/api/revocation", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class RevocationController {


    @Autowired
    private KeyStoreService keyStoreService;

    @Autowired
    private CertificateSummaryRepository certificateSummaryRepository;

    @Autowired
    private RevocationService revocationService;

    @PostMapping(value = "/revoke")
    public ResponseEntity<String> revokeCertificate(@RequestBody CertificateDTO dto) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {

        CertificateSummary certSum = certificateSummaryRepository.findBySerialNumber(dto.getSerialNumber());
        if (certSum == null) {
            return new ResponseEntity<String>("Certificate with requested serial number doesn't exist.", HttpStatus.BAD_REQUEST);
        }

        if (certSum.isRevoked()) {
            return new ResponseEntity<String>("Certificate with requested serial number is already revoked.", HttpStatus.BAD_REQUEST);
        }

        certSum.setRevoked(true);
        certSum.setRevocationDate(Calendar.getInstance().getTime());
        certificateSummaryRepository.save(certSum);

        //TODO da li raditi revocation reason hm hm? mrzi me
        revocationService.recursiveRevocation(certSum);

        return new ResponseEntity<>("Certificate revocation successful.", HttpStatus.OK);
    }


}
