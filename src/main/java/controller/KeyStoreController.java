package controller;

import dto.CertificateDTO;
import dto.DataDTO;
import model.IssuerData;
import org.bouncycastle.cert.cmp.CertificateStatus;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.KeyStoreService;

import javax.websocket.server.PathParam;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/api/keyStore", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class KeyStoreController {


    @Autowired
    KeyStoreService keyStoreService;

    @GetMapping(value="/getAliases/{password}")
    public ResponseEntity<List<String>> getAllCertAliasesFromKeyStore(@PathVariable("password") String password){

        try {
            List<String> aliases = keyStoreService.getAllCertAliasesFromKeyStore("intermediate", password);
            return new ResponseEntity<>(aliases, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value="/getIssuerData/{alias}/{keyStorePassword}/{keyPassword}")
    public ResponseEntity<DataDTO> getIssuerData(@PathVariable("alias") String alias, @PathVariable("keyStorePassword") String keyStorePassword, @PathVariable("keyPassword") String keyPassword){

        try {
            DataDTO dataDTO = keyStoreService.readIssuerFromStore(alias, keyStorePassword, keyPassword);
            return new ResponseEntity<>(dataDTO, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @GetMapping(value="/getCerts/{keyStorePassword}")
    public ResponseEntity<List<CertificateDTO>> getAllCertificates(@PathVariable("keyStorePassword") String keyStorePassword){

        try {
            List<CertificateDTO> root = keyStoreService.getAllCertificates("root", keyStorePassword);
            List<CertificateDTO> intermediate = keyStoreService.getAllCertificates("intermediate", keyStorePassword);
            List<CertificateDTO> endentity = keyStoreService.getAllCertificates("endentity", keyStorePassword);

            List<CertificateDTO> all = new ArrayList<>();
            if(root != null)all.addAll(root);
            if(intermediate != null)all.addAll(intermediate);
            if(endentity != null)all.addAll(endentity);

            if(all.isEmpty()){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(all, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value="/getCert/{keyStorePassword}/{keyPassword}")
    public ResponseEntity<CertificateDTO> getCertificate(@PathVariable("keyStorePassword") String keyStorePassword, @PathVariable("keyPassword") String keyPassword){

        try {
            List<CertificateDTO> root = keyStoreService.getAllCertificates("root", keyStorePassword);
            List<CertificateDTO> intermediate = keyStoreService.getAllCertificates("intermediate", keyStorePassword);
            List<CertificateDTO> endentity = keyStoreService.getAllCertificates("endentity", keyStorePassword);

            List<CertificateDTO> all = new ArrayList<>();
            if(root != null)all.addAll(root);
            if(intermediate != null)all.addAll(intermediate);
            if(endentity != null)all.addAll(endentity);
            CertificateDTO res = null;
            System.out.println("Sifra "+keyPassword);
            for(CertificateDTO cDTO : all){
                System.out.println("password "+cDTO.getAlias());
                if(cDTO.getAlias().equals(keyPassword)){
                    System.out.println("nasao");
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
    public ResponseEntity<CertificateDTO> downloadCertificate(@RequestBody CertificateDTO dto) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        keyStoreService.downloadCertificate(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
