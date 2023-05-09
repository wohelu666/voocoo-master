package com.voocoo.pet.modules.user;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.constant.Constant;
import com.voocoo.pet.common.manager.DevicesManager;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.common.utils.Validations;
import com.voocoo.pet.common.widgets.AppDialog;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Register;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.main.MainActivity;
import com.voocoo.pet.modules.web.LocalWebActivity;

import org.apache.http.Header;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends AbsBaseActivity {

    @BindView(R.id.cb_privacy)
    CheckBox cbPrivacy;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick({R.id.btn_login, R.id.tv_code_login,
            R.id.tv_privacy_protocol, R.id.tv_license, R.id.iv_wechat})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                //一键登录
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                break;
            case R.id.tv_code_login:
                startActivity(new Intent(LoginActivity.this, LoginByCodeActivity.class));
                break;
            case R.id.iv_wechat:

                break;
            case R.id.tv_privacy_protocol:
                LocalWebActivity.open(LoginActivity.this, getString(R.string.text_privacy), "privacy_zh.html");
                break;
            case R.id.tv_license:
                LocalWebActivity.open(LoginActivity.this, getString(R.string.text_license), "license_zh.html");
                break;
        }
    }
}
