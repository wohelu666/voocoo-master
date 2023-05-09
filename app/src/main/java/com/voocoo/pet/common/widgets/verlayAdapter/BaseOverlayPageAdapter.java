package com.voocoo.pet.common.widgets.verlayAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.voocoo.pet.R;
import com.voocoo.pet.entity.Device;
import com.voocoo.pet.modules.dev.activity.FeedDevActivity;
import com.voocoo.pet.modules.dev.activity.FeedPlanActivity;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class BaseOverlayPageAdapter extends PagerAdapter {
    private Context context;
    private List<Device> deviceList;
    protected RequestOptions mRequestOptions;

    public BaseOverlayPageAdapter(Context context, @NonNull RequestOptions imageOptions) {
        this.context = context;
        this.mRequestOptions = imageOptions;
    }

    /**
     * item布局
     *
     * @return
     */
    protected abstract View itemView();

    public void setDevicesAndBindViewPager(ViewPager vp, List<Device> deviceList, int layerAmount) {
        setDevicesAndBindViewPager(vp, deviceList, layerAmount, -1, -1);
    }

    /**
     * @param vp
     * @param deviceList
     * @param layerAmount 显示层数
     */
    public void setDevicesAndBindViewPager(ViewPager vp, List<Device> deviceList, int layerAmount, float scaleOffset, float transOffset) {
        this.deviceList = deviceList;
        if (deviceList != null && deviceList.size() > 0) {
            vp.setOffscreenPageLimit(layerAmount);
            OverlayTransformer transformer = new OverlayTransformer(layerAmount, scaleOffset, transOffset);
            vp.setPageTransformer(true, transformer);
        }
    }

    @Override
    public int getCount() {
        if (null == deviceList)
            return 0;
        if (deviceList.size() <= 1)
            return deviceList.size();
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final int p = position % deviceList.size();
        View view = itemView();
        if (null == view) {
            throw new RuntimeException("you should set a item layout");
        }
        TextView tvOffline = view.findViewById(R.id.tv_offline);
        ImageView ivStatus = view.findViewById(R.id.iv_status);
        TextView tvName = view.findViewById(R.id.tv_name);
        if (deviceList.get(p).getStatus().equals("0")) {
            //在线
            tvOffline.setVisibility(View.GONE);
            ivStatus.setImageResource(R.drawable.oval_online);
            tvName.setTextColor(Color.parseColor("#000000"));
        } else {
            tvOffline.setVisibility(View.GONE);
            ivStatus.setImageResource(R.drawable.oval_offline);
            tvName.setTextColor(Color.parseColor("#9FA6BD"));
        }

        tvName.setText(deviceList.get(p).getDeviceName());

        if (deviceList.get(p).getDeviceType().equals("0")) {
            //饮水机
            ImageView ivIcon = view.findViewById(R.id.iv_icon);
            TextView tvTime = view.findViewById(R.id.tv_time);
            TextView tvTime2 = view.findViewById(R.id.tv_time2);
            TextView tvHour = view.findViewById(R.id.tv_hour);
            TextView tvHourUnit = view.findViewById(R.id.tv_hour_unit);
            TextView tvMin = view.findViewById(R.id.tv_min);
            TextView tvMinUnit = view.findViewById(R.id.tv_min_unit);
            TextView tvSecond = view.findViewById(R.id.tv_second);
            TextView tvSecondUnit = view.findViewById(R.id.tv_second_unit);
            TextView tvBottomTitle = view.findViewById(R.id.tv_bottom_title);

            ivIcon.setImageResource(R.mipmap.dev_water);

            if (deviceList.get(p).getStatus().equals("0")) {
                //在线
                tvTime.setTextColor(Color.parseColor("#000000"));
                tvTime2.setTextColor(Color.parseColor("#000000"));
                tvMin.setTextColor(Color.parseColor("#000000"));
                tvMinUnit.setTextColor(Color.parseColor("#000000"));
                tvSecond.setTextColor(Color.parseColor("#000000"));
                tvSecondUnit.setTextColor(Color.parseColor("#000000"));
                tvBottomTitle.setTextColor(Color.parseColor("#000000"));
            } else {
                tvTime.setTextColor(Color.parseColor("#9FA6BD"));
                tvTime2.setTextColor(Color.parseColor("#9FA6BD"));
                tvMin.setTextColor(Color.parseColor("#9FA6BD"));
                tvMinUnit.setTextColor(Color.parseColor("#9FA6BD"));
                tvSecond.setTextColor(Color.parseColor("#9FA6BD"));
                tvSecondUnit.setTextColor(Color.parseColor("#9FA6BD"));
                tvBottomTitle.setTextColor(Color.parseColor("#9FA6BD"));
            }

            tvBottomTitle.setText(context.getString(R.string.text_water_left));
            tvTime.setText(deviceList.get(p).getWaterMap().getDrinkingTimes() + "");
            View lyFeedCenterView = view.findViewById(R.id.ly_feed_center_view);
            View lyWaterCenterView = view.findViewById(R.id.ly_water_center_view);
            lyWaterCenterView.setVisibility(View.VISIBLE);
            lyFeedCenterView.setVisibility(View.GONE);

            View lyWater = view.findViewById(R.id.ly_water);
            View lyFeed = view.findViewById(R.id.ly_feed);
            lyWater.setVisibility(View.VISIBLE);
            lyFeed.setVisibility(View.GONE);

            View view1 = view.findViewById(R.id.view_step1);
            View view2 = view.findViewById(R.id.view_step2);
            View view3 = view.findViewById(R.id.view_step3);
            if (deviceList.get(p).getStatus().equals("0")) {
                if (deviceList.get(p).getWaterMap().getPurifiedWaterSurplus().equals("1")) {
                    view1.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.GONE);
                    view3.setVisibility(View.GONE);
                } else if (deviceList.get(p).getWaterMap().getPurifiedWaterSurplus().equals("2")) {
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.VISIBLE);
                    view3.setVisibility(View.GONE);
                } else {
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.GONE);
                    view3.setVisibility(View.VISIBLE);
                }
            } else {
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.GONE);
                view3.setVisibility(View.GONE);
            }
            String stayTime = deviceList.get(p).getWaterMap().getFormatAvgStayTime();
            if (stayTime.contains("小时")) {
                tvHour.setText(stayTime.substring(0, stayTime.indexOf("小时")));
                tvMin.setText(stayTime.substring(stayTime.indexOf("小时") + 2, stayTime.indexOf("分")));
                tvSecond.setText(stayTime.substring(stayTime.indexOf("分") + 1, stayTime.indexOf("秒")));
            } else if (stayTime.contains("分")) {
                tvMin.setText(stayTime.substring(0, stayTime.indexOf("分")));
                tvSecond.setText(stayTime.substring(stayTime.indexOf("分") + 1, stayTime.indexOf("秒")));
                tvHour.setVisibility(View.GONE);
                tvHourUnit.setVisibility(View.GONE);
            } else {
                tvSecond.setText(stayTime.substring(0, stayTime.indexOf("秒")));
                tvMin.setVisibility(View.GONE);
                tvMinUnit.setVisibility(View.GONE);
                tvHour.setVisibility(View.GONE);
                tvHourUnit.setVisibility(View.GONE);
            }
        } else if (deviceList.get(p).getDeviceType().equals("1")) {
            //喂食器
            ImageView ivIcon = view.findViewById(R.id.iv_icon);
            TextView tvTime = view.findViewById(R.id.tv_feed_time_today);
            TextView tvTime2 = view.findViewById(R.id.tv_feed_time_today2);
            TextView tvEatTime = view.findViewById(R.id.tv_feed_eat_time);
            TextView tvEatTime2 = view.findViewById(R.id.tv_feed_eat_time2);
            TextView tvFeedTime = view.findViewById(R.id.tv_feed_time);
            TextView tvFeedWeight = view.findViewById(R.id.tv_feed_weight);
            TextView tvBottomTitle = view.findViewById(R.id.tv_bottom_title);

            ivIcon.setImageResource(R.mipmap.dev_feed);
            if (deviceList.get(p).getStatus().equals("0")) {
                //在线
                tvTime.setTextColor(Color.parseColor("#000000"));
                tvTime2.setTextColor(Color.parseColor("#000000"));
                tvEatTime.setTextColor(Color.parseColor("#000000"));
                tvEatTime2.setTextColor(Color.parseColor("#000000"));
                tvFeedTime.setTextColor(Color.parseColor("#000000"));
                tvFeedWeight.setTextColor(Color.parseColor("#000000"));
                tvBottomTitle.setTextColor(Color.parseColor("#000000"));
            } else {
                tvTime.setTextColor(Color.parseColor("#9FA6BD"));
                tvTime2.setTextColor(Color.parseColor("#9FA6BD"));
                tvEatTime.setTextColor(Color.parseColor("#9FA6BD"));
                tvEatTime2.setTextColor(Color.parseColor("#9FA6BD"));
                tvFeedTime.setTextColor(Color.parseColor("#9FA6BD"));
                tvFeedWeight.setTextColor(Color.parseColor("#9FA6BD"));
                tvBottomTitle.setTextColor(Color.parseColor("#9FA6BD"));
            }


            tvBottomTitle.setText(context.getString(R.string.text_next_feed));
            View lyFeedCenterView = view.findViewById(R.id.ly_feed_center_view);
            View lyWaterCenterView = view.findViewById(R.id.ly_water_center_view);
            lyWaterCenterView.setVisibility(View.GONE);
            lyFeedCenterView.setVisibility(View.VISIBLE);

            View lyWater = view.findViewById(R.id.ly_water);
            View lyFeed = view.findViewById(R.id.ly_feed);
            lyWater.setVisibility(View.GONE);
            lyFeed.setVisibility(View.VISIBLE);

            tvTime.setText(deviceList.get(p).getFeederMap().getFeedingTimes() + "");
            tvEatTime.setText(deviceList.get(p).getFeederMap().getEatingTimes() + "");
            tvFeedTime.setText(deviceList.get(p).getFeederMap().getNextDietTime());
            if (deviceList.get(p).getFeederMap().getNextDietTime().equals("还未设置，设置出粮计划")) {
                tvFeedWeight.setVisibility(View.GONE);
                String str = "还未设置，设置出粮计划";
                SpannableString spannableString = new SpannableString(str);
                GoPlanSpan goPlanSpan = new GoPlanSpan(deviceList.get(p), str, context);
                spannableString.setSpan(goPlanSpan, str.indexOf("，") + 1, str.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                tvFeedTime.setText(spannableString);
                tvFeedTime.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                tvFeedWeight.setVisibility(View.VISIBLE);
                tvFeedWeight.setText(deviceList.get(p).getFeederMap().getNextDietAmount() + "g");
            }
        }

        view.findViewById(R.id.ly_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickDevListener != null)
                    clickDevListener.onClick(deviceList.get(p));
            }
        });

        container.addView(view);
        return view;
    }

    public class GoPlanSpan extends ClickableSpan {
        String string;
        Context context;
        Device d;

        public GoPlanSpan(Device d, String str, Context context) {
            super();
            this.string = str;
            this.context = context;
            this.d = d;
        }


        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ContextCompat.getColor(context, R.color.color_tab_select));
            //getColor(R.color.text_blue));
        }


        @Override
        public void onClick(View widget) {
            {
                Intent intent = new Intent(context, FeedPlanActivity.class);
                intent.putExtra("device", d);
                context.startActivity(intent);
            }
        }

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void setClickDevListener(ClickDevListener clickDevListener) {
        this.clickDevListener = clickDevListener;
    }

    ClickDevListener clickDevListener;

    public interface ClickDevListener {
        void onClick(Device device);
    }
}
