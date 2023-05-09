package com.voocoo.pet.common.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by legendmohe on 16/6/10.
 */
public class ThreadUtil {

    public static void delay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }


    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }

    public static AsyncTask backgroundSleep(final int millis, final Runnable runnable) {
        AsyncTask asyncTask = new AsyncTask<Integer, Integer, Integer>() {

            @Override
            protected Integer doInBackground(Integer... params) {
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException ignored) {

                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                runnable.run();
            }
        }.execute();
        return asyncTask;
    }

    public static Thread runInBackground(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
        return thread;
    }

    public static void runInMainThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public static boolean currentThreadIsMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static boolean isRunningBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (currentPackageName != null && currentPackageName.equals(context.getPackageName())) {
            return false;
        }
        return true;
    }
}
