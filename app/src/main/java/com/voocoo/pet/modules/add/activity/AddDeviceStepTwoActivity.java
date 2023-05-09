package com.voocoo.pet.modules.add.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.common.event.AddDeviceSuccessEvent;
import com.voocoo.pet.common.event.FinishAddDeviceStepTwoActivityEvent;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.common.widgets.AppDialog;
import com.voocoo.pet.modules.add.contract.AddDeviceStepTwoActivityContract;
import com.voocoo.pet.modules.add.presenter.AddDeviceStepTwoActivityPresenter;
import com.voocoo.pet.modules.dev.activity.DevSettingActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import ru.alexbykov.nopermission.PermissionHelper;

public class AddDeviceStepTwoActivity extends AbsBaseActivity<AddDeviceStepTwoActivityPresenter> implements AddDeviceStepTwoActivityContract.View {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.top_right)
    TextView tvRight;

    @BindView(R.id.tv_wifi_ssid)
    TextView tvWifiSsid;

    @BindView(R.id.et_wifi_pwd)
    EditText etWifiPwd;

    int type = 0;
    boolean isShow = false;

    PermissionHelper permissionHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        type = getIntent().getIntExtra("type", 0);
        isShow = getIntent().getBooleanExtra("isShowGuide", false);
        if (isShow) {
            tvRight.setVisibility(View.VISIBLE);
            tvRight.setText("重置引导");
            tvRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AddDeviceStepTwoActivity.this, AddDeviceStepOneActivity.class);
                    intent.putExtra("type", type);
                    startActivity(intent);
                    finish();
                }
            });
        }
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void onSuccess() {
        presenter.getCurWifiSsid();
    }

    private void onDenied() {
        showToast("权限被拒绝，9.0系统无法获取SSID");
    }

    private void onNeverAskAgain() {
        showToast("权限被拒绝，9.0系统无法获取SSID,下次不会在询问了");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        tvTitle.setText("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        permissionHelper = new PermissionHelper(this);

        etWifiPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device_step_two;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        permissionHelper.check(Manifest.permission.ACCESS_FINE_LOCATION).onSuccess(this::onSuccess).onDenied(this::onDenied).onNeverAskAgain(this::onNeverAskAgain).run();
    }

    @Nullable
    @Override
    protected AddDeviceStepTwoActivityPresenter createPresenter() {
        return new AddDeviceStepTwoActivityPresenter(this);
    }


    @OnCheckedChanged(R.id.cb_show_pwd)
    public void showPwdCheckedChanged(boolean isChecked) {
        etWifiPwd.setTransformationMethod(!isChecked ? PasswordTransformationMethod.getInstance() :
                HideReturnsTransformationMethod.getInstance());
    }

    @OnClick({R.id.btn_arraw})
    void ClickToWifiList() {
        startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
    }

    @OnClick({R.id.tv_tips})
    void showTips() {
        AppDialog.showPromptDialog(AddDeviceStepTwoActivity.this, "如若联不上网，请您先确认以下问题：\n" +
                "1.您的网络是否是2.4G网络，我们的产品暂时只支持2.4G网络，暂不支持5G网络；\n" +
                "2.如果您的网络不是2.4G，请切换为2.4G重新尝试联网；\n" +
                "3.以上操作之后，联网失败，请前往原购买渠道，咨询客服。").show();
    }


    @Override
    public void showCurrentSSID(String ssid) {
        tvWifiSsid.setText(ssid);
        if (!TextUtils.isEmpty(SharedPreferencesUtil.queryValue(ssid + "_pwd"))) {
            etWifiPwd.setText(SharedPreferencesUtil.queryValue(ssid + "_pwd"));
        }
    }

    @Override
    public String getWifiName() {
        return tvWifiSsid.getText().toString();
    }

    @Override
    public String getWifiPassword() {
        return etWifiPwd.getText().toString();
    }

    @OnClick(R.id.btn_next)
    void ClickToNext() {
        presenter.next();
    }

    /**
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddDeviceSuccess(AddDeviceSuccessEvent event) {
        finish();
    }

    /**
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinish(FinishAddDeviceStepTwoActivityEvent event) {
        finish();
    }
}
