package com.voocoo.pet.modules.add.presenter;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.event.FinishAddDeviceStepTwoActivityEvent;
import com.voocoo.pet.common.widgets.AppDialog;
import com.voocoo.pet.modules.add.activity.AddDeviceStepThreeActivity;
import com.voocoo.pet.modules.add.activity.BindDeviceActivity;
import com.voocoo.pet.modules.add.activity.HandChangeHotActivity;
import com.voocoo.pet.modules.add.contract.AddDeviceStepThreeActivityContract;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class AddDeviceStepThreeActivityPresenter extends BaseActivityPresenter<AddDeviceStepThreeActivity> implements AddDeviceStepThreeActivityContract.Presenter {

    private BluetoothAdapter mBluetoothAdapter;

    public AddDeviceStepThreeActivityPresenter(AddDeviceStepThreeActivity activity) {
        super(activity);
    }

    @Override
    public void connectDeviceHot() {
        //ble
        checkPermissions();
    }

    public void throughBle() {

    }

    public void checkPermissions() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
       /* if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            getView().startActivityForResult(enableBtIntent, 3);
            return;
        }*/

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(getContext(), permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(getView(), deniedPermissions, AddDeviceStepThreeActivity.REQUEST_CODE_PERMISSION_LOCATION);
        }
    }

    public boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("提示")
                            .setMessage("是否同意打开GPS")
                            .setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            getView().startActivityForResult(intent, AddDeviceStepThreeActivity.REQUEST_CODE_OPEN_GPS);
                                        }
                                    })

                            .setCancelable(false)
                            .show();
                } else {
                    throughBle();
                }
                break;
        }
    }
}
