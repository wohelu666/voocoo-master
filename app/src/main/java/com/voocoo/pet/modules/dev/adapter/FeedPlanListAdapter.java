package com.voocoo.pet.modules.dev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.voocoo.pet.R;
import com.voocoo.pet.entity.FeedPlan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedPlanListAdapter extends RecyclerView.Adapter<FeedPlanListAdapter.ViewHolder> {

    Context context;
    private List<FeedPlan.FeedPlanDiets> recordList = new ArrayList<>();

    public void setData(List<FeedPlan.FeedPlanDiets> RecordList) {
        this.recordList = RecordList;
        notifyDataSetChanged();
    }

    public FeedPlanListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_feed_plan, parent,
                false);
        ViewHolder itemViewHolder = new ViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewHolder itemViewHolder = ((ViewHolder) holder);
        itemViewHolder.tvTime.setText(recordList.get(position).dietTime);
        int hour=Integer.valueOf(recordList.get(position).dietTime.split(":")[0]);
        if (hour >= 5 && hour < 10) {
            itemViewHolder.tvName.setText(Arrays.asList(context.getResources().getStringArray(R.array.select_label_array)).get(0));
        } else if (hour >= 10 && hour < 15) {
            itemViewHolder.tvName.setText(Arrays.asList(context.getResources().getStringArray(R.array.select_label_array)).get(1));
        } else if (hour >= 15 && hour < 21) {
            itemViewHolder.tvName.setText(Arrays.asList(context.getResources().getStringArray(R.array.select_label_array)).get(2));
        } else {
            itemViewHolder.tvName.setText(Arrays.asList(context.getResources().getStringArray(R.array.select_label_array)).get(3));
        }
        itemViewHolder.tvNum.setText(recordList.get(position).dietAmount+"g");
        itemViewHolder.tvNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dietClickListener != null) {
                    dietClickListener.onClick(position,recordList.get(position));
                }
            }
        });
    }

    DietClickListener dietClickListener;

    public void setDietClickListener(DietClickListener dietClickListener) {
        this.dietClickListener = dietClickListener;
    }

    public interface DietClickListener {
        void onClick(int index,FeedPlan.FeedPlanDiets diets);
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_num)
        TextView tvNum;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }
}

