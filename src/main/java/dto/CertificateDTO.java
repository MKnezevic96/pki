package dto;

import java.util.Date;

public class CertificateDTO {

    private String serialNumber;
    private DataDTO issuerData;
    private DataDTO subjectData;
    private String alias;
    private String keyPassword;
    private String keyStorePassword;
    private String notBefore;
    private String notAfter;
    private boolean basicConstrains;
    private ExtendedKeyUsageDTO extendedKeyUsageDTO;
    private KeyUsageDTO keyUsageDTO;
    private boolean authorityKeyIdentifier;
    private boolean subjectKeyIdentifier;
    private String type;

    private String issuerAlias;


    public CertificateDTO(){

    }

    public CertificateDTO(String serialNumber, DataDTO issuerData, DataDTO subjectData, String alias, String keyPassword,
                          String keyStorePassword, String notBefore, String notAfter, boolean basicConstrains,
                          ExtendedKeyUsageDTO extendedKeyUsageDTO, KeyUsageDTO keyUsageDTO, boolean authorityKeyIdentifier, boolean subjectKeyIdentifier,
                          String issuerAlias, String type) {
        this.serialNumber = serialNumber;

        this.alias = alias;
        this.keyPassword = keyPassword;
        this.keyStorePassword = keyStorePassword;
        this.notBefore = notBefore;
        this.notAfter = notAfter;
        this.basicConstrains = basicConstrains;
        this.authorityKeyIdentifier = authorityKeyIdentifier;
        this.subjectKeyIdentifier = subjectKeyIdentifier;
        this.issuerAlias = issuerAlias;
        this.type = type;

        KeyUsageDTO keyUsage = new KeyUsageDTO();
        keyUsage.setKeyUsage(keyUsageDTO.isKeyUsage());
        keyUsage.setcRLSign(keyUsageDTO.getCRLSign());
        keyUsage.setDataEncipherment(keyUsageDTO.getDataEncipherment());
        keyUsage.setDecipherOnly(keyUsageDTO.getDecipherOnly());
        keyUsage.setEncihperOnly(keyUsageDTO.getEncihperOnly());
        keyUsage.setDigitalSignature(keyUsageDTO.getDigitalSignature());
        keyUsage.setKeyCertSign(keyUsageDTO.getKeyCertSign());
        keyUsage.setNonRepudiation(keyUsageDTO.getNonRepudiation());
        keyUsage.setKeyEncipherment(keyUsageDTO.getKeyEncipherment());
        this.keyUsageDTO = keyUsage;

        ExtendedKeyUsageDTO extendedKeyUsage = new ExtendedKeyUsageDTO();
        extendedKeyUsage.setExtendedKeyUsage(extendedKeyUsageDTO.isExtendedKeyUsage());
        extendedKeyUsage.setServerAuth(extendedKeyUsageDTO.getServerAuth());
        extendedKeyUsage.setClientAuth(extendedKeyUsageDTO.getClientAuth());
        extendedKeyUsage.setCodeSigning(extendedKeyUsageDTO.getCodeSigning());
        extendedKeyUsage.setEmailProtection(extendedKeyUsageDTO.getEmailProtection());
        extendedKeyUsage.setOcspSigning(extendedKeyUsageDTO.getOcspSigning());
        extendedKeyUsage.setTimeStamping(extendedKeyUsageDTO.getTimeStamping());
        this.extendedKeyUsageDTO = extendedKeyUsage;

        DataDTO issuerDataDto = new DataDTO();
        issuerDataDto.setCommonName(issuerData.getCommonName());
        issuerDataDto.setCountryName(issuerData.getCountryName());
        issuerDataDto.setEmail(issuerData.getEmail());
        issuerDataDto.setGivenName(issuerData.getGivenName());
        issuerDataDto.setLocalityName(issuerData.getLocalityName());
        issuerDataDto.setOrganisationName(issuerData.getOrganisationName());
        issuerDataDto.setOrganisationUnitName(issuerData.getOrganisationUnitName());
        this.issuerData = issuerDataDto;

        DataDTO subjectDataDto = new DataDTO();
        subjectDataDto.setCommonName(subjectData.getCommonName());
        subjectDataDto.setCountryName(subjectData.getCountryName());
        subjectDataDto.setEmail(subjectData.getEmail());
        subjectDataDto.setGivenName(subjectData.getGivenName());
        subjectDataDto.setLocalityName(subjectData.getLocalityName());
        subjectDataDto.setOrganisationName(subjectData.getOrganisationName());
        subjectDataDto.setOrganisationUnitName(subjectData.getOrganisationUnitName());
        this.subjectData = subjectDataDto;
    }


    public void setIssuerData(DataDTO issuerData) {
        this.issuerData = issuerData;
    }

    public void setSubjectData(DataDTO subjectData) {
        this.subjectData = subjectData;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public DataDTO getIssuerData() {
        return issuerData;
    }

    public DataDTO getSubjectData() {
        return subjectData;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(String notBefore) {
        this.notBefore = notBefore;
    }

    public String getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(String notAfter) {
        this.notAfter = notAfter;
    }

    public boolean isBasicConstrains() {
        return basicConstrains;
    }

    public void setBasicConstrains(boolean basicConstrains) {
        this.basicConstrains = basicConstrains;
    }

    public ExtendedKeyUsageDTO getExtendedKeyUsageDTO() {
        return extendedKeyUsageDTO;
    }

    public void setExtendedKeyUsageDTO(ExtendedKeyUsageDTO extendedKeyUsageDTO) {
        this.extendedKeyUsageDTO = extendedKeyUsageDTO;
    }

    public KeyUsageDTO getKeyUsageDTO() {
        return keyUsageDTO;
    }

    public void setKeyUsageDTO(KeyUsageDTO keyUsageDTO) {
        this.keyUsageDTO = keyUsageDTO;
    }

    public String getKeyPassword() {
        return keyPassword;
    }

    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    public String isAuthorityKeyIdentifier() {
        if(authorityKeyIdentifier){
            return "2.5.29.35";
        } else {
            return "";
        }
    }

    public boolean getAuthorityKeyIdentifier() {
        return authorityKeyIdentifier;
    }

    public void setAuthorityKeyIdentifier(boolean authorityKeyIdentifier) {
        this.authorityKeyIdentifier = authorityKeyIdentifier;
    }

    public String isSubjectKeyIdentifier() {
        if(subjectKeyIdentifier){
            return "2.5.29.14";
        } else {
            return "";
        }
    }

    public boolean getSubjectKeyIdentifier() {
        return subjectKeyIdentifier;
    }

    public void setSubjectKeyIdentifier(boolean subjectKeyIdentifier) {
        this.subjectKeyIdentifier = subjectKeyIdentifier;
    }

    public String getIssuerAlias() {
        return issuerAlias;
    }

    public void setIssuerAlias(String issuerAlias) {
        this.issuerAlias = issuerAlias;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
