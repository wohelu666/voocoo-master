package com.voocoo.pet.entity;

import java.util.List;

public class News {

    private List<Record> records;

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public class Record {
        private String id;
        private String userId;
        private int momentType;
        private String moment;
        private String sourceUrl;
        private int commentNum;
        private int priseNum;
        private String ctime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getMomentType() {
            return momentType;
        }

        public void setMomentType(int momentType) {
            this.momentType = momentType;
        }

        public String getMoment() {
            return moment;
        }

        public void setMoment(String moment) {
            this.moment = moment;
        }

        public String getSourceUrl() {
            return sourceUrl;
        }

        public void setSourceUrl(String sourceUrl) {
            this.sourceUrl = sourceUrl;
        }

        public int getCommentNum() {
            return commentNum;
        }

        public void setCommentNum(int commentNum) {
            this.commentNum = commentNum;
        }

        public int getPriseNum() {
            return priseNum;
        }

        public void setPriseNum(int priseNum) {
            this.priseNum = priseNum;
        }

        public String getCtime() {
            return ctime;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }
    }
}
