package com.voocoo.pet.modules.dev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.voocoo.pet.R;
import com.voocoo.pet.common.manager.PetsManager;
import com.voocoo.pet.common.utils.ToastUtil;
import com.voocoo.pet.common.widgets.RoundImageView;
import com.voocoo.pet.entity.FeedPlan;
import com.voocoo.pet.entity.Pet;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PetListAdapter extends RecyclerView.Adapter<PetListAdapter.ViewHolder> {

    Context context;
    private List<Pet> recordList = new ArrayList<>();
    private List<Integer> petIds = new ArrayList<>();

    public List<Integer> getPetIds() {
        return petIds;
    }

    public int pos = -1;
    public void setPetIds(List<Integer> petIds) {
        this.petIds = petIds;
        notifyDataSetChanged();
    }
    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }


    public void setData(List<Pet> RecordList) {
        if (RecordList.size() == 0){
            return;
        }
        this.recordList = RecordList;
        notifyDataSetChanged();
    }

    public PetListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pet_list, parent,
                false);
        ViewHolder itemViewHolder = new ViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewHolder itemViewHolder = ((ViewHolder) holder);
        itemViewHolder.tvName.setText(recordList.get(position).petNickname);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .priority(Priority.HIGH)
                .placeholder(R.mipmap.app_icon)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        Glide.with(context).load(recordList.get(position).petImg).apply(options).into(itemViewHolder.ivHead);
        if (petIds.contains(recordList.get(position).petId)) {
            itemViewHolder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            itemViewHolder.ivCheck.setVisibility(View.INVISIBLE);
        }
        itemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (petIds.contains(recordList.get(position).petId)) {
                    petIds.remove((Integer)recordList.get(position).petId);
                    notifyDataSetChanged();
                } else {
                    //0猫 1狗
                    if (petIds.size() > 0) {
                        int type = 0;
                        if (PetsManager.getInstance().getFromId(petIds.get(0)) == null) {
                            return;
                        }
                        if (PetsManager.getInstance().getFromId(petIds.get(0)).petType == 0) {
                            type = 0;
                            //猫
                            if (recordList.get(position).petType == 1) {
                                //狗
                                ToastUtil.getInstance().longToast(context, "猫和狗无法共用哦，请选择同一物种。");
                                return;
                            }
                        } else {
                            type = 1;
                            //狗
                            if (recordList.get(position).petType == 0) {
                                //猫
                                ToastUtil.getInstance().longToast(context, "猫和狗无法共用哦，请选择同一物种。");
                                return;
                            }
                        }
                        float allWeight = 0;
                        for (int i = 0; i < petIds.size(); i++) {
                            if (PetsManager.getInstance().getFromId(petIds.get(i)) != null)
                                allWeight += PetsManager.getInstance().getFromId(petIds.get(i)).petWeight;
                        }
                        if (type == 0) {
                            if (allWeight >= 31) {
                                ToastUtil.getInstance().longToast(context, "所选宠物已达上限，可以多购置一台设备给毛孩子使用哦～");
                                return;
                            }
                        } else {
                            if (allWeight >= 24) {
                                ToastUtil.getInstance().longToast(context, "所选宠物已达上限，可以多购置一台设备给毛孩子使用哦～");
                                return;
                            }
                        }
                        petIds.add(recordList.get(position).petId);
                        notifyDataSetChanged();
                    } else {
                        petIds.add(recordList.get(position).petId);
                        notifyDataSetChanged();
                    }
                }
            }
        });
    }

    DietClickListener dietClickListener;

    public void setDietClickListener(DietClickListener dietClickListener) {
        this.dietClickListener = dietClickListener;
    }

    public interface DietClickListener {
        void onClick(Pet diets);
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.iv_head)
        RoundImageView ivHead;
        @BindView(R.id.iv_check)
        ImageView ivCheck;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }
}

