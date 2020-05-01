package controller;

import dto.CertificateDTO;
import dto.ExtendedKeyUsageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.CertificateService;
import service.KeyStoreService;
import service.RevocationService;


@RestController
@RequestMapping(value="/api/certificates", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private KeyStoreService keyStoreService;

    @Autowired
    private RevocationService revocationService;


    @PostMapping(value = "/generateCertificate")
    public ResponseEntity<Void> generateCertificate(@RequestBody CertificateDTO dto) {
        try {
            certificateService.generateCertificate(dto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value="/generateSelfSigned")
    public ResponseEntity<Void> generateSelfSignedSertificate(@RequestBody CertificateDTO dto){

        if(!dto.getIssuerData().getCommonName().equals(dto.getSubjectData().getCommonName())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ExtendedKeyUsageDTO ekuDTO = dto.getExtendedKeyUsageDTO();
        if(ekuDTO.isServerAuth().equals("1.3.6.1.5.5.7.3.1"))
            System.out.println("CertificateController-----------> server");
        if(ekuDTO.isClientAuth().equals("1.3.6.1.5.5.7.3.2"))
            System.out.println("CertificateController-----------> client");
        if(ekuDTO.isCodeSigning().equals("1.3.6.1.5.5.7.3.1.3"))
            System.out.println("CertificateController-----------> code");
        if(ekuDTO.isEmailProtection().equals("1.3.6.1.5.5.7.3.4"))
            System.out.println("CertificateController-----------> email");
        if(ekuDTO.isEmailProtection().equals("1.3.6.1.5.5.7.3.8"))
            System.out.println("CertificateController-----------> time");
        if(ekuDTO.isEmailProtection().equals("1.3.6.1.5.5.7.3.9"))
            System.out.println("CertificateController-----------> ocsp");

        System.out.println();
        try {
            certificateService.generateSelfSignedX509Certificate(dto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


}
