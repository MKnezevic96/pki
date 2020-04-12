package controller;

import dto.DataDTO;
import model.IssuerData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.KeyStoreService;

import javax.websocket.server.PathParam;
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


}
