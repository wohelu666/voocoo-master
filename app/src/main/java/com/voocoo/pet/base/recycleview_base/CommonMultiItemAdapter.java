package com.voocoo.pet.base.recycleview_base;

import android.view.ViewGroup;

import java.util.List;

/**
 * @author sswukang on 2016/2/15 14:50
 * @version 1.0
 *          RecyclerView通用多布局Adapter。
 */
public abstract class CommonMultiItemAdapter<T> extends CommonAdapter<T> {
    private MultiItemTypeSupport<T> multiItemTypeSupport;

    /**
     * @param data                 数据
     * @param multiItemTypeSupport 多布局支持接口
     */
    public CommonMultiItemAdapter(List<T> data, MultiItemTypeSupport<T> multiItemTypeSupport) {
        super(-1, data);
        this.multiItemTypeSupport = multiItemTypeSupport;

        if (multiItemTypeSupport == null)
            throw new IllegalArgumentException("the MultiItemTypeSupport<T> can not be null.");
    }

    // 根据item类型分配布局类型
    @Override
    public int getItemViewType(int position) {
        return multiItemTypeSupport.getItemViewType(position, getItem(position));
    }

    // 创建hold
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return RecyclerViewHolder.get(parent, multiItemTypeSupport.getLayoutId(viewType), viewType, this);
    }

}
