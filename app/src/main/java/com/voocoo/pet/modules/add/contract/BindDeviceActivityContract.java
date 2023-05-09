package com.voocoo.pet.modules.add.contract;

public interface BindDeviceActivityContract {
    interface View {

    }

    interface Presenter {
        void setDeviceNetwork(String ssid, String password);

        void bindDevice();
    }
}
