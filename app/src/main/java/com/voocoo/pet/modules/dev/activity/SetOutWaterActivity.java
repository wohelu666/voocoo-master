package com.voocoo.pet.modules.dev.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyDialogListener;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.event.AddDeviceSuccessEvent;
import com.voocoo.pet.common.event.SetOutWaterEvent;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Device;
import com.voocoo.pet.entity.DeviceDetail;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.add.activity.BindDeviceFailActivity;
import com.voocoo.pet.modules.add.activity.SelModeActivity;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

public class SetOutWaterActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.tv_tips)
    TextView tvTips;
    @BindView(R.id.tv_select1)
    TextView tvSelect1;
    @BindView(R.id.iv_select1)
    View ivSelect1;
    @BindView(R.id.tv_select2)
    TextView tvSelect2;
    @BindView(R.id.iv_select2)
    View ivSelect2;
    @BindView(R.id.tv_select3)
    TextView tvSelect3;
    @BindView(R.id.iv_select3)
    View ivSelect3;
    @BindView(R.id.tv_select4)
    TextView tvSelect4;
    @BindView(R.id.iv_select4)
    View ivSelect4;

    @BindView(R.id.sel1)
    TextView sel1;
    @BindView(R.id.sel2)
    TextView sel2;
    @BindView(R.id.sel3)
    TextView sel3;
    @BindView(R.id.sel4)
    TextView sel4;

    @BindView(R.id.tv_one_out_water)
    TextView tvOneOutWater;
    @BindView(R.id.tv_day_out_water)
    TextView tvDayOutWater;
    @BindView(R.id.tv_out_water_use_day)
    TextView tvOutWaterUseDay;

    int select = 1;
    Device device;
    String mac;
    String name;
    String wifiName;
    int selMode = 3;
    int retryNum = 0;

    @BindView(R.id.ly_progress)
    View lyProgress;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (retryNum >= 20) {
                Intent intent = new Intent(SetOutWaterActivity.this, BindDeviceFailActivity.class);
                startActivity(intent);
                finish();
            } else {
                retryNum++;
                HttpManage.getInstance().addDev(name, selMode + "", mac, select, new HttpManage.ResultCallback<String>() {
                    @Override
                    public void onError(Header[] headers, HttpManage.Error error) {
                        dismissLoading();
                        cn.forward.androids.utils.LogUtil.d("addDev onError");
                    }

                    @Override
                    public void onSuccess(int code, String response) {
                        cn.forward.androids.utils.LogUtil.d("addDev onSuccess->" + response);
                        BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                        }.getType());
                        if (result.getCode() == 200) {
                            EventBus.getDefault().post(new AddDeviceSuccessEvent());
                            finish();
                        } else if (result.getCode() == 500) {
                            if (!TextUtils.isEmpty(result.getMsg()) && result.getMsg().contains("已绑定")) {
                                EventBus.getDefault().post(new AddDeviceSuccessEvent());
                                finish();
                            } else {
                                mHandler.sendEmptyMessageDelayed(0, 3000);
                            }
                        } else if (result.getCode() == 502) {
                            dismissLoading();
                            StyledDialog.buildIosAlert(result.getMsg(), "", new MyDialogListener() {
                                @Override
                                public void onFirst() {
                                    showLoading();

                                    HttpManage.getInstance().reAddDev(name, selMode + "", mac, select, new HttpManage.ResultCallback<String>() {
                                        @Override
                                        public void onError(Header[] headers, HttpManage.Error error) {
                                            dismissLoading();
                                            cn.forward.androids.utils.LogUtil.d("addDev onError");
                                        }

                                        @Override
                                        public void onSuccess(int code, String response) {
                                            dismissLoading();
                                            cn.forward.androids.utils.LogUtil.d("addDev onSuccess->" + response);
                                            BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                                            }.getType());
                                            if (result.getCode() == 200) {
                                                EventBus.getDefault().post(new AddDeviceSuccessEvent());
                                                finish();
                                            } else {
                                                showToast(result.getMsg());
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onSecond() {

                                }
                            }).show();
                        } else {
                            mHandler.sendEmptyMessageDelayed(0, 3000);
                        }
                    }
                });
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        device = (Device) getIntent().getSerializableExtra("device");
        selMode = getIntent().getIntExtra("mode", 3);
        mac = getIntent().getStringExtra("mac");
        name = getIntent().getStringExtra("name");
        wifiName = getIntent().getStringExtra("wifiName");
        boolean isFromAdd = getIntent().getBooleanExtra("isFromAdd", false);
        if (isFromAdd) {
            lyProgress.setVisibility(View.VISIBLE);
        } else {
            lyProgress.setVisibility(View.GONE);
        }
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StyledDialog.init(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void setUi(int ml) {
        select = ml;
        tvSelect1.setTextColor(Color.parseColor("#6A6B71"));
        tvSelect2.setTextColor(Color.parseColor("#6A6B71"));
        tvSelect3.setTextColor(Color.parseColor("#6A6B71"));
        tvSelect4.setTextColor(Color.parseColor("#6A6B71"));
        ivSelect1.setBackgroundColor(Color.parseColor("#E4E9F2"));
        ivSelect2.setBackgroundColor(Color.parseColor("#E4E9F2"));
        ivSelect3.setBackgroundColor(Color.parseColor("#E4E9F2"));
        ivSelect4.setBackgroundColor(Color.parseColor("#E4E9F2"));
        if (ml == 0 || ml == 1) {
            tvSelect1.setTextColor(Color.parseColor("#FB7E37"));
            ivSelect1.setBackgroundColor(Color.parseColor("#FB7E37"));
            tvTips.setText(getString(R.string.text_out_water_text1));
            tvOneOutWater.setText("200ml");
            tvDayOutWater.setText("600ml");
            tvOutWaterUseDay.setText("8天");
            sel1.setVisibility(View.VISIBLE);
            sel2.setVisibility(View.INVISIBLE);
            sel3.setVisibility(View.INVISIBLE);
            sel4.setVisibility(View.INVISIBLE);
        } else if (ml == 2) {
            tvSelect2.setTextColor(Color.parseColor("#FB7E37"));
            ivSelect2.setBackgroundColor(Color.parseColor("#FB7E37"));
            tvTips.setText(getString(R.string.text_out_water_text2));
            tvOneOutWater.setText("250ml");
            tvDayOutWater.setText("750ml");
            tvOutWaterUseDay.setText("6天");
            sel1.setVisibility(View.INVISIBLE);
            sel2.setVisibility(View.VISIBLE);
            sel3.setVisibility(View.INVISIBLE);
            sel4.setVisibility(View.INVISIBLE);
        } else if (ml == 3) {
            tvSelect3.setTextColor(Color.parseColor("#FB7E37"));
            ivSelect3.setBackgroundColor(Color.parseColor("#FB7E37"));
            tvTips.setText(getString(R.string.text_out_water_text3));
            tvOneOutWater.setText("300ml");
            tvDayOutWater.setText("900ml");
            tvOutWaterUseDay.setText("5天");
            sel1.setVisibility(View.INVISIBLE);
            sel2.setVisibility(View.INVISIBLE);
            sel3.setVisibility(View.VISIBLE);
            sel4.setVisibility(View.INVISIBLE);
        } else if (ml == 4) {
            tvSelect4.setTextColor(Color.parseColor("#FB7E37"));
            ivSelect4.setBackgroundColor(Color.parseColor("#FB7E37"));
            tvTips.setText(getString(R.string.text_out_water_text4));
            tvOneOutWater.setText("350ml");
            tvDayOutWater.setText("1050ml");
            tvOutWaterUseDay.setText("4天");
            sel1.setVisibility(View.INVISIBLE);
            sel2.setVisibility(View.INVISIBLE);
            sel3.setVisibility(View.INVISIBLE);
            sel4.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.btn_ensure, R.id.ly_select1, R.id.ly_select2, R.id.ly_select3, R.id.ly_select4})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ensure:
                if (device == null) {
                    showLoading();
                    retryNum = 0;
                    HttpManage.getInstance().addDev(name, selMode + "", mac, select, new HttpManage.ResultCallback<String>() {
                        @Override
                        public void onError(Header[] headers, HttpManage.Error error) {
                            dismissLoading();
                            LogUtil.d("addDev onError");
                        }

                        @Override
                        public void onSuccess(int code, String response) {
                            LogUtil.d("addDev onSuccess->" + response);
                            BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                            }.getType());
                            if (result.getCode() == 200) {
                                EventBus.getDefault().post(new AddDeviceSuccessEvent());
                                finish();
                            } else if (result.getCode() == 500) {
                                if (!TextUtils.isEmpty(result.getMsg()) && result.getMsg().contains("已绑定")) {
                                    EventBus.getDefault().post(new AddDeviceSuccessEvent());
                                    finish();
                                } else {
                                    mHandler.sendEmptyMessageDelayed(0, 3000);
                                }
                            } else if (result.getCode() == 502) {
                                dismissLoading();
                                StyledDialog.buildIosAlert(result.getMsg(), "", new MyDialogListener() {
                                    @Override
                                    public void onFirst() {
                                        showLoading();

                                        HttpManage.getInstance().reAddDev(name, selMode + "", mac, wifiName,new HttpManage.ResultCallback<String>() {
                                            @Override
                                            public void onError(Header[] headers, HttpManage.Error error) {
                                                dismissLoading();
                                                cn.forward.androids.utils.LogUtil.d("addDev onError");
                                            }

                                            @Override
                                            public void onSuccess(int code, String response) {
                                                dismissLoading();
                                                cn.forward.androids.utils.LogUtil.d("addDev onSuccess->" + response);
                                                BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                                                }.getType());
                                                if (result.getCode() == 200) {
                                                    EventBus.getDefault().post(new AddDeviceSuccessEvent());
                                                    finish();
                                                } else {
                                                    showToast(result.getMsg());
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onSecond() {

                                    }
                                }).show();
                            } else {
                                mHandler.sendEmptyMessageDelayed(0, 3000);
                            }
                        }
                    });
                    return;
                }
                showLoading();
                HttpManage.getInstance().sendHomeMode(device.getDeviceId(), homeFamily, select, new HttpManage.ResultCallback<String>() {
                    @Override
                    public void onError(Header[] headers, HttpManage.Error error) {
                        dismissLoading();
                    }

                    @Override
                    public void onSuccess(int code, String response) {
                        dismissLoading();
                        LogUtil.d("sendHomeMode->" + response);
                        BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                        }.getType());
                        if (result.getCode() == 200) {
                            finish();
                        } else {
                            showToast(result.getMsg());
                        }
                    }
                });
                break;
            case R.id.ly_select1:
                setUi(1);
                break;
            case R.id.ly_select2:
                setUi(2);
                break;
            case R.id.ly_select3:
                setUi(3);
                break;
            case R.id.ly_select4:
                setUi(4);
                break;
        }
    }


    String homeFamily = "";

    private void initData() {
        if (device == null) {
            setUi(1);
            return;
        }
        showLoading();
        HttpManage.getInstance().devDetail(device.getDeviceId(), new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                dismissLoading();
            }

            @Override
            public void onSuccess(int code, String response) {
                dismissLoading();
                LogUtil.d("devDetail->" + response);
                BaseEntity<DeviceDetail> result = new Gson().fromJson(response, new TypeToken<BaseEntity<DeviceDetail>>() {
                }.getType());

                if (result.getCode() == 200) {
                    homeFamily = result.getData().getFamilyModel();
                    setUi(result.getData().getWater());
                }
            }
        });
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_black_ic);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText(getString(R.string.text_out_water_set));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_out_water_setting;
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
