package com.voocoo.pet.modules.add.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.umeng.commonsdk.debug.I;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.common.event.AddDeviceSuccessEvent;
import com.voocoo.pet.modules.add.contract.AddDeviceStepOneActivityContract;
import com.voocoo.pet.modules.add.presenter.AddDeviceStepOneActivityPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

import java.util.ArrayList;
import java.util.List;

public class AddDeviceStepOneActivity extends AbsBaseActivity<AddDeviceStepOneActivityPresenter> implements AddDeviceStepOneActivityContract.View {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.iv_dev)
    ImageView ivDev;

    @BindView(R.id.cb_wifi_light)
    CheckBox cbWifiLight;

    @BindView(R.id.btn_next)
    Button btnNext;

    @BindView(R.id.tv_tips)
    TextView tvTips;

    int type = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        type = getIntent().getIntExtra("type", 0);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void setLoginBtnEnabled(boolean isEnabled) {
        if (isEnabled) {
            btnNext.setEnabled(true);
            btnNext.setBackgroundResource(R.drawable.bg_btn_able);
        } else {
            btnNext.setEnabled(false);
            btnNext.setBackgroundResource(R.drawable.bg_btn_unable);
        }
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        tvTitle.setText(getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (type == 1) {
            ivDev.setImageResource(R.mipmap.water_reset2);
            tvTips.setText("开机状态下，连按上图所示按钮两下，直至指示灯闪烁。");
        } else {
            ivDev.setImageResource(R.mipmap.feed_reset);
            tvTips.setText("开机状态下，连按上图所示按钮三下，直至指示灯闪烁。");
        }

        cbWifiLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setLoginBtnEnabled(b);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device_step_one;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @Nullable
    @Override
    protected AddDeviceStepOneActivityPresenter createPresenter() {
        return new AddDeviceStepOneActivityPresenter(this);
    }

    @OnClick(R.id.btn_next)
    void ClickToNext() {
        Intent intent = new Intent(AddDeviceStepOneActivity.this, AddDeviceStepTwoActivity.class);
        startActivity(intent);
    }

    private BluetoothAdapter mBluetoothAdapter;

    public void checkPermissions() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 3);
            return;
        }

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(AddDeviceStepOneActivity.this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(AddDeviceStepOneActivity.this, deniedPermissions, AddDeviceStepThreeActivity.REQUEST_CODE_PERMISSION_LOCATION);
        }
    }

    public boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static final int REQUEST_CODE_OPEN_GPS = 1;
    public static final int REQUEST_CODE_PERMISSION_LOCATION = 2;

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                    new AlertDialog.Builder(AddDeviceStepOneActivity.this)
                            .setTitle(getString(R.string.alert))
                            .setMessage(getString(R.string.text_gps_tips))
                            .setNegativeButton(getString(R.string.cancel),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                            .setPositiveButton(getString(R.string.ensure),
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
                    presenter.next();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_OPEN_GPS) {
            if (checkGPSIsOpen()) {
                presenter.next();
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

    /**
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddDeviceSuccess(AddDeviceSuccessEvent event) {
        finish();
    }
}
