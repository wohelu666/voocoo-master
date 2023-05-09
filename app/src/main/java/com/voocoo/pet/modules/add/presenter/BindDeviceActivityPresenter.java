package com.voocoo.pet.modules.add.presenter;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.entity.Banner;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.add.activity.AddDeviceSuccessActivity;
import com.voocoo.pet.modules.add.activity.BindDeviceActivity;
import com.voocoo.pet.modules.add.activity.BindDeviceFailActivity;
import com.voocoo.pet.modules.add.contract.BindDeviceActivityContract;

import org.apache.http.Header;

import java.util.List;

public class BindDeviceActivityPresenter extends BaseActivityPresenter<BindDeviceActivity> implements BindDeviceActivityContract.Presenter {
    public BindDeviceActivityPresenter(BindDeviceActivity activity) {
        super(activity);
    }

    private final int AllTime = 20;
    private int curTime = 0;
    private String bindDevMac;

    public void onDestory() {
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (curTime < AllTime) {
                bindDevice();
            } else {
                Intent intent = new Intent(getContext(), BindDeviceFailActivity.class);
                getContext().startActivity(intent);
                finish();
            }
        }
    };

    @Override
    public void setDeviceNetwork(String ssid, String password) {

    }

    public void startBindDevice() {
        curTime = 0;
        mHandler.sendEmptyMessageDelayed(0, 5000);
    }

    @Override
    public void bindDevice() {
        LogUtil.d("start bind device");
    }
}
