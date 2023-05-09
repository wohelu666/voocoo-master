package com.voocoo.pet.modules.dev.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.BaseRowsEntity;
import com.voocoo.pet.entity.Device;
import com.voocoo.pet.entity.Record;
import com.voocoo.pet.entity.WaitToDoList;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.dev.adapter.UseRecordListAdapter;
import com.voocoo.pet.modules.main.adapter.TodoListAdapter;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class WaitToDoActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.rv_water_records)
    RecyclerView rvWaterRecords;
    @BindView(R.id.rv_feed_records)
    RecyclerView rvFeedRecords;

    @BindView(R.id.tv_water_title)
    TextView tvWaterTitle;
    @BindView(R.id.tv_feed_title)
    TextView tvFeedTitle;

    TodoListAdapter waterTodoListAdapter;
    TodoListAdapter feedTodoListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        getWaitTodoList();
    }


    private void initView() {
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_black_ic);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText(getString(R.string.text_wait_to_do));

        waterTodoListAdapter = new TodoListAdapter(WaitToDoActivity.this);
        waterTodoListAdapter.setClickToDetileListener(new TodoListAdapter.ClickToDetileListener() {
            @Override
            public void onClick(WaitToDoList.WaitToDo momentDto) {

            }
        });
        rvWaterRecords.setLayoutManager(new LinearLayoutManager(WaitToDoActivity.this));
        rvWaterRecords.setItemAnimator(new DefaultItemAnimator());
        rvWaterRecords.setAdapter(waterTodoListAdapter);

        feedTodoListAdapter = new TodoListAdapter(WaitToDoActivity.this);
        feedTodoListAdapter.setClickToDetileListener(new TodoListAdapter.ClickToDetileListener() {
            @Override
            public void onClick(WaitToDoList.WaitToDo momentDto) {

            }
        });
        rvFeedRecords.setLayoutManager(new LinearLayoutManager(WaitToDoActivity.this));
        rvFeedRecords.setItemAnimator(new DefaultItemAnimator());
        rvFeedRecords.setAdapter(feedTodoListAdapter);
    }

    private void getWaitTodoList() {
        HttpManage.getInstance().getWaitTodo(new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                LogUtil.d("getWaitTodo onError");
            }

            @Override
            public void onSuccess(int code, String response) {
                LogUtil.d("getWaitTodo->" + response);
                BaseEntity<WaitToDoList> result = new Gson().fromJson(response, new com.google.gson.reflect.TypeToken<BaseEntity<WaitToDoList>>() {
                }.getType());

                if (result.getCode() == 200) {
                    waterTodoListAdapter.setData(result.getData().waterToDoList);
                    feedTodoListAdapter.setData(result.getData().feederToDoList);
                    if (result.getData().waterToDoList.size() == 0) {
                        tvWaterTitle.setVisibility(View.GONE);
                    } else {
                        tvWaterTitle.setVisibility(View.VISIBLE);
                    }

                    if (result.getData().feederToDoList.size() == 0) {
                        tvFeedTitle.setVisibility(View.GONE);
                    } else {
                        tvFeedTitle.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wait_to_do;
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
