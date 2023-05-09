package com.voocoo.pet.base.recycleview_base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


/**
 * @author sswukang on 2016/2/15 16:01
 * @version 1.0
 *          自定义 RecyclerView 的 ViewHolder
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private final SparseArray<View> views;
    @LayoutRes
    private int layoutId;
    private int viewType;
    private RecyclerAdapterListener listener;

    private RecyclerViewHolder(ViewGroup parent, @LayoutRes int layoutId, int viewType,
                               RecyclerAdapterListener listener) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
        this.views = new SparseArray<>();
        this.layoutId = layoutId;
        this.viewType = viewType;
        this.listener = listener;

        //添加监听事件
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    static RecyclerViewHolder get(ViewGroup parent, @LayoutRes int layoutId, int viewType,
                                  RecyclerAdapterListener listener) {
        return new RecyclerViewHolder(parent, layoutId, viewType, listener);
    }

    /**
     * 得到view
     *
     * @param viewId view在当前layout里设置的id
     * @param <T>    view的子类型
     * @return view的子类型实例
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(@IdRes int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            // 为TextView设置字体
//            if (view instanceof TextView) {
//                ((TextView) view).setTypeface(TTFUtil.tf_2nd);
//            }
            views.put(viewId, view);
        }
        return (T) view;
    }

    @Override
    public void onClick(View v) {
        listener.onItemClick(v, getLayoutPosition(), getViewType());
    }

    @Override
    public boolean onLongClick(View v) {
        return listener.onItemLongClick(v, getLayoutPosition(), getViewType());
    }

    public Context getContext() {
        return itemView.getContext();
    }

    @LayoutRes
    public int getLayoutId() {
        return layoutId;
    }

    public int getViewType() {
        return viewType;
    }

    public void setText(@IdRes int viewId, @StringRes int resId) {
        TextView tv = getView(viewId);
        tv.setText(resId);
    }

    public void setText(@IdRes int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
    }

    public void setTextColor(@IdRes int viewId, @ColorInt int color) {
        TextView tv = getView(viewId);
        tv.setTextColor(color);
    }

    public void setTextColorRes(@IdRes int viewId, @ColorRes int resId) {
        TextView tv = getView(viewId);
        tv.setTextColor(ContextCompat.getColor(getContext(), resId));
    }

    public void setTextStartImage(@IdRes int viewId, @DrawableRes int resId) {
        TextView tv = getView(viewId);
        Drawable drawable = ContextCompat.getDrawable(getContext(), resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv.setCompoundDrawables(drawable, null, null, null);
    }

    public void setImageResource(@IdRes int viewId, @DrawableRes int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
    }

}
