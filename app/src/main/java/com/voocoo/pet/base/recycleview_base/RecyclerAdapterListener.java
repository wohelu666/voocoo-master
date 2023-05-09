package com.voocoo.pet.base.recycleview_base;

import android.view.View;

/**
 * @author sswukang on 2016/2/15 14:55
 * @version 1.0
 *          RecyclerView Item 添加监听接口
 */
interface RecyclerAdapterListener {
    void onItemClick(View v, int position, int viewType);

    boolean onItemLongClick(View v, int position, int viewType);
}

