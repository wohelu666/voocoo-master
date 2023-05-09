package com.voocoo.pet.common.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * 吐司工具：对吐司进行优化
 */

public class ToastUtil {
    private Toast toast;
    private Handler handler = new Handler();
    private volatile static ToastUtil instance;


    public static ToastUtil getInstance() {
        if (instance == null) {
            synchronized (ToastUtil.class) {
                if (instance == null) {
                    instance = new ToastUtil();
                }
            }
        }
        return instance;
    }

    private ToastUtil() {

    }

    private Runnable r = new Runnable() {
        public void run() {
//            if (context instanceof Activity) {
//                if (((Activity) context).isFinishing()) {
                    toast.cancel();
//                }
//            }
        }
    };

    private void showToast(String text, int duration) {
        handler.removeCallbacks(r);
        if (toast != null) {
            toast.setText(text);
        } else {
            LogUtil.e("ContextUtil.getInstance().getCurrentActivity()=="+ContextUtil.getInstance().getCurrentActivity());
            toast = Toast.makeText(ContextUtil.getInstance().getCurrentActivity(), text, duration);
        }
        handler.postDelayed(r, 1000);

        toast.show();
    }

    private void showToast(int resId, int duration) {
        showToast(ContextUtil.getInstance().getCurrentActivity().getResources().getString(resId), duration);
    }

    public void shortToast(String text) {
        showToast(text, Toast.LENGTH_SHORT);
    }

    public void shortToast(int resId) {
        showToast(resId, Toast.LENGTH_SHORT);
    }

    public void longToast(Context context, String text) {
        showToast(text, Toast.LENGTH_LONG);
    }

    public void longToast(Context context, int resId) {
        showToast(resId, Toast.LENGTH_LONG);
    }

}
