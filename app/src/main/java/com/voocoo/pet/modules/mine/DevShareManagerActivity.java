package com.voocoo.pet.modules.mine;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyDialogListener;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMWeb;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.DevShare;
import com.voocoo.pet.entity.Device;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.dev.adapter.ShareDevManagerListAdapter;
import org.apache.http.Header;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class DevShareManagerActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.ly_not_dev)
    View notDev;

    @BindView(R.id.rv_dev)
    RecyclerView recyclerView;

    ShareDevManagerListAdapter shareListAdapter;
    int devId;
    Device device;
    private IWXAPI api;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_dev_share_manager;
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

    @OnClick({R.id.tv_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add:
                share();
                break;
        }
    }

    private void share() {
        if (!isWxInstall(DevShareManagerActivity.this)) {
            showToast("没有安装微信");
            return;
        }
     /*   String content = "http://pet.voocoo.co:8082/#/?userId=" + SharedPreferencesUtil.queryIntValue("userId") +
                "&deviceId=" + device.getDeviceId() + "&userName=" + URLEncoder.encode(SharedPreferencesUtil.queryValue("userName")
                + "&deviceType=" + device.getDeviceType());*/
        String content = "http://pet.voocoo.co:8082/#/?userId=" + SharedPreferencesUtil.queryIntValue("userId") +
                "&deviceId=" + device.getDeviceId() + "&userName=" + SharedPreferencesUtil.queryValue("userName") + "&deviceType=" + device.getDeviceType();
        LogUtil.d(content);
      //初始化一个WXWebpageObject，填写url
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl =content;

         //用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title ="设备共享 ";
        msg.description ="好友用户" + SharedPreferencesUtil.queryValue("userName") + "向您分享" + device.getDeviceName();


       //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(SharedPreferencesUtil.queryIntValue("userId") + System.currentTimeMillis());
        req.message =msg;
        req.scene =SendMessageToWX.Req.WXSceneSession;
        req.userOpenId = String.valueOf(SharedPreferencesUtil.queryIntValue("userId"));

       //调用api接口，发送数据到微信
        api.sendReq(req);
//        UMWeb web = new UMWeb(content);
//        web.setTitle("设备共享");
//        web.setDescription("好友用户" + SharedPreferencesUtil.queryValue("userName") + "向您分享" + device.getDeviceName());
//        new ShareAction(DevShareManagerActivity.this)
//                .setPlatform(SHARE_MEDIA.WEIXIN)//传入平台
//                .withMedia(web)//分享内容
//                .setCallback(new UMShareListener() {
//                    @Override
//                    public void onStart(SHARE_MEDIA share_media) {
//                        LogUtil.d("onStart");
//                    }
//
//                    @Override
//                    public void onResult(SHARE_MEDIA share_media) {
//                        LogUtil.d("onResult");
//                    }
//
//                    @Override
//                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
//                        LogUtil.d("onError");
//                    }
//
//                    @Override
//                    public void onCancel(SHARE_MEDIA share_media) {
//                        LogUtil.d("onCancel");
//                    }
//                })//回调监听器
//                .share();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StyledDialog.init(this);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_black_ic);
        device = (Device) getIntent().getSerializableExtra("device");
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText(device.getDeviceName() + "共享管理");
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, "wx266ab732a021a078", true);

        // 将应用的appId注册到微信
        api.registerApp("wx266ab732a021a078");
        initData();

        notDev.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        shareListAdapter = new ShareDevManagerListAdapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(shareListAdapter);
        shareListAdapter.setCancelShareListener(new ShareDevManagerListAdapter.ShareListener() {
            @Override
            public void onShare(DevShare shareBean) {
                StyledDialog.buildIosAlert("", getString(R.string.text_cancel_share_tips), new MyDialogListener() {
                    @Override
                    public void onFirst() {
                        showLoading();
                        HttpManage.getInstance().cancelShare(shareBean.deviceShareId, new HttpManage.ResultCallback<String>() {
                            @Override
                            public void onError(Header[] headers, HttpManage.Error error) {
                                dismissLoading();
                            }

                            @Override
                            public void onSuccess(int code, String response) {
                                dismissLoading();
                                BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                                }.getType());
                                if (result.getCode() == 200) {
                                    initData();
                                } else {
                                    showToast(result.getMsg());
                                }
                            }
                        });
                    }

                    @Override
                    public void onSecond() {

                    }
                }).show();


            }
        });
    }

    private void initData() {
        HttpManage.getInstance().devShareList(device.getDeviceId(), new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {

            }

            @Override
            public void onSuccess(int code, String response) {
                LogUtil.d(response);
                BaseEntity<List<DevShare>> result = new Gson().fromJson(response, new TypeToken<BaseEntity<List<DevShare>>>() {
                }.getType());
                if (result.getCode() == 200) {
                    shareListAdapter.setData(result.getData());
                } else {
                    showToast(result.getMsg());
                }
            }
        });
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        UMShareAPI.get(this).release();
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