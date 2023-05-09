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
import android.widget.CheckBox;
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
import com.voocoo.pet.entity.Restful.Login;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.main.MainActivity;
import com.voocoo.pet.modules.mine.SettingActivity;
import com.voocoo.pet.modules.web.LocalWebActivity;

import org.apache.http.Header;

import butterknife.BindView;
import butterknife.OnClick;

public class CancelAccountActivity extends AbsBaseActivity {

    @BindView(R.id.et_phone)
    EditText etPhone;

    @BindView(R.id.et_pwd)
    EditText etPwd;

    @BindView(R.id.btn_cancel)
    Button btnLogin;

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    private boolean isMatchPhone = false;
    private boolean isPasswordEmpty = false;
    private String phone;

    @BindView(R.id.tv_get_code)
    TextView tvGetCode;

    int countTime;

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
        return R.layout.activity_cancel_account;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phone = getIntent().getStringExtra("phone");

        etPhone.setText(phone);
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

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mhandler.removeCallbacksAndMessages(null);
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

    @OnClick({R.id.tv_get_code, R.id.btn_cancel})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.tv_get_code:
                if (TextUtils.isEmpty(etPhone.getText().toString())) {
                    showToast(getString(R.string.input_username_hint));
                    return;
                }
                if (Validations.matchesPhoneNumber(etPhone.getText().toString())) {
                    showLoading();
                    HttpManage.getInstance().getVerifyCode(etPhone.getText().toString(), 2, new HttpManage.ResultCallback<String>() {
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
                    showToast(getString(R.string.input_username_error));
                }
                break;
            case R.id.btn_cancel:
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
                    HttpManage.getInstance().cancelAccount(etPhone.getText().toString(), etPwd.getText().toString(), new HttpManage.ResultCallback<String>() {
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
                                //反注册
                                SharedPreferencesUtil.keepShared("password", "");
                                SharedPreferencesUtil.keepShared("token", "");
                                Intent intent = new Intent(CancelAccountActivity.this, LoginByCodeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
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
        }
    }

    @Nullable
    @Override
    protected BaseActivityPresenter createPresenter() {
        return null;
    }
}
