package controller;

import dto.CertificateDTO;
import model.CertificateSummary;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
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
import service.OCSPService;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

@RestController
@RequestMapping(value="/api/ocsp", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class OCSPController {


    @Autowired
    private KeyStoreService keyStoreService;

    @Autowired
    private OCSPService ocspService;

    @Autowired
    private CertificateSummaryRepository certificateSummaryRepository;



    @PostMapping(value = "/ocspResponse")
    public ResponseEntity<String> getOCSPResponse(@RequestBody  CertificateDTO dto) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, OCSPException, OperatorCreationException, UnrecoverableKeyException {

        CertificateSummary certSum = certificateSummaryRepository.findByAlias(dto.getAlias());

        List<CertificateSummary> rootList = certificateSummaryRepository.findByIsRootTrue();
        CertificateSummary issuerCertSum = rootList.get(0);

        char[] keyStorePasswordArray = dto.getKeyStorePassword().toCharArray();
        String keyStorePath = "keystores/root.p12";
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try {
            keyStore.load(new FileInputStream(keyStorePath), keyStorePasswordArray);
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            return null;
        }

        Certificate issuerCert = keyStore.getCertificate(issuerCertSum.getAlias());
        Certificate[] chain = keyStoreService.getCertificateChain(dto.getKeyStorePassword(), issuerCertSum.getAlias());

        X509CertificateHolder[] chainHolder = new X509CertificateHolder[chain.length];
        for (int i = 0; i < chain.length; i++) {
            chainHolder[i] = new JcaX509CertificateHolder((X509Certificate) chain[i]);
        }

        JcaX509CertificateHolder issuerCertHolder = new JcaX509CertificateHolder((X509Certificate) issuerCert);

        PrivateKey issuerPrivateKey = (PrivateKey) keyStore.getKey(issuerCertSum.getAlias(), dto.getKeyPassword().toCharArray());

        OCSPReq ocspReq = ocspService.generateOCSPRequest(issuerCertHolder, certSum.getSerialNumber());
        OCSPResp ocspResp = ocspService.doProcessOCSPRequest(ocspReq, chainHolder[0], issuerCertHolder, issuerPrivateKey, chainHolder);

        String status = (ocspResp == null) ? null : ocspService.doProcessOCSPResponse(ocspResp);
        System.out.println(status);
        if (status != null) {
            return new ResponseEntity<>(status, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
}





