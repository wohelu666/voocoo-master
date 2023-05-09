package com.voocoo.pet.modules.dev.adapter;

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
import com.voocoo.pet.entity.DevShare;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareDevManagerListAdapter extends RecyclerView.Adapter<ShareDevManagerListAdapter.ViewHolder> {

    Context context;
    private List<DevShare> scanList = new ArrayList<>();
    private ShareListener cancelShareListener;

    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");

    public void setData(List<DevShare> scanList) {
        this.scanList = scanList;
        notifyDataSetChanged();
    }

    public ShareDevManagerListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_share_manager, parent,
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
        itemViewHolder.tvName.setText(scanList.get(position).shareUserNickName);
        itemViewHolder.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cancelShareListener != null) {
                    cancelShareListener.onShare(scanList.get(position));
                }
            }
        });
        try {
            Date date = format.parse(scanList.get(position).createTime);
            itemViewHolder.tvName2.setText(format2.format(date)+"已接收邀请");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .priority(Priority.HIGH)
                .placeholder(R.mipmap.app_icon)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        Glide.with(context).load(scanList.get(position).shareUserPhoto).apply(options).into(itemViewHolder.ivDev);
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
        @BindView(R.id.tv_cancel)
        TextView tvCancel;
        @BindView(R.id.iv_dev)
        RoundImageView ivDev;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }

    public interface ShareListener {
        void onShare(DevShare shareBean);
    }
}

