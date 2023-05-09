package com.voocoo.pet.entity;

public class AddDeviceEntity {


    /**
     * msg : 操作成功
     * deviceType : 1
     * code : 200
     * deviceId : 55
     */

    private String msg;
    private String deviceType;
    private int code;
    private int deviceId;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
}
