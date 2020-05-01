package service;

import dto.CertificateDTO;
import dto.DataDTO;
import dto.ExtendedKeyUsageDTO;
import model.CertificateSummary;
import model.IssuerData;
import model.SubjectData;


import org.bouncycastle.asn1.ASN1Encodable;
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
import repository.CertificateSummaryRepository;

import javax.net.ssl.X509ExtendedKeyManager;

@Service
public class CertificateService {


    @Autowired
    private KeyStoreService keyStoreService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private CertificateSummaryRepository certificateSummaryRepository;


    public void generateCertificate(CertificateDTO dto) {
        try {
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256withECDSA");
            builder = builder.setProvider("BC");

            addBouncyCastleAsSecurityProvider();

            IssuerData issuerData = generateIssuerData(dto);
            SubjectData subjectData = generateSubjectData(dto);

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


            certGen.addExtension(Extension.subjectKeyIdentifier, false,
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

            Certificate[] chain = keyStoreService.getCertificateChain(dto.getKeyStorePassword(), dto.getIssuerAlias());
            if(validationService.validateCertificateChain(chain)){

                CertificateSummary certificateSummary = new CertificateSummary();
                certificateSummary.setAlias(dto.getAlias());
                certificateSummary.setIssuerAlias(dto.getIssuerAlias());
                certificateSummary.setSerialNumber(new BigInteger(subjectData.getSerialNumber()).toString());
                certificateSummaryRepository.save(certificateSummary);

                keyStoreService.saveCertificate(dto.getKeyPassword(), dto.getAlias(),  dto.getKeyStorePassword(), subjectData.getPrivateKey(), cert);
                System.out.println("-------------------------------------------"+cert+"-------------------------------");
            }

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

        IssuerData issuerData = keyStoreService.readIssuerDataFromStore(dto.getIssuerAlias(), dto.getKeyStorePassword(), dto.getKeyPassword());

        return issuerData;
    }

    //TODO podeliti na user vs system
    private SubjectData generateSubjectData(CertificateDTO dto) {
        try {

            DataDTO data = dto.getSubjectData();
            KeyPair keyPairSubject = generateKeyPair();

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

            SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            Calendar cal = Calendar.getInstance();

            Date notBefore;
            Date notAfter;

            if(!dto.getSubjectData().getCommonName().equals(dto.getIssuerData().getCommonName())){
                cal.add(Calendar.HOUR, -8);
                Date eightHoursEarlier = cal.getTime();
                notBefore = sdf.parse(eightHoursEarlier.toString());
                notAfter = sdf.parse(dto.getNotAfter());
            } else {
                cal.add(Calendar.HOUR, -8);
                Date eightHoursEarlier = cal.getTime();
                notBefore = sdf.parse(eightHoursEarlier.toString());

                cal.add(Calendar.YEAR, 2);
                Date twoYearsLater = cal.getTime();
                notAfter = sdf.parse(twoYearsLater.toString());
            }

            return new SubjectData(keyPairSubject.getPublic(), builder.build(), serialNumber, notBefore, notAfter, keyPairSubject.getPrivate());

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
            OperatorCreationException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, IOException, ParseException {

        addBouncyCastleAsSecurityProvider();

        SubjectData subjectData = generateSubjectData(dto);
        IssuerData issuerData = generateIssuerDataRoot(subjectData.getPrivateKey(), subjectData.getPublicKey(), dto.getIssuerData());

        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256withECDSA");
        builder = builder.setProvider("BC");

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
        certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));


        ExtendedKeyUsageDTO ekuDTO = dto.getExtendedKeyUsageDTO();
        if(ekuDTO.isServerAuth().equals("1.3.6.1.5.5.7.3.1"))
            System.out.println("CertificateService-----------> server");
        if(ekuDTO.isClientAuth().equals("1.3.6.1.5.5.7.3.2"))
            System.out.println("CertificateService-----------> client");
        if(ekuDTO.isCodeSigning().equals("1.3.6.1.5.5.7.3.1.3"))
            System.out.println("CertificateService-----------> code");
        if(ekuDTO.isEmailProtection().equals("1.3.6.1.5.5.7.3.4"))
            System.out.println("CertificateService-----------> email");
        if(ekuDTO.isEmailProtection().equals("1.3.6.1.5.5.7.3.8"))
            System.out.println("CertificateService-----------> time");
        if(ekuDTO.isEmailProtection().equals("1.3.6.1.5.5.7.3.9"))
            System.out.println("CertificateService-----------> ocsp");


        if(dto.getExtendedKeyUsageDTO().isExtendedKeyUsage()){
            Collection<ASN1ObjectIdentifier> usages = getOids(dto.getExtendedKeyUsageDTO());
            System.out.println("usages.size()"+usages.size());
            certGen.addExtension(Extension.extendedKeyUsage, true, createExtendedUsage(usages));
        }


        certGen.addExtension(Extension.authorityKeyIdentifier, false,
                new AuthorityKeyIdentifier(issuerData.getPublicKey().getEncoded()));

        certGen.addExtension(Extension.subjectKeyIdentifier, false,
                new SubjectKeyIdentifier(subjectData.getPublicKey().getEncoded()));

        X509CertificateHolder certHolder = certGen.build(contentSigner);

        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
        certConverter = certConverter.setProvider("BC");
        X509Certificate cert = certConverter.getCertificate(certHolder);

        CertificateSummary certificateSummary = new CertificateSummary();
        certificateSummary.setAlias(dto.getAlias());
        certificateSummary.setIssuerAlias(dto.getAlias());
        certificateSummary.setSerialNumber(new BigInteger(subjectData.getSerialNumber()).toString());
        certificateSummary.setRoot(true);
        certificateSummaryRepository.save(certificateSummary);

        System.out.println("-------------------------------------------"+cert+"-------------------------------");
        keyStoreService.saveCertificate(dto.getKeyPassword(), dto.getAlias(),  dto.getKeyStorePassword(), subjectData.getPrivateKey(), cert);

    }

    public void addBouncyCastleAsSecurityProvider() {
        Security.addProvider(new BouncyCastleProvider());
    }



    private static ExtendedKeyUsage createExtendedUsage(Collection<ASN1ObjectIdentifier> usages) {

        List<ASN1ObjectIdentifier> list = new ArrayList<>(usages);
        List<ASN1ObjectIdentifier> sortedUsages = sortOidList(list);
        KeyPurposeId[] kps = new KeyPurposeId[sortedUsages.size()];

        int idx = 0;
        for (ASN1ObjectIdentifier oid : sortedUsages) {
            kps[idx++] = KeyPurposeId.getInstance(oid);
        }

        return new ExtendedKeyUsage(kps);
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
