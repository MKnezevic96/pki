package service;


import model.CertificateSummary;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.*;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.CertificateSummaryRepository;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Service
public class OCSPService {

    @Autowired
    private CertificateSummaryRepository certificateSummaryRepository;

    @Autowired
    private KeyStoreService keyStoreService;


    public OCSPReq generateOCSPRequest(X509CertificateHolder issuerCert, String serialNumber) throws OperatorCreationException, CertificateEncodingException, OCSPException, IOException, KeyStoreException {
        CertificateSummary certSum = certificateSummaryRepository.findBySerialNumber(serialNumber);

        if (certSum == null) {
            return null;
        }

        OCSPReq ocspReq;

        BcDigestCalculatorProvider util = new BcDigestCalculatorProvider();

        CertificateID id = new CertificateID(util.get(CertificateID.HASH_SHA1), new X509CertificateHolder(issuerCert.getEncoded()), new BigInteger(serialNumber));
        OCSPReqBuilder ocspGen = new OCSPReqBuilder();

        ocspGen.addRequest(id);

        BigInteger nonce = BigInteger.valueOf(System.currentTimeMillis());
        Extension ext = new Extension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, true, new DEROctetString(nonce.toByteArray()));
        ocspGen.setRequestExtensions(new Extensions(new Extension[] { ext }));

        ocspReq = ocspGen.build();

        return ocspReq;
    }



    public OCSPResp doProcessOCSPRequest(OCSPReq ocspReq, X509CertificateHolder subjectCert, X509CertificateHolder issuerCert, PrivateKey issuerPrivateKey, X509CertificateHolder[] chainHolder)
            throws OCSPException, OperatorCreationException {

        DigestCalculatorProvider digestCalculatorProvider = new JcaDigestCalculatorProviderBuilder().setProvider("BC").build();
        RespID responderID = new RespID(subjectCert.getSubjectPublicKeyInfo(),
                digestCalculatorProvider.get(new DefaultDigestAlgorithmIdentifierFinder().find("SHA-1")));

        BasicOCSPRespBuilder responseBuilder = new BasicOCSPRespBuilder(responderID);

        checkForValidRequest(ocspReq);


        Collection<Extension> responseExtensions = new ArrayList<>();
        Extension nonceExtension = ocspReq.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);
        if (nonceExtension != null) {
            responseExtensions.add(nonceExtension);
        }

        Extension[] extensions = responseExtensions.toArray(new Extension[responseExtensions.size()]);
        responseBuilder.setResponseExtensions(new Extensions(extensions));


        Req[] requests = ocspReq.getRequestList();
        for (Req request : requests) {
            addResponse(responseBuilder, request, issuerCert);
        }
        return buildResponse(responseBuilder, issuerPrivateKey, chainHolder);
    }


    private void checkForValidRequest(OCSPReq ocspReq) throws OCSPException {
        if (ocspReq == null) {
            throw new BadRequestException("Could not find a request in the payload!",
                    Response.status(Response.Status.BAD_REQUEST).entity(
                            new OCSPRespBuilder().build(OCSPRespBuilder.MALFORMED_REQUEST, null)
                    ).build()
            );
        }
        // Check signature if present
        if (ocspReq.isSigned() && !isSignatureValid(ocspReq)) {
            throw new BadRequestException("Your signature was invalid!",
                    Response.status(Response.Status.BAD_REQUEST).entity(
                            new OCSPRespBuilder().build(OCSPRespBuilder.MALFORMED_REQUEST, null)
                    ).build()
            );
        }
    }


    private boolean isSignatureValid(OCSPReq ocspReq) throws OCSPException {
        try {
            return ocspReq.isSignatureValid(
                    new JcaContentVerifierProviderBuilder() // Can we reuse this builder?
                            .setProvider("BC")
                            .build(ocspReq.getCerts()[0])
            );
        } catch (CertificateException | OperatorCreationException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void addResponse(BasicOCSPRespBuilder responseBuilder, Req request, X509CertificateHolder issuerCert) throws OCSPException, OperatorCreationException {
        CertificateID certificateID = request.getCertID();

        Extensions extensions = new Extensions(new Extension[]{});
        Extensions requestExtensions = request.getSingleRequestExtensions();
        if (requestExtensions != null) {
            Extension nonceExtension = requestExtensions.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);
            if (nonceExtension != null) {
                extensions = new Extensions(nonceExtension);
            }
        }

        boolean matchesIssuer = certificateID.matchesIssuer(issuerCert, new JcaDigestCalculatorProviderBuilder().setProvider("BC").build());

        if (!matchesIssuer) {
            responseBuilder.addResponse(request.getCertID(), new UnknownStatus(), new Date(), extensions);

        } else {
            CertificateSummary certificateSummary = certificateSummaryRepository.findBySerialNumber(certificateID.getSerialNumber().toString());
            responseBuilder.addResponse(request.getCertID(), getOCSPCertificateStatus(certificateSummary), new Date(), extensions);
        }
    }



   private CertificateStatus getOCSPCertificateStatus(CertificateSummary certSum){

        if(certSum == null){
            return new UnknownStatus();
        } else if (!certSum.isRevoked()){
            return CertificateStatus.GOOD;
        } else {
            return new RevokedStatus(new Date(), CRLReason.keyCompromise);
        }

   }


    private OCSPResp buildResponse(BasicOCSPRespBuilder responseBuilder, PrivateKey issuerPrivateKey, X509CertificateHolder[] chain)
            throws OCSPException, OperatorCreationException {
        BasicOCSPResp basicOCSPResponse = responseBuilder.build(new JcaContentSignerBuilder("SHA256withECDSA")
                .setProvider("BC").build(issuerPrivateKey), chain, new Date());
        return new OCSPRespBuilder().build(OCSPRespBuilder.SUCCESSFUL, basicOCSPResponse);
    }



    public String doProcessOCSPResponse(OCSPResp response) throws OCSPException {
        BasicOCSPResp basicResponse = (BasicOCSPResp) response.getResponseObject();
        SingleResp[] responses = (basicResponse == null) ? null : basicResponse.getResponses();
        String retVal = "Some error has occurred!";

        if (responses != null) {
            for (SingleResp singleResponse: responses) {
                CertificateStatus status = singleResponse.getCertStatus();
                retVal = convertStatusCode(status);
            }
        }

        return retVal;
    }

    private String convertStatusCode(CertificateStatus status) {
        String retVal;

        if (status == CertificateStatus.GOOD) {
            retVal = "GOOD";
        } else if (status instanceof RevokedStatus) {
            retVal = "REVOKED";
        }  else {
            retVal = "UNKNOWN";
        }

        return retVal;
    }


}
