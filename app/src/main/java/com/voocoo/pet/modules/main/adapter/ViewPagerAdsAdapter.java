package com.voocoo.pet.modules.main.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class ViewPagerAdsAdapter extends PagerAdapter {

    List<View> images;

    public ViewPagerAdsAdapter(List<View> images) {
        this.images = images;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        if (images.get(position).getParent() != null) {
            container.removeView(images.get(position));
        }
        container.addView(images.get(position));
        return images.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(images.get(position));
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}