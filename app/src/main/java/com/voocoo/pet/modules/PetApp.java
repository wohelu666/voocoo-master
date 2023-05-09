package com.voocoo.pet.modules;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.h3c.shengshiqu.widget.ShengShiQuPicker;
import com.hss01248.dialog.StyledDialog;
import com.kongzue.dialogx.DialogX;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.voocoo.pet.R;
import com.voocoo.pet.base.ContextHolder;
import com.voocoo.pet.ble.BleManager;
import com.voocoo.pet.common.utils.ContextUtil;
import com.voocoo.pet.common.utils.LogFileOperationUtils;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.common.utils.ToastUtil;

import java.util.ArrayList;

import cn.jiguang.api.utils.JCollectionAuth;
import cn.jpush.android.api.JPushInterface;

public class PetApp extends MultiDexApplication {

    private static PetApp application;

    private static ContextHolder<AppCompatActivity> mCurrentActivity;

    private static ArrayList<Activity> listActivity = new ArrayList<Activity>();
    private long exitTime;

    public static String APP_ID = "wx5ef1c942c3bbc1c6";
    public static IWXAPI api;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
        StyledDialog.init(this);
    }

    public void setupUMeng() {
        UMConfigure.init(this, "635a4e1405844627b5713be4", "VOOCOO", UMConfigure.DEVICE_TYPE_PHONE, "");
        UMConfigure.setLogEnabled(false);
        PlatformConfig.setWeixin("wx266ab732a021a078", "969c6bcc6bcb318f1f6e3e2d6c56d1fb");
        PlatformConfig.setWXFileProvider("com.voocoo.pet.fileProvider");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        //初始化
        DialogX.init(this);
        initEnvironment();
        //initSdk();
        setUp();
        SharedPreferencesUtil.init(this);
        ShengShiQuPicker.initSSQPikcerColorInApplication(getColor(R.color.color_tab_unselect), 12, getColor(R.color.color_cccccc));

        LogFileOperationUtils.init(this);
        BleManager.getInstance().init(this);
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            // 通知渠道的id。
//            String id = "pet";
//            // 用户可以看到的通知渠道的名字。
//            CharSequence name = "notification channel";
//            // 用户可以看到的通知渠道的描述。
//            String description = "notification description";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
//            // 配置通知渠道的属性。
//            mChannel.setDescription(description);
//            // 设置通知出现时的闪灯（如果Android设备支持的话）。
//            mChannel.enableLights(true);
//            mChannel.setLightColor(Color.RED);
//            // 设置通知出现时的震动（如果Android设备支持的话）。
//            mChannel.enableVibration(true);
//            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//            // 最后在notificationmanager中创建该通知渠道。
//            mNotificationManager.createNotificationChannel(mChannel);
//        }

        JPushInterface.setDebugMode(false);
        JCollectionAuth.setAuth(this, false);
        // 调整点一：初始化代码前增加setAuth调用
        JPushInterface.init(this);

//        sendNotification();

        // setupUMeng();

        // CrashReport.initCrashReport(getApplicationContext(), "52b792dcfc", true);
    }
    private static final int NOTIFICATION_ID = 1001;
//    private void sendNotification() {
//        //1、NotificationManager
//        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//        /** 2、Builder->Notification
//         *  必要属性有三项
//         *  小图标，通过 setSmallIcon() 方法设置
//         *  标题，通过 setContentTitle() 方法设置
//         *  内容，通过 setContentText() 方法设置*/
//        Notification.Builder builder = new Notification.Builder(this);
//        builder.setContentInfo("Content info")
//                .setContentText("Content text")//设置通知内容
//                .setContentTitle("Content title")//设置通知标题
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
//                .setSmallIcon(R.mipmap.ic_launcher_round)//不能缺少的一个属性
//                .setSubText("Subtext")
//                .setTicker("滚动消息......")
//                .setWhen(System.currentTimeMillis());//设置通知时间，默认为系统发出通知的时间，通常不用设置
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel("001","my_channel",NotificationManager.IMPORTANCE_DEFAULT);
//            channel.enableLights(true); //是否在桌面icon右上角展示小红点
//            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
//            // 设置通知出现时的闪灯（如果Android设备支持的话）。
//            channel.enableLights(true);
//            channel.setLightColor(Color.RED);
//            // 设置通知出现时的震动（如果Android设备支持的话）。
//            channel.enableVibration(true);
//            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//
//            manager.createNotificationChannel(channel);
//            builder.setChannelId("001");
//        }
//
//        Notification n = builder.build();
//        //3、manager.notify()
//        manager.notify(NOTIFICATION_ID,n);
//    }

    private void setUp() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                listActivity.add(activity);

                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (activity instanceof AppCompatActivity) {
                    mCurrentActivity = new ContextHolder<>((AppCompatActivity) activity);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    /**
     * 初始化环境配置
     */
    private void initEnvironment() {
        //初始化ContextUtil
        ContextUtil.init(this);
    }


    public static PetApp getInstance() {
        return application;
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallback(ActivityLifecycleCallbacks callbacks) {
        registerActivityLifecycleCallbacks(callbacks);
    }

    public static AppCompatActivity getCurrentActivity() {
        if (mCurrentActivity != null && mCurrentActivity.isAlive())
            return mCurrentActivity.get();
        else
            return null;
    }

    public ArrayList<Activity> getAllActivity() {
        return listActivity;
    }

    /**
     * 是否退出
     */
    public boolean isExit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.getInstance().shortToast(getString(R.string.exit_app));
            exitTime = System.currentTimeMillis();
            return false;
        }
        return true;
    }
}
