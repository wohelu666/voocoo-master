package com.voocoo.pet.modules.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyDialogListener;
import com.umeng.commonsdk.debug.I;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.manager.PetsManager;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.common.widgets.AppDialog;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.UserInfo;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.user.BindWechatActivity;
import com.voocoo.pet.modules.user.CancelAccountActivity;
import com.voocoo.pet.modules.user.ConfirmBindWechatActivity;
import com.voocoo.pet.modules.user.LoginByCodeActivity;
import com.voocoo.pet.modules.user.ModifyPhoneActivity;

import org.apache.http.Header;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.forward.androids.utils.LogUtil;
import cn.jpush.android.api.JPushInterface;

public class SettingActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.tv_phone)
    TextView tvPhone;

    @BindView(R.id.tv_wechat)
    TextView tvWechat;

    private boolean isBinded;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+08:00");//年-月-日 时-分


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StyledDialog.init(this);
        getData();
    }

    long days = 0;

    private void getData() {
        HttpManage.getInstance().getUserInfo(new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {

            }

            @Override
            public void onSuccess(int code, String response) {
                LogUtil.d("getUserInfo->" + response);
                BaseEntity<UserInfo> result = new Gson().fromJson(response, new com.google.gson.reflect.TypeToken<BaseEntity<UserInfo>>() {
                }.getType());
                if (result.getCode() == 200) {
                    SharedPreferencesUtil.keepShared("userId", result.getData().getUserId());
                    SharedPreferencesUtil.keepShared("phone", result.getData().getPhoneNumber());
                    tvPhone.setText(result.getData().getPhoneNumber());
                    try {
                        days = daysBetween(new Date(), dateFormat.parse(result.getData().getCtime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (result.getData().getWxId() == 0) {
                        //未绑定
                        isBinded = false;
                        tvWechat.setText(getString(R.string.text_not_bind));
                    } else {
                        //已绑定
                        isBinded = true;
                        tvWechat.setText(getString(R.string.text_binded));
                    }
                }
            }
        });
    }


    private static long daysBetween(Date one, Date two) {

        long difference = (one.getTime() - two.getTime()) / 86400000;

        return Math.abs(difference);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tvTitle.setTextColor(getColor(R.color.color_000000));
        tvTitle.setText(getString(R.string.text_setting));

        String phone = SharedPreferencesUtil.queryValue("phone");
        tvPhone.setText(phone);
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

    private void getPlatformIfo(final Activity activity, final SHARE_MEDIA platform) {
        UMShareAPI.get(SettingActivity.this).deleteOauth(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {

            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                UMShareAPI.get(SettingActivity.this).getPlatformInfo(activity, platform, new UMAuthListener() {

                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> userInfo) {
                        LogUtil.d("userInfo->" + userInfo);
                     /*   Intent intent = new Intent(SettingActivity.this, BindWechatActivity.class);
                        intent.putExtra("phone", SharedPreferencesUtil.queryValue("phone"));
                        intent.putExtra("wxJson", new Gson().toJson(userInfo));
                        startActivity(intent);*/

                        Intent intent = new Intent(SettingActivity.this, ConfirmBindWechatActivity.class);
                        intent.putExtra("phone", SharedPreferencesUtil.queryValue("phone"));
                        intent.putExtra("wxJson", new Gson().toJson(userInfo));
                        startActivity(intent);
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media, int i) {
                        dismissLoading();
                    }
                });
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {

            }
        });
    }

    @OnClick({R.id.ly_cancel, R.id.ly_quit, R.id.ly_bind, R.id.ly_phone})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ly_phone:
                startActivity(new Intent(SettingActivity.this, ModifyPhoneActivity.class));
                break;
            case R.id.ly_bind:
                if (isBinded) {
                    StyledDialog.buildIosAlert(getString(R.string.text_ensure_unbind), "", new MyDialogListener() {
                        @Override
                        public void onFirst() {
                            showLoading();

                            HttpManage.getInstance().unbindWechat(new HttpManage.ResultCallback<String>() {
                                @Override
                                public void onError(Header[] headers, HttpManage.Error error) {
                                    dismissLoading();
                                }

                                @Override
                                public void onSuccess(int code, String response) {
                                    dismissLoading();
                                    LogUtil.d(response);
                                    getData();
                                }
                            });
                        }

                        @Override
                        public void onSecond() {

                        }
                    }).show();
                } else {
                    //开始绑定
                    if (isWxInstall(SettingActivity.this)) {
                        SharedPreferencesUtil.keepShared("isUseWechat", true);
                        getPlatformIfo(this, SHARE_MEDIA.WEIXIN);
                    } else {
                        showToast("没有安装微信");
                    }
                }
                break;
            case R.id.ly_cancel:
                StyledDialog.buildIosAlert(getString(R.string.text_ensure_cancel), "", new MyDialogListener() {
                    @Override
                    public void onFirst() {
                        showLoading();

                        HttpManage.getInstance().cancelAccountBefore(new HttpManage.ResultCallback<String>() {
                            @Override
                            public void onError(Header[] headers, HttpManage.Error error) {
                                dismissLoading();
                            }

                            @Override
                            public void onSuccess(int code, String response) {
                                dismissLoading();
                                LogUtil.d("cancelAccountBefore-》" + response);
                                BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                                }.getType());
                                if (result.getCode() == 200) {

                                    AppDialog.cancelAccountDialog(SettingActivity.this, days + "", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(SettingActivity.this, CancelAccountActivity.class);
                                            intent.putExtra("phone", SharedPreferencesUtil.queryValue("phone"));
                                            startActivity(intent);
                                        }
                                    }, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(SettingActivity.this, FeedbackActivity.class);
                                            startActivity(intent);
                                        }
                                    }).show();


                                } else {
                                    StyledDialog.buildIosAlert(result.getMsg(), "", new MyDialogListener() {
                                        @Override
                                        public void onFirst() {

                                        }

                                        @Override
                                        public void onSecond() {

                                        }
                                    }).show();
                                }
                            }
                        });
                        //反注册
                       /* JPushInterface.setAlias(SettingActivity.this, 1,"");

                        SharedPreferencesUtil.keepShared("password", "");
                        SharedPreferencesUtil.keepShared("token", "");
                        Intent intent = new Intent(SettingActivity.this, LoginByCodeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);*/
                    }

                    @Override
                    public void onSecond() {

                    }
                }).show();
                break;
            case R.id.ly_quit:
                StyledDialog.buildIosAlert(getString(R.string.text_ensure_quit), "", new MyDialogListener() {
                    @Override
                    public void onFirst() {

                        JPushInterface.stopPush(SettingActivity.this);
                        JPushInterface.deleteAlias(SettingActivity.this, 1);
                        //反注册
                        PetsManager.getInstance().deletePet();
                        SharedPreferencesUtil.keepShared("password", "");
                        SharedPreferencesUtil.keepShared("token", "");
                        Intent intent = new Intent(SettingActivity.this, LoginByCodeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onSecond() {

                    }
                }).show();

                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
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
