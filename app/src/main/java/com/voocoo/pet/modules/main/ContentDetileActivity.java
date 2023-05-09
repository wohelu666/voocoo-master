package com.voocoo.pet.modules.main;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Comment;
import com.voocoo.pet.http.HttpManage;

import org.apache.http.Header;

import butterknife.BindView;

public class ContentDetileActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.imageview)
    ImageView imageView;

    @BindView(R.id.tv_title)
    TextView tvContentTitle;

    @BindView(R.id.tv_time)
    TextView tvTime;

    @BindView(R.id.tv_content)
    TextView tvContent;
  /*

    @BindView(R.id.tv_like)
    TextView tvLike;

    @BindView(R.id.tv_star)
    TextView tvStar;*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String id = getIntent().getStringExtra("id");
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_black_ic);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText(getString(R.string.text_content_detile));
        initData(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.share:

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initData(String id) {
        HttpManage.getInstance().getComentDetile(id, new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                dismissLoading();
            }

            @Override
            public void onSuccess(int code, String response) {
                LogUtil.d(response);
                dismissLoading();
                BaseEntity<Comment.Record> result = new Gson().fromJson(response, new TypeToken<BaseEntity<Comment.Record>>() {
                }.getType());
                if (result.getCode() == 200) {
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.mipmap.ic_launcher)
                            .priority(Priority.HIGH)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                    Glide.with(ContentDetileActivity.this).load(result.getData().getImgUrl().split(",")[0]).apply(options).into(imageView);

                    tvContentTitle.setText(result.getData().getTitle());
                    tvContent.setText(result.getData().getContent());
                    tvTime.setText(result.getData().getPublicTime());
                    //tvLike.setText(result.getData().getCommentNum() + "");
                    //tvStar.setText(result.getData().getFavNum() + "");
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_content_detile;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @Nullable
    @Override
    protected BaseActivityPresenter createPresenter() {
        return null;
    }
}
