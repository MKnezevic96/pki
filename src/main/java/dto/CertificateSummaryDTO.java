package dto;

public class CertificateSummaryDTO {

    private long id;
    private String serialNumber;
    private String alias;
    private String issuerAlias;
    private boolean isRoot = false;
    private boolean isCa = false;
    private boolean isRevoked = false;
    private String revocationDate;
    private String revocationReason;


    public CertificateSummaryDTO(){

    }

    public CertificateSummaryDTO(long id, String serialNumber, String alias, String issuerAlias, boolean isRoot, boolean isCa, boolean isRevoked, String revocationDate, String revocationReason) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.alias = alias;
        this.isRoot = isRoot;
        this.isCa = isCa;
        this.isRevoked = isRevoked;
        this.revocationDate = revocationDate;
        this.revocationReason = revocationReason;
        this.issuerAlias = issuerAlias;
    }

    public boolean isRevoked() {
        return isRevoked;
    }

    public void setRevoked(boolean revoked) {
        isRevoked = revoked;
    }

    public String getRevocationReason() {
        return revocationReason;
    }

    public void setRevocationReason(String revocationReason) {
        this.revocationReason = revocationReason;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public boolean isCa() {
        return isCa;
    }

    public void setCa(boolean ca) {
        isCa = ca;
    }

    public String getRevocationDate() {
        return revocationDate;
    }

    public void setRevocationDate(String revocationDate) {
        this.revocationDate = revocationDate;
    }

    public String getIssuerAlias() {
        return issuerAlias;
    }

    public void setIssuerAlias(String issuerAlias) {
        this.issuerAlias = issuerAlias;
    }
}
