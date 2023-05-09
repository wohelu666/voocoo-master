package com.voocoo.pet.common.widgets;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;


import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.voocoo.pet.R;
import com.voocoo.pet.common.utils.LogUtil;
import com.zkk.view.rulerview.RulerView;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.forward.androids.views.ScrollPickerView;
import cn.forward.androids.views.StringScrollPicker;


/**
 * App 用到的dialog类型
 */

public class AppDialog extends Dialog {

    public AppDialog(Context context, View view, @LayoutRes int layoutRes) {
        super(context, R.style.AppDialog_Bottom);
        // set content
        if (view != null) setContentView(view);
        else setContentView(layoutRes);

        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.BOTTOM;
            window.setAttributes(params);
        }
    }

    private AppDialog(Context context, @LayoutRes int layoutRes, int width, int height) {
        super(context, R.style.AppDialog);
        // set content
        setContentView(layoutRes);

        Window window = getWindow();
        if (window != null) {

            WindowManager.LayoutParams params = window.getAttributes();
            if (width > 0) {
                params.width = width;
            } else {
                params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            }
            if (height > 0) {
                params.height = height;
            } else {
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            }

            window.setAttributes(params);
        }
    }

    private AppDialog(Context context, @LayoutRes int layoutRes) {
        super(context, R.style.AppDialog);
        // set content
        setContentView(layoutRes);
    }

    public static AppDialog bottomSheetList(Context context, @NonNull String[] listContent,
                                            AdapterView.OnItemClickListener listener) {
        LinearLayoutCompat list = new LinearLayoutCompat(context);
        list.setBackgroundColor(ContextCompat.getColor(context, R.color.color_ffffff));
        list.setOrientation(LinearLayoutCompat.VERTICAL);
        list.setShowDividers(LinearLayoutCompat.SHOW_DIVIDER_MIDDLE);
        list.setDividerDrawable(ContextCompat.getDrawable(context, R.drawable.divider_line));

        for (int i = 0, j = listContent.length; i < j; i++) {
            list.addView(textItem(context, listContent[i], j > 2 && i == j - 1, i, listener));
        }

        final AppDialog dialog = new AppDialog(context, list, 0);
        dialog.setCanceledOnTouchOutside(true);

    /*    Display defaultDisplay = dialog.getWindow().getWindowManager().getDefaultDisplay();
        //获取屏幕高度
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getRealMetrics(outMetrics);
        int heightPixels = outMetrics.heightPixels;

        //获取内容高度
        DisplayMetrics outMetrics2 = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics2);
        int heightPixels2 = outMetrics2.heightPixels;

        LogUtil.d("heightPixels" + heightPixels);
        LogUtil.d("heightPixels2" + heightPixels2);
        LogUtil.d("getStatusBarHeight" + getStatusBarHeight(context));


        if (heightPixels - heightPixels2 - getStatusBarHeight(context) != 0) {

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

            lp.setMargins(0, 0, 0, getNavigationBarHeight(context));
            //view要设置margin的view
            list.setLayoutParams(lp);
        }*/

        dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        dialog.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        //布局位于状态栏下方
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        //全屏
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        //隐藏导航栏
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                if (Build.VERSION.SDK_INT >= 19) {
                    uiOptions |= 0x00001000;
                } else {
                    uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                }
                dialog.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
            }
        });
        return dialog;
    }

    //获取虚拟按键的高度
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    public static int getStatusBarHeight(Context context) {

        Resources resources = context.getResources();

        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");

        int height = resources.getDimensionPixelSize(resourceId);

        Log.v("status bar>>>", "height:" + height);

        return height;

    }

    /**
     * 判断虚拟按键栏是否重写
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }

    /**
     * 检查是否存在虚拟按键栏
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    private static TextView textItem(Context context, String content, boolean isLast, final int index,
                                     final AdapterView.OnItemClickListener listener) {
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55,
                context.getResources().getDisplayMetrics());
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height);

        TextView tv = new TextView(context);
        tv.setText(content);
        tv.setTextColor(ContextCompat.getColor(context, isLast ? R.color.color_e33a39 : R.color.color_b9bfc2));
        tv.setLayoutParams(params);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(null, v, index, v.getId());
            }
        });
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    /**
     * 简单的加载等待框
     */
    public static AppDialog loading(Context context) {
        AppDialog dialog = new AppDialog(context, R.layout.dialog_loading,
                (int) (context.getResources().getDisplayMetrics().widthPixels * 0.35f),
                (int) (context.getResources().getDisplayMetrics().widthPixels * 0.35f));
        dialog.setCanceledOnTouchOutside(false);

        ContentLoadingProgressBar bar = (ContentLoadingProgressBar) dialog.findViewById(R.id.loading_progress);
        if (bar != null) {
            bar.show();
        }

        return dialog;
    }

    public static AppDialog showPromptDialog(Context context, String errorString) {
        final AppDialog dialog = new AppDialog(context, R.layout.dialog_prompt,
                (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8f),
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(false);

        TextView content = (TextView) dialog.findViewById(R.id.tv_error_content);
        if (content != null) {
            content.setText(errorString);
        }
        dialog.findViewById(R.id.btn_ensure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public static AppDialog showAlertDialog(Context context, String errorString) {
        final AppDialog dialog = new AppDialog(context, R.layout.dialog_alert,
                (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8f),
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(false);

        TextView content = (TextView) dialog.findViewById(R.id.tv_error_content);
        if (content != null) {
            content.setText(errorString);
        }
        dialog.findViewById(R.id.btn_ensure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    /**
     * 包含标题、内容和单按钮以及按钮监听的dialog
     */
    public static AppDialog doubleTextOneButton(Context context, String title, String content,
                                                final View.OnClickListener listener) {
        final AppDialog dialog = new AppDialog(context, R.layout.dialog_double_text_one_button,
                (int) (context.getResources().getDisplayMetrics().widthPixels * 0.85f), 0);
        dialog.setCanceledOnTouchOutside(false);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.text_title);
        if (tvTitle != null) {
            if (!TextUtils.isEmpty(title))
                tvTitle.setText(title);
        }
        TextView tvContent = (TextView) dialog.findViewById(R.id.text_content);
        if (tvContent != null) {
            if (!TextUtils.isEmpty(content))
                tvContent.setText(content);
        }
        AppCompatButton bt = (AppCompatButton) dialog.findViewById(R.id.text_button);
        if (bt != null) {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onClick(v);
                    dialog.dismiss();
                }
            });
        }

        return dialog;
    }

    /**
     * 包含标题、内容和单按钮的dialog
     */
    public static AppDialog doubleTextOneButton(Context context, String title, String content) {
        return doubleTextOneButton(context, title, content, null);
    }


    public static AppDialog doubleTextDoubleButton(Context context, String title, String content,
                                                   View.OnClickListener leftListener, View.OnClickListener rightListener) {
        return doubleTextDoubleButton(context, title, content, null, null, leftListener, rightListener);
    }

    /**
     * 包含标题、内容和双按钮的dialog
     */
    public static AppDialog doubleTextDoubleButton2(Context context, String title, String right, final View.OnClickListener leftListener,
                                                    final View.OnClickListener rightListener) {
        final AppDialog dialog = new AppDialog(context, R.layout.dialog_double_text_double_button2,
                (int) (context.getResources().getDisplayMetrics().widthPixels * 0.85f), 0);
        dialog.setCanceledOnTouchOutside(false);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.text_title);
        if (tvTitle != null) {
            if (!TextUtils.isEmpty(title))
                tvTitle.setText(title);
        }
        TextView tvContent = (TextView) dialog.findViewById(R.id.tv_privacy_protocol);
        tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftListener.onClick(v);
            }
        });

        final CheckBox checkBox = dialog.findViewById(R.id.cb_agreement);

        AppCompatButton confirm = (AppCompatButton) dialog.findViewById(R.id.button_confirm);
        if (!TextUtils.isEmpty(right)) {
            confirm.setText(right);
        }
        if (confirm != null) {
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!checkBox.isChecked()) {
                        return;
                    }
                    dialog.dismiss();
                    if (rightListener != null)
                        rightListener.onClick(v);
                }
            });
        }

        return dialog;
    }


    /**
     * 包含标题、内容和双按钮的dialog
     */
    public static AppDialog doubleTextDoubleButton(Context context, String title, String content,
                                                   String left, String right, final View.OnClickListener leftListener,
                                                   final View.OnClickListener rightListener) {
        final AppDialog dialog = new AppDialog(context, R.layout.dialog_double_text_double_button,
                (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9f), 0);
        dialog.setCanceledOnTouchOutside(false);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.text_title);
        if (tvTitle != null) {
            if (!TextUtils.isEmpty(title))
                tvTitle.setText(title);
        }
        TextView tvContent = (TextView) dialog.findViewById(R.id.text_content);
        if (tvContent != null) {
            if (!TextUtils.isEmpty(content))
                tvContent.setText(content);
        }

        AppCompatButton cancel = (AppCompatButton) dialog.findViewById(R.id.button_cancel);
        if (!TextUtils.isEmpty(left)) {
            cancel.setText(left);
        } else {
            cancel.setVisibility(View.GONE);
        }
        if (cancel != null) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (leftListener != null)
                        leftListener.onClick(v);
                }
            });
        }

        AppCompatButton confirm = (AppCompatButton) dialog.findViewById(R.id.button_confirm);
        if (!TextUtils.isEmpty(right)) {
            confirm.setText(right);
        }
        if (confirm != null) {
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (rightListener != null)
                        rightListener.onClick(v);
                }
            });
        }

        return dialog;
    }

    //选择时间弹窗
    public static AppDialog showSetTime(Activity context, int startHour, int startMinute, int endHour, int endMinute, final TimerSetListener listener) {
        final AppDialog dialog = new AppDialog(context, null, R.layout.dialog_select_time);
        dialog.setCanceledOnTouchOutside(true);

        final WheelView wvStartHour = (WheelView) dialog.findViewById(R.id.wv_start_hour);
        ArrayList<String> hours = new ArrayList<>();

        for (int i = 0; i < 24; i++) {
            hours.add(i + "");
        }

        wvStartHour.setAdapter(new ArrayWheelAdapter<>(hours));
        wvStartHour.setCyclic(false);
        wvStartHour.setCurrentItem(startHour);

        final WheelView wvEndHour = (WheelView) dialog.findViewById(R.id.wv_end_hour);

        for (int i = 0; i < 24; i++) {
            hours.add(i + "");
        }

        wvEndHour.setAdapter(new ArrayWheelAdapter<>(hours));
        wvEndHour.setCyclic(false);
        wvEndHour.setCurrentItem(endHour);

        final WheelView wvStartMinute = (WheelView) dialog.findViewById(R.id.wv_start_min);

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            list.add(String.format("%02d", i));
        }

        wvStartMinute.setAdapter(new ArrayWheelAdapter<>(list));
        wvStartMinute.setCyclic(false);
        wvStartMinute.setCurrentItem(startMinute);

        final WheelView wvEndMinute = (WheelView) dialog.findViewById(R.id.wv_end_min);

        for (int i = 0; i < 60; i++) {
            list.add(String.format("%02d", i));
        }

        wvEndMinute.setAdapter(new ArrayWheelAdapter<>(list));
        wvEndMinute.setCyclic(false);
        wvEndMinute.setCurrentItem(endMinute);

        Button cancel = (Button) dialog.findViewById(R.id.dialog_bt_cancel);
        if (cancel != null) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        Button confirm = (Button) dialog.findViewById(R.id.diaglog_bt_sure);
        if (confirm != null) {
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    listener.setTimer(wvStartHour.getCurrentItem(), wvStartMinute.getCurrentItem(),
                            wvEndHour.getCurrentItem(), wvEndMinute.getCurrentItem());
                }
            });
        }

        return dialog;
    }

    public interface TimerSetListener {
        void setTimer(int startHour, int startMinutes, int endHour, int endMinutes);
    }


    //选择时间弹窗
    public static AppDialog showSetMintues(Activity context, int minute, final MinSetListener listener) {
        final AppDialog dialog = new AppDialog(context, null, R.layout.dialog_select_time);
        dialog.setCanceledOnTouchOutside(true);

        final WheelView wvMinute = (WheelView) dialog.findViewById(R.id.wv_mintue);

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            list.add(String.format("%02d", i));
        }

        wvMinute.setAdapter(new ArrayWheelAdapter<>(list));
        wvMinute.setCyclic(false);
        wvMinute.setCurrentItem(minute);

        Button cancel = (Button) dialog.findViewById(R.id.dialog_bt_cancel);
        if (cancel != null) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        Button confirm = (Button) dialog.findViewById(R.id.diaglog_bt_sure);
        if (confirm != null) {
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.setTimer(wvMinute.getCurrentItem());
                    dialog.dismiss();
                }
            });
        }

        return dialog;
    }

    public interface MinSetListener {
        void setTimer(int minutes);
    }


    //选择时间弹窗
    public static AppDialog showSetWeight(Activity context, List<String> weightList, int weight, final WeightSetListener listener) {
        final AppDialog dialog = new AppDialog(context, R.layout.dialog_select_weight, (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9f), 0);
        dialog.setCanceledOnTouchOutside(true);

        final WheelView wvHour = (WheelView) dialog.findViewById(R.id.wv_hour);

        wvHour.setAdapter(new ArrayWheelAdapter<>(weightList));
        wvHour.setCyclic(false);
        wvHour.setCurrentItem(weight);

        Button cancel = (Button) dialog.findViewById(R.id.dialog_bt_cancel);
        if (cancel != null) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        Button confirm = (Button) dialog.findViewById(R.id.diaglog_bt_sure);
        if (confirm != null) {
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.setWeight(wvHour.getCurrentItem());
                }
            });
        }

        return dialog;
    }

    public interface WeightSetListener {
        void setWeight(int weight);
    }

    /**
     * 包含标题、内容和双按钮的dialog
     */
    public static AppDialog privacyDialog(Context context, String title, String right, final View.OnClickListener leftListener, final View.OnClickListener leftListener2, final View.OnClickListener rightListener, final View.OnClickListener leftListener3) {
        final AppDialog dialog = new AppDialog(context, R.layout.dialog_privacy,
                (int) (context.getResources().getDisplayMetrics().widthPixels * 0.85f), 0);
        dialog.setCanceledOnTouchOutside(false);

        CheckBox checkBox = dialog.findViewById(R.id.cb_privacy);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.text_title);
        if (tvTitle != null) {
            if (!TextUtils.isEmpty(title))
                tvTitle.setText(title);
        }

        TextView tv = (TextView) dialog.findViewById(R.id.tv_privacy_protocol);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftListener.onClick(v);
            }
        });

        TextView tv2 = (TextView) dialog.findViewById(R.id.tv_license);
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftListener2.onClick(v);
            }
        });

        AppCompatButton confirm = (AppCompatButton) dialog.findViewById(R.id.button_confirm);
        if (!TextUtils.isEmpty(right)) {
            confirm.setText(right);
        }
        if (confirm != null) {
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        dialog.dismiss();
                        if (rightListener != null)
                            rightListener.onClick(v);
                    }
                }
            });
        }
        TextView tvDis = dialog.findViewById(R.id.tv_dis);
        tvDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (leftListener3 != null)
                    leftListener3.onClick(v);
            }
        });


        return dialog;
    }

    //选择份量弹窗
    public static AppDialog showSetFoodWeight(Activity context, int weight, final WeightSetListener listener) {
        final AppDialog dialog = new AppDialog(context, null, R.layout.dialog_select_food_weight);
        dialog.setCanceledOnTouchOutside(true);

        final WheelView wvHour = (WheelView) dialog.findViewById(R.id.wv_hour);

        ArrayList<String> list = new ArrayList<>();
        for (int i = 1; i < 21; i++) {
            list.add(i + "份");
        }

        wvHour.setAdapter(new ArrayWheelAdapter<>(list));
        wvHour.setCyclic(false);
        wvHour.setCurrentItem(weight);
        Button cancel = (Button) dialog.findViewById(R.id.dialog_bt_cancel);
        if (cancel != null) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        Button confirm = (Button) dialog.findViewById(R.id.diaglog_bt_sure);
        if (confirm != null) {
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.setWeight(wvHour.getCurrentItem());
                    dialog.dismiss();
                }
            });
        }

        return dialog;
    }

    //选择时间弹窗
    public static AppDialog showSetFeedPlanTime(Activity context, int type, int hour, int min, final FeedTimerSetListener listener) {
        final AppDialog dialog = new AppDialog(context, null, R.layout.dialog_select_plan_time);
        dialog.setCanceledOnTouchOutside(true);

        final WheelView wvType = (WheelView) dialog.findViewById(R.id.wv_type);
        ArrayList<String> types = new ArrayList<>();

        types.add("上午");
        types.add("下午");

        wvType.setAdapter(new ArrayWheelAdapter<>(types));
        wvType.setCyclic(false);
        wvType.setCurrentItem(type);

        final WheelView wvStartHour = (WheelView) dialog.findViewById(R.id.wv_start_hour);
        ArrayList<String> hours = new ArrayList<>();

        for (int i = 0; i < 24; i++) {
            hours.add(i + "");
        }

        wvStartHour.setAdapter(new ArrayWheelAdapter<>(hours));
        wvStartHour.setCyclic(false);
        wvStartHour.setCurrentItem(hour);

        final WheelView wvStartMinute = (WheelView) dialog.findViewById(R.id.wv_start_min);

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            list.add(String.format("%02d", i));
        }

        wvStartMinute.setAdapter(new ArrayWheelAdapter<>(list));
        wvStartMinute.setCyclic(false);
        wvStartMinute.setCurrentItem(min);

        Button cancel = (Button) dialog.findViewById(R.id.dialog_bt_cancel);
        if (cancel != null) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        Button confirm = (Button) dialog.findViewById(R.id.diaglog_bt_sure);
        if (confirm != null) {
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    listener.setTimer(wvType.getCurrentItem(), wvStartHour.getCurrentItem(), wvStartMinute.getCurrentItem());
                }
            });
        }

        return dialog;
    }

    public interface FeedTimerSetListener {
        void setTimer(int type, int hour, int min);
    }

    //选择时间弹窗
    public static AppDialog showSetFeedPlanTime2(Activity context, int hour, int min, final FeedTimerSetListener2 listener) {
        final AppDialog dialog = new AppDialog(context, null, R.layout.dialog_select_plan_time2);
        dialog.setCanceledOnTouchOutside(true);

        final WheelView wvStartHour = (WheelView) dialog.findViewById(R.id.wv_start_hour);
        ArrayList<String> hours = new ArrayList<>();

        for (int i = 0; i < 24; i++) {
            hours.add(i + "");
        }

        wvStartHour.setAdapter(new ArrayWheelAdapter<>(hours));
        wvStartHour.setCyclic(false);
        wvStartHour.setCurrentItem(hour);

        final WheelView wvStartMinute = (WheelView) dialog.findViewById(R.id.wv_start_min);

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            list.add(String.format("%02d", i));
        }

        wvStartMinute.setAdapter(new ArrayWheelAdapter<>(list));
        wvStartMinute.setCyclic(false);
        wvStartMinute.setCurrentItem(min);

        Button cancel = (Button) dialog.findViewById(R.id.dialog_bt_cancel);
        if (cancel != null) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        Button confirm = (Button) dialog.findViewById(R.id.diaglog_bt_sure);
        if (confirm != null) {
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    listener.setTimer(wvStartHour.getCurrentItem(), wvStartMinute.getCurrentItem());
                }
            });
        }

        return dialog;
    }

    public interface FeedTimerSetListener2 {
        void setTimer(int hour, int min);
    }

    //选择时间弹窗
    public static AppDialog showSetFoodSize(AppDialog dialog, Activity context, int size, boolean isFirst, final FeedSizeSetListener listener) {
        dialog.setCanceledOnTouchOutside(true);
        Log.e("TAG", "showSetFoodSize: " + size);
        StringScrollPicker stringScrollPicker = dialog.findViewById(R.id.picker_05_horizontal);
        stringScrollPicker.setIsCirculation(false);
        TextView tvSize = dialog.findViewById(R.id.tv_size);
        tvSize.setText("约" + (10 + size) + "g");

        ArrayList<String> list = new ArrayList<>();
        for (int i = 10; i < 101; i++) {
            list.add(i + "");
        }
        stringScrollPicker.setData(list);
        stringScrollPicker.setSelectedPosition(size);
        stringScrollPicker.setOnSelectedListener(new ScrollPickerView.OnSelectedListener() {
            @Override
            public void onSelected(ScrollPickerView scrollPickerView, int position) {
                tvSize.setText("约" + (10+position) + "g");

            }
        });


        Button cancel = (Button) dialog.findViewById(R.id.dialog_bt_cancel);
        if (cancel != null) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        Button confirm = (Button) dialog.findViewById(R.id.diaglog_bt_sure);
        if (confirm != null) {
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    listener.setSize(stringScrollPicker.getSelectedItem() + "");
                }
            });
        }
        dialog.show();
        return dialog;
    }

    public interface FeedSizeSetListener {
        void setSize(String size);
    }


    //选择体重弹窗
    public static AppDialog showSetPetWeight(Activity context, float weight, final PetWeightSetListener listener) {
        final AppDialog dialog = new AppDialog(context, null, R.layout.dialog_select_pet_weight);
        dialog.setCanceledOnTouchOutside(true);
        TextView tvWeight = dialog.findViewById(R.id.tv_weight);
        RulerView ruler_weight = dialog.findViewById(R.id.ruler_weight);

        ruler_weight.setOnValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                tvWeight.setText(value + "");
            }
        });
        tvWeight.setText(weight + "");
        ruler_weight.setValue(weight, 0, 100, (float) 0.1);

        Button cancel = (Button) dialog.findViewById(R.id.dialog_bt_cancel);
        if (cancel != null) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        Button confirm = (Button) dialog.findViewById(R.id.diaglog_bt_sure);
        if (confirm != null) {
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    listener.setSize(Float.valueOf(tvWeight.getText().toString()));
                }
            });
        }

        return dialog;
    }

    public interface PetWeightSetListener {
        void setSize(float size);
    }

    /**
     * 获取两个日期之间的间隔天数
     *
     * @return
     */
    public static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    public static void setDayText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//年-月-日 时-分
        int year = wvYear.getCurrentItem() + 1949;
        int month = wvMonth.getCurrentItem() + 1;
        int date = wvDate.getCurrentItem() + 1;

        Calendar sel = Calendar.getInstance();
        sel.set(Calendar.YEAR, year);
        sel.set(Calendar.MONTH, month - 1);
        sel.set(Calendar.DATE, date);
        LogUtil.d(dateFormat.format(sel.getTime()));

        age = getGapCount(sel.getTime(), Calendar.getInstance().getTime());
        tvDays.setText(getFormatAge(age));

    }

    static int age;

    static WheelView wvYear;
    static WheelView wvMonth;
    static WheelView wvDate;
    static TextView tvDays;

    private static String getFormatAge(int day) {
        int year = day / 365;
        int month = (day - year * 365) / 30;
        int date = day - ((year * 365) + (month * 30));
        String content = "";
        if (year > 0) {
            content += year + "岁";
        }
        if (month > 0) {
            content += month + "月";
        }
        if (date > 0) {
            content += date + "天";
        }
        return content;
    }

    //选择体重弹窗
    public static AppDialog showSetPetAge(Activity context, int cAge, final PetAgeSetListener listener) {
        final AppDialog dialog = new AppDialog(context, null, R.layout.dialog_select_pet_age);
        dialog.setCanceledOnTouchOutside(true);
        tvDays = dialog.findViewById(R.id.tv_days);

        try {
            tvDays.setText(getFormatAge(cAge));
        } catch (Exception e) {
            e.printStackTrace();
        }

        wvYear = dialog.findViewById(R.id.wv_year);
        wvMonth = dialog.findViewById(R.id.wv_month);
        wvDate = dialog.findViewById(R.id.wv_date);

        ArrayList<String> years = new ArrayList<>();

        for (int i = 1949; i < Calendar.getInstance().get(Calendar.YEAR) + 1; i++) {
            years.add(i + "");
        }

        wvYear.setAdapter(new ArrayWheelAdapter<>(years));
        wvYear.setCyclic(false);
        wvYear.setTextSize(15);
        wvYear.setCurrentItem(Calendar.getInstance().get(Calendar.YEAR) - 1949);
        wvYear.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                setDayText();
            }
        });

        ArrayList<String> month = new ArrayList<>();

        for (int i = 1; i < 13; i++) {
            month.add(i + "");
        }

        wvMonth.setAdapter(new ArrayWheelAdapter<>(month));
        wvMonth.setCyclic(false);
        wvMonth.setTextSize(15);
        wvMonth.setCurrentItem(Calendar.getInstance().get(Calendar.MONTH));
        wvMonth.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                setDayText();
            }
        });

        ArrayList<String> days = new ArrayList<>();

        for (int i = 1; i < 31; i++) {
            days.add(i + "");
        }

        wvDate.setAdapter(new ArrayWheelAdapter<>(days));
        wvDate.setCyclic(false);
        wvDate.setTextSize(15);
        wvDate.setCurrentItem(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - 1);
        wvDate.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                setDayText();
            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.dialog_bt_cancel);
        if (cancel != null) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        Button confirm = (Button) dialog.findViewById(R.id.diaglog_bt_sure);
        if (confirm != null) {
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//年-月-日 时-分
                    int year = wvYear.getCurrentItem() + 1949;
                    int month = wvMonth.getCurrentItem() + 1;
                    int date = wvDate.getCurrentItem() + 1;

                    Calendar sel = Calendar.getInstance();
                    sel.set(Calendar.YEAR, year);
                    sel.set(Calendar.MONTH, month - 1);
                    sel.set(Calendar.DATE, date);
                    listener.setSize(age, dateFormat.format(sel.getTime()));
                }
            });
        }

        return dialog;
    }

    public interface PetAgeSetListener {
        void setSize(int size, String str);
    }

    static int sel = 0;

    //选择体重弹窗
    public static AppDialog showSelWork(Activity context, final WorkSetListener listener) {
        final AppDialog dialog = new AppDialog(context, null, R.layout.dialog_select_work);
        dialog.setCanceledOnTouchOutside(true);
        final WheelView wvHour = (WheelView) dialog.findViewById(R.id.wv_hour);

        List<String> list = Arrays.asList(context.getResources().getStringArray(R.array.work_array));
        wvHour.setAdapter(new ArrayWheelAdapter<>(list));
        wvHour.setCyclic(false);
        wvHour.setCurrentItem(0);

        Button cancel = (Button) dialog.findViewById(R.id.dialog_bt_cancel);
        if (cancel != null) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        Button confirm = (Button) dialog.findViewById(R.id.diaglog_bt_sure);
        if (confirm != null) {
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.setSize(list.get(wvHour.getCurrentItem()));
                    dialog.dismiss();
                }
            });
        }

        return dialog;
    }

    public interface WorkSetListener {
        void setSize(String work);
    }

    //选择体重弹窗
    public static AppDialog showSelAreaCode(Activity context, final AreaCodeSetListener listener) {
        final AppDialog dialog = new AppDialog(context, null, R.layout.dialog_select_area_code);
        dialog.setCanceledOnTouchOutside(true);
        TextView tvAreaCode1 = dialog.findViewById(R.id.tv_area_code1);
        TextView tvAreaCode2 = dialog.findViewById(R.id.tv_area_code2);
        TextView tvAreaCode3 = dialog.findViewById(R.id.tv_area_code3);
        TextView tvAreaCode4 = dialog.findViewById(R.id.tv_area_code4);
        tvAreaCode1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.setCode("+86");
                dialog.dismiss();
            }
        });
        tvAreaCode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.setCode("+852");
                dialog.dismiss();
            }
        });
        tvAreaCode3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.setCode("+853");
                dialog.dismiss();
            }
        });
        tvAreaCode4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.setCode("+886");
                dialog.dismiss();
            }
        });


        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public interface AreaCodeSetListener {
        void setCode(String code);
    }


    /**
     * 包含标题、内容和双按钮的dialog
     */
    public static AppDialog cancelAccountDialog(Context context, String days, final View.OnClickListener leftListener,
                                                final View.OnClickListener rightListener) {
        final AppDialog dialog = new AppDialog(context, R.layout.dialog_cancel,
                (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9f), 0);
        dialog.setCanceledOnTouchOutside(false);

        TextView tvDays = (TextView) dialog.findViewById(R.id.tv_day);
        tvDays.setText(context.getString(R.string.text_cancel_account_tips, days));

        AppCompatButton cancel = (AppCompatButton) dialog.findViewById(R.id.button_cancel);
        if (cancel != null) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (leftListener != null)
                        leftListener.onClick(v);
                }
            });
        }

        AppCompatButton confirm = (AppCompatButton) dialog.findViewById(R.id.button_feedback);
        if (confirm != null) {
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (rightListener != null)
                        rightListener.onClick(v);
                }
            });
        }

        AppCompatButton dis = (AppCompatButton) dialog.findViewById(R.id.button_confirm);
        if (dis != null) {
            dis.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        return dialog;
    }
}