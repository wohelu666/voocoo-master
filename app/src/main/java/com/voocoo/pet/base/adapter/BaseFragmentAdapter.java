package com.voocoo.pet.base.adapter;



import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.voocoo.pet.base.fragment.AbsBaseFragment;

import java.util.ArrayList;


/**
 * FragmentPager 通用适配器
 *
 * @author sswukang on 2016/8/30 11:28
 * @version 1.0
 */
public class BaseFragmentAdapter<T extends AbsBaseFragment> extends FragmentPagerAdapter {
    private ArrayList<T> fragList; // 碎片集合
    private ArrayList<CharSequence> fragTags; // 碎片tag集合

    public BaseFragmentAdapter(FragmentManager fm, ArrayList<T> fragList) {
        super(fm);
        this.fragList = fragList;
    }

    public BaseFragmentAdapter(FragmentManager fm, ArrayList<T> fragList,
                               ArrayList<CharSequence> fragTags) {
        super(fm);
        this.fragList = fragList;
        this.fragTags = fragTags;

        if (fragList.size() != fragTags.size())
            throw new IllegalArgumentException("Fragment list size must be the same with tag list size.");
    }

    @Override
    public T getItem(int position) {
        return fragList.get(position);
    }

    @Override
    public int getCount() {
        return fragList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (fragTags == null)
            return "";
        return fragTags.get(position);
    }

}
