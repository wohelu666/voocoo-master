package com.xlink.ext.decode;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.xlink.ext.CaptureBaseActivity;

import java.util.concurrent.CountDownLatch;

final class DecodeThread extends Thread {

    CaptureBaseActivity activity;
    private Handler handler;
    private final CountDownLatch handlerInitLatch;
    private Looper looper;

    DecodeThread(CaptureBaseActivity activity) {
        this.activity = activity;
        handlerInitLatch = new CountDownLatch(1);
    }

    Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
            Log.d("DecodeThread ", ie.toString());
        }
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        looper = Looper.myLooper();
        handler = new DecodeHandler(activity);
        handlerInitLatch.countDown();
        Looper.loop();
    }

    public void exit() {
        if (looper != null) {
            looper.quit();
            looper = null;
        }
    }

}
