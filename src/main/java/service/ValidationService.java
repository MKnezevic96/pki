package service;


import org.springframework.stereotype.Service;

import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.*;
import java.util.Calendar;


@Service
public class ValidationService {


    public boolean validateCertificate(Certificate certificate, PublicKey publicKey) {

        if(!verifySignature(certificate, publicKey)){
            System.out.println("NE VALJA POTPIS");
            return false;
        }

        if(!validateDate(certificate)){
            System.out.println("NE VALJA DATUM");
            return false;
        }

        return true;

    }


    public boolean validateCertificateChain(Certificate[] chain){

        for (int i = chain.length - 1; i > 0; i--) {
            if (!validateCertificate(chain[i - 1], chain[i].getPublicKey())) {
                return false;
            }
        }
        return true;
    }


    public boolean validateDate(Certificate cert){

        try {
            ((X509Certificate)cert).checkValidity(Calendar.getInstance().getTime());
            return true;
        } catch (CertificateNotYetValidException | CertificateExpiredException e) {
            e.printStackTrace();
            return false;
        }
    }


    private boolean verifySignature(Certificate cert, PublicKey publicKey){

        try {
            cert.verify(publicKey);
            return true;
        } catch (SignatureException | InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
            return false;
        }
    }

}
