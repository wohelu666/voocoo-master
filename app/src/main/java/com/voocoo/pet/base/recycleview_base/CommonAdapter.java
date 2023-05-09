package com.voocoo.pet.base.recycleview_base;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @author sswukang on 2016/2/15 14:30
 * @version 1.0
 *          RecyclerView通用的基础Adapter。
 */
public abstract class CommonAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder>
        implements RecyclerAdapterListener {
    @LayoutRes
    private int layoutId;
    private List<T> data;

    /**
     * @param layoutId adapter需要的布局资源id
     * @param data     数据
     */
    public CommonAdapter(@LayoutRes int layoutId, List<T> data) {
        this.layoutId = layoutId;
        this.data = data;
        setHasStableIds(true);
    }

    // 数据总数
    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        return 0;
    }

    // 设置ID，保证item操作不错乱
    @Override
    public long getItemId(int position) {
        T t = getItem(position);
        if (t != null)
            return t.hashCode();
        else
            return super.getItemId(position);
    }

    // 获得item数据封装
    public T getItem(int position) {
        if (data != null && data.size() > position)
            return data.get(position);
        return null;
    }

    // 创建hold
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return RecyclerViewHolder.get(parent, layoutId, viewType, this);
    }

    // 绑定hold
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        convert(holder, getItem(position), position);
    }

    /**
     * 实现该抽象方法，完成数据的填充。
     *
     * @param holder   {@link RecyclerViewHolder}
     * @param t        每个 position 对应的对象
     * @param position 当前行数，采用{@link RecyclerViewHolder#getLayoutPosition()}
     */
    public abstract void convert(RecyclerViewHolder holder, T t, int position);

    // 单击
    @Override
    public void onItemClick(View v, int position, int viewType) {
    }

    // 长按
    @Override
    public boolean onItemLongClick(View v, int position, int viewType) {
        return false;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
