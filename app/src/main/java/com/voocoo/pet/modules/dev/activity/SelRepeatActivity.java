package com.voocoo.pet.modules.dev.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.hss01248.dialog.StyledDialog;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.event.ChangeRecyclerEvent;
import com.voocoo.pet.common.widgets.SwitchView;
import com.voocoo.pet.entity.Device;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SelRepeatActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.iv_day1)
    ImageView ivDay1;
    @BindView(R.id.iv_day2)
    ImageView ivDay2;
    @BindView(R.id.iv_day3)
    ImageView ivDay3;
    @BindView(R.id.iv_day4)
    ImageView ivDay4;
    @BindView(R.id.iv_day5)
    ImageView ivDay5;
    @BindView(R.id.iv_day6)
    ImageView ivDay6;
    @BindView(R.id.iv_day7)
    ImageView ivDay7;

    private ArrayList<Integer> repeat = new ArrayList<>();

    Device device;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        device = (Device) getIntent().getSerializableExtra("device");
        repeat = getIntent().getIntegerArrayListExtra("data");
        initView();
        initData();
    }

    private void initData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        StyledDialog.init(this);
    }

    private void refreshUi() {
        if (repeat.contains(7)) {
            ivDay7.setVisibility(View.VISIBLE);
        } else {
            ivDay7.setVisibility(View.INVISIBLE);
        }
        if (repeat.contains(1)) {
            ivDay1.setVisibility(View.VISIBLE);
        } else {
            ivDay1.setVisibility(View.INVISIBLE);
        }
        if (repeat.contains(2)) {
            ivDay2.setVisibility(View.VISIBLE);
        } else {
            ivDay2.setVisibility(View.INVISIBLE);
        }
        if (repeat.contains(3)) {
            ivDay3.setVisibility(View.VISIBLE);
        } else {
            ivDay3.setVisibility(View.INVISIBLE);
        }
        if (repeat.contains(4)) {
            ivDay4.setVisibility(View.VISIBLE);
        } else {
            ivDay4.setVisibility(View.INVISIBLE);
        }
        if (repeat.contains(5)) {
            ivDay5.setVisibility(View.VISIBLE);
        } else {
            ivDay5.setVisibility(View.INVISIBLE);
        }
        if (repeat.contains(6)) {
            ivDay6.setVisibility(View.VISIBLE);
        } else {
            ivDay6.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick({R.id.btn_ensure, R.id.ly_day7, R.id.ly_day1, R.id.ly_day2
            , R.id.ly_day3, R.id.ly_day4, R.id.ly_day5, R.id.ly_day6})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ensure:
                EventBus.getDefault().post(new ChangeRecyclerEvent(repeat));
                finish();
                break;
            case R.id.ly_day7:
                if (repeat.contains(7)) {
                    repeat.remove(Integer.valueOf(7));
                } else {
                    repeat.add(Integer.valueOf(7));
                }
                refreshUi();
                break;
            case R.id.ly_day1:
                if (repeat.contains(1)) {
                    repeat.remove(Integer.valueOf(1));
                } else {
                    repeat.add(Integer.valueOf(1));
                }
                refreshUi();
                break;
            case R.id.ly_day2:
                if (repeat.contains(2)) {
                    repeat.remove(Integer.valueOf(2));
                } else {
                    repeat.add(Integer.valueOf(2));
                }
                refreshUi();
                break;
            case R.id.ly_day3:
                if (repeat.contains(3)) {
                    repeat.remove(Integer.valueOf(3));
                } else {
                    repeat.add(Integer.valueOf(3));
                }
                refreshUi();
                break;
            case R.id.ly_day4:
                if (repeat.contains(4)) {
                    repeat.remove(Integer.valueOf(4));
                } else {
                    repeat.add(Integer.valueOf(4));
                }
                refreshUi();
                break;
            case R.id.ly_day5:
                if (repeat.contains(5)) {
                    repeat.remove(Integer.valueOf(5));
                } else {
                    repeat.add(Integer.valueOf(5));
                }
                refreshUi();
                break;
            case R.id.ly_day6:
                if (repeat.contains(6)) {
                    repeat.remove(Integer.valueOf(6));
                } else {
                    repeat.add(Integer.valueOf(6));
                }
                refreshUi();
                break;
        }
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_black_ic);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText(getString(R.string.text_start_recycle));
        refreshUi();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sel_repeat;
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
