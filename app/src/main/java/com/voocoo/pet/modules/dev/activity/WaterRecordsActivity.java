package com.voocoo.pet.modules.dev.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.BaseRowsEntity;
import com.voocoo.pet.entity.Device;
import com.voocoo.pet.entity.DeviceDetail;
import com.voocoo.pet.entity.Record;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.dev.adapter.UseRecordListAdapter;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class WaterRecordsActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.ly_records)
    View recordsView;
    @BindView(R.id.rv_water_records)
    RecyclerView rvWaterRecords;

    UseRecordListAdapter useRecordListAdapter;

    Device device;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        device = (Device) getIntent().getSerializableExtra("device");
        initView();
        initData();
    }


    private void initData() {
        HttpManage.getInstance().waterDevDetail(device.getDeviceId(), new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {

            }

            @Override
            public void onSuccess(int code, String response) {
                LogUtil.d("waterDevDetail->" + response);
                BaseRowsEntity<List<Record>> result = new Gson().fromJson(response, new TypeToken<BaseRowsEntity<List<Record>>>() {
                }.getType());

                if (result.getCode() == 200) {
                    useRecordListAdapter.setData(result.getRows());
                }
            }
        });
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_black_ic);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText(getString(R.string.text_water_records));

        rvWaterRecords.setLayoutManager(new LinearLayoutManager(WaterRecordsActivity.this));

        useRecordListAdapter = new UseRecordListAdapter(WaterRecordsActivity.this);
        rvWaterRecords.setAdapter(useRecordListAdapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_water_records;
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
