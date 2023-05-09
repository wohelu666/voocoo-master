package com.voocoo.pet.modules.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.voocoo.pet.R;

import java.util.ArrayList;
import java.util.List;

public class PushPhotoGridAdapter extends BaseAdapter {

    List<String> imageUrls = new ArrayList<>();

    private LayoutInflater mInflater;

    private Context mContext;

    public PushPhotoGridAdapter(Context context, List<String> imageUrls) {
        this.imageUrls = imageUrls;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<String> data) {
        this.imageUrls = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewTag viewTag;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_failure_declare_post_photo, null);

            // construct an item tag
            viewTag = new ItemViewTag(convertView.findViewById(R.id.bgView),
                    (ImageView) convertView.findViewById(R.id.iv_select_photo),
                    (ImageView) convertView.findViewById(R.id.iv_delete),
                    (ImageView) convertView.findViewById(R.id.tv_add_photo));
            convertView.setTag(viewTag);
        } else {
            viewTag = (ItemViewTag) convertView.getTag();
        }

        if (imageUrls.get(position).equals("")) {
            viewTag.mAddPhoto.setVisibility(View.VISIBLE);
            viewTag.mPhoto.setVisibility(View.INVISIBLE);
            viewTag.mDelete.setVisibility(View.INVISIBLE);
        } else {
            viewTag.mAddPhoto.setVisibility(View.INVISIBLE);
            viewTag.mPhoto.setVisibility(View.VISIBLE);
            viewTag.mDelete.setVisibility(View.VISIBLE);
            //设置图片
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .priority(Priority.HIGH)
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
            Glide.with(mContext).load(imageUrls.get(position)).apply(options).into(viewTag.mPhoto);
        }

        return convertView;
    }

    class ItemViewTag {
        protected View mbgView;
        protected ImageView mPhoto;
        protected ImageView mAddPhoto;
        protected ImageView mDelete;

        /**
         * The constructor to construct a navigation view tag
         */
        public ItemViewTag(View mbgView, ImageView mPhoto, ImageView mDelete, ImageView mAddPhoto) {
            this.mbgView = mbgView;
            this.mPhoto = mPhoto;
            this.mAddPhoto = mAddPhoto;
            this.mDelete = mDelete;
        }
    }
}
