package com.voocoo.pet.modules.mine;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.voocoo.pet.common.widgets.RoundImageView;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.News;
import com.voocoo.pet.entity.UserInfo;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.mine.adapter.MyNewsListAdapter;
import com.scu.miomin.shswiperefresh.core.SHSwipeRefreshLayout;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 我的动态
 */
public class MyNewsActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.iv_top)
    View ivTop;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.iv_head)
    RoundImageView ivHead;
    @BindView(R.id.tv_nickname)
    TextView tvNickName;
    @BindView(R.id.tv_fans_num)
    TextView tvFansNum;

    @BindView(R.id.swipeRefreshLayout)
    SHSwipeRefreshLayout swipeRefreshLayout;

    private List<News.Record> recordList = new ArrayList<>();
    private int curPage = 1;
    MyNewsListAdapter newsListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_white_ic);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText("");

        initView();
        initData();
    }

    private void getUserInfoFromCloud() {
        showLoading();
        HttpManage.getInstance().getMyInfo(new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                dismissLoading();
                LogUtil.d("getMyInfo onError");
            }

            @Override
            public void onSuccess(int code, String response) {
                LogUtil.d("getMyInfo->" + response);
                BaseEntity<UserInfo> result = new Gson().fromJson(response, new TypeToken<BaseEntity<UserInfo>>() {
                }.getType());
                if (result.getCode() == 200) {
                    tvNickName.setText(result.getData().getNickName());
                    tvFansNum.setText(getString(R.string.text_frident) + result.getData().getFansCount() + "  " + getString(R.string.text_follow2) + result.getData().getFollowCount() + " " + getString(R.string.text_news) + result.getData().getMomentCount());
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .priority(Priority.HIGH)
                            .placeholder(R.mipmap.ic_launcher)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                    Glide.with(MyNewsActivity.this).load(result.getData().getAvatarUrl()).apply(options).into(ivHead);

                    getNewsFromCloud(curPage);
                } else {
                    dismissLoading();
                }
            }
        });
    }

    private void getNewsFromCloud(int page) {
        HttpManage.getInstance().getNewsList(page, 10, new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                dismissLoading();
                LogUtil.d("getNewsList onError");
                swipeRefreshLayout.finishRefresh();
                swipeRefreshLayout.finishLoadmore();
            }

            @Override
            public void onSuccess(int code, String response) {
                dismissLoading();
                LogUtil.d("getNewsList->" + response);
                BaseEntity<News> result = new Gson().fromJson(response, new TypeToken<BaseEntity<News>>() {
                }.getType());
                if (result.getCode() == 200) {
                    swipeRefreshLayout.finishRefresh();
                    swipeRefreshLayout.finishLoadmore();
                    if (result.getData().getRecords().size() == 0) {
                        //showToast("没有更多了");
                        //没有更多了
                        curPage--;
                    } else {
                        if (curPage == 1) {
                            recordList.clear();
                        }
                        recordList.addAll(result.getData().getRecords());
                        newsListAdapter.setData(recordList);
                    }
                }
            }
        });
    }

    private void initData() {
        getUserInfoFromCloud();
    }

    private void initView() {
        newsListAdapter = new MyNewsListAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(newsListAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SHSwipeRefreshLayout.SHSOnRefreshListener() {
            @Override
            public void onRefresh() {
                curPage = 1;
                getNewsFromCloud(curPage);
            }

            @Override
            public void onLoading() {
                curPage++;
                getNewsFromCloud(curPage);
            }

            /**
             * 监听下拉刷新过程中的状态改变
             *
             * @param percent 当前下拉距离的百分比（0-1）
             * @param state   分三种状态{NOT_OVER_TRIGGER_POINT：还未到触发下拉刷新的距离；OVER_TRIGGER_POINT：已经到触发下拉刷新的距离；START：正在下拉刷新}
             */
            @Override
            public void onRefreshPulStateChange(float percent, int state) {
                switch (state) {
                    case SHSwipeRefreshLayout.NOT_OVER_TRIGGER_POINT:
                        swipeRefreshLayout.setLoaderViewText("下拉刷新");
                        break;
                    case SHSwipeRefreshLayout.OVER_TRIGGER_POINT:
                        swipeRefreshLayout.setLoaderViewText("松开刷新");
                        break;
                    case SHSwipeRefreshLayout.START:
                        swipeRefreshLayout.setLoaderViewText("正在刷新");
                        break;
                }
            }

            @Override
            public void onLoadmorePullStateChange(float percent, int state) {
                switch (state) {
                    case SHSwipeRefreshLayout.NOT_OVER_TRIGGER_POINT:
                        LogUtil.d("上拉加载");
                        break;
                    case SHSwipeRefreshLayout.OVER_TRIGGER_POINT:
                        LogUtil.d("松开加载");
                        break;
                    case SHSwipeRefreshLayout.START:
                        LogUtil.d("正在加载...");
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.more, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.more:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_news;
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
