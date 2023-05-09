package com.voocoo.pet.common.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;


public class ViewUtils {
    /**
     * gridview计算高度
     */
    public static void setGridViewHeightByChildren(Context context, GridView gridView) {
        //获取gridview高度
        ListAdapter listAdaper = gridView.getAdapter();
        if (listAdaper == null) {
            return;
        }
        //总高度
        int totalHeight = 0;
        //计算行数 向上取整
        int lineNum = (int) Math.ceil((double) listAdaper.getCount() / (double) gridView.getNumColumns());
        View item = listAdaper.getView(0, null, gridView);
        item.measure(0, 0);
        //获取高度和
        totalHeight = item.getMeasuredHeight() * lineNum;
        //布局参数
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        //设置布局高度
        params.height = totalHeight;
        //设置margin
        ((ViewGroup.MarginLayoutParams) params).setMargins(DensityUtil.dip2px(context, 30), 10, DensityUtil.dip2px(context, 30), 10);
        //设置参数
        gridView.setLayoutParams(params);
    }


    /**
     * 获取状态栏高度
     */
    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return px
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    public static void setTextViewTopImg(Context context, TextView textView, int src) {
        Drawable nav_up = null;
        nav_up = context.getResources().getDrawable(src);
        nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
        textView.setCompoundDrawables(null, nav_up, null, null);
    }

}
