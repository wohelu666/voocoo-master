package com.voocoo.pet.http;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.voocoo.pet.modules.PetApp;

import java.io.File;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 类名称：Public <br>
 * 类描述：公用对象，用来保存一些静态获取全局对象以及方法 <br>
 * 修改备注： <br>
 */
@SuppressLint("Recycle")
public class Public {

    private static QLSp sp;
    public static boolean clearBuffer = true;

    public static boolean isDebug = false;

    public static String packageName = "";

    /**
     * 门户id 在qlbundle里面还有一个，需要同时设置
     */
    public static final int portalId = 1;

    /**
     * a04cb68afd2c7aaba3211b20bb9ca7a1bea74a49
     */
    public static String clientToken = "";

    private static String umeng_device_token = "";

    //private static UserInfo userInfo=new UserInfo();

    public static final String clientName = "mainUser";
    /**
     * 服务器返回的时间格式
     */
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());


    /**
     * 获取一个QLSp对象，使用沙盒的xml文件进行持久化存储
     *
     * @param context
     * @return
     */
    public static QLSp getSp(Context context) {
        context = PetApp.getInstance();
        if (null == sp) {
            sp = new QLSp(context);
        }
        return sp;
    }

    private static Gson gson;

    /**
     * 获取GSON
     *
     * @return
     */
    public static Gson getGson() {
        if (null == gson) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm");
            gsonBuilder.registerTypeAdapter(Timestamp.class, new QLTimestampTypeAdapter());
            gson = gsonBuilder.create();
        }
        return gson;
    }

    private static Gson mgson;

    /**
     * 获取GSON 时间精确到秒
     *
     * @return
     */
    public static Gson getGsonDetTime() {
        if (null == mgson) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
            gsonBuilder.registerTypeAdapter(Timestamp.class, new QLTimestampTypeAdapter());
            mgson = gsonBuilder.create();
        }
        return mgson;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getTimeNow() {
        return Public.formatter.format(new Date(System.currentTimeMillis()));
    }

    /**
     * 通过Push调出Activity返回到客户端
     *
     * @param activity
     */
    public static void pushCallBackActivity(Activity activity) {
        if (activity.getIntent() != null && activity.getIntent().getBooleanExtra("callback.activity", false)) {
            Intent intent = new Intent();
            intent.setAction("callback.activity");
            intent.putExtra("className", activity.getClass().getName());
            activity.sendBroadcast(intent);
        }
    }


    public static boolean isActivityTop(Context context, String activityName) {
        boolean top = false;
        List<RunningTaskInfo> list = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
        if (list != null && !list.isEmpty()) {
            //QLLog.e("my_tag","这里最上面的activity------>"+list.get(0).topActivity);
            if (list.get(0).topActivity.getClassName().contains(activityName))
                top = true;
        }
        return top;
    }

    /*
     * 递归删除文件
     */
    public static void deleteFile(File file) {
        if (file == null) return;
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
    }

}
