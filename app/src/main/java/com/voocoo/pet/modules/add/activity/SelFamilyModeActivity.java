package com.voocoo.pet.modules.add.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
import com.voocoo.pet.modules.dev.activity.SetOutWaterActivity;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import cn.forward.androids.utils.LogUtil;

public class SelFamilyModeActivity extends AbsBaseActivity {


    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;
    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.iv_sel_single_cat)
    ImageView ivSelSingleCat;
    @BindView(R.id.iv_sel_single_dog)
    ImageView ivSelSingleDog;
    @BindView(R.id.iv_sel_two_cat)
    ImageView ivSelTwoCat;
    @BindView(R.id.iv_sel_three_cat)
    ImageView ivSelThreeCat;

    @BindView(R.id.btn_complete)
    Button btnComplete;

    @BindView(R.id.view3)
    View view3;

    String mac;
    String name;
    String wifiName;
    int selMode = 3;
    int retryNum = 0;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (retryNum >= 20) {
                Intent intent = new Intent(SelFamilyModeActivity.this, BindDeviceFailActivity.class);
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
        setSupportActionBar(topToolbar);
        tvTitle.setText(getString(R.string.text_sel_family));
        mac = getIntent().getStringExtra("mac");
        name = getIntent().getStringExtra("name");
        wifiName = getIntent().getStringExtra("name");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setFamilyModeUi(3);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StyledDialog.init(this);
    }

    private void setFamilyModeUi(int mode) {
        if (mode == 4) {
            btnComplete.setText(getString(R.string.next));
        } else {
            btnComplete.setText(getString(R.string.start_use));
        }
        selMode = mode;
        ivSelSingleCat.setImageResource(R.mipmap.check_box_normal);
        ivSelSingleDog.setImageResource(R.mipmap.check_box_normal);
        ivSelTwoCat.setImageResource(R.mipmap.check_box_normal);
        ivSelThreeCat.setImageResource(R.mipmap.check_box_normal);
        if (mode == 3) {
            ivSelSingleCat.setImageResource(R.mipmap.checkbox_select);
            view3.setVisibility(View.GONE);
        } else if (mode == 4) {
            ivSelSingleDog.setImageResource(R.mipmap.checkbox_select);
            view3.setVisibility(View.VISIBLE);
        } else if (mode == 5) {
            ivSelTwoCat.setImageResource(R.mipmap.checkbox_select);
            view3.setVisibility(View.GONE);
        } else {
            ivSelThreeCat.setImageResource(R.mipmap.checkbox_select);
            view3.setVisibility(View.GONE);
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
        return R.layout.activity_select_family_model;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @OnClick({R.id.rl_single_cat, R.id.rl_single_dog, R.id.rl_two_cat, R.id.rl_three_cat, R.id.btn_complete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_single_cat:
                setFamilyModeUi(3);
                break;
            case R.id.rl_single_dog:
                setFamilyModeUi(4);
                break;
            case R.id.rl_two_cat:
                setFamilyModeUi(5);
                break;
            case R.id.rl_three_cat:
                setFamilyModeUi(6);
                break;
            case R.id.btn_complete:
                if (selMode == 4) {
                    Intent intent = new Intent(SelFamilyModeActivity.this, SelModeActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("mac", mac);
                    intent.putExtra("mode", selMode);
                    intent.putExtra("wifiName", wifiName);
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
                break;
        }
    }

}
