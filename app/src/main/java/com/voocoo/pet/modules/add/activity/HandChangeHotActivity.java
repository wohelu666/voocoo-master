package com.voocoo.pet.modules.add.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.common.utils.CommonUtil;
import com.voocoo.pet.modules.add.contract.HandChangeHotActivityContract;
import com.voocoo.pet.modules.add.presenter.HandChangeHotActivityPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class HandChangeHotActivity extends AbsBaseActivity<HandChangeHotActivityPresenter> implements HandChangeHotActivityContract.View {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    private String _ssid;
    private String _password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _ssid = getIntent().getStringExtra("ssid");
        _password = getIntent().getStringExtra("password");
        initView();
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        tvTitle.setText(getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_hand_change_hot;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @Nullable
    @Override
    protected HandChangeHotActivityPresenter createPresenter() {
        return new HandChangeHotActivityPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String ssid = CommonUtil.getCurrentWifiSSID(this);
        if (ssid.equals("Granwin_AP")) {
            //开始配网
            Intent intent = new Intent(this, BindDeviceActivity.class);
            intent.putExtra("ssid", _ssid);
            intent.putExtra("password", _password);
            startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.btn_copy)
    public void copy() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label", "12345678");
        cm.setPrimaryClip(mClipData);

        startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
    }

}
