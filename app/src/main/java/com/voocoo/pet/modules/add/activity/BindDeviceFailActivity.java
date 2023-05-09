package com.voocoo.pet.modules.add.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.modules.add.contract.BindDeviceFailActivityContract;
import com.voocoo.pet.modules.add.presenter.BindDeviceFailActivityPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class BindDeviceFailActivity extends AbsBaseActivity<BindDeviceFailActivityPresenter> implements BindDeviceFailActivityContract.View {

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
        tvTitle.setText("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bind_device_fail;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @Nullable
    @Override
    protected BindDeviceFailActivityPresenter createPresenter() {
        return new BindDeviceFailActivityPresenter(this);
    }


    @OnClick(R.id.btn_next)
    public void retry() {
        finish();
    }
}
