package com.voocoo.pet.modules.add.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
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
import com.voocoo.pet.common.event.SetOutWaterEvent;
import com.voocoo.pet.common.event.AddDeviceSuccessEvent;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.dev.activity.SetOutWaterActivity;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import cn.forward.androids.utils.LogUtil;

public class SelModeActivity extends AbsBaseActivity {


    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;
    @BindView(R.id.top_title)
    TextView tvTitle;
    @BindView(R.id.ly_smart)
    View lySmart;
    @BindView(R.id.iv_ganying_sel)
    ImageView ivGanyingSel;

    String mac;
    String name;
    String wifiName;
    int selMode = 3;
    int retryNum = 0;
    private boolean isSelGanying = false;

    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (retryNum >= 20) {
                Intent intent = new Intent(SelModeActivity.this, BindDeviceFailActivity.class);
                startActivity(intent);
                finish();
            } else {
                retryNum++;
                HttpManage.getInstance().addDev(name, selMode + "", mac, new HttpManage.ResultCallback<String>() {
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

                                    HttpManage.getInstance().reAddDev(name, selMode + "", mac,wifiName, new HttpManage.ResultCallback<String>() {
                                        @Override
                                        public void onError(Header[] headers, HttpManage.Error error) {
                                            dismissLoading();
                                            LogUtil.d("addDev onError");
                                        }

                                        @Override
                                        public void onSuccess(int code, String response) {
                                            dismissLoading();
                                            LogUtil.d("addDev onSuccess->" + response);
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
        EventBus.getDefault().register(this);
        setSupportActionBar(topToolbar);
        tvTitle.setText(getString(R.string.text_sel_mode));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        selMode = getIntent().getIntExtra("mode", 3);
        mac = getIntent().getStringExtra("mac");
        name = getIntent().getStringExtra("name");
        wifiName = getIntent().getStringExtra("wifiName");
        if (selMode == 4) {
            lySmart.setVisibility(View.VISIBLE);
        } else {
            lySmart.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StyledDialog.init(this);
    }

    @Nullable
    @Override
    protected BaseActivityPresenter createPresenter() {
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new AddDeviceSuccessEvent());
    }

    int water = 1;

    /**
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetOutWater(SetOutWaterEvent event) {
        water = event.water;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_mode;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @OnClick({R.id.ly_smart, R.id.ly_ganying, R.id.btn_ensure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ly_smart:
                selMode = 4;
                Intent intent = new Intent(SelModeActivity.this, SetOutWaterActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("mac", mac);
                intent.putExtra("mode", selMode);
                intent.putExtra("wifiName", wifiName);

                intent.putExtra("isFromAdd", true);
                startActivity(intent);
                break;
            case R.id.ly_ganying:
                selMode = 3;
                if (isSelGanying) {
                    isSelGanying = false;
                    ivGanyingSel.setVisibility(View.INVISIBLE);
                } else {
                    isSelGanying = true;
                    ivGanyingSel.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_ensure:
                showLoading();
                retryNum = 0;
                HttpManage.getInstance().addDev(name, selMode + "", mac, new HttpManage.ResultCallback<String>() {
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
                                            LogUtil.d("addDev onError");
                                        }

                                        @Override
                                        public void onSuccess(int code, String response) {
                                            dismissLoading();
                                            LogUtil.d("addDev onSuccess->" + response);
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
                break;
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
