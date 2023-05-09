package com.voocoo.pet.modules.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.voocoo.pet.R;
import com.voocoo.pet.common.widgets.RoundImageView;
import com.voocoo.pet.entity.MomentDto;

import java.util.List;

public class HomeDetileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_HEADER = 0;  //说明是带有Header的
    public static final int TYPE_FOOTER = 1;  //说明是带有Footer的
    public static final int TYPE_NORMAL = 2;  //说明是不带有header和footer的

    private List<MomentDto.MomentCommentDto.MomentCommentRecords> mDatas;
    //HeaderView, FooterView
    private View mHeaderView;
    private View mFooterView;

    private Context context;

    public HomeDetileAdapter(Context mContext, List<MomentDto.MomentCommentDto.MomentCommentRecords> list) {
        this.mDatas = list;
        this.context = mContext;
    }

    //HeaderView和FooterView的get和set函数
    public View getHeaderView() {
        return mHeaderView;
    }

    public void setData(List<MomentDto.MomentCommentDto.MomentCommentRecords> mDatas) {
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getFooterView() {
        return mFooterView;
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
        notifyItemInserted(getItemCount() - 1);
    }

    /**
     * 重写这个方法，很重要，是加入Header和Footer的关键，我们通过判断item的类型，从而绑定不同的view    *
     */
    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null && mFooterView == null) {
            return TYPE_NORMAL;
        }
        if (position == 0) {
            //第一个item应该加载Header
            return TYPE_HEADER;
        }
        if (position == getItemCount() - 1) {
            //最后一个,应该加载Footer
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    //创建View，如果是HeaderView或者是FooterView，直接在Holder中返回
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new ListHolder(mHeaderView);
        }
        if (mFooterView != null && viewType == TYPE_FOOTER) {
            return new ListHolder(mFooterView);
        }
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_detile_comment, parent, false);
        return new ListHolder(layout);
    }

    //绑定View，这里是根据返回的这个position的类型，从而进行绑定的，   HeaderView和FooterView, 就不同绑定了
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            if (holder instanceof ListHolder) {
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .priority(Priority.HIGH)
                        .placeholder(R.mipmap.ic_launcher_round)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                Glide.with(context).load(mDatas.get(position - 1).getAvatarUrl()).apply(options).into(((ListHolder) holder).ivHead);

                ((ListHolder) holder).tvNickName.setText(mDatas.get(position - 1).getNickName());
                ((ListHolder) holder).tvComment.setText(mDatas.get(position - 1).getComment());
                ((ListHolder) holder).tvTime.setText("");
                ((ListHolder) holder).tvLikeNum.setText(mDatas.get(position - 1).getPriseNum() + "");
                return;
            }
            return;
        } else if (getItemViewType(position) == TYPE_HEADER) {
            return;
        } else {
            if (holder instanceof ListHolder) {
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .priority(Priority.HIGH)
                        .placeholder(R.mipmap.ic_launcher_round)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                Glide.with(context).load(mDatas.get(position - 1).getAvatarUrl()).apply(options).into(((ListHolder) holder).ivHead);

                ((ListHolder) holder).tvNickName.setText(mDatas.get(position - 1).getNickName());
                ((ListHolder) holder).tvComment.setText(mDatas.get(position - 1).getComment());
                ((ListHolder) holder).tvTime.setText("");
                ((ListHolder) holder).tvLikeNum.setText(mDatas.get(position - 1).getPriseNum() + "");
                return;
            }
            return;
        }
    }

    //在这里面加载ListView中的每个item的布局
    class ListHolder extends RecyclerView.ViewHolder {
        RoundImageView ivHead;
        TextView tvNickName;
        TextView tvComment;
        TextView tvTime;
        TextView tvLikeNum;

        public ListHolder(View itemView) {
            super(itemView);
            //如果是headerview或者是footerview,直接返回
            if (itemView == mHeaderView) {
                return;
            }
            if (itemView == mFooterView) {
                return;
            }
            ivHead = (RoundImageView) itemView.findViewById(R.id.iv_head);
            tvNickName = (TextView) itemView.findViewById(R.id.tv_nickname);
            tvComment = (TextView) itemView.findViewById(R.id.tv_comment);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvLikeNum = (TextView) itemView.findViewById(R.id.tv_like_num);
        }

    }

    //返回View中Item的个数，这个时候，总的个数应该是ListView中Item的个数加上HeaderView和FooterView
    @Override
    public int getItemCount() {
        if (mHeaderView == null && mFooterView == null) {
            return mDatas.size();
        } else if (mHeaderView == null && mFooterView != null) {
            return mDatas.size() + 1;
        } else if (mHeaderView != null && mFooterView == null) {
            return mDatas.size() + 1;
        } else {
            return mDatas.size() + 2;
        }
    }
}