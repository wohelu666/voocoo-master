package com.voocoo.pet.entity;

public class Record {
    public String createBy;
    public String createTime;
    public String updateBy;
    public String updateTime;
    public String content;

    public Record(String createTime, String content) {
        this.createTime = createTime;
        this.content = content;
    }
}
