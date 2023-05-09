package com.voocoo.pet.modules.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.VideoDecoder;
import com.bumptech.glide.request.RequestOptions;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.umeng.commonsdk.debug.I;
import com.voocoo.pet.R;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.entity.Baike;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.ViewHolder> {

    public static final String TAG = "HomeListAdapter";
    Context context;
    private List<Baike> BaikeList = new ArrayList<>();

    public void setData(List<Baike> BaikeList) {
        this.BaikeList = BaikeList;
        notifyDataSetChanged();
    }

    public HomeListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_baike, parent,
                false);
        ViewHolder itemViewHolder = new ViewHolder(view);
        return itemViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ViewHolder itemViewHolder = ((ViewHolder) holder);
        holder.tvTitle.setText(BaikeList.get(position).title);
        //视频
        String url = BaikeList.get(position).url;
        LogUtil.d(url);
        RequestOptions requestOptions=new RequestOptions();
        requestOptions.frame(1);
        requestOptions.set(VideoDecoder.FRAME_OPTION, MediaMetadataRetriever.OPTION_CLOSEST);
        requestOptions.transform(new BitmapTransformation() {
            @Override
            protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
                return toTransform;
            }

            @Override
            public void updateDiskCacheKey(MessageDigest messageDigest) {
                try {
                    messageDigest.update((context.getPackageName() + "RotateTransform").getBytes("utf-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .into(itemViewHolder.imageView);

        itemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickListener!=null)
                    clickListener.onClick(BaikeList.get(position).title,url);
            }
        });
       /* holder.gsyVideoPlayer.setVisibility(View.VISIBLE);
        holder.gsyVideoPlayer.setUpLazy(url, true, null, null, "");
        //增加title
        holder.gsyVideoPlayer.getTitleTextView().setVisibility(View.GONE);
        //设置返回键
        holder.gsyVideoPlayer.getBackButton().setVisibility(View.GONE);
        //设置全屏按键功能
        holder.gsyVideoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.gsyVideoPlayer.startWindowFullscreen(context, false, true);
            }
        });
        //防止错位设置
        holder.gsyVideoPlayer.setPlayTag(TAG);
        holder.gsyVideoPlayer.setLockLand(true);
        holder.gsyVideoPlayer.setPlayPosition(position);
        //是否根据视频尺寸，自动选择竖屏全屏或者横屏全屏，这个标志为和 setLockLand 冲突，需要和 orientationUtils 使用
        holder.gsyVideoPlayer.setAutoFullWithSize(false);
        //音频焦点冲突时是否释放
        holder.gsyVideoPlayer.setReleaseWhenLossAudio(false);
        //全屏动画
        holder.gsyVideoPlayer.setShowFullAnimation(true);
        //小屏时不触摸滑动
        holder.gsyVideoPlayer.setIsTouchWiget(false);
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        Glide.with(context)
                .setDefaultRequestOptions(
                        new RequestOptions()
                                .frame(0)
                                .centerCrop()
                )
                .load(url)
                .into(imageView);
        holder.gsyVideoPlayer.clearThumbImageView();
        holder.gsyVideoPlayer.setThumbImageView(imageView);*/
    }

    /**
     * 获取网络视频第一帧
     *
     * @param videoUrl
     * @return
     */
    public void loadFirst(String videoUrl, @NonNull ImageView cover) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据url获取缩略图
            retriever.setDataSource(videoUrl, new HashMap());
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
        } finally {
            retriever.release();
        }
        if (bitmap != null) {
            cover.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return BaikeList.size();
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        @BindView(R.id.iv_img)
        ImageView imageView;
        @BindView(R.id.tv_title)
        TextView tvTitle;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }
    ClickListener clickListener;

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener{
        void onClick(String title,String url);
    }
}

