package com.voocoo.pet.base.recycleview_base;


import androidx.annotation.LayoutRes;

/**
 * @author sswukang on 2016/2/15 14:51
 * @version 1.0
 *          多布局支持接口
 */
public interface MultiItemTypeSupport<T> {
    @LayoutRes
    int getLayoutId(int viewType);

    int getItemViewType(int position, T t);
}
