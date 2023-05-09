package com.voocoo.pet.modules.add.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.modules.add.contract.ConnectDeviceFailActivityContract;
import com.voocoo.pet.modules.add.presenter.ConnectDeviceFailActivityPresenter;

import butterknife.BindView;

public class ConnectDeviceFailActivity extends AbsBaseActivity<ConnectDeviceFailActivityPresenter> implements ConnectDeviceFailActivityContract.View {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        tvTitle.setText(getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device_fail;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @Nullable
    @Override
    protected ConnectDeviceFailActivityPresenter createPresenter() {
        return new ConnectDeviceFailActivityPresenter(this);
    }
}
