package com.voocoo.pet.modules.main.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.voocoo.pet.R;
import com.voocoo.pet.entity.Pet;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PetListAdapter extends RecyclerView.Adapter<PetListAdapter.ViewHolder> {

    Context context;
    private List<Pet> deviceList = new ArrayList<>();

    public void setData(List<Pet> DeviceList) {
        this.deviceList = DeviceList;
        notifyDataSetChanged();
    }

    public PetListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dev_banner, parent,
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
        void onClick(Pet device);
    }

    ClickDevListener clickDevListener;
}

