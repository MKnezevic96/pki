package dto;


import model.IssuerData;
import org.bouncycastle.asn1.x500.style.BCStyle;

public class DataDTO {

    private String countryName;
    private String stateName;
    private String localityName;
    private String organisationName;
    private String organisationUnitName;
    private String commonName;
    private String email;
    private String givenName;
    private String surname;
    private String uid;
    private String serialNumber;
    private String notAfter;

    public DataDTO(){

    }

    public DataDTO(String notAfter, String countryName, String stateName, String localityName, String organisationName, String organisationUnitName, String commonName, String email, String givenName, String surname, String uid, String serialNumber) {
        this.countryName = countryName;
        this.stateName = stateName;
        this.localityName = localityName;
        this.organisationName = organisationName;
        this.organisationUnitName = organisationUnitName;
        this.commonName = commonName;
        this.email = email;
        this.givenName = givenName;
        this.surname = surname;
        this.uid = uid;
        this.serialNumber = serialNumber;
        this.notAfter = notAfter;
    }

    public DataDTO(DataDTO data){
        this.countryName = data.getCountryName();
        this.stateName = data.getStateName();
        this.localityName = data.getLocalityName();
        this.organisationName = data.getOrganisationName();
        this.organisationUnitName = data.getOrganisationUnitName();
        this.commonName = data.getCommonName();
        this.email = data.getEmail();
        this.givenName = data.getGivenName();
        this.surname = data.getSurname();
        this.uid = data.getUid();
        this.serialNumber = data.getSerialNumber();
        this.notAfter = data.getNotAfter();
    }


    public DataDTO(IssuerData issuerData){
        this.commonName = issuerData.getX500name().getRDNs(BCStyle.CN).toString();
        this.countryName = issuerData.getX500name().getRDNs(BCStyle.C).toString();
        this.email = issuerData.getX500name().getRDNs(BCStyle.E).toString();
        this.localityName = issuerData.getX500name().getRDNs(BCStyle.L).toString();
        this.stateName = issuerData.getX500name().getRDNs(BCStyle.SN).toString();
        this.organisationName =  issuerData.getX500name().getRDNs(BCStyle.O).toString();
        this.organisationUnitName =  issuerData.getX500name().getRDNs(BCStyle.OU).toString();
        this.notAfter = issuerData.getNotAfter().toString();
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getOrganisationUnitName() {
        return organisationUnitName;
    }

    public void setOrganisationUnitName(String organisationUnitName) {
        this.organisationUnitName = organisationUnitName;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(String notAfter) {
        this.notAfter = notAfter;
    }
}
