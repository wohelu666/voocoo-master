package com.voocoo.pet.modules.main.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.voocoo.pet.R;
import com.voocoo.pet.entity.WaitToDoList;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder> {

    public static final String TAG = "TodoListAdapter";
    Context context;
    private List<WaitToDoList.WaitToDo> dataList = new ArrayList<>();

    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final DateFormat format2 = new SimpleDateFormat("HH:mm");

    public void setData(List<WaitToDoList.WaitToDo> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public List<WaitToDoList.WaitToDo> getDataList() {
        return dataList;
    }

    public TodoListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wait_todo, parent,
                false);
        ViewHolder itemViewHolder = new ViewHolder(view);
        return itemViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ViewHolder itemViewHolder = ((ViewHolder) holder);
        itemViewHolder.tvContent.setText(dataList.get(position).content);

        try {
            Date date = format.parse(dataList.get(position).time);
            itemViewHolder.tvTime.setText(format2.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        itemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickToDetileListener != null)
                    clickToDetileListener.onClick(dataList.get(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.tv_time)
        TextView tvTime;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }

    public void setClickToDetileListener(ClickToDetileListener clickToDetileListener) {
        this.clickToDetileListener = clickToDetileListener;
    }

    ClickToDetileListener clickToDetileListener;

    public interface ClickToDetileListener {
        void onClick(WaitToDoList.WaitToDo momentDto);
    }
}

