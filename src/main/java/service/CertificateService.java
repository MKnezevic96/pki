package service;

import dto.CertificateDTO;
import dto.DataDTO;
import dto.ExtendedKeyUsageDTO;
import model.IssuerData;
import model.SubjectData;


import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.X509KeyUsage;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;


import java.security.cert.Certificate;
import java.security.spec.ECGenParameterSpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

@Service
public class CertificateService {


    @Autowired
    private KeyStoreService keyStoreService;


    public void generateCertificate(CertificateDTO dto) {
        try {
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256withECDSA");
            builder = builder.setProvider("BC");

            //adding provider
            addBouncyCastleAsSecurityProvider();

            KeyPair keyPairIssuer = generateKeyPair();
            IssuerData issuerData = generateIssuerData(dto);
            SubjectData subjectData = generateSubjectData(dto.getSubjectData());

            ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());

            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
                    new BigInteger(subjectData.getSerialNumber()),
                    subjectData.getNotBefore(),
                    subjectData.getNotAfter(),
                    subjectData.getX500name(),
                    subjectData.getPublicKey());



            if(dto.getKeyUsageDTO().isKeyUsage()){
                X509KeyUsage keyuse = new X509KeyUsage(
                        dto.getKeyUsageDTO().isDigitalSignature() |
                                dto.getKeyUsageDTO().isNonRepudiation()   |
                                dto.getKeyUsageDTO().isKeyEncipherment()  |
                                dto.getKeyUsageDTO().isDataEncipherment() |
                                dto.getKeyUsageDTO().isKeyAgreement() |
                                dto.getKeyUsageDTO().isKeyCertSign() |
                                dto.getKeyUsageDTO().iscRLSign() |
                                dto.getKeyUsageDTO().isEncihperOnly() |
                                dto.getKeyUsageDTO().isDecipherOnly()
                );

                certGen.addExtension(Extension.keyUsage, true, keyuse);

            }


            if(dto.isBasicConstrains()){
                certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
            }

            certGen.addExtension(Extension.authorityKeyIdentifier, false,
                    new AuthorityKeyIdentifier(issuerData.getPublicKey().getEncoded()));


            certGen.addExtension(Extension.subjectKeyIdentifier, true,
                    new SubjectKeyIdentifier(subjectData.getPublicKey().getEncoded()));



            //TODO proveriti za ovu ekstenziju
            if(dto.getExtendedKeyUsageDTO().isExtendedKeyUsage()) {
                Collection<ASN1ObjectIdentifier> usages = getOids(dto.getExtendedKeyUsageDTO());
                certGen.addExtension(Extension.extendedKeyUsage, true, createExtendedUsage(usages));
            }

            X509CertificateHolder certHolder = certGen.build(contentSigner);


            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            X509Certificate cert = certConverter.getCertificate(certHolder);
            System.out.println("-------------------------------------------"+cert+"-------------------------------");
            keyStoreService.saveCertificate(dto.getKeyPassword(), dto.getAlias(),  dto.getKeyStorePassword(), keyPairIssuer.getPrivate(), cert);


        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

    }



    private IssuerData generateIssuerDataRoot(PrivateKey issuerKey, PublicKey publicKeyIssuer, DataDTO data) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, data.getCommonName());
//        builder.addRDN(BCStyle.SURNAME, data.getSurname());
//        builder.addRDN(BCStyle.GIVENNAME, data.getGivenName());
        builder.addRDN(BCStyle.O, data.getOrganisationName());
        builder.addRDN(BCStyle.OU, data.getOrganisationUnitName());
        builder.addRDN(BCStyle.C, data.getCountryName());
        builder.addRDN(BCStyle.E, data.getEmail());
        builder.addRDN(BCStyle.L, data.getLocalityName());
//        builder.addRDN(BCStyle.UID, data.getUid());

        return new IssuerData(builder.build(), issuerKey, publicKeyIssuer);
    }


    private IssuerData generateIssuerData(CertificateDTO dto) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, IOException {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, dto.getIssuerData().getCommonName());
//        builder.addRDN(BCStyle.SURNAME, data.getSurname());
//        builder.addRDN(BCStyle.GIVENNAME, data.getGivenName());
        builder.addRDN(BCStyle.O, dto.getIssuerData().getOrganisationName());
        builder.addRDN(BCStyle.OU, dto.getIssuerData().getOrganisationUnitName());
        builder.addRDN(BCStyle.C, dto.getIssuerData().getCountryName());
        builder.addRDN(BCStyle.E, dto.getIssuerData().getEmail());
        builder.addRDN(BCStyle.L, dto.getIssuerData().getLocalityName());
//        builder.addRDN(BCStyle.UID, data.getUid());

        IssuerData issuerData = keyStoreService.readIssuerDataFromStore(dto.getIssuerAlias(), dto.getKeyStorePassword(), dto.getKeyPassword());

        return issuerData;
    }

    //TODO podeliti na user vs system
    private SubjectData generateSubjectData(DataDTO data) {
        try {
            KeyPair keyPairSubject = generateKeyPair();

           // SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");

            SimpleDateFormat iso8601Formater = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
                    Locale.ENGLISH);

            Date startDate1 =  new Date(System.currentTimeMillis());
            Date endDate1 = new Date(System.currentTimeMillis() + 20 * 365 * 24 * 60 * 60 * 1000);

            Date startDate = iso8601Formater.parse(startDate1.toString());
            Date endDate = iso8601Formater.parse(endDate1.toString());

            byte[] serialNumber = getSerialNumber();

            X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
            builder.addRDN(BCStyle.CN, data.getCommonName());
  //          builder.addRDN(BCStyle.SURNAME, data.getSurname());
  //          builder.addRDN(BCStyle.GIVENNAME, data.getGivenName());
            builder.addRDN(BCStyle.O, data.getOrganisationName());
            builder.addRDN(BCStyle.OU, data.getOrganisationUnitName());
            builder.addRDN(BCStyle.C, data.getCountryName());
            builder.addRDN(BCStyle.E, data.getEmail());
            builder.addRDN(BCStyle.L, data.getLocalityName());
//            builder.addRDN(BCStyle.UID, data.getUid());


            return new SubjectData(keyPairSubject.getPublic(), builder.build(), serialNumber, startDate, endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", "SunEC");
            ECGenParameterSpec ecsp;
            ecsp = new ECGenParameterSpec("secp256k1");
            keyGen.initialize(ecsp);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }


    //TODO proveriti duzinu trajanja
    public void generateSelfSignedX509Certificate(CertificateDTO dto) throws CertificateException, IllegalStateException,
            OperatorCreationException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, IOException {
        //adding provider
        addBouncyCastleAsSecurityProvider();

        // generate a key pair
        KeyPair keyPair = generateKeyPair();
        IssuerData issuerData = generateIssuerDataRoot(keyPair.getPrivate(), keyPair.getPublic(), dto.getIssuerData());
        SubjectData subjectData = generateSubjectData(dto.getSubjectData());

        //2 years validation
        Calendar cal = Calendar.getInstance();
        Date notBefore = cal.getTime();
        cal.add(Calendar.YEAR, 2);
        Date notAfter = cal.getTime();


        // build a certificate generator
        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256withECDSA");
        builder = builder.setProvider("BC");

        ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());

        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
                new BigInteger(subjectData.getSerialNumber()),
                notBefore,
                notAfter,
                subjectData.getX500name(),
                subjectData.getPublicKey());

            certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));



        certGen.addExtension(Extension.authorityKeyIdentifier, false,
                new AuthorityKeyIdentifier(issuerData.getPublicKey().getEncoded()));

        certGen.addExtension(Extension.subjectKeyIdentifier, false,
                new SubjectKeyIdentifier(subjectData.getPublicKey().getEncoded()));

        X509CertificateHolder certHolder = certGen.build(contentSigner);

        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
        certConverter = certConverter.setProvider("BC");
        X509Certificate cert = certConverter.getCertificate(certHolder);

        keyStoreService.saveCertificate(dto.getKeyPassword(), dto.getAlias(),  dto.getKeyStorePassword(), keyPair.getPrivate(), cert);

    }

    public void addBouncyCastleAsSecurityProvider() {
        Security.addProvider(new BouncyCastleProvider());
    }


    // certificate reader from file, da li da ga smesta u listu ?

    public static final String BASE64_ENC_CERT_FILE = "./data/sertifikati.cer";
    public static final String BIN_ENC_CERT_FILE = "./data/sertifikatibin.cer";

    private void readFromBase64EncFile() {
        try {
            FileInputStream fis = new FileInputStream(BASE64_ENC_CERT_FILE);
            BufferedInputStream bis = new BufferedInputStream(fis);

            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            //Cita sertifikat po sertifikat
            //Svaki certifikat je izmedju
            //-----BEGIN CERTIFICATE-----,
            //i
            //-----END CERTIFICATE-----.
            while (bis.available() > 0) {
                Certificate cert = cf.generateCertificate(bis);
                System.out.println(cert.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    private static ExtendedKeyUsage createExtendedUsage(Collection<ASN1ObjectIdentifier> usages) {

        List<ASN1ObjectIdentifier> list = new ArrayList<>(usages);
        List<ASN1ObjectIdentifier> sortedUsages = sortOidList(list);
        KeyPurposeId[] kps = new KeyPurposeId[sortedUsages.size()];

        int idx = 0;
        for (ASN1ObjectIdentifier oid : sortedUsages) {
            kps[idx++] = KeyPurposeId.getInstance(oid);
        }

        return new ExtendedKeyUsage(kps[0]);
    }


    private static List<ASN1ObjectIdentifier> sortOidList(List<ASN1ObjectIdentifier> oids) {

        List<String> list = new ArrayList<>(oids.size());
        for (ASN1ObjectIdentifier m : oids) {
            list.add(m.getId());
        }
        Collections.sort(list);

        List<ASN1ObjectIdentifier> sorted = new ArrayList<>(oids.size());
        for (String m : list) {
            for (ASN1ObjectIdentifier n : oids) {
                if (m.equals(n.getId()) && !sorted.contains(n)) {
                    sorted.add(n);
                }
            }
        }
        return sorted;
    }

    private Collection<ASN1ObjectIdentifier> getOids(ExtendedKeyUsageDTO dto) {

        List<ASN1ObjectIdentifier> list = new ArrayList<>();

        if(dto.getServerAuth()) list.add(new ASN1ObjectIdentifier(dto.isServerAuth()));
        if(dto.getClientAuth()) list.add(new ASN1ObjectIdentifier(dto.isClientAuth()));
        if(dto.getCodeSigning()) list.add(new ASN1ObjectIdentifier(dto.isCodeSigning()));
        if(dto.getTimeStamping()) list.add(new ASN1ObjectIdentifier(dto.isTimeStamping()));
        if(dto.getEmailProtection()) list.add(new ASN1ObjectIdentifier(dto.isEmailProtection()));
        if(dto.getOcspSigning()) list.add(new ASN1ObjectIdentifier(dto.isOcspSigning()));

        return list;
    }

    private byte[] getSerialNumber() {
        SecureRandom random;
        try {
            random = SecureRandom.getInstance("Windows-PRNG");
        } catch (NoSuchAlgorithmException e) {
            random = new SecureRandom();
        }
        byte[] bytes = new byte[10];
        random.nextBytes(bytes);
        return bytes;
    }




}
