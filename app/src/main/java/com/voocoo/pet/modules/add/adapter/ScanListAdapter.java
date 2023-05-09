package com.voocoo.pet.modules.add.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.voocoo.pet.R;
import com.voocoo.pet.ble.data.BleDevice;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanListAdapter extends RecyclerView.Adapter<ScanListAdapter.ViewHolder> {

    Context context;
    private List<BleDevice> scanList = new ArrayList<>();
    private CancelShareListener cancelShareListener;

    public void setData(List<BleDevice> scanList) {
        this.scanList = scanList;
        notifyDataSetChanged();
    }

    public ScanListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_scan, parent,
                false);
        ViewHolder itemViewHolder = new ViewHolder(view);
        return itemViewHolder;
    }

    public void setCancelShareListener(CancelShareListener cancelShareListener) {
        this.cancelShareListener = cancelShareListener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ViewHolder itemViewHolder = ((ViewHolder) holder);
        itemViewHolder.tvName2.setText(scanList.get(position).getName().equals("waterble") ? "饮水机" : "自动喂食器");
        itemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cancelShareListener != null) {
                    cancelShareListener.onCancel(scanList.get(position));
                }
            }
        });
        if (scanList.get(position).getName().equals("waterble")) {
            itemViewHolder.ivDev.setImageResource(R.mipmap.dev_water);
        } else {
            itemViewHolder.ivDev.setImageResource(R.mipmap.dev_feed);
        }
    }

    @Override
    public int getItemCount() {
        return scanList.size();
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_name2)
        TextView tvName2;
        @BindView(R.id.iv_dev)
        ImageView ivDev;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }

    public interface CancelShareListener {
        void onCancel(BleDevice shareBean);
    }
}

