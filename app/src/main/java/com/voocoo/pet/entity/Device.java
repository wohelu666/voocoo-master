package com.voocoo.pet.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Device implements Serializable {

    private int deviceId;
    private String deviceType;
    public String mac;
    private String deviceName;
    private String status;
    private WaterMap waterMap;
    private FeederMap feederMap;
    List<DevShare> devShareList=new ArrayList<>();

    public List<DevShare> getDevShareList() {
        return devShareList;
    }

    public void setDevShareList(List<DevShare> devShareList) {
        this.devShareList = devShareList;
    }

    public WaterMap getWaterMap() {
        return waterMap;
    }

    public void setWaterMap(WaterMap waterMap) {
        this.waterMap = waterMap;
    }

    public FeederMap getFeederMap() {
        return feederMap;
    }

    public void setFeederMap(FeederMap feederMap) {
        this.feederMap = feederMap;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public class WaterMap implements Serializable {
        private int drinkingTimes;
        private String formatAvgStayTime;
        private String purifiedWaterSurplus;

        public int getDrinkingTimes() {

            return drinkingTimes;
        }

        public void setDrinkingTimes(int drinkingTimes) {
            this.drinkingTimes = drinkingTimes;
        }

        public String getFormatAvgStayTime() {
            return formatAvgStayTime;
        }

        public void setFormatAvgStayTime(String formatAvgStayTime) {
            this.formatAvgStayTime = formatAvgStayTime;
        }

        public String getPurifiedWaterSurplus() {
            return purifiedWaterSurplus;
        }

        public void setPurifiedWaterSurplus(String purifiedWaterSurplus) {
            this.purifiedWaterSurplus = purifiedWaterSurplus;
        }
    }

    public class FeederMap implements Serializable {
        private int eatingTimes;
        private int feedingTimes;
        private String nextDietTime;
        private int nextDietAmount;

        public int getEatingTimes() {
            return eatingTimes;
        }

        public void setEatingTimes(int eatingTimes) {
            this.eatingTimes = eatingTimes;
        }

        public int getFeedingTimes() {
            return feedingTimes;
        }

        public void setFeedingTimes(int feedingTimes) {
            this.feedingTimes = feedingTimes;
        }

        public String getNextDietTime() {
            return nextDietTime;
        }

        public void setNextDietTime(String nextDietTime) {
            this.nextDietTime = nextDietTime;
        }

        public int getNextDietAmount() {
            return nextDietAmount;
        }

        public void setNextDietAmount(int nextDietAmount) {
            this.nextDietAmount = nextDietAmount;
        }
    }
}
