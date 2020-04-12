package dto;

public class ExtendedKeyUsageDTO {

    private boolean serverAuth;
    private boolean clientAuth;
    private boolean codeSigning;
    private boolean emailProtection;
    private boolean timeStamping;
    private boolean ocspSigning;

    private boolean extendedKeyUsage;


    public ExtendedKeyUsageDTO(){
        this.serverAuth = false;
        this.clientAuth = false;
        this.codeSigning = false;
        this.emailProtection = false;
        this.timeStamping = false;
        this.ocspSigning = false;
        this.extendedKeyUsage = false;
    }

    public ExtendedKeyUsageDTO(boolean serverAuth, boolean clientAuth, boolean codeSigning, boolean emailProtection, boolean timeStamping, boolean ocspSigning, boolean extendedKeyUsage) {
        this.serverAuth = serverAuth;
        this.clientAuth = clientAuth;
        this.codeSigning = codeSigning;
        this.emailProtection = emailProtection;
        this.timeStamping = timeStamping;
        this.ocspSigning = ocspSigning;
        this.extendedKeyUsage = extendedKeyUsage;
    }

    public String isServerAuth() {
        if(serverAuth){
            return "1.3.6.1.5.5.7.3.1";
        } else {
            return "";
        }
    }

    public void setServerAuth(boolean serverAuth) {
        this.serverAuth = serverAuth;
    }

    public String isClientAuth() {
        if(clientAuth){
            return "1.3.6.1.5.5.7.3.2";
        } else {
            return "";
        }
    }

    public void setClientAuth(boolean clientAuth) {
        this.clientAuth = clientAuth;
    }

    public String isCodeSigning() {
        if(codeSigning){
            return "1.3.6.1.5.5.7.3.1.3";
        } else {
            return "";
        }
    }

    public void setCodeSigning(boolean codeSigning) {
        this.codeSigning = codeSigning;
    }

    public String  isEmailProtection() {
        if(emailProtection){
            return "1.3.6.1.5.5.7.3.4";
        } else {
            return "";
        }
    }

    public void setEmailProtection(boolean emailProtection) {
        this.emailProtection = emailProtection;
    }

    public String isTimeStamping() {
        if(timeStamping){
            return "1.3.6.1.5.5.7.3.8";
        } else {
            return "";
        }
    }

    public void setTimeStamping(boolean timeStamping) {
        this.timeStamping = timeStamping;
    }

    public String isOcspSigning() {
        if(ocspSigning){
            return "1.3.6.1.5.5.7.3.9";
        } else {
            return "";
        }
    }

    public void setOcspSigning(boolean ocspSigning) {
        this.ocspSigning = ocspSigning;
    }


    public boolean isExtendedKeyUsage() {
        return extendedKeyUsage;
    }

    public void setExtendedKeyUsage(boolean extendedKeyUsage) {
        this.extendedKeyUsage = extendedKeyUsage;
    }

    public boolean getServerAuth(){
        return serverAuth;
    }

    public boolean getClientAuth(){
        return clientAuth;
    }

    public boolean getOcspSigning(){
        return ocspSigning;
    }

    public boolean getEmailProtection(){
        return emailProtection;
    }

    public boolean getTimeStamping(){
        return timeStamping;
    }

    public boolean getCodeSigning(){
        return codeSigning;
    }
}
