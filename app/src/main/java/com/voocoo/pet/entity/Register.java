package com.voocoo.pet.entity;

public class Register {
    private String token;
    private String isBind;
    private String iotDn;
    private String iotDs;
    private String iotId;
    private String iotPk;
    private String iotPs;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIotPs() {
        return iotPs;
    }

    public void setIotPs(String iotPs) {
        this.iotPs = iotPs;
    }

    public String getIotDn() {
        return iotDn;
    }

    public void setIotDn(String iotDn) {
        this.iotDn = iotDn;
    }

    public String getIotDs() {
        return iotDs;
    }

    public void setIotDs(String iotDs) {
        this.iotDs = iotDs;
    }

    public String getIotId() {
        return iotId;
    }

    public void setIotId(String iotId) {
        this.iotId = iotId;
    }

    public String getIotPk() {
        return iotPk;
    }

    public void setIotPk(String iotPk) {
        this.iotPk = iotPk;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIsBind() {
        return isBind;
    }

    public void setIsBind(String isBind) {
        this.isBind = isBind;
    }
}
