package com.voocoo.pet.entity;

import java.util.List;

public class HomeMoment {
    private Moment followMoment;
    private Moment hotMoment;
    private Moment latestMoment;
    private Moment officialMoment;
    private List<MomentDto> records;

    public List<MomentDto> getRecords() {
        return records;
    }

    public void setRecords(List<MomentDto> records) {
        this.records = records;
    }

    public Moment getFollowMoment() {
        return followMoment;
    }

    public void setFollowMoment(Moment followMoment) {
        this.followMoment = followMoment;
    }

    public Moment getHotMoment() {
        return hotMoment;
    }

    public void setHotMoment(Moment hotMoment) {
        this.hotMoment = hotMoment;
    }

    public Moment getLatestMoment() {
        return latestMoment;
    }

    public void setLatestMoment(Moment latestMoment) {
        this.latestMoment = latestMoment;
    }

    public Moment getOfficialMoment() {
        return officialMoment;
    }

    public void setOfficialMoment(Moment officialMoment) {
        this.officialMoment = officialMoment;
    }

    public class Moment{
        private String nextId;
        private boolean nextValid;
        private List<MomentDto> records;

        public String getNextId() {
            return nextId;
        }

        public void setNextId(String nextId) {
            this.nextId = nextId;
        }

        public boolean isNextValid() {
            return nextValid;
        }

        public void setNextValid(boolean nextValid) {
            this.nextValid = nextValid;
        }

        public List<MomentDto> getRecords() {
            return records;
        }

        public void setRecords(List<MomentDto> records) {
            this.records = records;
        }
    }
}
