package com.voocoo.pet.modules.main;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.commonsdk.debug.W;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.adapter.BaseFragmentAdapter;
import com.voocoo.pet.base.fragment.AbsBaseFragment;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.constant.Constant;
import com.voocoo.pet.common.event.UpdateDevNumEvent;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.common.widgets.NoScrollViewPager;
import com.voocoo.pet.entity.AppUpdate;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Device;
import com.voocoo.pet.entity.UserInfo;
import com.voocoo.pet.entity.Version;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.PetApp;
import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyDialogListener;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.voocoo.pet.modules.mine.AboutUsActivity;
import com.voocoo.pet.modules.user.LoginByCodeActivity;
import com.voocoo.pet.modules.web.WebActivity;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

public class MainActivity extends AbsBaseActivity {

    @BindView(R.id.bg_content)
    View bgContent;

    @BindView(R.id.view_pager)
    NoScrollViewPager viewPager;

    @BindView(R.id.iv_home)
    ImageView ivHome;
    @BindView(R.id.iv_mail)
    ImageView ivMail;
    @BindView(R.id.iv_mine)
    ImageView ivMine;

    @BindView(R.id.tv_home)
    TextView tvHome;
    @BindView(R.id.tv_mail)
    TextView tvMail;
    @BindView(R.id.tv_mine)
    TextView tvMine;

    private long mExitTimeStamp;

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getPackageVersionName(Context context, String pkgName) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(pkgName, 0); //PackageManager.GET_CONFIGURATIONS
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        Log.e(TAG, "onCreate: "+JPushInterface.getRegistrationID(this) );

        setupViewPager(viewPager);

        HttpManage.getInstance().checkLastVersion(new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {

            }

            @Override
            public void onSuccess(int code, String response) {
                LogUtil.d("checkLastVersion->" + response);
                AppUpdate result = new Gson().fromJson(response, new TypeToken<AppUpdate>() {
                }.getType());
                if (result.getCode() == 200) {
                    if (result.getData().size() == 0) {
                        return;
                    }
                    String versionCode = result.getData().get(0).appVersionNumber;
                    LogUtil.d("versionCode->" + versionCode);
                    String[] a = versionCode.split("\\.");
                    int version1 = Integer.valueOf(a[0]);
                    int version2 = Integer.valueOf(a[1]);
                    int version3 = Integer.valueOf(a[2]);

                    String[] b = getPackageVersionName(MainActivity.this, "com.voocoo.pet").split("\\.");
                    int version11 = Integer.valueOf(b[0]);
                    int version22 = Integer.valueOf(b[1]);
                    int version33 = Integer.valueOf(b[2]);

                        boolean isNeedUpdate = false;
                        if (version1 > version11) {
                            isNeedUpdate = true;
                        } else if (version1 < version11) {
                            isNeedUpdate = false;
                        } else {
                            if (version2 > version22) {
                                isNeedUpdate = true;
                            } else if (version2 < version22) {
                                isNeedUpdate = false;
                            } else {
                                if (version3 > version33) {
                                    isNeedUpdate = true;
                                } else if (version3 < version33) {
                                    isNeedUpdate = false;
                                } else {
                                    isNeedUpdate = false;
                                }
                            }
                        }

//                        if (isNeedUpdate) {
//                            //提示
//                            StyledDialog.buildIosAlert(getString(R.string.text_check_title), result.getData().get(0).appVersionDescribe, new MyDialogListener() {
//                                @Override
//                                public void onFirst() {
//                                    if (isYYBInstall(MainActivity.this)) {
//                                        launchAppDetail("com.voocoo.pet", "com.tencent.android.qqdownloader");
//                                    } else {
//                                        showToast("没有安装应用宝");
//                                    }
//                                }
//
//                                @Override
//                                public void onSecond() {
//
//                                }
//                            }).setBtnText("升级","取消").show();
//                        }
                    }
            }
        });
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



    private void setupViewPager(NoScrollViewPager viewPager) {
        ArrayList<AbsBaseFragment> fragList = new ArrayList<>();
        fragList.add(HomeFragment.newInstance());
        fragList.add(MailFragment.newInstance());
        fragList.add(MineFragment.newInstance());

        ArrayList<CharSequence> fragTags = new ArrayList<>();
        fragTags.add(getString(R.string.tab_home));
        fragTags.add(getString(R.string.tab_mail));
        fragTags.add(getString(R.string.tab_mine));

        BaseFragmentAdapter adapter = new BaseFragmentAdapter<>(getSupportFragmentManager(), fragList, fragTags);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setCurrentItem(0);
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTimeStamp) > 2000) {
            Toast.makeText(PetApp.getInstance(), getString(R.string.exit_app), Toast.LENGTH_SHORT).show();
            mExitTimeStamp = System.currentTimeMillis();
        } else {
            if (GSYVideoManager.backFromWindowFull(this)) {
                return;
            }
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            ActivityManager manager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
            manager.killBackgroundProcesses(getPackageName());
            System.exit(0);
            super.onBackPressed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick({R.id.tab_home, R.id.tab_mail, R.id.tab_mine})
    public void clickTab(View view) {
        switch (view.getId()) {
            case R.id.tab_home:
                resetToDefaultIcon();
                ivHome.setImageResource(R.mipmap.nav_home_sel_ic);
                tvHome.setTextColor(getResources().getColor(R.color.color_tab_select));
                viewPager.setCurrentItem(0);
                break;
            case R.id.tab_mail:
               /* resetToDefaultIcon();
                ivMail.setImageResource(R.mipmap.nav_mail_select_ic);
                tvMail.setTextColor(getResources().getColor(R.color.color_tab_select));
                viewPager.setCurrentItem(1);*/
                Intent intent = new Intent(MainActivity.this, WebActivity.class);
                intent.putExtra("url", "https://shop91447720.m.youzan.com/v2/feature/uckUdCS8AK");
                startActivity(intent);
                break;
            case R.id.tab_mine:
                EventBus.getDefault().post(new UpdateDevNumEvent());
                resetToDefaultIcon();
                ivMine.setImageResource(R.mipmap.nav_me_select_ic);
                tvMine.setTextColor(getResources().getColor(R.color.color_tab_select));
                viewPager.setCurrentItem(2);
                break;
        }
    }

    private void resetToDefaultIcon() {
        ivHome.setImageResource(R.mipmap.nav_home_ic);
        ivMail.setImageResource(R.mipmap.nav_mail_ic);
        ivMine.setImageResource(R.mipmap.nav_me_ic);

        tvHome.setTextColor(getResources().getColor(R.color.color_tab_unselect));
        tvMail.setTextColor(getResources().getColor(R.color.color_tab_unselect));
        tvMine.setTextColor(getResources().getColor(R.color.color_tab_unselect));
    }

    private void initView() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
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
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StyledDialog.init(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
