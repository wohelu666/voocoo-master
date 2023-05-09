package com.voocoo.pet.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FeedPlan implements Serializable {
    public int feederPlanId;
    public int feederPlanDevice;
    public String planCycle;
    public String status;
    public int feedingTimes;
    public int feedingAmount;
    public ArrayList<Integer> feederPlanCycle;
    public List<FeedPlanDiets> planDiets;

    public static class FeedPlanDiets implements Serializable {
        public String searchValue;
        public String createBy;
        public String createTime;
        public String updateBy;
        public String updateTime;
        public String remark;
        public int dietId;
        public int dietPlan;
        public String dietTag;
        public int dietAmount;
        public String dietTime;
        public String status;
        public String delFlag;
    }

    public int getFeederPlanId() {
        return feederPlanId;
    }

    public void setFeederPlanId(int feederPlanId) {
        this.feederPlanId = feederPlanId;
    }

    public int getFeederPlanDevice() {
        return feederPlanDevice;
    }

    public void setFeederPlanDevice(int feederPlanDevice) {
        this.feederPlanDevice = feederPlanDevice;
    }

    public String getPlanCycle() {
        return planCycle;
    }

    public void setPlanCycle(String planCycle) {
        this.planCycle = planCycle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFeedingTimes() {
        return feedingTimes;
    }

    public void setFeedingTimes(int feedingTimes) {
        this.feedingTimes = feedingTimes;
    }

    public int getFeedingAmount() {
        return feedingAmount;
    }

    public void setFeedingAmount(int feedingAmount) {
        this.feedingAmount = feedingAmount;
    }

    public ArrayList<Integer> getFeederPlanCycle() {
        return feederPlanCycle;
    }

    public void setFeederPlanCycle(ArrayList<Integer> feederPlanCycle) {
        this.feederPlanCycle = feederPlanCycle;
    }

    public List<FeedPlanDiets> getPlanDiets() {
        return planDiets;
    }

    public void setPlanDiets(List<FeedPlanDiets> planDiets) {
        this.planDiets = planDiets;
    }
}
