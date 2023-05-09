package com.voocoo.pet.modules.dev.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyDialogListener;
import com.hss01248.dialog.interfaces.MyItemDialogListener;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.event.AddFeedPlanFinishEvent;
import com.voocoo.pet.common.event.DelFeedPlanFinishEvent;
import com.voocoo.pet.common.event.UpdateFeedPlanFinishEvent;
import com.voocoo.pet.common.utils.CommonUtil;
import com.voocoo.pet.common.utils.TimeUtil;
import com.voocoo.pet.common.widgets.AppDialog;
import com.voocoo.pet.entity.Device;
import com.voocoo.pet.entity.Diet;
import com.voocoo.pet.entity.FeedPlan;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.mine.FeedbackActivity;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.forward.androids.utils.LogUtil;
import cn.forward.androids.views.StringScrollPicker;

public class AddFeedPlanActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.top_right)
    TextView tvRight;

    @BindView(R.id.tv_food_time)
    TextView tvFoodTime;
    @BindView(R.id.tv_label)
    TextView tvLabel;
    @BindView(R.id.tv_food_size)
    TextView tvFoodSize;

    private int labelIndex;
    int size;
    private ArrayList<Integer> repeat = new ArrayList<>();
    private int index = -1;
    FeedPlan.FeedPlanDiets feedPlanDiets;
    private ArrayList<FeedPlan.FeedPlanDiets> listPlan = new ArrayList<>();
    private boolean isFirst = true;
    private int number;
    private AppDialog dialog;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (feedPlanDiets != null) {
            getMenuInflater().inflate(R.menu.del, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.del:
                //返回去
                StyledDialog.buildIosAlert(getString(R.string.text_ensure_del_feed_plan), "", new MyDialogListener() {
                    @Override
                    public void onFirst() {
                        EventBus.getDefault().post(new DelFeedPlanFinishEvent(feedPlanDiets));
                        finish();
                    }

                    @Override
                    public void onSecond() {

                    }
                }).show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repeat = getIntent().getIntegerArrayListExtra("data");
        size = getIntent().getIntExtra("size", 0);
        feedPlanDiets = (FeedPlan.FeedPlanDiets) getIntent().getSerializableExtra("diet");
        listPlan = (ArrayList<FeedPlan.FeedPlanDiets>) getIntent().getSerializableExtra("planList");
        index = getIntent().getIntExtra("index", -1);
        initView();
        initData();
        dialog = new AppDialog(AddFeedPlanActivity.this, null, R.layout.dialog_select_food_size);

    }

    private void initData() {
        if (feedPlanDiets != null) {
            tvFoodTime.setText(feedPlanDiets.dietTime);
            tvLabel.setText(getResources().getStringArray(R.array.select_label_array)[Integer.valueOf(feedPlanDiets.dietTag)]);
            tvFoodSize.setText(feedPlanDiets.dietAmount + "g");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StyledDialog.init(this);
    }

    @OnClick({R.id.ly_time, R.id.ly_label, R.id.ly_food, R.id.btn_ensure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ly_time:
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE);
                if (feedPlanDiets != null && feedPlanDiets.dietTime != null) {
                    hour = Integer.parseInt(feedPlanDiets.dietTime.split(":")[0]);
                    min = Integer.parseInt(feedPlanDiets.dietTime.split(":")[1]);
                }
                AppDialog.showSetFeedPlanTime2(AddFeedPlanActivity.this, hour, min, new AppDialog.FeedTimerSetListener2() {
                    @Override
                    public void setTimer(int hour, int min) {
                        tvFoodTime.setText(CommonUtil.zero(hour) + ":" + CommonUtil.zero(min));
                        if (hour >= 5 && hour < 10) {
                            tvLabel.setText(Arrays.asList(getResources().getStringArray(R.array.select_label_array)).get(0));
                        } else if (hour >= 10 && hour < 15) {
                            tvLabel.setText(Arrays.asList(getResources().getStringArray(R.array.select_label_array)).get(1));
                        } else if (hour >= 15 && hour < 21) {
                            tvLabel.setText(Arrays.asList(getResources().getStringArray(R.array.select_label_array)).get(2));
                        } else {
                            tvLabel.setText(Arrays.asList(getResources().getStringArray(R.array.select_label_array)).get(3));
                        }
                    }
                }).show();
                break;
           /* case R.id.ly_label:
                StyledDialog.buildBottomItemDialog(Arrays.asList(getResources().getStringArray(R.array.select_label_array)), getString(R.string.text_cancel), new MyItemDialogListener() {
                    @Override
                    public void onItemClick(CharSequence text, int position) {
                        labelIndex = position;
                        tvLabel.setText(text);
                    }
                }).show();
                break;*/
            case R.id.ly_food:
                if (feedPlanDiets == null) { //添加
//                    number = 0;
                } else { //修改
                    if (isFirst) {//未修改过
                        number = feedPlanDiets.dietAmount - 10;
                    } else { //已修改过

                    }
                }
                dialog.showSetFoodSize(dialog, AddFeedPlanActivity.this, number, isFirst, new AppDialog.FeedSizeSetListener() {
                    @Override
                    public void setSize(String size) {
                        number = Integer.parseInt(size) - 10;
                        isFirst = false;
                        tvFoodSize.setText(size + "g");
//                        feedPlanDiets = new FeedPlan.FeedPlanDiets();

                    }
                });

                break;
            case R.id.btn_ensure:
                if (TextUtils.isEmpty(tvFoodTime.getText().toString())) {
                    showToast("请选择时间");
                    return;
                }
                if (TextUtils.isEmpty(tvFoodSize.getText().toString())) {
                    showToast("请选择份量");
                    return;
                }
                if (feedPlanDiets == null) { // 添加喂食计划
                    feedPlanDiets = new FeedPlan.FeedPlanDiets();
                    //暂用id作为编辑使用
                    feedPlanDiets.dietId = size + 1;
                    feedPlanDiets.dietTime = tvFoodTime.getText().toString();
                    feedPlanDiets.dietTag = labelIndex + "";
                    feedPlanDiets.dietAmount = Integer.valueOf(tvFoodSize.getText().toString().replaceAll("g", ""));
                    String add = TimeUtil.dateToStamp(tvFoodTime.getText().toString());

                    for (int i = 0; i < listPlan.size(); i++) {
                        String listTime = TimeUtil.dateToStamp(listPlan.get(i).dietTime);
                        if (Math.abs(Integer.parseInt(listTime) - Integer.parseInt(add)) <= 300000) {
                            showToast("喂食相隔时间不能小于五分钟");
                            AppDialog.showAlertDialog(AddFeedPlanActivity.this, "喂食相隔时间不能小于五分钟").show();

//                            AppDialog.doubleTextOneButton(AddFeedPlanActivity.this, "温馨提示", "喂食相隔时间不能小于五分钟").show();
                            feedPlanDiets = null;
                            return;
                        }
                    }

                    //返回去
                    EventBus.getDefault().post(new AddFeedPlanFinishEvent(feedPlanDiets));
                } else { // 修改喂食计划
                    feedPlanDiets.dietTime = tvFoodTime.getText().toString();
                    String add = TimeUtil.dateToStamp(tvFoodTime.getText().toString());

                    for (int i = 0; i < listPlan.size(); i++) {
                        if (i != index) {
                            String listTime = TimeUtil.dateToStamp(listPlan.get(i).dietTime);
                            if (Math.abs(Integer.parseInt(listTime) - Integer.parseInt(add)) <= 300000) {
                                showToast("喂食相隔时间不能小于五分钟");
                                AppDialog.showAlertDialog(AddFeedPlanActivity.this, "喂食相隔时间不能小于五分钟").show();

                                return;
                            }
                        } else {
                        }
                    }
                    feedPlanDiets.dietTag = labelIndex + "";
                    feedPlanDiets.dietAmount = Integer.valueOf(tvFoodSize.getText().toString().replaceAll("g", ""));
                    //返回去
                    EventBus.getDefault().post(new UpdateFeedPlanFinishEvent(feedPlanDiets));
                }
                finish();

                break;
        }
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_black_ic);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        if (feedPlanDiets != null) {
            tvTitle.setText(getString(R.string.text_update_feed_plan));
        } else {
            tvTitle.setText(getString(R.string.text_add_feed_plan));
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_feed_plan;
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
