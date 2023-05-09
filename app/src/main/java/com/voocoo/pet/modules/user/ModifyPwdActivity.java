package com.voocoo.pet.modules.user;

import android.os.Bundle;
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
import com.voocoo.pet.http.HttpManage;

import org.apache.http.Header;

import butterknife.BindView;
import butterknife.OnClick;

public class ModifyPwdActivity extends AbsBaseActivity {

    @BindView(R.id.et_old_pwd)
    EditText etOldPwd;

    @BindView(R.id.et_pwd)
    EditText etPwd;

    @BindView(R.id.et_pwd_re)
    EditText etPwdRe;

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.btn_ensure)
    Button btn;

    private boolean isOldPasswordEmpty = true;
    private boolean isPasswordEmpty = true;
    private boolean isPasswordReEmpty = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText(getString(R.string.text_modity_pwd));

        etOldPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    isOldPasswordEmpty = true;
                } else {
                    isOldPasswordEmpty = false;
                }
                if (!isOldPasswordEmpty && !isPasswordEmpty && !isPasswordReEmpty) {
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
                if (!isOldPasswordEmpty && !isPasswordEmpty && !isPasswordReEmpty) {
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
                if (!isOldPasswordEmpty && !isPasswordEmpty && !isPasswordReEmpty) {
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
        return R.layout.activity_modity_pwd;
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

    @OnClick({R.id.btn_ensure})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn_ensure:
                if (TextUtils.isEmpty(etOldPwd.getText().toString())) {
                    showToast(getString(R.string.input_pwd_hint));
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

                showLoading();
                HttpManage.getInstance().modityPwd(etOldPwd.getText().toString(), etPwd.getText().toString(), new HttpManage.ResultCallback<String>() {
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
                        if (result.getCode() == 200) {
                            showToast(getString(R.string.modity_success));
                            finish();
                        } else {
                            showToast(result.getMsg());
                        }
                    }
                });
                break;
        }
    }
}
