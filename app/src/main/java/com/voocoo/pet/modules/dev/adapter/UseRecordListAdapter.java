package com.voocoo.pet.modules.dev.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.voocoo.pet.R;
import com.voocoo.pet.entity.Record;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UseRecordListAdapter extends RecyclerView.Adapter<UseRecordListAdapter.ViewHolder> {

    Context context;
    private List<Record> recordList = new ArrayList<>();
    private int timezone = 8;

    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final DateFormat format2 = new SimpleDateFormat("HH:mm");

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public void setData(List<Record> RecordList) {
        this.recordList = RecordList;
        notifyDataSetChanged();
    }

    public UseRecordListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_use_record, parent,
                false);
        ViewHolder itemViewHolder = new ViewHolder(view);
        return itemViewHolder;
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 != null && cal2 != null) {
            return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                    && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        } else {
            return false;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ViewHolder itemViewHolder = ((ViewHolder) holder);
        try {
            Date date = format.parse(recordList.get(position).createTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            if (isSameDay(Calendar.getInstance(), calendar)) {
                itemViewHolder.tvTime.setText(format2.format(date));
            } else {
                itemViewHolder.tvTime.setText(recordList.get(position).createTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        itemViewHolder.tvContent.setText(recordList.get(position).content);
        if (position == 0) {
            itemViewHolder.line1.setVisibility(View.INVISIBLE);
            itemViewHolder.rlRecord1.setVisibility(View.VISIBLE);
            itemViewHolder.rlRecord2.setVisibility(View.GONE);
            itemViewHolder.tvTime.setTextColor(Color.parseColor("#282A30"));
            itemViewHolder.tvContent.setTextColor(Color.parseColor("#282A30"));
        } else {
            itemViewHolder.line1.setVisibility(View.VISIBLE);
            itemViewHolder.rlRecord1.setVisibility(View.GONE);
            itemViewHolder.rlRecord2.setVisibility(View.VISIBLE);
            itemViewHolder.tvTime.setTextColor(Color.parseColor("#9FA6BD"));
            itemViewHolder.tvContent.setTextColor(Color.parseColor("#9FA6BD"));
        }
    }

    ClickListener clickListener;

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onClick();
    }

    //时区转换 东8区转换为对应时区做显示
    private String transformationDeviceTimeZone(Date date) {
        //设备当前设置的时区

        String result = "";
        if (timezone > 0) {
            result = String.format("GMT+%d:%02d", timezone, 0);
        } else {
            result = String.format("GMT%d:%02d", timezone, 0);
        }

        Calendar east8 = Calendar.getInstance();
        TimeZone tzEast8 = TimeZone.getTimeZone("GMT+08:00");
        east8.setTimeZone(tzEast8);
        east8.setTime(date);

        TimeZone devTimezone = TimeZone.getTimeZone(result);
        DateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dft.setTimeZone(devTimezone);
        return dft.format(east8.getTime());
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.rl_record1)
        View rlRecord1;
        @BindView(R.id.rl_record2)
        View rlRecord2;
        @BindView(R.id.line1)
        View line1;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }
}

