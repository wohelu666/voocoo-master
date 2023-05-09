package com.voocoo.pet.jpush;

import static com.kongzue.dialogx.interfaces.BaseDialog.getApplicationContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

import com.tencent.mmkv.MMKV;

import java.util.Locale;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;

/**
 * 处理tagalias相关的逻辑
 */
public class TagAliasOperatorHelper {
    private static final String TAG = "JIGUANG-TagAliasHelper";

    private Context context;

    private static TagAliasOperatorHelper mInstance;

    private TagAliasOperatorHelper() {
    }

    public static TagAliasOperatorHelper getInstance() {
        if (mInstance == null) {
            synchronized (TagAliasOperatorHelper.class) {
                if (mInstance == null) {
                    mInstance = new TagAliasOperatorHelper();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        }
    }

    /**
     * 标签操作回调
     * tag 增删查改的操作会在此方法中回调结果。
     *
     * @param context
     * @param jPushMessage
     */
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        Log.i(TAG, "action - onTagOperatorResult, sequence:" + sequence + ",tags:" + jPushMessage.getTags());
        Log.i(TAG, "tags size:" + jPushMessage.getTags().size());
        init(context);

        if (jPushMessage.getErrorCode() == 0) {
            Log.i(TAG, "action - modify tag Success,sequence:" + sequence);
            ToastHelper.showOk(context, "modify success");
        } else {
            String logs = "Failed to modify tags";
            if (jPushMessage.getErrorCode() == 6018) {
                //tag数量超过限制,需要先清除一部分再add
                logs += ", tags is exceed limit need to clean";
            }
            logs += ", errorCode:" + jPushMessage.getErrorCode();
            Log.e(TAG, logs);
            ToastHelper.showOther(context, logs);
        }
    }

    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        Log.i(TAG, "action - onCheckTagOperatorResult, sequence:" + sequence + ",checktag:" + jPushMessage.getCheckTag());
        init(context);
        if (jPushMessage.getErrorCode() == 0) {
            String logs = "modify tag " + jPushMessage.getCheckTag() + " bind state success,state:" + jPushMessage.getTagCheckStateResult();
            Log.i(TAG, logs);
            ToastHelper.showOk(context, "modify success");
        } else {
            String logs = "Failed to modify tags, errorCode:" + jPushMessage.getErrorCode();
            Log.e(TAG, logs);
            ToastHelper.showOther(context, logs);
        }
    }
    private static final int MSG_SET_ALIAS = 1001;
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAlias(getApplicationContext(),
                            1, (String) msg.obj
                           );
                    break;
                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };
    /**
     * 别名操作回调
     * alias 相关的操作会在此方法中回调结果。
     *
     * @param context
     * @param jPushMessage
     */
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        Log.i(TAG, "action - onAliasOperatorResult, sequence:" + sequence + ",alias:" + jPushMessage.getAlias());
        init(context);

        if (jPushMessage.getErrorCode() == 0) {
            Log.i(TAG, "action - modify alias Success,sequence:" + sequence);
            ToastHelper.showOk(context, "modify success");
        } else if (jPushMessage.getErrorCode() == 6002) {
            String logs = "Failed to modify alias, errorCode:" + jPushMessage.getErrorCode();
            Log.e(TAG, logs);
            ToastHelper.showOther(context, logs);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, jPushMessage.getAlias()), 1000 * 60);

//            MMKV.defaultMMKV().putString(AdvActivity.ALIAS_DATA, "");
        } else {
            String logs = "Failed to modify alias, errorCode:" + jPushMessage.getErrorCode();
            Log.e(TAG, logs);
            ToastHelper.showOther(context, logs);
        }
    }

    //设置手机号码回调
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        Log.i(TAG, "action - onMobileNumberOperatorResult, sequence:" + sequence + ",mobileNumber:" + jPushMessage.getMobileNumber());
        init(context);
        if (jPushMessage.getErrorCode() == 0) {
            Log.i(TAG, "action - set mobile number Success,sequence:" + sequence);
            ToastHelper.showOk(context, "modify success");
        } else {
            String logs = "Failed to set mobile number, errorCode:" + jPushMessage.getErrorCode();
            Log.e(TAG, logs);
            ToastHelper.showOther(context, logs);
//            MMKV.defaultMMKV().putString(AdvActivity.MN_DATA, "");
        }
    }


}
