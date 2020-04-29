package service;

import dto.CertificateDTO;
import dto.DataDTO;
import enumeration.CertificateType;
import model.IssuerData;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class KeyStoreService {


    @Autowired
    private CertificateService certificateService;

    @Autowired
    private OCSPService ocspService;

    @Autowired
    private Environment env;


    public KeyStore loadKeystore(String keyStorePath) throws KeyStoreException {
        String keystorePass = env.getProperty("spring.keystore.keystorePassword");
        char[] keyStorePasswordArray = keystorePass.toCharArray();

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try {
            keyStore.load(new FileInputStream(keyStorePath), keyStorePasswordArray);
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            return null;
        }

        return keyStore;
    }


    public KeyStore createNewKeystore(String type) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

        String keystorePass = env.getProperty("spring.keystore.keystorePassword");
        char[] keystorePassArray = keystorePass.toCharArray();

        File file = new File("keystores/" + type.toLowerCase() + ".p12");
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        keyStore.load(null, keystorePassArray);
        keyStore.store(new FileOutputStream(file), keystorePassArray);
        return keyStore;
    }

    //TODO kada izbaci eksepsn za pw da vrati 400 a ne 200
    public void saveCertificate(String alias, PrivateKey privateKey, Certificate certificate) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, NoSuchProviderException {

        String keystorePass = env.getProperty("spring.keystore.keystorePassword");
        String keyPass = env.getProperty("spring.keystore.keyPassword");

        char[] keyPassArray = keyPass.toCharArray();
        char[] keyStorePassArray = keystorePass.toCharArray();

        CertificateType type = getCertificateType(certificate);
        String keyStorePath = "keystores/" + type + ".p12";

        KeyStore keyStore = loadKeystore(keyStorePath);
        if(keyStore == null){
            keyStore = createNewKeystore(type.toString());
        }

        keyStore.setKeyEntry(alias, privateKey, keyPassArray, new Certificate[] { certificate });
        keyStore.store(new FileOutputStream(keyStorePath), keyStorePassArray);

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


    private String getCertificateType(CertificateDTO dto){

        if (dto.getSubjectData().getCommonName().equals(dto.getIssuerData().getCommonName())){
            return CertificateType.ROOT.toString().toLowerCase();
        } else if(dto.isBasicConstrains()){
            return CertificateType.INTERMEDIATE.toString().toLowerCase();
        } else {
            return CertificateType.ENDENTITY.toString().toLowerCase();
        }
    }


    public DataDTO readIssuerFromStore(String alias)
    {
        try {
            String keyStorePath = "keystores/root.p12";
            KeyStore keyStore = loadKeystore(keyStorePath);

            String keyPass = env.getProperty("spring.keystore.keyPassword");
            char[] keyPasswordArray = keyPass.toCharArray();

            Certificate cert = keyStore.getCertificate(alias);
            PrivateKey privKey;
            X500Name issuerName;
            PublicKey pubKey;

            if(cert == null){
                keyStorePath = "keystores/intermediate.p12";
                keyStore = loadKeystore(keyStorePath);

                cert = keyStore.getCertificate(alias);
                pubKey = cert.getPublicKey();
                privKey = (PrivateKey) keyStore.getKey(alias, keyPasswordArray);
                issuerName = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();
            } else {
                privKey = (PrivateKey) keyStore.getKey(alias, keyPasswordArray);
                pubKey = cert.getPublicKey();
                issuerName = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();
            }

            IssuerData issuerData = new IssuerData(issuerName, privKey, pubKey, ((X509Certificate) cert).getNotAfter());
            DataDTO dto = new DataDTO(issuerData);
            return dto;
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException | CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }


    public IssuerData readIssuerDataFromStore(String alias) {
        try {
            String keyPass = env.getProperty("spring.keystore.keyPassword");
            char[] keyPasswordArray = keyPass.toCharArray();

            String keyStorePath = "keystores/root.p12";
            KeyStore keyStore = loadKeystore(keyStorePath);

            Certificate cert = keyStore.getCertificate(alias);
            if(cert == null){
                keyStorePath = "keystores/intermediate.p12";
                keyStore = loadKeystore(keyStorePath);
                cert = keyStore.getCertificate(alias);
            }

            PrivateKey privKey = (PrivateKey) keyStore.getKey(alias, keyPasswordArray);
            PublicKey publicKey = cert.getPublicKey();

            X500Name issuerName = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();
            return new IssuerData(issuerName, privKey, publicKey);
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }



    public Certificate readCertificate(String keyStorePath, String alias) {
        try {
            KeyStore ks = loadKeystore(keyStorePath);

            if(ks.isKeyEntry(alias)) {
                Certificate cert = ks.getCertificate(alias);
                return cert;
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        return null;
    }


    public List<String> getAllCertAliasesFromKeyStore() throws KeyStoreException {
        String keyStorePath = "keystores/root.p12";
        KeyStore keyStore = loadKeystore(keyStorePath);

        List<String> list = Collections.list(keyStore.aliases());

        keyStorePath = "keystores/intermediate.p12";
        try{
            keyStore = loadKeystore(keyStorePath);
            List<String> listCa = Collections.list(keyStore.aliases());
            list.addAll(listCa);
        } catch (NullPointerException ignored) { }


        return list;
    }


    public Certificate[] getCertificateChain(String alias) throws KeyStoreException {
        String keyStorePath = "keystores/root.p12";
        KeyStore keyStore = loadKeystore(keyStorePath);

        Certificate[] chain = keyStore.getCertificateChain(alias);

        if(chain == null ){
            keyStorePath = "keystores/intermediate.p12";
            keyStore = loadKeystore(keyStorePath);
            chain = keyStore.getCertificateChain(alias);
        }

        return chain;
    }




    public List<CertificateDTO> getAllCertificates(String type) throws CertificateException, KeyStoreException {
        String keyStorePath = "keystores/" + type.toLowerCase() + ".p12";

        List<CertificateDTO> certs = new ArrayList<>();

        try{
            KeyStore keyStore = loadKeystore(keyStorePath);
            List<String> list = Collections.list(keyStore.aliases());

            for(String alias : list){
                Certificate cert = readCertificate(keyStorePath, alias);

                CertificateDTO dto = new CertificateDTO();
                dto.setAlias(alias);

                JcaX509CertificateHolder certHolder = new JcaX509CertificateHolder((X509Certificate) cert);
                dto.setSerialNumber(certHolder.getSerialNumber().toString());

                X500Name x500name = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();
                RDN cn = x500name.getRDNs(BCStyle.CN)[0];

                DataDTO subjectData = new DataDTO();
                subjectData.setCommonName(IETFUtils.valueToString(cn.getFirst().getValue()));
                dto.setSubjectData(subjectData);

                x500name = new JcaX509CertificateHolder((X509Certificate) cert).getIssuer();
                cn = x500name.getRDNs(BCStyle.CN)[0];

                DataDTO issuerData = new DataDTO();
                issuerData.setCommonName(IETFUtils.valueToString(cn.getFirst().getValue()));
                dto.setIssuerData(issuerData);

                dto.setType(getCertificateType(cert).toString().toLowerCase());

                certs.add(dto);
            }
        } catch (NullPointerException ignored) {}

        return certs;
    }


    public void downloadCertificate(CertificateDTO dto) throws CertificateException, IOException {

        String keystorePass = env.getProperty("spring.keystore.keystorePassword");

        String keyStorePath = "keystores/" + dto.getType() + ".p12";
        Certificate certificate = readCertificate(keyStorePath, dto.getAlias());

        FileOutputStream os = new FileOutputStream("certificates/" + dto.getType() + "_" + dto.getAlias() + ".p12");
        os.write("---------------BEGIN CERTIFICATE---------------\n".getBytes("US-ASCII"));
        os.write(Base64.encodeBase64(certificate.getEncoded(), true));
        os.write("---------------END CERTIFICATE---------------\n".getBytes("US-ASCII"));
        os.close();
    }


}