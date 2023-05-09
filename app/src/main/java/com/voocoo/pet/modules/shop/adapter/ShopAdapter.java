package com.voocoo.pet.modules.shop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.voocoo.pet.R;
import com.voocoo.pet.common.widgets.RoundImageView;
import com.voocoo.pet.entity.MomentDto;
import com.voocoo.pet.entity.Shop;
import com.voocoo.pet.modules.main.adapter.HomeListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    public static final String TAG = "ShopAdapter";
    Context context;
    private List<Shop> shopList = new ArrayList<>();

    public ShopAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Shop> shopList) {
        this.shopList = shopList;
        notifyDataSetChanged();
    }

    public void setClickToDetileListener(ClickToDetileListener clickToDetileListener) {
        this.clickToDetileListener = clickToDetileListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shop_list, parent,
                false);
        ViewHolder itemViewHolder = new ViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewHolder itemViewHolder = ((ViewHolder) holder);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .priority(Priority.HIGH)
                .placeholder(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        Glide.with(context).load(shopList.get(position).getImgs()).apply(options).into(itemViewHolder.ivShop);

        itemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickToDetileListener != null)
                    clickToDetileListener.onClick(shopList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @BindView(R.id.iv_shop)
        RoundImageView ivShop;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }

    ClickToDetileListener clickToDetileListener;

    public interface ClickToDetileListener {
        void onClick(Shop shop);
    }
}
