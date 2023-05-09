package com.voocoo.pet.modules.dev.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyDialogListener;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.event.DelDeviceEvent;
import com.voocoo.pet.common.event.DelFeedPlanFinishEvent;
import com.voocoo.pet.common.event.RenameDeviceEvent;
import com.voocoo.pet.common.utils.CommonUtil;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.common.utils.ToastUtil;
import com.voocoo.pet.common.widgets.AppDialog;
import com.voocoo.pet.common.widgets.SwitchView;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Device;
import com.voocoo.pet.entity.DeviceDetail;
import com.voocoo.pet.entity.queryOTAbean;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.add.activity.AddDeviceStepOneActivity;
import com.voocoo.pet.modules.add.activity.AddDeviceStepTwoActivity;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

public class FeedDevSettingActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_status)
    TextView tvStatus;

    @BindView(R.id.ly_time)
    View timeView;
    @BindView(R.id.tv_time)
    TextView tvTime;

    @BindView(R.id.line2)
    View line2;

    @BindView(R.id.switch_view_night)
    SwitchView svNight;

    @BindView(R.id.switch_view_lock)
    SwitchView svLock;

    @BindView(R.id.tv_wifi)
    TextView tvWifi;

    @BindView(R.id.tv_city)
    TextView tvCity;

    Device device;

    int sHour = 23;
    int sMin = 0;
    int eHour = 7;
    int eMin = 0;
    String onLine;
    public static final int version_Code = 10010;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        device = (Device) getIntent().getSerializableExtra("device");


        initView();
        initData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        StyledDialog.init(this);
        setCurWifiSsid();
        queryVersion();
    }

    @OnClick({R.id.ly_city,R.id.ly_wifi, R.id.ly_time, R.id.btn_del, R.id.tv_name, R.id.ly_help})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ly_city:
                Intent intent1 = new Intent(FeedDevSettingActivity.this, OTAActivity.class);
                intent1.putExtra("device", device);
                int powerPlug =  getIntent().getIntExtra("powerPlug",-1);
                intent1.putExtra("powerPlug",powerPlug);
                intent1.putExtra("onLine",onLine);
                startActivity(intent1);
                break;

            case R.id.ly_wifi:
                if (SharedPreferencesUtil.queryBooleanValue("is_go_water_guide")) {
                    Intent intent = new Intent(FeedDevSettingActivity.this, AddDeviceStepTwoActivity.class);
                    intent.putExtra("type", 1);
                    intent.putExtra("isShowGuide", true);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(FeedDevSettingActivity.this, AddDeviceStepOneActivity.class);
                    intent.putExtra("type", 1);
                    startActivity(intent);
                    SharedPreferencesUtil.keepShared("is_go_water_guide", true);
                }
                break;
            case R.id.ly_time:
                AppDialog.showSetTime(FeedDevSettingActivity.this, sHour, sMin, eHour, eMin, new AppDialog.TimerSetListener() {
                    @Override
                    public void setTimer(int startHour, int startMinutes, int endHour, int endMinutes) {
                        tvTime.setText(CommonUtil.zero(startHour) + ":" + CommonUtil.zero(startMinutes) + " - " + CommonUtil.zero(endHour) + ":" + CommonUtil.zero(endMinutes));
                        showLoading();
                        HttpManage.getInstance().sendFeedNightMode(device.getDeviceId(), "0", getSupplementStr(endHour) + ":" + getSupplementStr(endMinutes),
                                getSupplementStr(startHour) + ":" + getSupplementStr(startMinutes),
                                new HttpManage.ResultCallback<String>() {
                                    @Override
                                    public void onError(Header[] headers, HttpManage.Error error) {
                                        dismissLoading();
                                        showToast(error.getMsg());
                                    }

                                    @Override
                                    public void onSuccess(int code, String response) {
                                        dismissLoading();
                                        LogUtil.d(response);
                                        BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                                        }.getType());
                                        showToast(result.getMsg());
                                    }
                                });
                    }
                }).show();
                break;
            case R.id.ly_help:
                Intent intent = new Intent(FeedDevSettingActivity.this, UseHelpActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
                break;
            case R.id.tv_name:
                StyledDialog.buildNormalInput("", getString(R.string.text_nickname), "", tvName.getText().toString(), "", new MyDialogListener() {
                    @Override
                    public void onFirst() {

                    }

                    @Override
                    public void onSecond() {
                        //save name
                    }

                    @Override
                    public boolean onInputValid(CharSequence input1, CharSequence input2, EditText editText1, EditText editText2) {
                        if (TextUtils.isEmpty(input1)) {
                            showToast(getString(R.string.hint_input_dev_name));
                            return super.onInputValid(input1, input2, editText1, editText2);
                        }
                        tvName.setText(input1);
                        showLoading();
                        HttpManage.getInstance().updateFeedDevDetail(device.getDeviceId(), input1.toString(), new HttpManage.ResultCallback<String>() {
                            @Override
                            public void onError(Header[] headers, HttpManage.Error error) {
                                dismissLoading();
                            }

                            @Override
                            public void onSuccess(int code, String response) {
                                LogUtil.d("updateDevDetail->" + response);
                                dismissLoading();
                                BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                                }.getType());
                                if (result.getCode() == 200) {
                                    EventBus.getDefault().post(new RenameDeviceEvent(input1.toString()));
                                }
                            }
                        });

                        return super.onInputValid(input1, input2, editText1, editText2);
                    }
                }).setBtnColor(R.color.color_tab_select, R.color.color_tab_select, R.color.color_tab_select).setBtnText(getString(R.string.confirm), getString(R.string.cancel)).show();
                break;
            case R.id.btn_del:
                StyledDialog.buildIosAlert(getString(R.string.text_ensure_del_dev), getString(R.string.text_del_dev_tips), new MyDialogListener() {
                    @Override
                    public void onFirst() {
                        showLoading();
                        HttpManage.getInstance().delDev(device.getDeviceId(), new HttpManage.ResultCallback<String>() {
                            @Override
                            public void onError(Header[] headers, HttpManage.Error error) {
                                dismissLoading();
                            }

                            @Override
                            public void onSuccess(int code, String response) {
                                LogUtil.d(response);
                                dismissLoading();
                                BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                                }.getType());
                                if (result.getCode() == 200) {
                                    EventBus.getDefault().post(new DelDeviceEvent());
                                    finish();
                                }
                            }
                        });
                    }

                    @Override
                    public void onSecond() {

                    }
                }).show();


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
                    tvStatus.setText(result.getData().getStatus().equals("0") ? getString(R.string.text_online) : getString(R.string.text_offline2));
                    onLine = result.getData().getStatus();
                    tvStatus.setTextColor(Color.parseColor(result.getData().getStatus().equals("0") ? "#28BD49" : "#9FA6BD"));

                    svNight.setOpened(result.getData().nightModel.equals("0"));
                    if (result.getData().nightModel.equals("0")) {
                        timeView.setVisibility(View.VISIBLE);
                        line2.setVisibility(View.VISIBLE);
                    } else {
                        timeView.setVisibility(View.GONE);
                        line2.setVisibility(View.GONE);
                    }
                    sHour = Integer.parseInt(result.getData().nightStartTime.split(":")[0]);
                    sMin = Integer.parseInt(result.getData().nightStartTime.split(":")[1]);
                    eHour = Integer.parseInt(result.getData().nightEndTime.split(":")[0]);
                    eMin = Integer.parseInt(result.getData().nightEndTime.split(":")[1]);
                    tvTime.setText(result.getData().nightStartTime + " - " + result.getData().nightEndTime);

                    svLock.setOpened(result.getData().childLock.equals("0"));
                }
            }
        });
    }

    public static String getSupplementStr(int value) {
        if (value < 10) {
            return "0" + value;
        }
        return value + "";
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_black_ic);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText(getString(R.string.text_setting));

        tvName.setText(device.getDeviceName());

        svLock.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                svLock.setOpened(true);
                showLoading();
                HttpManage.getInstance().sendFeedChildLock(device.getDeviceId(), "0",
                        new HttpManage.ResultCallback<String>() {
                            @Override
                            public void onError(Header[] headers, HttpManage.Error error) {
                                dismissLoading();
                            }

                            @Override
                            public void onSuccess(int code, String response) {
                                dismissLoading();
                                LogUtil.d(response);
                                BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                                }.getType());
                                showToast(result.getMsg());
                            }
                        });
            }

            @Override
            public void toggleToOff(SwitchView view) {
                svLock.setOpened(false);
                showLoading();
                HttpManage.getInstance().sendFeedChildLock(device.getDeviceId(), "1",
                        new HttpManage.ResultCallback<String>() {
                            @Override
                            public void onError(Header[] headers, HttpManage.Error error) {
                                dismissLoading();
                            }

                            @Override
                            public void onSuccess(int code, String response) {
                                dismissLoading();
                                LogUtil.d(response);
                                BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                                }.getType());
                                showToast(result.getMsg());
                            }
                        });
            }
        });
        svNight.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                svNight.setOpened(true);
                timeView.setVisibility(View.VISIBLE);
                line2.setVisibility(View.VISIBLE);
                int sHour = 23;
                int sMin = 0;
                int eHour = 7;
                int eMin = 0;
                HttpManage.getInstance().sendFeedNightMode(device.getDeviceId(), "0", getSupplementStr(eHour) + ":" + getSupplementStr(eMin),
                        getSupplementStr(sHour) + ":" + getSupplementStr(sMin),
                        new HttpManage.ResultCallback<String>() {
                            @Override
                            public void onError(Header[] headers, HttpManage.Error error) {
                                dismissLoading();
                            }

                            @Override
                            public void onSuccess(int code, String response) {
                                dismissLoading();
                                LogUtil.d(response);
                                BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                                }.getType());
                                showToast(result.getMsg());
                            }
                        });
            }

            @Override
            public void toggleToOff(SwitchView view) {
                svNight.setOpened(false);
                timeView.setVisibility(View.GONE);
                line2.setVisibility(View.GONE);
                showLoading();
                HttpManage.getInstance().sendFeedNightMode(device.getDeviceId(), "1", new HttpManage.ResultCallback<String>() {
                    @Override
                    public void onError(Header[] headers, HttpManage.Error error) {

                    }

                    @Override
                    public void onSuccess(int code, String response) {
                        dismissLoading();
                        LogUtil.d(response);
                        BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                        }.getType());
                        showToast(result.getMsg());
                    }
                });
            }
        });

    }

    private void queryVersion() {
        HttpManage.getInstance().selectFeederVersion(device.getDeviceId(), new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                Log.e("TAG", "onError: " + error.getMsg() );
            }

            @Override
            public void onSuccess(int code, String response) {
                Log.e("TAG", "onSuccess: " + response );
                BaseEntity<queryOTAbean> result = new Gson().fromJson(response, new TypeToken<BaseEntity<queryOTAbean>>() {
                }.getType());
                if(result.getCode() == 200){
                    if (result.getData().getLastVersion() == 0) {
                        tvCity.setText(getString(R.string.isNewVersion));
                    } else {
                        tvCity.setText(getString(R.string.current_version)+result.getData().getCurrentVersion());
                    }
                }else{
                    showToast(getString(R.string.query_fail));
                }

            }
        });
    }

    public void setCurWifiSsid() {
        String ssid = CommonUtil.getCurrentWifiSSID(FeedDevSettingActivity.this);
        if (!TextUtils.isEmpty(ssid)) {
            if (ssid.startsWith("\""))
                ssid = ssid.substring(1, ssid.length());
            if (ssid.endsWith("\""))
                ssid = ssid.substring(0, ssid.length() - 1);

            if (ssid.equals("<unknown ssid>")) {
                tvWifi.setText("");
                return;
            }
            tvWifi.setText(ssid);
        } else {
            tvWifi.setText("");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feed_dev_setting;
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
