package com.voocoo.pet.modules.user;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.event.AddDeviceSuccessEvent;
import com.voocoo.pet.common.widgets.RoundImageView;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Restful.Login;
import com.voocoo.pet.http.HttpManage;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class ConfirmBindWechatActivity extends AbsBaseActivity {

    @BindView(R.id.iv_head)
    RoundImageView ivHead;

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    String wxJsonStr;
    String phone;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_confirm_bind_wechat;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wxJsonStr = getIntent().getStringExtra("wxJson");
        phone = getIntent().getStringExtra("phone");
        tvTitle.setText("确认关联蔚刻账号");
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        Map<String, Object> wxJson = new Gson().fromJson(wxJsonStr, new TypeToken<Map<String, Object>>() {
        }.getType());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.app_icon);
        Glide.with(this).load(wxJson.get("profile_image_url").toString()).apply(options).into(ivHead);
        tvName.setText(wxJson.get("name").toString());
    }

    @OnClick({R.id.btn_bind, R.id.btn_cancel})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn_bind:
                showLoading();
                HttpManage.getInstance().bindWechat(phone, wxJsonStr, new HttpManage.ResultCallback<String>() {
                    @Override
                    public void onError(Header[] headers, HttpManage.Error error) {
                        dismissLoading();
                    }

                    @Override
                    public void onSuccess(int code, String response) {
                        dismissLoading();
                        BaseEntity result = new Gson().fromJson(response, new com.google.common.reflect.TypeToken<BaseEntity>() {
                        }.getType());
                        if (result.getCode() == 200) {
                            finish();
                        } else {
                            showToast(result.getMsg());
                        }
                    }
                });
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    @Nullable
    @Override
    protected BaseActivityPresenter createPresenter() {
        return null;
    }
}
