package com.voocoo.pet.modules.main.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.voocoo.pet.R;
import com.voocoo.pet.common.constant.Constant;
import com.voocoo.pet.entity.Device;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DevListAdapter extends RecyclerView.Adapter<DevListAdapter.ViewHolder> {

    Context context;
    private List<Device> deviceList = new ArrayList<>();
    private boolean isRecommond;

    public void setRecommond(boolean recommond) {
        isRecommond = recommond;
    }

    public void setData(List<Device> DeviceList) {
        this.deviceList = DeviceList;
        notifyDataSetChanged();
    }

    public DevListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dev_list, parent,
                false);
        ViewHolder itemViewHolder = new ViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        ViewHolder itemViewHolder = ((ViewHolder) holder);

    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.mView)
        public View mView;
        @BindView(R.id.iv_dev)
        public ImageView ivDev;
        @BindView(R.id.tv_tag)
        public TextView tvTag;
        @BindView(R.id.tv_tips)
        public TextView tvTips;
        @BindView(R.id.iv_online)
        ImageView ivOnline;
        @BindView(R.id.iv_warning)
        ImageView ivWaring;
        @BindView(R.id.tv_name)
        TextView tvName;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }

    public void setClickDevListener(ClickDevListener clickDevListener) {
        this.clickDevListener = clickDevListener;
    }

    public interface ClickDevListener {
        void onClick(Device device);
    }

    ClickDevListener clickDevListener;
}

