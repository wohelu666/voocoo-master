package com.voocoo.pet.modules.add.presenter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.utils.CommonUtil;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.common.widgets.AppDialog;
import com.voocoo.pet.modules.add.activity.AddDeviceStepThreeActivity;
import com.voocoo.pet.modules.add.activity.AddDeviceStepTwoActivity;
import com.voocoo.pet.modules.add.contract.AddDeviceStepTwoActivityContract;

public class AddDeviceStepTwoActivityPresenter extends BaseActivityPresenter<AddDeviceStepTwoActivity> implements AddDeviceStepTwoActivityContract.Presenter {

    public AddDeviceStepTwoActivityPresenter(AddDeviceStepTwoActivity activity) {
        super(activity);
    }

    @Override
    public void getCurWifiSsid() {
        String ssid = CommonUtil.getCurrentWifiSSID(getContext());
        if (!TextUtils.isEmpty(ssid)) {
            if (ssid.startsWith("\""))
                ssid = ssid.substring(1, ssid.length());
            if (ssid.endsWith("\""))
                ssid = ssid.substring(0, ssid.length() - 1);

            if (ssid.equals("<unknown ssid>")) {
                getView().showCurrentSSID("");
                return;
            }
            getView().showCurrentSSID(ssid);
        } else {
            getView().showCurrentSSID("");
        }
    }

    @Override
    public void next() {
        if (TextUtils.isEmpty(getView().getWifiName())) {
            getView().showToast("请先连接WiFi");
            return;
        }

        if (!TextUtils.isEmpty(getView().getWifiPassword())) {
            if (getView().getWifiPassword().trim().length() < 8) {
                getView().showToast("WiFi密码小于8位，请检查输入后重试。");
                return;
            }
        }

        if (getView().getWifiName().contains("5G")) {
            AppDialog.doubleTextDoubleButton(getContext(), "切换网络提示", "检测到您的WIFI名称中含有“5G”字段，请确认您是否正在连接2.4GWifi。注意这款设备不支持5G热点哦！", "继续", "返回",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), AddDeviceStepThreeActivity.class);
                            intent.putExtra("ssid", getView().getWifiName());
                            intent.putExtra("password", getView().getWifiPassword());
                            getContext().startActivity(intent);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
            return;
        }

        Intent intent = new Intent(getContext(), AddDeviceStepThreeActivity.class);
        intent.putExtra("ssid", getView().getWifiName());
        intent.putExtra("password", getView().getWifiPassword());
        SharedPreferencesUtil.keepShared(getView().getWifiName() + "_pwd", getView().getWifiPassword());
        getContext().startActivity(intent);
    }
}
