package com.voocoo.pet.modules.dev.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Device;
import com.voocoo.pet.entity.DeviceDetail;
import com.voocoo.pet.entity.RefreshFilterElement;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.web.WebActivity;

import org.apache.http.Header;

import butterknife.BindView;
import butterknife.OnClick;

public class FilterElementActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.tv_filter_day)
    TextView tvFilterDay;
    @BindView(R.id.tv_filter_surplus)
    TextView tvFilterSurplus;

    Device device;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        device = (Device) getIntent().getSerializableExtra("device");
        initView();
        initData();
    }

    @OnClick({R.id.btn_buy_filter, R.id.tv_refresh})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_buy_filter:
                Intent intent = new Intent(FilterElementActivity.this, WebActivity.class);
                intent.putExtra("url", "https://shop91447720.m.youzan.com/wscgoods/detail/2oj9x4g3tj3dc5g");
                startActivity(intent);
                break;
            case R.id.tv_refresh:
                showLoading();
                HttpManage.getInstance().refreshFilterElement(device.getDeviceId(), new HttpManage.ResultCallback<String>() {
                    @Override
                    public void onError(Header[] headers, HttpManage.Error error) {
                        dismissLoading();
                    }

                    @Override
                    public void onSuccess(int code, String response) {
                        LogUtil.d(response);
                        dismissLoading();
                        BaseEntity<Integer> result = new Gson().fromJson(response, new TypeToken<BaseEntity<Integer>>() {
                        }.getType());
                        if (result.getCode() == 200)
                            tvFilterDay.setText(getString(R.string.text_days, result.getData() + ""));
                    }
                });
                break;
        }
    }


    private void initData() {
        HttpManage.getInstance().devDetail(device.getDeviceId(), new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {

            }

            @Override
            public void onSuccess(int code, String response) {
                LogUtil.d("devDetail->" + response);
                BaseEntity<DeviceDetail> result = new Gson().fromJson(response, new TypeToken<BaseEntity<DeviceDetail>>() {
                }.getType());

                if (result.getCode() == 200) {
                    tvFilterSurplus.setText((int) ((result.getData().getFilterSurplusDays() / 90f) * 100) + "%");
                    tvFilterDay.setText(getString(R.string.text_days, result.getData().getFilterSurplusDays() + ""));
                }
            }
        });
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_black_ic);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText(getString(R.string.text_filter_element));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_filter_element;
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
