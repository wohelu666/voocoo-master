package com.voocoo.pet.entity;

import java.io.Serializable;
import java.util.List;

public class DeviceDetail implements Serializable {

    private int drinkingTimes;
    private int drinkingTimesYesterday;
    public int drinkingCompareYesterday;
    private int avgStayTime;
    private int avgStayTimeYesterday;
    private int stayCompareYesterday;
    private String formatAvgStayTime;
    private String formatStayTimeCompare;
    private String status;
    private String familyModel;
    private String workingModel;
    private String electricity;
    private String filterElementSurplus;
    private int filterSurplusDays;
    private String purifiedWaterSurplus;
    private String wasteWaterSurplus;
    private List<Record> drinkingList;
    private List<Record> feederLogList;
    private List<Record> waterLogVoList;
    private List<FeedingData> feedingList;

    public int desiccanSurplusDays;
    public int eatingAmount;
    public String nextDietTime;
    public int nextDietAmount;
    public int alreadyFeedingAmount;
    public int planFeedingAmount;
    public int needFeedingAmount;
    public String bowlSurplus;
    public String bucketSurplus;
    public String childLock;
    public String nightModel;
    public String nightStartTime;
    public String nightEndTime;
    public String feedingPlan;
    private int water;
    public int powerPlug;

    public int getWater() {
        return water;
    }

    public void setWater(int water) {
        this.water = water;
    }

    public List<FeedingData> getFeedingList() {
        return feedingList;
    }

    public void setFeedingList(List<FeedingData> feedingList) {
        this.feedingList = feedingList;
    }

    public List<Record> getWaterLogVoList() {
        return waterLogVoList;
    }

    public void setWaterLogVoList(List<Record> waterLogVoList) {
        this.waterLogVoList = waterLogVoList;
    }

    public List<Record> getFeederLogList() {
        return feederLogList;
    }

    public void setFeederLogList(List<Record> feederLogList) {
        this.feederLogList = feederLogList;
    }

    public List<Record> getDrinkingList() {
        return drinkingList;
    }

    public void setDrinkingList(List<Record> drinkingList) {
        this.drinkingList = drinkingList;
    }

    public int getDrinkingTimes() {
        return drinkingTimes;
    }

    public void setDrinkingTimes(int drinkingTimes) {
        this.drinkingTimes = drinkingTimes;
    }

    public int getDrinkingTimesYesterday() {
        return drinkingTimesYesterday;
    }

    public void setDrinkingTimesYesterday(int drinkingTimesYesterday) {
        this.drinkingTimesYesterday = drinkingTimesYesterday;
    }

    public int getDrinkingCompareYesterday() {
        return drinkingCompareYesterday;
    }

    public void setDrinkingCompareYesterday(int drinkingCompareYesterday) {
        this.drinkingCompareYesterday = drinkingCompareYesterday;
    }

    public int getAvgStayTime() {
        return avgStayTime;
    }

    public void setAvgStayTime(int avgStayTime) {
        this.avgStayTime = avgStayTime;
    }

    public int getAvgStayTimeYesterday() {
        return avgStayTimeYesterday;
    }

    public void setAvgStayTimeYesterday(int avgStayTimeYesterday) {
        this.avgStayTimeYesterday = avgStayTimeYesterday;
    }

    public int getStayCompareYesterday() {
        return stayCompareYesterday;
    }

    public void setStayCompareYesterday(int stayCompareYesterday) {
        this.stayCompareYesterday = stayCompareYesterday;
    }

    public String getFormatAvgStayTime() {
        return formatAvgStayTime;
    }

    public void setFormatAvgStayTime(String formatAvgStayTime) {
        this.formatAvgStayTime = formatAvgStayTime;
    }

    public String getFormatStayTimeCompare() {
        return formatStayTimeCompare;
    }

    public void setFormatStayTimeCompare(String formatStayTimeCompare) {
        this.formatStayTimeCompare = formatStayTimeCompare;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFamilyModel() {
        return familyModel;
    }

    public void setFamilyModel(String familyModel) {
        this.familyModel = familyModel;
    }

    public String getWorkingModel() {
        return workingModel;
    }

    public void setWorkingModel(String workingModel) {
        this.workingModel = workingModel;
    }

    public String getElectricity() {
        return electricity;
    }

    public void setElectricity(String electricity) {
        this.electricity = electricity;
    }

    public String getFilterElementSurplus() {
        return filterElementSurplus;
    }

    public void setFilterElementSurplus(String filterElementSurplus) {
        this.filterElementSurplus = filterElementSurplus;
    }

    public int getFilterSurplusDays() {
        return filterSurplusDays;
    }

    public void setFilterSurplusDays(int filterSurplusDays) {
        this.filterSurplusDays = filterSurplusDays;
    }

    public String getPurifiedWaterSurplus() {
        return purifiedWaterSurplus;
    }

    public void setPurifiedWaterSurplus(String purifiedWaterSurplus) {
        this.purifiedWaterSurplus = purifiedWaterSurplus;
    }

    public String getWasteWaterSurplus() {
        return wasteWaterSurplus;
    }

    public void setWasteWaterSurplus(String wasteWaterSurplus) {
        this.wasteWaterSurplus = wasteWaterSurplus;
    }
}
