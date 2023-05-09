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
import com.voocoo.pet.common.event.AddDeviceSuccessEvent;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.dev.activity.FeedPlanActivity;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import cn.forward.androids.utils.LogUtil;

/**
 * 喂食器设置页面
 */
public class SelFamilyModeForFeedActivity extends AbsBaseActivity {


    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;
    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.iv_sel_single_cat)
    ImageView ivSelSingleCat;
    @BindView(R.id.iv_sel_single_dog)
    ImageView ivSelSingleDog;

    String mac;
    String name;

    int selMode = 0;
    int retryNum = 0;

    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (retryNum >= 20) {
                Intent intent = new Intent(SelFamilyModeForFeedActivity.this, BindDeviceFailActivity.class);
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
            }
        }
    };
    private String wifiName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(topToolbar);
        tvTitle.setText(getString(R.string.text_sel_mode));
        mac = getIntent().getStringExtra("mac");
        name = getIntent().getStringExtra("name");
        wifiName = getIntent().getStringExtra("wifiName");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setFamilyModeUi(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StyledDialog.init(this);
    }

    private void setFamilyModeUi(int mode) {
        selMode = mode;
        ivSelSingleCat.setImageResource(R.mipmap.check_box_normal);
        ivSelSingleDog.setImageResource(R.mipmap.check_box_normal);
        if (mode == 0) {
            ivSelSingleCat.setImageResource(R.mipmap.checkbox_select);
        } else if (mode == 1) {
            ivSelSingleDog.setImageResource(R.mipmap.checkbox_select);
        }
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
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_family_model2;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @OnClick({R.id.rl_single_cat, R.id.rl_single_dog, R.id.btn_complete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_single_cat:
                setFamilyModeUi(0);
                break;
            case R.id.rl_single_dog:
                setFamilyModeUi(1);
                break;
            case R.id.btn_complete:
                if (selMode == 0 || selMode == 1) {
                    //都去
                    //单狗
                    Intent intent = new Intent(SelFamilyModeForFeedActivity.this, FeedPlanActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("mac", mac);
                    intent.putExtra("mode", selMode);
                    intent.putExtra("wifiName",wifiName );
                    startActivity(intent);
                    finish();
                } else {
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
                break;
        }
    }

}
