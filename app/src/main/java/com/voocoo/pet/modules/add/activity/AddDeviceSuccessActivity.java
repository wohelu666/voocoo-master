package com.voocoo.pet.modules.add.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.common.event.AddDeviceSuccessEvent;
import com.voocoo.pet.common.manager.DevicesManager;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Device;
import com.voocoo.pet.entity.DeviceListData;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.add.contract.AddDeviceSuccessActivityContract;
import com.voocoo.pet.modules.add.presenter.AddDeviceSuccessActivityPresenter;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class AddDeviceSuccessActivity extends AbsBaseActivity<AddDeviceSuccessActivityPresenter> implements AddDeviceSuccessActivityContract.View {


    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;
    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.et_name)
    EditText etName;

    @BindView(R.id.ly_progress)
    View lyProgress;

    @BindView(R.id.view3)
    View view3;

    String mac;
    String wifiName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setSupportActionBar(topToolbar);
        tvTitle.setText(getString(R.string.text_dev_name));
        mac = getIntent().getStringExtra("mac");
        wifiName = getIntent().getStringExtra("wifiName");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int type = SharedPreferencesUtil.queryIntValue("add_type");
        if (type == 0) {
            etName.setText(getString(R.string.text_water_default_name));
            view3.setVisibility(View.GONE);
            // lyProgress.setVisibility(View.GONE);
        } else {
            etName.setText(getString(R.string.text_feeder_default_name));
            // lyProgress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device_success;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @Nullable
    @Override
    protected AddDeviceSuccessActivityPresenter createPresenter() {
        return new AddDeviceSuccessActivityPresenter(this);
    }

    @OnClick(R.id.btn_complete)
    public void complete() {
        int type = SharedPreferencesUtil.queryIntValue("add_type");
        if (TextUtils.isEmpty(etName.getText().toString())) {
            showToast("请输入设备名称");
            return;
        }
        Intent intent;
        if (type == 0) { // type 0 饮水机，为1 喂食器
            intent = new Intent(AddDeviceSuccessActivity.this, SelFamilyModeActivity.class);
        } else {
            intent = new Intent(AddDeviceSuccessActivity.this, SelFamilyModeForFeedActivity.class);
        }
        intent.putExtra("name", etName.getText().toString());
        intent.putExtra("mac", mac);
        intent.putExtra("wifiName", wifiName);
        startActivity(intent);
    }

    /**
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddDeviceSuccess(AddDeviceSuccessEvent event) {
        finish();
    }
}
