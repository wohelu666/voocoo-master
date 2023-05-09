package com.voocoo.pet.common.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.voocoo.pet.modules.PetApp;

import java.lang.ref.WeakReference;


public class ContextUtil {
    private static ContextUtil contextUtil;
    private static Handler mainHandler;

    private PetApp myApp;
    private WeakReference<Activity> currentAct;
    private int activityCount = 0;

    public static void init(PetApp myApp) {
        if (contextUtil == null) {
            synchronized (ContextUtil.class) {
                if (contextUtil == null) {
                    contextUtil = new ContextUtil();
                    contextUtil.myApp = myApp;
                    contextUtil.myApp.registerActivityLifecycleCallback(new Application.ActivityLifecycleCallbacks() {
                        @Override
                        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                        }

                        @Override
                        public void onActivityStarted(Activity activity) {
                            contextUtil.activityCount ++;
                        }

                        @Override
                        public void onActivityResumed(Activity activity) {
                            contextUtil.currentAct = new WeakReference<>(activity);
                        }

                        @Override
                        public void onActivityPaused(Activity activity) {
                        }

                        @Override
                        public void onActivityStopped(Activity activity) {
                            contextUtil.activityCount --;
                        }

                        @Override
                        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                        }

                        @Override
                        public void onActivityDestroyed(Activity activity) {
                        }
                    });
                }
            }
        }
    }

    public static ContextUtil getInstance() {
        if (contextUtil == null) {
            throw new IllegalStateException("You should call init method first.");
        }
        return contextUtil;
    }



    public static Handler getMainHandler() {
        if(mainHandler == null){
            synchronized (ContextUtil.class) {
                if (mainHandler == null) {
                    mainHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return mainHandler;
    }

    public static void runOnMainThread(Runnable runnable){
        if (Looper.myLooper() != Looper.getMainLooper()){
            getMainHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    private ContextUtil(){}

    public Activity getCurrentActivity(){
        if (currentAct != null) {
            return currentAct.get();
        } else {
            return null;
        }
    }

    /**
     * @return App是否在前台运行
     */
    public static boolean isRunningForeground() {
        return contextUtil != null && contextUtil.activityCount > 0;
    }
}
