package com.voocoo.pet.modules.mine;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyDialogListener;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.event.DelDeviceEvent;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.entity.AboutUsEntity;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Restful.Login;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.dev.activity.UseHelpActivity;
import com.voocoo.pet.modules.main.MainActivity;
import com.voocoo.pet.modules.user.BindWechatActivity;
import com.voocoo.pet.modules.user.LoginByCodeActivity;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class AboutUsActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.ly_qdf)
    View lyQdf;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_black_ic);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText(getString(R.string.text_about));

//        if (System.currentTimeMillis()/1000 < 1670947200) {
//            lyQdf.setVisibility(View.GONE);
//        } else {
//            lyQdf.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StyledDialog.init(this);
    }

    /**
     * 检测是否安装微信 * * @param context * @return
     */
    public static boolean isYYBInstall(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.android.qqdownloader")) {
                    return true;
                }
            }
        }

        return false;
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

    /**
     * 复制内容到剪贴板
     *
     * @param content
     * @param context
     */
    public void copyContentToClipboard(String content, Context context) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }


    @OnClick({R.id.ly_qdf,R.id.ly_phone, R.id.ly_license, R.id.ly_privacy})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ly_qdf:
                if (isYYBInstall(AboutUsActivity.this)) {
                    launchAppDetail("com.voocoo.pet", "com.tencent.android.qqdownloader");
                } else {
                    showToast("没有安装应用宝");
                }
                break;
            case R.id.ly_phone:
                StyledDialog.buildIosAlert(getString(R.string.text_follow_us), getString(R.string.text_gzh_tips), new MyDialogListener() {
                    @Override
                    public void onFirst() {
                        if (isWxInstall(AboutUsActivity.this)) {
                            copyContentToClipboard("VOOCOO蔚刻", AboutUsActivity.this);
                            String package_name = "com.tencent.mm";
                            PackageManager packageManager = getPackageManager();
                            Intent it = packageManager.getLaunchIntentForPackage(package_name);
                            startActivity(it);
                        } else {
                            showToast("没有安装微信");
                        }
                    }

                    @Override
                    public void onSecond() {

                    }
                }).show();
                break;
            case R.id.ly_license:
                Intent intent = new Intent(AboutUsActivity.this, UseHelpActivity.class);
                intent.putExtra("type", 4);
                startActivity(intent);
                break;
            case R.id.ly_privacy:
                intent = new Intent(AboutUsActivity.this, UseHelpActivity.class);
                intent.putExtra("type", 3);
                startActivity(intent);
                break;
        }
    }

    /**
     * 跳转到应用市场app详情界面
     *
     * @param appPkg    App的包名
     * @param marketPkg 应用市场包名
     */
    public void launchAppDetail(String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg))
                return;
            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg))
                intent.setPackage(marketPkg);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    protected BaseActivityPresenter createPresenter() {
        return null;
    }

}
