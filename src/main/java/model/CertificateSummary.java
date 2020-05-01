package model;


import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity(name = "certSummary")
@Table
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class CertificateSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "alias", nullable = false, unique = true)
    private String alias;

    @Column(name = "issuerAlias", nullable = false)
    private String issuerAlias;

    @Column(name = "serialNumber", nullable = false, unique = true)
    private String serialNumber;

    @Column(name = "revoked", nullable = false)
    private boolean isRevoked;

    @Column(name = "root", nullable = false)
    private boolean isRoot;

    @Column(name = "ca")
    private boolean isCa;

//    @Column(name = "revocationReason")
//    @Enumerated(EnumType.ORDINAL)
//    private RevocationReason revocationReason;

    @Column(name = "revocationDate", nullable = true)
    private Date revocationDate;


    public CertificateSummary(){

    }

    public CertificateSummary(Long id, String alias, String issuerAlias, String serialNumber, boolean isRevoked, boolean isRoot, boolean isCa, Date revocationDate) {
        this.id = id;
        this.alias = alias;
        this.serialNumber = serialNumber;
        this.isRevoked = isRevoked;
        this.isRoot = isRoot;
        this.isCa = isCa;
        this.revocationDate = revocationDate;
        this.issuerAlias = issuerAlias;
    }

    public boolean isRevoked() {
        return isRevoked;
    }

    public void setRevoked(boolean revoked) {
        isRevoked = revoked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIssuerAlias() {
        return issuerAlias;
    }

    public void setIssuerAlias(String issuerAlias) {
        this.issuerAlias = issuerAlias;
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

    public Date getRevocationDate() {
        return revocationDate;
    }

    public void setRevocationDate(Date revocationDate) {
        this.revocationDate = revocationDate;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

}

