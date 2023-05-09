package com.voocoo.pet.modules.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.voocoo.pet.R;
import com.voocoo.pet.common.widgets.RoundImageView;
import com.voocoo.pet.entity.Comment;
import com.voocoo.pet.entity.News;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyNewsListAdapter extends RecyclerView.Adapter<MyNewsListAdapter.ViewHolder> {

    Context context;
    private List<News.Record> newList = new ArrayList<>();

    public void setData(List<News.Record> newList) {
        this.newList = newList;
        notifyDataSetChanged();
    }

    public MyNewsListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_news, parent,
                false);
        ViewHolder itemViewHolder = new ViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewHolder itemViewHolder = ((ViewHolder) holder);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//年-月-日 时-分
        try {
            Date date = dateFormat.parse(newList.get(position).getCtime());//开始时间
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            itemViewHolder.tvDay.setText(cal.get(Calendar.DAY_OF_MONTH) + "");
            itemViewHolder.tvMonth.setText(cal.get(Calendar.MONTH) + 1 + "月");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        itemViewHolder.tvTitle.setText(newList.get(position).getMoment());
        if (newList.get(position).getMomentType() == 0) {
            //图片
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
            Glide.with(context).load(newList.get(position).getSourceUrl().split(",")[0]).apply(options).into(itemViewHolder.ivImg);
        } else {
            //视频
        }
    }

    @Override
    public int getItemCount() {
        return newList.size();
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        @BindView(R.id.tv_day)
        TextView tvDay;
        @BindView(R.id.tv_month)
        TextView tvMonth;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.iv_img)
        RoundImageView ivImg;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }
}

