package com.voocoo.pet.modules.add.contract;

public interface AddDeviceStepTwoActivityContract {
    interface View {
        void showCurrentSSID(String ssid);

        String getWifiName();

        String getWifiPassword();
    }

    interface Presenter {
        void getCurWifiSsid();

        void next();
    }
}
