package service;

import dto.CertificateDTO;
import dto.DataDTO;
import dto.ExtendedKeyUsageDTO;
import enumeration.CertificateType;
import model.IssuerData;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Service
public class KeyStoreService {


    @Autowired
    private CertificateService certificateService;

    @Autowired
    private OCSPService ocspService;


    public KeyStore createNewKeystore(String type, String keyStorePassword) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, NoSuchProviderException {
        char[] passwordArray = keyStorePassword.toCharArray();

        File file = new File("keystores/" + type.toLowerCase() + ".p12");
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
        String keyStorePath = "keystores/" + type + ".p12";

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


    private String getCertificateType(CertificateDTO dto){

        if (dto.getSubjectData().getCommonName().equals(dto.getIssuerData().getCommonName())){
            return CertificateType.ROOT.toString().toLowerCase();
        } else if(dto.isBasicConstrains()){
            return CertificateType.INTERMEDIATE.toString().toLowerCase();
        } else {
            return CertificateType.ENDENTITY.toString().toLowerCase();
        }
    }




    public DataDTO readIssuerFromStore(String alias, String keyStorePassword, String keyPassword)
    {
        try {
            char[] keyPasswordArray = keyPassword.toCharArray();
            char[] keyStorePasswordArray = keyStorePassword.toCharArray();

            String keyStorePath = "keystores/root.p12";
            KeyStore keyStore = KeyStore.getInstance("PKCS12");

            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStorePath));
            keyStore.load(in, keyStorePasswordArray);

            Certificate cert = keyStore.getCertificate(alias);
            PrivateKey privKey;
            X500Name issuerName;
            PublicKey pubKey;

            if(cert == null){ //ako se ne nalazi u root-u, da potrazi u intermediate
                keyStorePath = "keystores/intermediate.p12";
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

            IssuerData issuerData = new IssuerData(issuerName, privKey, pubKey, ((X509Certificate) cert).getNotAfter() );
            DataDTO dto = new DataDTO(issuerData);
            return dto;
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public IssuerData readIssuerDataFromStore(String alias, String keyStorePassword, String keyPassword) {
        try {
            char[] keyPasswordArray = keyPassword.toCharArray();
            char[] keyStorePasswordArray = keyStorePassword.toCharArray();

            String keyStorePath = "keystores/root.p12";
            KeyStore keyStore = KeyStore.getInstance("PKCS12");

            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStorePath));
            keyStore.load(in, keyStorePasswordArray);

            Certificate cert = keyStore.getCertificate(alias);
            if(cert == null){
                keyStorePath = "keystores/intermediate.p12";
                in = new BufferedInputStream(new FileInputStream(keyStorePath));
                keyStore.load(in, keyStorePasswordArray);
                cert = keyStore.getCertificate(alias);
            }

            PrivateKey privKey = (PrivateKey) keyStore.getKey(alias, keyPasswordArray);
            PublicKey publicKey = cert.getPublicKey();

            X500Name issuerName = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();
            return new IssuerData(issuerName, privKey, publicKey);
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | IOException | KeyStoreException e) {
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
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
            e.printStackTrace();
        }
        System.out.println("return null");
        return null;
    }

    //TODO namestiti putanju
    public List<String> getAllCertAliasesFromKeyStore(String type, String keyStorePassword)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        String keyStorePath = "keystores/root.p12";
        char[] keyStorePasswordArray = keyStorePassword.toCharArray();

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try {
            keyStore.load(new FileInputStream(keyStorePath), keyStorePasswordArray);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Enumeration<String> aliases = keyStore.aliases();
        List<String> list = Collections.list(aliases);

        keyStorePath = "keystores/intermediate.p12";
        try {
            keyStore.load(new FileInputStream(keyStorePath), keyStorePasswordArray);
        } catch (FileNotFoundException e) {
            //  e.printStackTrace();
        }
        aliases = keyStore.aliases();
        List<String> listCa = Collections.list(aliases);

        list.addAll(listCa);

        return list;
    }


    public Certificate[] getCertificateChain(String keyStorePassword, String alias)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        String keyStorePath = "keystores/root.p12";
        char[] keyStorePasswordArray = keyStorePassword.toCharArray();

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try {
            keyStore.load(new FileInputStream(keyStorePath), keyStorePasswordArray);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Certificate[] chain = keyStore.getCertificateChain(alias);

        if(chain == null ){
            keyStorePath = "keystores/intermediate.p12";

            try {
                keyStore.load(new FileInputStream(keyStorePath), keyStorePasswordArray);
            } catch (FileNotFoundException e) {
                return null;
            }

            chain = keyStore.getCertificateChain(alias);
        }

        return chain;
    }




    public List<CertificateDTO> getAllCertificates(String type, String keyStorePassword)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        String keyStorePath = "keystores/" + type.toLowerCase() + ".p12";
        char[] keyStorePasswordArray = keyStorePassword.toCharArray();

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try {
            keyStore.load(new FileInputStream(keyStorePath), keyStorePasswordArray);
        } catch (FileNotFoundException e) {
            return null;
        }

        Enumeration<String> aliases = keyStore.aliases();
        List<String> list = Collections.list(aliases);
        List<CertificateDTO> certs = new ArrayList<>();

        for(String alias : list){
            Certificate cert = readCertificate(keyStorePath, keyStorePassword, alias);

            CertificateDTO dto = new CertificateDTO();
            dto.setAlias(alias);

            JcaX509CertificateHolder certHolder = new JcaX509CertificateHolder((X509Certificate) cert);
            dto.setSerialNumber(certHolder.getSerialNumber().toString());
            //---------------------------
//            JcaX509CertificateHolder certEKU = new JcaX509CertificateHolder((X509Certificate) cert);
//            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
//            X509Certificate cer = certConverter.getCertificate(certEKU);
//            System.out.println("****------->>>>"+cer.getExtendedKeyUsage());
            //*****************************************************************
            ExtendedKeyUsageDTO ekuDTO = new ExtendedKeyUsageDTO();
            X509Certificate xcert =  (X509Certificate) cert;
            System.out.println("****"+xcert);
            if(xcert == null){
                System.out.println("null je");
            }else{
                System.out.println("ima necega");
            }
            List<String> g = xcert.getExtendedKeyUsage();
            final boolean[] k = xcert.getKeyUsage();
            System.out.println("----"+g);
            System.out.println("++++"+k);
            for(String s : g){
                if(s.equals("1.3.6.1.5.5.7.3.1")){
                    ekuDTO.setServerAuth(true);
                }
                if(s.equals("1.3.6.1.5.5.7.3.2")){
                    ekuDTO.setClientAuth(true);
                }
                if(s.equals("1.3.6.1.5.5.7.3.1.3")){
                    ekuDTO.setCodeSigning(true);
                }
                if(s.equals("1.3.6.1.5.5.7.3.4")){
                    ekuDTO.setEmailProtection(true);
                }
                if(s.equals("1.3.6.1.5.5.7.3.8")){
                    ekuDTO.setTimeStamping(true);
                }
                if(s.equals("1.3.6.1.5.5.7.3.9")){
                    ekuDTO.setOcspSigning(true);
                }
            }
            dto.setExtendedKeyUsageDTO(ekuDTO);
            //**********************************************
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

        return certs;
    }


    public void downloadCertificate(CertificateDTO dto) throws CertificateException, IOException {


        String keyStorePath = "keystores/" + dto.getType() + ".p12";
        Certificate certificate = readCertificate(keyStorePath, dto.getKeyStorePassword(), dto.getAlias());

        FileOutputStream os = new FileOutputStream("certificates/" + dto.getType() + "_" + dto.getAlias() + ".p12");
        os.write("---------------BEGIN CERTIFICATE---------------\n".getBytes("US-ASCII"));
        os.write(Base64.encodeBase64(certificate.getEncoded(), true));
        os.write("---------------END CERTIFICATE---------------\n".getBytes("US-ASCII"));
        os.close();
    }


}