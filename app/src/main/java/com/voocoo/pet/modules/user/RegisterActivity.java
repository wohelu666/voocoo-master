package com.voocoo.pet.modules.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.utils.CommonUtil;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.common.utils.Validations;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Register;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.main.MainActivity;

import org.apache.http.Header;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterActivity extends AbsBaseActivity {

    @BindView(R.id.et_phone)
    EditText etPhone;

    @BindView(R.id.et_code)
    EditText etCode;

    @BindView(R.id.et_pwd)
    EditText etPwd;

    @BindView(R.id.et_pwd_re)
    EditText etPwdRe;

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.btn_register_and_login)
    Button btn;

    @BindView(R.id.tv_get_code)
    TextView tvGetCode;

    int countTime;

    private boolean isPhoneEmpty = true;
    private boolean isCodeEmpty = true;
    private boolean isPasswordEmpty = true;
    private boolean isPasswordReEmpty = true;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (countTime == 0) {
                tvGetCode.setEnabled(true);
                tvGetCode.setText("重新获取验证码");
            } else {
                tvGetCode.setEnabled(false);
                countTime--;
                tvGetCode.setText(countTime + "秒");
                mhandler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    isPhoneEmpty = true;
                } else {
                    isPhoneEmpty = false;
                }
                if (!isPhoneEmpty && !isCodeEmpty && !isPasswordEmpty && !isPasswordReEmpty) {
                    setLoginBtnEnabled(true);
                } else {
                    setLoginBtnEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    isCodeEmpty = true;
                } else {
                    isCodeEmpty = false;
                }
                if (!isPhoneEmpty && !isCodeEmpty && !isPasswordEmpty && !isPasswordReEmpty) {
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
                if (!isPhoneEmpty && !isCodeEmpty && !isPasswordEmpty && !isPasswordReEmpty) {
                    setLoginBtnEnabled(true);
                } else {
                    setLoginBtnEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etPwdRe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    isPasswordReEmpty = true;
                } else {
                    isPasswordReEmpty = false;
                }
                if (!isPhoneEmpty && !isCodeEmpty && !isPasswordEmpty && !isPasswordReEmpty) {
                    setLoginBtnEnabled(true);
                } else {
                    setLoginBtnEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setLoginBtnEnabled(boolean isEnabled) {
        if (isEnabled) {
            btn.setEnabled(true);
            btn.setBackgroundResource(R.drawable.bg_btn_normal);
        } else {
            btn.setEnabled(false);
            btn.setBackgroundResource(R.drawable.bg_btn_unable);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mhandler.removeCallbacksAndMessages(null);
    }

    @OnClick({R.id.tv_get_code, R.id.btn_register_and_login})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.tv_get_code:
                if (TextUtils.isEmpty(etPhone.getText().toString())) {
                    showToast(getString(R.string.input_phone_hint));
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
                            dismissLoading();
                            LogUtil.d(response);
                            countTime = 60;
                            mhandler.sendEmptyMessageDelayed(0, 1000);
                        }
                    });
                } else if (Validations.checkEmail(etPhone.getText().toString())) {
                    showLoading();
                    HttpManage.getInstance().getVerifyCodeFromEmail(etPhone.getText().toString(), "register", new HttpManage.ResultCallback<String>() {
                        @Override
                        public void onError(Header[] headers, HttpManage.Error error) {
                            dismissLoading();
                        }

                        @Override
                        public void onSuccess(int code, String response) {
                            dismissLoading();
                            LogUtil.d(response);
                            countTime = 60;
                            mhandler.sendEmptyMessageDelayed(0, 1000);
                        }
                    });
                } else {
                    showToast(getString(R.string.input_phone_error));
                }

                break;
            case R.id.btn_register_and_login:
                if (TextUtils.isEmpty(etPhone.getText().toString())) {
                    showToast(getString(R.string.input_phone_hint));
                    return;
                }

                if (TextUtils.isEmpty(etCode.getText().toString())) {
                    showToast(getString(R.string.input_code_hint));
                    return;
                }

                if (TextUtils.isEmpty(etPwd.getText().toString())) {
                    showToast(getString(R.string.input_pwd_hint));
                    return;
                }

                if (!Validations.checkPassword(etPwd.getText().toString())) {
                    showToast(getString(R.string.input_password_error));
                    return;
                }

                if (TextUtils.isEmpty(etPwdRe.getText().toString())) {
                    showToast(getString(R.string.input_pwd_hint));
                    return;
                }

                if (!CommonUtil.checkPassword(etPwdRe.getText().toString())) {
                    showToast(getString(R.string.input_password_error));
                    return;
                }


                if (!etPwd.getText().toString().equals(etPwdRe.getText().toString())) {
                    showToast(getString(R.string.password_not_equal_ensure_password));
                    return;
                }

                if (Validations.matchesPhoneNumber(etPhone.getText().toString())) {
                    showLoading();
                    HttpManage.getInstance().register(etPhone.getText().toString(), etPwd.getText().toString(), etCode.getText().toString(), new HttpManage.ResultCallback<String>() {
                        @Override
                        public void onError(Header[] headers, HttpManage.Error error) {
                            dismissLoading();
                        }

                        @Override
                        public void onSuccess(int code, String response) {
                            dismissLoading();
                            LogUtil.d(response);
                            BaseEntity<Register> result = new Gson().fromJson(response, new TypeToken<BaseEntity<Register>>() {
                            }.getType());
                            if (result.getCode() == 200) {
                                showToast(getString(R.string.register_success));
                                SharedPreferencesUtil.keepShared("token", result.getData().getToken());
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            } else {
                                showToast(result.getMsg());
                            }
                        }
                    });
                } else if (Validations.checkEmail(etPhone.getText().toString())) {
                    showLoading();
                    HttpManage.getInstance().registerFromEmail(etPhone.getText().toString(), etPwd.getText().toString(), etCode.getText().toString(), new HttpManage.ResultCallback<String>() {
                        @Override
                        public void onError(Header[] headers, HttpManage.Error error) {
                            dismissLoading();
                        }

                        @Override
                        public void onSuccess(int code, String response) {
                            dismissLoading();
                            LogUtil.d(response);
                            BaseEntity<Register> result = new Gson().fromJson(response, new TypeToken<BaseEntity<Register>>() {
                            }.getType());
                            if (result.getCode() == 200) {
                                showToast(getString(R.string.register_success));
                                SharedPreferencesUtil.keepShared("token", result.getData().getToken());
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            } else {
                                showToast(result.getMsg());
                            }
                        }
                    });
                } else {
                    showToast(getString(R.string.input_phone_error));
                }


                break;
        }
    }
}
