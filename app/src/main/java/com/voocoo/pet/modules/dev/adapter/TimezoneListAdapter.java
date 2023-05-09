package com.voocoo.pet.modules.dev.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.voocoo.pet.R;
import com.voocoo.pet.entity.Record;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimezoneListAdapter extends RecyclerView.Adapter<TimezoneListAdapter.ViewHolder> {

    Context context;
    private List<TimeZone> timeZoneList = new ArrayList<>();
    private ClickItemListener clickItemListener;

    public void setClickItemListener(ClickItemListener clickItemListener) {
        this.clickItemListener = clickItemListener;
    }

    public void setData(List<TimeZone> timeZoneList) {
        this.timeZoneList = timeZoneList;
        notifyDataSetChanged();
    }

    public TimezoneListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_timezone, parent,
                false);
        ViewHolder itemViewHolder = new ViewHolder(view);
        return itemViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ViewHolder itemViewHolder = ((ViewHolder) holder);
        itemViewHolder.tvName.setText(timeZoneList.get(position).getDisplayName());
        itemViewHolder.tvTimezone.setText(displayTimeZone(timeZoneList.get(position)));

        itemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickItemListener != null)
                    clickItemListener.onClick(timeZoneList.get(position));
            }
        });
        //String eWeight = recordList.get(position).getExcrementWeight();
        // itemViewHolder.tvContent.setText(context.getString(R.string.text_record_content, recordList.get(position).getShitTime(), df.format(weight),eWeight));
    }

    private static String displayTimeZone(TimeZone tz) {

        long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset())
                - TimeUnit.HOURS.toMinutes(hours);
        // avoid -4:-30 issue
        minutes = Math.abs(minutes);

        String result = "";
        if (hours > 0) {
            result = String.format("(GMT+%d:%02d)", hours, minutes);
        } else {
            result = String.format("(GMT%d:%02d)", hours, minutes);
        }

        return result;

    }

    @Override
    public int getItemCount() {
        return timeZoneList.size();
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_timezone)
        TextView tvTimezone;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }

    public interface ClickItemListener {
        void onClick(TimeZone timeZone);
    }
}

