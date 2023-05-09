package com.voocoo.pet.modules.shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.widgets.AppDialog;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Shop;
import com.voocoo.pet.entity.ShopList;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.shop.adapter.ShopAdapter;
import com.voocoo.pet.modules.web.WebActivity;
import com.scu.miomin.shswiperefresh.core.SHSwipeRefreshLayout;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ShopListActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    ShopAdapter shopAdapter;
    @BindView(R.id.swipeRefreshLayout)
    SHSwipeRefreshLayout swipeRefreshLayout;
    private List<Shop> shopList = new ArrayList<>();
    private int curPage = 1;
    private AppDialog appDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        getShopList(curPage);
    }

    private void getShopList(int page) {
        LogUtil.d("page-》" + page);
        HttpManage.getInstance().shopList(page, 20, new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                LogUtil.d("getHomeMomentList onError");
                swipeRefreshLayout.finishRefresh();
                swipeRefreshLayout.finishLoadmore();
            }

            @Override
            public void onSuccess(int code, String response) {
                LogUtil.d(response);
                BaseEntity<ShopList> result = new Gson().fromJson(response, new TypeToken<BaseEntity<ShopList>>() {
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
                            //clear
                            shopList.clear();
                        }
                        shopList.addAll(result.getData().getRecords());
                        shopAdapter.setData(shopList);
                    }
                }
            }
        });
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        tvTitle.setTextColor(getColor(android.R.color.black));
        tvTitle.setText(getString(R.string.text_my_shop));

        shopAdapter = new ShopAdapter(ShopListActivity.this);
        shopAdapter.setClickToDetileListener(new ShopAdapter.ClickToDetileListener() {
            @Override
            public void onClick(Shop shop) {
                appDialog = AppDialog.bottomSheetList(ShopListActivity.this, getResources().getStringArray(R.array.select_shop_type), new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            Intent intent = new Intent(ShopListActivity.this, WebActivity.class);
                            intent.putExtra("url", shop.getTmallLink());
                            startActivity(intent);
                            appDialog.dismiss();
                        } else if (position == 1) {
                            Intent intent = new Intent(ShopListActivity.this, WebActivity.class);
                            intent.putExtra("url", shop.getJdLink());
                            startActivity(intent);
                            appDialog.dismiss();
                        } else if (position == 2) {
                            appDialog.dismiss();
                        }
                    }
                });
                appDialog.show();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ShopListActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(shopAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SHSwipeRefreshLayout.SHSOnRefreshListener() {
            @Override
            public void onRefresh() {
                curPage = 1;
                getShopList(curPage);
            }

            @Override
            public void onLoading() {
                curPage++;
                getShopList(curPage);
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
    protected int getLayoutId() {
        return R.layout.activity_shop_list;
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
