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
            System.out.println(" ++++++++++++++++++"+dto.getCertType());
            certificateService.generateCertificate(dto);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value="/generateSelfSigned")
    public ResponseEntity<Void> generateSelfSignedSertificate(@RequestBody CertificateDTO dto){
        System.out.println(dto.getAlias());
        if(!dto.getIssuerData().getCommonName().equals(dto.getSubjectData().getCommonName())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            certificateService.generateSelfSignedX509Certificate(dto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


}
