package com.voocoo.pet.modules.add.activity;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.ble.BleManager;
import com.voocoo.pet.ble.callback.BleScanCallback;
import com.voocoo.pet.ble.data.BleDevice;
import com.voocoo.pet.ble.scan.BleScanRuleConfig;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.modules.add.adapter.ScanListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ScanDeviceActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.rv_dev)
    RecyclerView recyclerView;

    ScanListAdapter scanListAdapter;

    public String bleDeviceName = "waterble";
    public String bleDeviceName2 = "VooCooFeed";
    List<BleDevice> bleDeviceList = new ArrayList<>();

    public static final int REQUEST_CODE_OPEN_GPS = 1;
    public static final int REQUEST_CODE_PERMISSION_LOCATION = 2;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        checkPermissions();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            checkPermissions();
        }
    }

    public void checkPermissions() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 2);
            return;
        }

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(ScanDeviceActivity.this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(ScanDeviceActivity.this, deniedPermissions, AddDeviceStepThreeActivity.REQUEST_CODE_PERMISSION_LOCATION);
        }
    }

    public boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                    new AlertDialog.Builder(ScanDeviceActivity.this)
                            .setTitle("提示")
                            .setMessage("添加设备需要您开启位置权限")
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
                                            startActivityForResult(intent, AddDeviceStepThreeActivity.REQUEST_CODE_OPEN_GPS);
                                        }
                                    })
                            .setCancelable(false)
                            .show();
                } else {
                    startScan();
                }
                break;
        }
    }

    public void startScan() {
        bleDeviceList = new ArrayList<>();
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setAutoConnect(false)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(30000)              // 扫描超时时间，可选，默认10秒
                .setDeviceName(true, bleDeviceName, bleDeviceName2)
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);

        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                if (success) {
                    LogUtil.d("开启扫描成功");
                } else {
                    LogUtil.d("开启扫描失败");
                }
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
                if (bleDevice == null) {
                    return;
                }
                String deviceName = bleDevice.getName();

                if (bleDevice == null || deviceName == null) {
                    return;
                }
                if (deviceName.contains(bleDeviceName) || deviceName.contains(bleDeviceName2)) {
                    LogUtil.d("描到设备->" + bleDevice.getMac());

                    boolean isHave = false;
                    for (int i = 0; i < bleDeviceList.size(); i++) {
                        if (bleDeviceList.get(i).getMac().equals(bleDevice.getMac())) {
                            isHave = true;
                        }
                    }
                    if (!isHave) {
                        bleDeviceList.add(bleDevice);
                        scanListAdapter.setData(bleDeviceList);
                    }
                } else {
                    if (bleDevice.getMac() != null)
                        LogUtil.d("扫描到其他设备->" + deviceName + "," + bleDevice.getMac());
                }
            }

            @Override
            public void onScanning(BleDevice bleDevice) {

            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_OPEN_GPS) {
            if (checkGPSIsOpen()) {
                startScan();
            }
        } else if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkPermissions();
                }
            }, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            BleManager.getInstance().cancelScan();
        } catch (Exception e) {
            Log.e(TAG, "onDestroy: "+e.getMessage() );
        }
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_black_ic);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText(getString(R.string.text_add_dev));

        scanListAdapter = new ScanListAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(ScanDeviceActivity.this));
        recyclerView.setAdapter(scanListAdapter);
        scanListAdapter.setCancelShareListener(new ScanListAdapter.CancelShareListener() {
            @Override
            public void onCancel(BleDevice bleDevice) {
                SharedPreferencesUtil.keepShared("add_type", bleDevice.getName().equals("waterble") ? 0 : 1);
                SharedPreferencesUtil.keepShared("add_mac", bleDevice.getMac());
                startActivity(new Intent(ScanDeviceActivity.this, AddDeviceStepTwoActivity.class));
                finish();
            }
        });
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan_dev;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @Nullable
    @Override
    protected BaseActivityPresenter createPresenter() {
        return null;
    }
}
