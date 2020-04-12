package service;

import dto.DataDTO;
import enumeration.CertificateType;
import model.IssuerData;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Service
public class KeyStoreService {




    //dva passworda: za pristup kistoru i za ki za setifikat

    public KeyStore createNewKeystore(String type, String keyStorePassword) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, NoSuchProviderException {
        char[] passwordArray = keyStorePassword.toCharArray();

        File file = new File("certificates/" + type.toLowerCase() + ".pfx");
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, passwordArray);
        keyStore.store(new FileOutputStream(file), keyStorePassword.toCharArray());
        return keyStore;
    }

    //TODO kada izbaci eksepsn za pw da vrati 400 a ne 200
    public void saveCertificate(String keyPassword, String alias, String keyStorePassword, PrivateKey privateKey, Certificate certificate) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, NoSuchProviderException {
        char[] keyPasswordArray = keyPassword.toCharArray();
        char[] keyStorePasswordArray = keyStorePassword.toCharArray();

        CertificateType type = getCertificateType(certificate);
        String keyStorePath = "certificates/" + type + ".pfx";

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try {
            keyStore.load(new FileInputStream(keyStorePath), keyStorePasswordArray);
            keyStore.setKeyEntry(alias, privateKey, keyPasswordArray, new Certificate[] { certificate });
            keyStore.store(new FileOutputStream(keyStorePath), keyPasswordArray);
        } catch (FileNotFoundException e) {
            keyStore = createNewKeystore(type.toString(), keyStorePassword);
            keyStore.setKeyEntry(alias, privateKey, keyPasswordArray, new Certificate[] { certificate });
            keyStore.store(new FileOutputStream(keyStorePath), keyPasswordArray);
        }

    }



    private CertificateType getCertificateType(Certificate cert) throws CertificateEncodingException {
        X509Certificate certificate = (X509Certificate) cert;
        X500Name subject = new JcaX509CertificateHolder(certificate).getSubject();
        X500Name issuer = new JcaX509CertificateHolder(certificate).getIssuer();

        if (subject.getRDNs(BCStyle.CN)[0].getFirst().getValue().toString().equals(issuer.getRDNs(BCStyle.CN)[0].getFirst().getValue().toString())) {
            return CertificateType.ROOT;
        } else if (certificate.getBasicConstraints() != -1) {
            return CertificateType.INTERMEDIATE;
        } else {
            return CertificateType.ENDENTITY;
        }
    }




    public DataDTO readIssuerFromStore(String alias, String keyStorePassword, String keyPassword)
    {
        try {
            char[] keyPasswordArray = keyPassword.toCharArray();
            char[] keyStorePasswordArray = keyStorePassword.toCharArray();

            String keyStorePath = "certificates/root.pfx";
            KeyStore keyStore = KeyStore.getInstance("PKCS12");

            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStorePath));
            keyStore.load(in, keyStorePasswordArray);

            Certificate cert = keyStore.getCertificate(alias);
             PrivateKey privKey;
             X500Name issuerName;
             PublicKey pubKey;

            if(cert == null){ //ako se ne nalazi u root-u, da potrazi u intermediate
                keyStorePath = "certificates/intermediate.pfx";
                keyStore = KeyStore.getInstance("PKCS12");

                in = new BufferedInputStream(new FileInputStream(keyStorePath));
                keyStore.load(in, keyStorePasswordArray);

                cert = keyStore.getCertificate(alias);
                pubKey = cert.getPublicKey();
                privKey = (PrivateKey) keyStore.getKey(alias, keyPasswordArray);
                issuerName = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();
            } else {
                privKey = (PrivateKey) keyStore.getKey(alias, keyPasswordArray);
                pubKey = cert.getPublicKey();
                issuerName = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();
            }


            System.out.println(keyStore.getCertificateChain(alias).length + "---CHAINNNNNNNNNNNN");

            IssuerData issuerData = new IssuerData(issuerName, privKey, pubKey);
            DataDTO dto = new DataDTO(issuerData);
            return dto;
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public IssuerData readIssuerDataFromStore(String alias, String keyStorePassword, String keyPassword) {
        try {
            char[] keyPasswordArray = keyPassword.toCharArray();
            char[] keyStorePasswordArray = keyStorePassword.toCharArray();

            String keyStorePath = "certificates/root.pfx";
            KeyStore keyStore = KeyStore.getInstance("PKCS12");

            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStorePath));
            keyStore.load(in, keyStorePasswordArray);

            Certificate cert = keyStore.getCertificate(alias);
            if(cert == null){
                keyStorePath = "certificates/intermediate.pfx";
                in = new BufferedInputStream(new FileInputStream(keyStorePath));
                keyStore.load(in, keyStorePasswordArray);
                cert = keyStore.getCertificate(alias);
            }

            PrivateKey privKey = (PrivateKey) keyStore.getKey(alias, keyPasswordArray);
            PublicKey publicKey = cert.getPublicKey();

            X500Name issuerName = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();
            return new IssuerData(issuerName, privKey, publicKey);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }



    public Certificate readCertificate(String keyStoreFile, String keyStorePass, String alias) {
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");

            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            ks.load(in, keyStorePass.toCharArray());

            if(ks.isKeyEntry(alias)) {
                Certificate cert = ks.getCertificate(alias);
                return cert;
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//TODO namestiti putanju
    public List<String> getAllCertAliasesFromKeyStore(String type, String keyStorePassword)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        String keyStorePath = "certificates/root.pfx";
        char[] keyStorePasswordArray = keyStorePassword.toCharArray();

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try {
            keyStore.load(new FileInputStream(keyStorePath), keyStorePasswordArray);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Enumeration<String> aliases = keyStore.aliases();
        List<String> list = Collections.list(aliases);

        keyStorePath = "certificates/intermediate.pfx";
        try {
            keyStore.load(new FileInputStream(keyStorePath), keyStorePasswordArray);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        aliases = keyStore.aliases();
        List<String> listCa = Collections.list(aliases);

        list.addAll(listCa);

        System.out.println(list+"*************");
        return list;
    }



    public PublicKey getPublicKey(CertificateType certificateType, String keyStorePassword, String alias)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, NoSuchProviderException {

        String keyStorePath = "certificates/intermediate.pfx";
        char[] keyStorePasswordArray = keyStorePassword.toCharArray();

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try {
            keyStore.load(new FileInputStream(keyStorePath), keyStorePasswordArray);
        } catch (FileNotFoundException e) {

        }
        Certificate certificate = keyStore.getCertificate(alias);
        return certificate.getPublicKey();
    }





}
