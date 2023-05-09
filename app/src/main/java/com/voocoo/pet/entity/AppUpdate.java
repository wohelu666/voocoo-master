package com.voocoo.pet.entity;

import java.util.List;

public class AppUpdate {
    private int code;
    private String msg;
    private List<AppUpdataInter> data;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<AppUpdataInter> getData() {
        return data;
    }

    public void setData(List<AppUpdataInter> data) {
        this.data = data;
    }

    public class AppUpdataInter{
        public String appVersionNumber;
        public String appVersionDescribe;
    }
}
