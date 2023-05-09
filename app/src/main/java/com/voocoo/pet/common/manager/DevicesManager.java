package com.voocoo.pet.common.manager;


import com.voocoo.pet.entity.Device;

import java.util.ArrayList;
import java.util.List;

public class DevicesManager {

    private static volatile DevicesManager instance;
    private List<Device> deviceList = new ArrayList<>();

    public static DevicesManager getInstance() {
        if (instance == null) {
            synchronized (DevicesManager.class) {
                if (instance == null) {
                    instance = new DevicesManager();
                }
            }
        }
        return instance;
    }


    private DevicesManager() {

    }

    public void deleteDevice() {
        deviceList.clear();
    }

    public void saveDevice(Device device) {
        deviceList.add(device);
    }

    public void setDeviceList(List<Device> list) {
        deviceList.clear();
        deviceList.addAll(list);
    }

    public void addDeviceList(List<Device> list) {
        deviceList.addAll(list);
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }
}


