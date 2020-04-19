package service;

import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.ocsp.*;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.provider.certpath.OCSP;

import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

@Service
public class RevokationService {

    @Autowired
    KeyStoreService keyStoreService;

    @Autowired
    ValidationService validationService;

     public OCSPResp generateOCSPResponse(Certificate subject, Certificate issuer,
                                                 CertificateStatus status, PrivateKey issuerPrivateKey, X509CertificateHolder[] chain) throws CertificateException {
        try {
            X509Certificate subjectCert = (X509Certificate) subject;
            X509Certificate issuerCert = (X509Certificate) issuer;

            X509CertificateHolder issuerCertHolder = new JcaX509CertificateHolder(issuerCert);

            DigestCalculatorProvider digCalcProv = new BcDigestCalculatorProvider();
            BasicOCSPRespBuilder basicBuilder = new BasicOCSPRespBuilder(SubjectPublicKeyInfo.getInstance(issuerCert.getPublicKey().getEncoded()),
                    digCalcProv.get(CertificateID.HASH_SHA1));

            CertificateID certId = new CertificateID(digCalcProv.get(CertificateID.HASH_SHA1), issuerCertHolder, subjectCert.getSerialNumber());

            basicBuilder.addResponse(certId, status);

            validationService.validateCertificate(issuerCert,subjectCert.getPublicKey());
            BasicOCSPResp resp = basicBuilder.build(new JcaContentSignerBuilder("SHA256withECDSA").build(issuerPrivateKey), chain, new Date());



            OCSPRespBuilder builder = new OCSPRespBuilder();
            OCSPResp ocspResp = builder.build(OCSPRespBuilder.SUCCESSFUL, resp);
System.out.println(subjectCert+"****************************************************");
            return ocspResp;

        } catch (Exception e) {
            throw new CertificateException("cannot generate OCSP response", e);
        }
    }



    public byte[] getOCSPResponseForGood(Certificate subject, Certificate issuer, PrivateKey issuerPrivateKey, X509CertificateHolder[] chain)
            throws CertificateException {
        try {
            return generateOCSPResponse(subject, issuer, CertificateStatus.GOOD, issuerPrivateKey, chain).getEncoded();
        } catch (IOException e) {
            throw new CertificateException(e);
        }
    }



    public byte[] getOCSPResponseForRevoked(Certificate subject, Certificate issuer, PrivateKey issuerPrivateKey, X509CertificateHolder[] chain)
            throws CertificateException {
        try {
            return generateOCSPResponse(subject, issuer, new RevokedStatus(new Date(), CRLReason.keyCompromise), issuerPrivateKey, chain).getEncoded();
        } catch (IOException e) {
            throw new CertificateException(e);
        }
    }


}
