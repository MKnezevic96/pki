package dto;

import org.bouncycastle.asn1.x509.KeyUsage;

public class KeyUsageDTO {

    private boolean digitalSignature;
    private boolean nonRepudiation;
    private boolean keyEncipherment;
    private boolean dataEncipherment;
    private boolean keyAgreement;
    private boolean keyCertSign;
    private boolean cRLSign;
    private boolean encihperOnly;
    private boolean decipherOnly; //ova dva smeju biti true samo ako je ki agriment na true

    private boolean keyUsage;


    public KeyUsageDTO(){
        this.digitalSignature = false;
        this.nonRepudiation = false;
        this.keyEncipherment = false;
        this.dataEncipherment = false;
        this.keyAgreement = false;
        this.keyCertSign = false;
        this.cRLSign = false;
        this.encihperOnly = false;
        this.decipherOnly = false;
        this.keyUsage = false;
    }


    public KeyUsageDTO(boolean digitalSignature, boolean nonRepudiation, boolean keyEncipherment, boolean dataEncipherment, boolean keyAgreement, boolean keyCertSign, boolean cRLSign, boolean encihperOnly, boolean decipherOnly, boolean keyUsage) {
        this.digitalSignature = digitalSignature;
        this.nonRepudiation = nonRepudiation;
        this.keyEncipherment = keyEncipherment;
        this.dataEncipherment = dataEncipherment;
        this.keyAgreement = keyAgreement;
        this.keyCertSign = keyCertSign;
        this.cRLSign = cRLSign;
        this.encihperOnly = encihperOnly;
        this.decipherOnly = decipherOnly;
        this.keyUsage = keyUsage;
    }

    public int isDigitalSignature() {
        if(digitalSignature){
            return KeyUsage.digitalSignature;
        } else {
            return 0;
        }
    }

    public void setDigitalSignature(boolean digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

    public int isNonRepudiation() {
        if(nonRepudiation){
            return KeyUsage.nonRepudiation;
        } else {
            return 0;
        }
    }

    public void setNonRepudiation(boolean nonRepudiation) {
        this.nonRepudiation = nonRepudiation;
    }

    public int isKeyEncipherment() {
        if(keyEncipherment){
            return KeyUsage.keyEncipherment;
        } else {
            return 0;
        }
    }

    public void setKeyEncipherment(boolean keyEncipherment) {
        this.keyEncipherment = keyEncipherment;
    }

    public int isDataEncipherment() {
        if(dataEncipherment){
            return KeyUsage.dataEncipherment;
        } else {
            return 0;
        }
    }

    public void setDataEncipherment(boolean dataEncipherment) {
        this.dataEncipherment = dataEncipherment;
    }

    public int isKeyAgreement() {
        if(keyAgreement){
            return KeyUsage.keyAgreement;
        } else {
            return 0;
        }
    }

    public void setKeyAgreement(boolean keyAgreement) {
        this.keyAgreement = keyAgreement;
    }

    public void setKeyCertSign(boolean keyCertSign) {
        this.keyCertSign = keyCertSign;
    }

    public void setcRLSign(boolean cRLSign) {
        this.cRLSign = cRLSign;
    }

    public void setEncihperOnly(boolean encihperOnly) {
        this.encihperOnly = encihperOnly;
    }

    public void setDecipherOnly(boolean decipherOnly) {
        this.decipherOnly = decipherOnly;
    }

    public int isKeyCertSign() {
        if(keyCertSign){
            return KeyUsage.keyCertSign;
        } else {
            return 0;
        }
    }



    public int iscRLSign() {
        if(cRLSign){
            return KeyUsage.cRLSign;
        } else {
            return 0;
        }
    }


    public int isEncihperOnly() {
        if(encihperOnly){
            return KeyUsage.encipherOnly;
        } else {
            return 0;
        }
    }


    public int isDecipherOnly() {
        if(decipherOnly){
            return KeyUsage.decipherOnly;
        } else {
            return 0;
        }
    }

    public boolean isKeyUsage() {
        return keyUsage;
    }

    public void setKeyUsage(boolean keyUsage) {
        this.keyUsage = keyUsage;
    }

    public boolean getDecipherOnly(){
        return decipherOnly;
    }

    public boolean getEncihperOnly(){
        return encihperOnly;
    }

    public boolean getCRLSign(){
        return cRLSign;
    }

    public boolean getKeyCertSign(){
        return keyCertSign;
    }

    public boolean getKeyAgreement(){
        return keyAgreement;
    }

    public boolean getDataEncipherment(){
        return dataEncipherment;
    }

    public boolean getKeyEncipherment(){
        return keyEncipherment;
    }

    public boolean getNonRepudiation(){
        return nonRepudiation;
    }

    public boolean getDigitalSignature(){
        return digitalSignature;
    }


}
