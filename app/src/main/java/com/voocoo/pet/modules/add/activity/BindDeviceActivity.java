package com.voocoo.pet.modules.add.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.modules.add.contract.BindDeviceActivityContract;
import com.voocoo.pet.modules.add.presenter.BindDeviceActivityPresenter;

import butterknife.BindView;

public class BindDeviceActivity extends AbsBaseActivity<BindDeviceActivityPresenter> implements BindDeviceActivityContract.View {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    boolean isUseBle = false;
    String ssid;
    String password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        ssid = getIntent().getStringExtra("ssid");
        password = getIntent().getStringExtra("password");
        isUseBle = getIntent().getBooleanExtra("isUseBle", false);

        presenter.setDeviceNetwork(ssid, password);
    }

    public boolean isUseBle() {
        return isUseBle;
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        tvTitle.setText(getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bind_device;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @Nullable
    @Override
    protected BindDeviceActivityPresenter createPresenter() {
        return new BindDeviceActivityPresenter(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestory();
    }

}
