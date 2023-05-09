package com.voocoo.pet.entity;

public class queryOTAbean {

    /**
     * searchValue : null
     * createBy : admin
     * createTime : 2023-04-18 13:59:31
     * updateBy : admin
     * updateTime : 2023-04-19 19:29:56
     * remark : 升级指令填写ota文件地址
     * params : {}
     * deviceVersionId : 3
     * deviceVersionType : 1
     * deviceVersionNumber : v1.0.1
     * deviceVersionDescribe : 修复一些bug
     * deviceVersionCommand : https://api.haihe.net.cn:1443/resources/images/cms/App.bin
     * status : 0
     * delFlag : 0
     * lastVersion : 1
     * currentVersion : 1.0.1
     */

    private Object searchValue;
    private String createBy;
    private String createTime;
    private String updateBy;
    private String updateTime;
    private String remark;
    private ParamsBean params;
    private int deviceVersionId;
    private String deviceVersionType;
    private String deviceVersionNumber;
    private String deviceVersionDescribe;
    private String deviceVersionCommand;
    private String status;
    private String delFlag;
    private int lastVersion;
    private String currentVersion;

    public Object getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(Object searchValue) {
        this.searchValue = searchValue;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public int getDeviceVersionId() {
        return deviceVersionId;
    }

    public void setDeviceVersionId(int deviceVersionId) {
        this.deviceVersionId = deviceVersionId;
    }

    public String getDeviceVersionType() {
        return deviceVersionType;
    }

    public void setDeviceVersionType(String deviceVersionType) {
        this.deviceVersionType = deviceVersionType;
    }

    public String getDeviceVersionNumber() {
        return deviceVersionNumber;
    }

    public void setDeviceVersionNumber(String deviceVersionNumber) {
        this.deviceVersionNumber = deviceVersionNumber;
    }

    public String getDeviceVersionDescribe() {
        return deviceVersionDescribe;
    }

    public void setDeviceVersionDescribe(String deviceVersionDescribe) {
        this.deviceVersionDescribe = deviceVersionDescribe;
    }

    public String getDeviceVersionCommand() {
        return deviceVersionCommand;
    }

    public void setDeviceVersionCommand(String deviceVersionCommand) {
        this.deviceVersionCommand = deviceVersionCommand;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public int getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(int lastVersion) {
        this.lastVersion = lastVersion;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public static class ParamsBean {
    }
}
