package com.voocoo.pet.modules.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.constant.Constant;
import com.voocoo.pet.common.manager.DevicesManager;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Register;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.PetApp;
import com.voocoo.pet.modules.main.MainActivity;
import com.voocoo.pet.modules.mine.SettingActivity;
import com.voocoo.pet.modules.user.LoginActivity;
import com.voocoo.pet.modules.user.LoginByCodeActivity;

import org.apache.http.Header;

import cn.jiguang.api.utils.JCollectionAuth;
import cn.jpush.android.api.JPushInterface;

public class SplashActivity extends AbsBaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(SharedPreferencesUtil.queryValue("token"))) {
                    PetApp.getInstance().setupUMeng();

                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginByCodeActivity.class));
                }
                if (SharedPreferencesUtil.queryBooleanValue("isAgree")) {
                    JCollectionAuth.setAuth(SplashActivity.this,true);

                    JPushInterface.resumePush(SplashActivity.this);
                    JPushInterface.setBadgeNumber(SplashActivity.this,0);

                }

                finish();
            }
        },2000);
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
