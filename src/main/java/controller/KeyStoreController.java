package controller;

import dto.CertificateDTO;
import dto.DataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.KeyStoreService;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/api/keyStore", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class KeyStoreController {


    @Autowired
    KeyStoreService keyStoreService;

    @GetMapping(value="/getAliases")
    public ResponseEntity<List<String>> getAllCertAliasesFromKeyStore(){

        try {
            List<String> aliases = keyStoreService.getAllCertAliasesFromKeyStore();
            return new ResponseEntity<>(aliases, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value="/getIssuerData/{alias}")
    public ResponseEntity<DataDTO> getIssuerData(@PathVariable("alias") String alias){

        try {
            DataDTO dataDTO = keyStoreService.readIssuerFromStore(alias);
            return new ResponseEntity<>(dataDTO, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @GetMapping(value="/getCerts")
    public ResponseEntity<List<CertificateDTO>> getAllCertificates(){

        try {
            List<CertificateDTO> all = new ArrayList<>();

            all.addAll(keyStoreService.getAllCertificates("root"));
            all.addAll(keyStoreService.getAllCertificates("intermediate"));
            all.addAll(keyStoreService.getAllCertificates("endentity"));

            if(all.isEmpty()){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(all, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    @GetMapping(value="/getCert/{keyPassword}")
    public ResponseEntity<CertificateDTO> getCertificate(@PathVariable("keyPassword") String keyPassword){

        try {
            List<CertificateDTO> root = keyStoreService.getAllCertificates("root");
            List<CertificateDTO> intermediate = keyStoreService.getAllCertificates("intermediate");
            List<CertificateDTO> endentity = keyStoreService.getAllCertificates("endentity");

            List<CertificateDTO> all = new ArrayList<>();
            if(root != null)
                all.addAll(root);
            else
                System.out.println("null root ");
            if(intermediate != null)
                all.addAll(intermediate);
            else
                System.out.println("null intermediate");
            if(endentity != null)
                all.addAll(endentity);
            else
                System.out.println("null endentity");
            CertificateDTO res = null;
            System.out.println("Sifra "+keyPassword);
            for(CertificateDTO cDTO : all){
                System.out.println("password "+cDTO.getAlias());
                if(cDTO.getAlias().equals(keyPassword)){
                    System.out.println("nasao");
                    if(cDTO.getExtendedKeyUsageDTO() == null)
                        System.out.println("nema getExtendedKeyUssage");
                    res = cDTO;
                }
            }
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value="/download")
    public ResponseEntity<CertificateDTO> downloadCertificate(@RequestBody CertificateDTO dto) throws CertificateException, IOException {
        keyStoreService.downloadCertificate(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
