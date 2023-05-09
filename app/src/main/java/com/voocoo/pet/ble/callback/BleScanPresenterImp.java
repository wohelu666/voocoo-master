package com.voocoo.pet.ble.callback;

import com.voocoo.pet.ble.data.BleDevice;

public interface BleScanPresenterImp {

    void onScanStarted(boolean success);

    void onScanning(BleDevice bleDevice);

}
