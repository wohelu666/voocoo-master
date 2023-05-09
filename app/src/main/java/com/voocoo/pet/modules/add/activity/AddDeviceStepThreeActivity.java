package com.voocoo.pet.modules.add.activity;

import static android.content.ContentValues.TAG;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.ble.BleManager;
import com.voocoo.pet.ble.callback.BleGattCallback;
import com.voocoo.pet.ble.callback.BleMtuChangedCallback;
import com.voocoo.pet.ble.callback.BleNotifyCallback;
import com.voocoo.pet.ble.callback.BleWriteCallback;
import com.voocoo.pet.ble.data.BleDevice;
import com.voocoo.pet.ble.exception.BleException;
import com.voocoo.pet.common.utils.CommonUtil;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.common.widgets.TempProgressBar;
import com.voocoo.pet.modules.add.contract.AddDeviceStepThreeActivityContract;
import com.voocoo.pet.modules.add.presenter.AddDeviceStepThreeActivityPresenter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import butterknife.BindView;

public class AddDeviceStepThreeActivity extends AbsBaseActivity<AddDeviceStepThreeActivityPresenter> implements AddDeviceStepThreeActivityContract.View {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.tv_step1)
    TextView tvStep1;
    @BindView(R.id.tv_step2)
    TextView tvStep2;
    @BindView(R.id.tv_step3)
    TextView tvStep3;
    @BindView(R.id.tv_step4)
    TextView tvStep4;

    @BindView(R.id.circle_progress)
    TempProgressBar circleProgress;
    @BindView(R.id.tv_progress)
    TextView tvProgress;

    private String ssid;
    private String password;
    private String wifiMac;

    BleDevice curBleDevice;
    private boolean isReceiveMac = false;

    public static final int REQUEST_CODE_OPEN_GPS = 1;
    public static final int REQUEST_CODE_PERMISSION_LOCATION = 2;

    public static UUID SERVICE_UUID = UUID.fromString("0000a002-0000-1000-8000-00805f9b34fb");
    public static UUID WRITE_CHARACTERISTIC_UUID = UUID.fromString("0000c304-0000-1000-8000-00805f9b34fb");
    public static UUID NOTIFY_CHARACTERISTIC_UUID = UUID.fromString("0000c305-0000-1000-8000-00805f9b34fb");
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                startActivity(new Intent(AddDeviceStepThreeActivity.this, BindDeviceFailActivity.class));
            } else {
                //配网成功才保存密码
                SharedPreferencesUtil.keepShared(ssid + "_pwd", password);

                //调用绑定设备接口
                Intent intent = new Intent(AddDeviceStepThreeActivity.this, AddDeviceSuccessActivity.class);

                intent.putExtra("mac", wifiMac);
                intent.putExtra("wifiName", ssid);

                startActivity(intent);
            }
            finish();
        }
    };

    private void setStep(int step) {
        if (step == 1) {
            tvStep1.setVisibility(View.VISIBLE);
            tvStep2.setVisibility(View.GONE);
            tvStep3.setVisibility(View.GONE);
            tvStep4.setVisibility(View.GONE);
            setProgressUi(1);
        } else if (step == 2) {
            tvStep1.setVisibility(View.VISIBLE);
            tvStep2.setVisibility(View.VISIBLE);
            tvStep3.setVisibility(View.GONE);
            tvStep4.setVisibility(View.GONE);
            setProgressUi(25);
        } else if (step == 3) {
            tvStep1.setVisibility(View.VISIBLE);
            tvStep2.setVisibility(View.VISIBLE);
            tvStep3.setVisibility(View.VISIBLE);
            tvStep4.setVisibility(View.GONE);
            setProgressUi(50);
        } else if (step == 4) {
            tvStep1.setVisibility(View.VISIBLE);
            tvStep2.setVisibility(View.VISIBLE);
            tvStep3.setVisibility(View.VISIBLE);
            tvStep4.setVisibility(View.VISIBLE);
            setProgressUi(75);
        } else {
            tvStep1.setVisibility(View.VISIBLE);
            tvStep2.setVisibility(View.VISIBLE);
            tvStep3.setVisibility(View.VISIBLE);
            tvStep4.setVisibility(View.VISIBLE);
            setProgressUi(100);
        }
    }

    public void setProgressUi(int progress) {
        circleProgress.setProgress(progress);
        tvProgress.setText(progress + "%");
    }

    public void connectDev() {
        setStep(1);
        String mac = SharedPreferencesUtil.queryValue("add_mac");
        BleManager.getInstance().connect(mac, new BleGattCallback() {
            @Override
            public void onStartConnect() {

            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {

            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                BleManager.getInstance().setMtu(bleDevice, 100, new BleMtuChangedCallback() {
                    @Override
                    public void onSetMTUFailure(BleException exception) {
                        curBleDevice = bleDevice;
                        isReceiveMac = false;
                        openNotify();
                        setStep(2);
                    }

                    @Override
                    public void onMtuChanged(int mtu) {
                        curBleDevice = bleDevice;
                        isReceiveMac = false;
                        openNotify();
                        setStep(2);
                    }
                });
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                curBleDevice = null;
                //连接断开代表配网成功
                LogUtil.d("onDisConnected");
                if (isReceiveMac) {
                    setStep(5);
                    LogUtil.d("mac=" + wifiMac);
                    mHandler.removeCallbacksAndMessages(null);
                    mHandler.sendEmptyMessageDelayed(1, 2000);
                }
            }
        });
    }

    private void sendInfoToDev() {
        String ssidInfo = "/ssid/" + ssid + "/#/pass/" + password + "/#";
        BleManager.getInstance().write(curBleDevice, String.valueOf(SERVICE_UUID), String.valueOf(WRITE_CHARACTERISTIC_UUID), ssidInfo.getBytes(), false, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                LogUtil.d("wifi信息发送成功");
            }

            @Override
            public void onWriteFailure(BleException exception) {

                startActivity(new Intent(AddDeviceStepThreeActivity.this, BindDeviceFailActivity.class));
                LogUtil.d("wifi信息发送失败->" + exception.getDescription());
                finish();
            }
        });
    }

    private void openNotify() {
        BleManager.getInstance().notify(
                curBleDevice,
                SERVICE_UUID.toString(),
                NOTIFY_CHARACTERISTIC_UUID.toString(),
                new BleNotifyCallback() {

                    @Override
                    public void onNotifySuccess() {
                        LogUtil.d("onNotifySuccess success");
                        setStep(3);
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        LogUtil.d("onNotifyFailure fail," + exception.getDescription());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        try {
                            LogUtil.d("onCharacteristicChanged," + new String(data, "utf-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        try {
                            parseBLEData(data);
                        } catch (Exception e) {
                            Log.e(TAG, "onCharacteristicChanged: "+e.getMessage() );
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void parseBLEData(byte[] data) {
        if (isReceiveMac) {
            return;
        }
        setStep(4);
        String response = new String(data, StandardCharsets.UTF_8);
        if (response.startsWith("/WF/")) {
            wifiMac = response.replaceAll("/WF/", "");
            wifiMac = wifiMac.replaceAll("/", "");
            isReceiveMac = true;
            //记录mac
            sendInfoToDev();
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ssid = getIntent().getStringExtra("ssid");
        password = getIntent().getStringExtra("password");
        initView();
        presenter.connectDeviceHot();
    }

    public String getSsid() {
        return ssid;
    }

    public String getPassword() {
        return password;
    }

    @SuppressLint("WrongConstant")
    private void initView() {
        setSupportActionBar(topToolbar);
        tvTitle.setText("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        connectDev();
        mHandler.sendEmptyMessageDelayed(0, 30000);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device_step_three;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @Nullable
    @Override
    protected AddDeviceStepThreeActivityPresenter createPresenter() {
        return new AddDeviceStepThreeActivityPresenter(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        String mac = SharedPreferencesUtil.queryValue("add_mac");
        if (BleManager.getInstance().isConnected(mac)){
            BleManager.getInstance().stopNotify( curBleDevice,
                    SERVICE_UUID.toString(),
                    NOTIFY_CHARACTERISTIC_UUID.toString());
        }

        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_OPEN_GPS) {
            if (presenter.checkGPSIsOpen()) {
                presenter.throughBle();
            }
        } else if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    presenter.checkPermissions();
                }
            }, 1000);
        }
    }
}
