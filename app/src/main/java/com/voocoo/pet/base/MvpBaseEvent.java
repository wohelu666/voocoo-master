package com.voocoo.pet.base;

/**
 * Created by chenjiahui on 16/4/11.
 */
public class MvpBaseEvent {

    private Object data;
    private Object arg1;
    private Object arg2;
    private Enum flag;

    public MvpBaseEvent() {

    }

    public MvpBaseEvent(Enum flag) {
        setFlag(flag);
    }

    public MvpBaseEvent(Enum flag, Object data) {
        setFlag(flag);
        setData(data);
    }

    public MvpBaseEvent(Enum flag, Object data, Object arg1) {
        this.data = data;
        this.arg1 = arg1;
        this.flag = flag;
    }

    public MvpBaseEvent(Enum flag, Object data, Object arg1, Object arg2) {
        this.data = data;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "flag: " + flag + " data: " + data;
    }

    public Enum getFlag() {
        return flag;
    }

    public void setFlag(Enum flag) {
        this.flag = flag;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getArg1() {
        return arg1;
    }

    public void setArg1(Object arg1) {
        this.arg1 = arg1;
    }

    public Object getArg2() {
        return arg2;
    }

    public void setArg2(Object arg2) {
        this.arg2 = arg2;
    }
}
