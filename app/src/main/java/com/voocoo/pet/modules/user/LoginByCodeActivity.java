package com.voocoo.pet.modules.user;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.utils.CommonUtil;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.common.utils.Validations;
import com.voocoo.pet.common.widgets.AppDialog;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Register;
import com.voocoo.pet.entity.Restful.Login;
import com.voocoo.pet.entity.UserInfo;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.PetApp;
import com.voocoo.pet.modules.dev.activity.DevSettingActivity;
import com.voocoo.pet.modules.dev.activity.UseHelpActivity;
import com.voocoo.pet.modules.main.MainActivity;
import com.voocoo.pet.modules.mine.SettingActivity;
import com.voocoo.pet.modules.web.LocalWebActivity;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jiguang.analytics.android.api.JAnalyticsInterface;
import cn.jiguang.api.utils.JCollectionAuth;
import cn.jpush.android.api.JPushInterface;

public class LoginByCodeActivity extends AbsBaseActivity {

    @BindView(R.id.et_phone)
    EditText etPhone;

    @BindView(R.id.et_pwd)
    EditText etPwd;

    @BindView(R.id.btn_login)
    Button btnLogin;

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    private boolean isMatchPhone = false;
    private boolean isPasswordEmpty = false;
    private String phone;

    @BindView(R.id.tv_area)
    TextView tvArea;

    @BindView(R.id.tv_get_code)
    TextView tvGetCode;

    int countTime;

    @BindView(R.id.cb_privacy)
    CheckBox cbPrivacy;

    @SuppressLint("HandlerLeak")
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (countTime == 0) {
                tvGetCode.setEnabled(true);
                tvGetCode.setBackgroundResource(R.drawable.bg_btn_able);
                tvGetCode.setText("获取验证码");
            } else {
                tvGetCode.setEnabled(false);
                tvGetCode.setBackgroundResource(R.drawable.bg_btn_unable);
                countTime--;
                tvGetCode.setText("重新获取" + countTime + "秒");
                mhandler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_by_code;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phone = getIntent().getStringExtra("phone");
        initView();

        tvArea.setText(SharedPreferencesUtil.queryValue("area_code", "+86"));

        if (!SharedPreferencesUtil.queryBooleanValue("isAgree")) {
            AppDialog.privacyDialog(LoginByCodeActivity.this, getString(R.string.text_privacy), getString(R.string.ensure), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginByCodeActivity.this, UseHelpActivity.class);
                    intent.putExtra("type", 3);
                    startActivity(intent);
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginByCodeActivity.this, UseHelpActivity.class);
                    intent.putExtra("type", 4);
                    startActivity(intent);
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PetApp.getInstance().setupUMeng();

                    SharedPreferencesUtil.keepShared("isAgree", true);
                    JCollectionAuth.setAuth(LoginByCodeActivity.this,true);
                    JAnalyticsInterface.init(LoginByCodeActivity.this);
                    JAnalyticsInterface.initCrashHandler(LoginByCodeActivity.this);

                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    ActivityManager manager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
                    manager.killBackgroundProcesses(getPackageName());
                    System.exit(0);
                }
            }).show();
        } else {
            PetApp.getInstance().setupUMeng();
            JCollectionAuth.setAuth(LoginByCodeActivity.this,true);
            JAnalyticsInterface.init(LoginByCodeActivity.this);
            JAnalyticsInterface.initCrashHandler(LoginByCodeActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mhandler.removeCallbacksAndMessages(null);
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Validations.matchesPhoneNumber(etPhone.getText().toString())) {
                    isMatchPhone = true;
                } else {
                    isMatchPhone = false;
                }
                if (isMatchPhone && etPwd.getText().length() == 6) {
                    setLoginBtnEnabled(true);
                } else {
                    setLoginBtnEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    isPasswordEmpty = true;
                } else {
                    isPasswordEmpty = false;
                }
                if (isMatchPhone && s.length() == 6 && !isPasswordEmpty) {
                    setLoginBtnEnabled(true);
                } else {
                    setLoginBtnEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    isPasswordEmpty = true;
                } else {
                    isPasswordEmpty = false;
                }
                if (isMatchPhone && s.length() == 6 && !isPasswordEmpty) {
                    setLoginBtnEnabled(true);
                } else {
                    setLoginBtnEnabled(false);
                }
            }
        });
    }

    public void setLoginBtnEnabled(boolean isEnabled) {
        if (isEnabled) {
            btnLogin.setEnabled(true);
            btnLogin.setBackgroundResource(R.drawable.bg_btn_able);
        } else {
            btnLogin.setEnabled(false);
            btnLogin.setBackgroundResource(R.drawable.bg_btn_unable);
        }
    }

    @OnClick({R.id.tv_area, R.id.tv_get_code, R.id.btn_login, R.id.tv_privacy_protocol, R.id.tv_license, R.id.iv_wechat})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.tv_area:
                AppDialog.showSelAreaCode(LoginByCodeActivity.this, new AppDialog.AreaCodeSetListener() {
                    @Override
                    public void setCode(String code) {
                        tvArea.setText(code);
                        SharedPreferencesUtil.keepShared("area_code", code);
                    }
                }).show();
                break;
            case R.id.tv_get_code:
                if (!cbPrivacy.isChecked()) {
                    showToast(getString(R.string.text_agree_first));
                    return;
                }
                if (TextUtils.isEmpty(etPhone.getText().toString())) {
                    showToast(getString(R.string.input_username_hint));
                    return;
                }
                if (Validations.matchesPhoneNumber(etPhone.getText().toString())) {
                    showLoading();
                    HttpManage.getInstance().getVerifyCode(etPhone.getText().toString(), new HttpManage.ResultCallback<String>() {
                        @Override
                        public void onError(Header[] headers, HttpManage.Error error) {
                            dismissLoading();
                        }

                        @Override
                        public void onSuccess(int code, String response) {
                            BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                            }.getType());
                            if (result.getCode() == 200) {
                                dismissLoading();
                                LogUtil.d(response);
                                countTime = 60;
                                mhandler.sendEmptyMessageDelayed(0, 1000);
                            } else {
                                dismissLoading();
                                showToast(result.getMsg());
                            }
                        }
                    });
                } else {
                    showToast(getString(R.string.input_username_error));
                }
                break;
            case R.id.btn_login:
                if (!cbPrivacy.isChecked()) {
                    showToast(getString(R.string.text_agree_first));
                    return;
                }
                if (TextUtils.isEmpty(etPhone.getText().toString())) {
                    showToast(getString(R.string.input_phone_hint));
                    return;
                }
                if (TextUtils.isEmpty(etPwd.getText().toString())) {
                    showToast(getString(R.string.input_code_hint));
                    return;
                }

                if (Validations.matchesPhoneNumber(etPhone.getText().toString())) {
                    showLoading();
                    HttpManage.getInstance().loginByCode(etPhone.getText().toString(), etPwd.getText().toString(), new HttpManage.ResultCallback<String>() {
                        @Override
                        public void onError(Header[] headers, HttpManage.Error error) {
                            dismissLoading();
                        }

                        @Override
                        public void onSuccess(int code, String response) {
                            dismissLoading();
                            LogUtil.d(response);
                            BaseEntity<Login> result = new Gson().fromJson(response, new TypeToken<BaseEntity<Login>>() {
                            }.getType());
                            if (result.getCode() == 200) {
//                                JPushInterface.setMobileNumber(LoginByCodeActivity.this,1,etPhone.getText().toString());
                                JPushInterface.setAlias(LoginByCodeActivity.this,1,etPhone.getText().toString());

                                showToast(getString(R.string.login_success));
                                SharedPreferencesUtil.keepShared("phone", etPhone.getText().toString());
                                SharedPreferencesUtil.keepShared("token", result.getData().getAccess_token());
                                startActivity(new Intent(LoginByCodeActivity.this, MainActivity.class));
                                getUserInfo();
                                finish();
                            } else {
                                showToast(result.getMsg());
                            }
                        }
                    });
                } else {
                    showToast(getString(R.string.input_username_error));
                }

                break;
            case R.id.iv_wechat:
                //开始绑定
                if (!cbPrivacy.isChecked()) {
                    showToast(getString(R.string.text_agree_first));
                    return;
                }
                if (isWxInstall(LoginByCodeActivity.this)) {
                    SharedPreferencesUtil.keepShared("isUseWechat", true);
                    UMShareAPI.get(LoginByCodeActivity.this).deleteOauth(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
                        @Override
                        public void onStart(SHARE_MEDIA share_media) {

                        }

                        @Override
                        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                            UMShareAPI.get(LoginByCodeActivity.this).getPlatformInfo(LoginByCodeActivity.this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {

                                @Override
                                public void onStart(SHARE_MEDIA share_media) {

                                }

                                @Override
                                public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> userInfo) {
                                    LogUtil.d("userInfo->" + userInfo);
                                    showLoading();
                                    HttpManage.getInstance().loginByWechat(new Gson().toJson(userInfo), new HttpManage.ResultCallback<String>() {
                                        @Override
                                        public void onError(Header[] headers, HttpManage.Error error) {
                                            Intent intent = new Intent(LoginByCodeActivity.this, BindWechatActivity.class);
                                            intent.putExtra("wxJson", new Gson().toJson(userInfo));
                                            startActivity(intent);
                                        }

                                        @Override
                                        public void onSuccess(int code, String response) {
                                            LogUtil.d(response);
                                            dismissLoading();

                                            BaseEntity<Login> result = new Gson().fromJson(response, new TypeToken<BaseEntity<Login>>() {
                                            }.getType());
                                            if (result.getCode() == 200) {

                                                showToast(getString(R.string.login_success));
                                                SharedPreferencesUtil.keepShared("phone", etPhone.getText().toString());
                                                SharedPreferencesUtil.keepShared("token", result.getData().getAccess_token());
                                                startActivity(new Intent(LoginByCodeActivity.this, MainActivity.class));
                                                finish();
                                            } else {
                                                Intent intent = new Intent(LoginByCodeActivity.this, BindWechatActivity.class);
                                                intent.putExtra("wxJson", new Gson().toJson(userInfo));
                                                startActivity(intent);
                                            }
                                        }
                                    });

                                }

                                @Override
                                public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                                }

                                @Override
                                public void onCancel(SHARE_MEDIA share_media, int i) {
                                    dismissLoading();
                                }
                            });
                        }

                        @Override
                        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

                        }

                        @Override
                        public void onCancel(SHARE_MEDIA share_media, int i) {

                        }
                    });
                    //getPlatformIfo(this, SHARE_MEDIA.WEIXIN);
                } else {
                    showToast("没有安装微信");
                }
                break;
            case R.id.tv_privacy_protocol:
                Intent intent = new Intent(LoginByCodeActivity.this, UseHelpActivity.class);
                intent.putExtra("type", 3);
                startActivity(intent);
                break;
            case R.id.tv_license:
                intent = new Intent(LoginByCodeActivity.this, UseHelpActivity.class);
                intent.putExtra("type", 4);
                startActivity(intent);
                break;
        }
    }

    /**
     * 检测是否安装微信 * * @param context * @return
     */
    public static boolean isWxInstall(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }
    private void getUserInfo() {
        HttpManage.getInstance().getUserInfo(new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {

            }

            @Override
            public void onSuccess(int code, String response) {
                LogUtil.d("getUserInfo->" + response);
                BaseEntity<UserInfo> result = new Gson().fromJson(response, new TypeToken<BaseEntity<UserInfo>>() {
                }.getType());
                if (result.getCode() == 200) {
                    SharedPreferencesUtil.keepShared("userId", result.getData().getUserId());
                }
            }
        });
    }
    private void getPlatformIfo(final Activity activity, final SHARE_MEDIA platform) {
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        UMShareAPI.get(LoginByCodeActivity.this).setShareConfig(config);

        UMShareAPI.get(LoginByCodeActivity.this).deleteOauth(activity, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                UMShareAPI.get(LoginByCodeActivity.this).doOauthVerify(activity, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> userInfo) {
                        UMShareAPI.get(LoginByCodeActivity.this).getPlatformInfo(activity, platform, new UMAuthListener() {

                            @Override
                            public void onStart(SHARE_MEDIA share_media) {

                            }

                            @Override
                            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> userInfo) {
                                LogUtil.d("userInfo->" + userInfo);
                                showLoading();
                                HttpManage.getInstance().loginByWechat(new Gson().toJson(userInfo), new HttpManage.ResultCallback<String>() {
                                    @Override
                                    public void onError(Header[] headers, HttpManage.Error error) {
                                        Intent intent = new Intent(LoginByCodeActivity.this, BindWechatActivity.class);
                                        intent.putExtra("wxJson", new Gson().toJson(userInfo));
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onSuccess(int code, String response) {
                                        LogUtil.d(response);
                                        dismissLoading();

                                        BaseEntity<Login> result = new Gson().fromJson(response, new TypeToken<BaseEntity<Login>>() {
                                        }.getType());
                                        if (result.getCode() == 200) {

                                            showToast(getString(R.string.login_success));
                                            SharedPreferencesUtil.keepShared("phone", etPhone.getText().toString());
                                            SharedPreferencesUtil.keepShared("token", result.getData().getAccess_token());
                                            startActivity(new Intent(LoginByCodeActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            Intent intent = new Intent(LoginByCodeActivity.this, BindWechatActivity.class);
                                            intent.putExtra("wxJson", new Gson().toJson(userInfo));
                                            startActivity(intent);
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                            }

                            @Override
                            public void onCancel(SHARE_MEDIA share_media, int i) {
                                dismissLoading();
                            }
                        });
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media, int i) {

                    }
                });
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {

            }
        });
    }

    @Nullable
    @Override
    protected BaseActivityPresenter createPresenter() {
        return null;
    }
}
