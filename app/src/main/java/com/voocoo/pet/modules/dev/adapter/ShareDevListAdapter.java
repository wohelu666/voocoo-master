package com.voocoo.pet.modules.dev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.voocoo.pet.R;
import com.voocoo.pet.entity.Device;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareDevListAdapter extends RecyclerView.Adapter<ShareDevListAdapter.ViewHolder> {

    Context context;
    private List<Device> scanList = new ArrayList<>();
    private ShareListener cancelShareListener;

    public void setData(List<Device> scanList) {
        this.scanList = scanList;
        notifyDataSetChanged();
    }

    public ShareDevListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_share, parent,
                false);
        ViewHolder itemViewHolder = new ViewHolder(view);
        return itemViewHolder;
    }

    public void setCancelShareListener(ShareListener cancelShareListener) {
        this.cancelShareListener = cancelShareListener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ViewHolder itemViewHolder = ((ViewHolder) holder);
        itemViewHolder.tvName.setText(scanList.get(position).getDeviceName());
        itemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cancelShareListener != null) {
                    cancelShareListener.onShare(scanList.get(position));
                }
            }
        });
        holder.tvName2.setText(scanList.get(position).getDevShareList().size() == 0 ? "未共享" : "共享至" + scanList.get(position).getDevShareList().get(0).shareUserNickName);
        if (scanList.get(position).getDeviceType().equals("0")) {
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

    public interface ShareListener {
        void onShare(Device shareBean);
    }
}

