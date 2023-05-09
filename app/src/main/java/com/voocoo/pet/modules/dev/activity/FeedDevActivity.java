package com.voocoo.pet.modules.dev.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyItemDialogListener;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.event.DelDeviceEvent;
import com.voocoo.pet.common.event.RenameDeviceEvent;
import com.voocoo.pet.common.manager.PetsManager;
import com.voocoo.pet.common.utils.DensityUtil;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.common.widgets.AppDialog;
import com.voocoo.pet.common.widgets.TempProgressBar;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Device;
import com.voocoo.pet.entity.DeviceDetail;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.dev.adapter.UseRecordListAdapter;
import com.voocoo.pet.modules.user.LoginByCodeActivity;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import pl.droidsonroids.gif.GifImageView;

public class FeedDevActivity extends AbsBaseActivity {

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.circle_progress)
    TempProgressBar circleProgress;
    @BindView(R.id.circleview)
    GifImageView circleview;

    @BindView(R.id.tv_eat_today)
    TextView tvEatToday;
    @BindView(R.id.tv_out_food)
    TextView tvOutFood;

    @BindView(R.id.tv_left)
    TextView tvLeft;

    @BindView(R.id.tv_today_out_food)
    TextView tvTodayOutFood;

    @BindView(R.id.tv_battery_title)
    TextView tvBatteryTitle;
    @BindView(R.id.tv_battery)
    TextView tvBattery;
    @BindView(R.id.tv_battery_tips)
    TextView tvBatteryTips;

    @BindView(R.id.iv_chongdian)
    ImageView ivChongdian;

    @BindView(R.id.circle_progress_battery)
    TempProgressBar circleProgressBattery;

    @BindView(R.id.top_title)
    TextView tvTitle;
    @BindView(R.id.top_mode)
    TextView tvMode;

    @BindView(R.id.tv_home_mode)
    TextView tvHomeMode;

    @BindView(R.id.tv_barrel_margin)
    TextView tvBarrelMargin;
    @BindView(R.id.tv_barrel_margin_tips)
    TextView tvBarrelMarginTips;

    @BindView(R.id.tv_today_plan_food)
    TextView tvTodayPlanFood;
    @BindView(R.id.tv_next_plan_food)
    TextView tvNextPlanFood;
    @BindView(R.id.tv_left_food)
    TextView tvLeftFood;
    @BindView(R.id.tv_plan_out_food)
    TextView tvPlanOutFood;

    @BindView(R.id.ly_records)
    View recordsView;
    @BindView(R.id.rv_food_records)
    RecyclerView rvFoodRecords;

    @BindView(R.id.tv_add_food)
    TextView tvAddFood;
    @BindView(R.id.iv_add_food)
    ImageView ivAddFood;
    UseRecordListAdapter useRecordListAdapter;
    Device device;

    @BindView(R.id.barChart)
    BarChart mBarChart;

    @BindView(R.id.empty_records)
    View emptyRecords;

    @BindView(R.id.iv_barrel_content)
    View viewBarrelContent;

    BarDataSet dataSet = null;
    BarDataSet dataSet2 = null;
    BarData barData = null;
    List<BarEntry> mutableList = new ArrayList<>();
    List<BarEntry> mutableList2 = new ArrayList<>();
    List<String> xAxisList = new ArrayList<>();
    int allInFood = 0;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private boolean isShowed = false;
    AppDialog alertDialog;
    DeviceDetail deviceDetail = null;

    /**
     * 喂食器界面
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        device = (Device) getIntent().getSerializableExtra("device");
        initView();
        initData();

        alertDialog = AppDialog.showAlertDialog(FeedDevActivity.this, "喂食器缺粮，请及时添粮，否则爱宠要挨饿啦！");
    }


    @Override
    protected void onStop() {
        super.onStop();
            isShowed = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        HttpManage.getInstance().devDetail(device.getDeviceId(), new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onSuccess(int code, String response) {
                swipeRefreshLayout.setRefreshing(false);
                LogUtil.d("devDetail->" + response);
                BaseEntity<DeviceDetail> result = new Gson().fromJson(response, new TypeToken<BaseEntity<DeviceDetail>>() {
                }.getType());

                if (result.getCode() == 200) {
                    parseHomeMode(result.getData().getFamilyModel());
                    deviceDetail = result.getData();
                    //今日进食
                    tvEatToday.setText(result.getData().eatingAmount + "");
                    tvOutFood.setText(result.getData().alreadyFeedingAmount + "g");
                    tvTodayOutFood.setText("约" + result.getData().eatingAmount);
                    tvLeft.setText(result.getData().desiccanSurplusDays + "");

                    //  circleview.setProgress(result.getData().planFeedingAmount == 0 ? 0 : (result.getData().eatingAmount / result.getData().planFeedingAmount) * 100);
                    int persent = result.getData().planFeedingAmount == 0 ? 0 : (result.getData().eatingAmount / result.getData().planFeedingAmount) * 100;

                    if (result.getData().getFamilyModel().equals("1")) {
                        //狗模式
                        if (result.getData().eatingAmount == 0) {
                            circleview.setVisibility(View.GONE);
                        } else if (result.getData().eatingAmount <= 30) {
                            circleview.setImageResource(R.drawable.d_one);
                            circleview.setVisibility(View.VISIBLE);
                        } else if (result.getData().eatingAmount <= 60) {
                            circleview.setImageResource(R.drawable.d_two);
                            circleview.setVisibility(View.VISIBLE);
                        } else if (result.getData().eatingAmount <= 90) {
                            circleview.setImageResource(R.drawable.d_three);
                            circleview.setVisibility(View.VISIBLE);
                        } else if (result.getData().eatingAmount <= 120) {
                            circleview.setImageResource(R.drawable.d_four);
                            circleview.setVisibility(View.VISIBLE);
                        } else if (result.getData().eatingAmount <= 150) {
                            circleview.setImageResource(R.drawable.d_five);
                            circleview.setVisibility(View.VISIBLE);
                        } else if (result.getData().eatingAmount <= 180) {
                            circleview.setImageResource(R.drawable.d_six);
                            circleview.setVisibility(View.VISIBLE);
                        } else if (result.getData().eatingAmount <= 210) {
                            circleview.setImageResource(R.drawable.d_seven);
                            circleview.setVisibility(View.VISIBLE);
                        } else if (result.getData().eatingAmount <= 240) {
                            circleview.setImageResource(R.drawable.d_eight);
                            circleview.setVisibility(View.VISIBLE);
                        } else if (result.getData().eatingAmount <= 270) {
                            circleview.setImageResource(R.drawable.d_nine);
                            circleview.setVisibility(View.VISIBLE);
                        } else {
                            circleview.setImageResource(R.drawable.d_ten);
                        }
                    } else {
                        if (result.getData().eatingAmount == 0) {
                            circleview.setVisibility(View.GONE);
                        } else if (result.getData().eatingAmount <= 10) {
                            circleview.setImageResource(R.drawable.d_one);
                            circleview.setVisibility(View.VISIBLE);
                        } else if (result.getData().eatingAmount <= 20) {
                            circleview.setImageResource(R.drawable.d_two);
                            circleview.setVisibility(View.VISIBLE);
                        } else if (result.getData().eatingAmount <= 30) {
                            circleview.setImageResource(R.drawable.d_three);
                            circleview.setVisibility(View.VISIBLE);
                        } else if (result.getData().eatingAmount <= 40) {
                            circleview.setImageResource(R.drawable.d_four);
                            circleview.setVisibility(View.VISIBLE);
                        } else if (result.getData().eatingAmount <= 50) {
                            circleview.setImageResource(R.drawable.d_five);
                            circleview.setVisibility(View.VISIBLE);
                        } else if (result.getData().eatingAmount <= 60) {
                            circleview.setImageResource(R.drawable.d_six);
                            circleview.setVisibility(View.VISIBLE);
                        } else if (result.getData().eatingAmount <= 70) {
                            circleview.setImageResource(R.drawable.d_seven);
                            circleview.setVisibility(View.VISIBLE);
                        } else if (result.getData().eatingAmount <= 80) {
                            circleview.setImageResource(R.drawable.d_eight);
                            circleview.setVisibility(View.VISIBLE);
                        } else if (result.getData().eatingAmount <= 90) {
                            circleview.setImageResource(R.drawable.d_nine);
                            circleview.setVisibility(View.VISIBLE);
                        } else {
                            circleview.setImageResource(R.drawable.d_ten);
                            circleview.setVisibility(View.VISIBLE);
                        }
                    }
                    //今日出粮
                    if (result.getData().planFeedingAmount == 0) {
                        float a = Float.valueOf(result.getData().alreadyFeedingAmount);
                        float b = result.getData().getFamilyModel().equals("1") ? Float.valueOf(400) : Float.valueOf(200);
                        int progress = (int) ((a / b) * 100);
                        circleProgress.setProgress(progress, 1000);
                    } else {
                        float a = Float.valueOf(result.getData().alreadyFeedingAmount);
                        float b = Float.valueOf(result.getData().planFeedingAmount);
                        int progress = (int) ((a / b) * 100);
                        circleProgress.setProgress(progress, 1000);
                    }
                    tvTodayPlanFood.setText(result.getData().needFeedingAmount + "g");
                    if (result.getData().nextDietAmount != 0) {
                        tvNextPlanFood.setText(result.getData().nextDietTime + " " + result.getData().nextDietAmount + "g");
                    } else {
                        tvNextPlanFood.setText(getString(R.string.text_not_plan_yet));
                    }
                    tvLeftFood.setText(result.getData().bowlSurplus);
                    tvPlanOutFood.setText(result.getData().planFeedingAmount + "");
                    tvBattery.setText(result.getData().getElectricity() + "%");
                    circleProgressBattery.setProgress(Integer.valueOf(result.getData().getElectricity()));

                    if (result.getData().powerPlug == 0) {
                        if (Integer.valueOf(result.getData().getElectricity()) <= 20) {
                            tvBatteryTips.setVisibility(View.VISIBLE);
                            tvBattery.setTextColor(Color.parseColor("#FB7E37"));
                            tvBatteryTips.setText(getString(R.string.text_chongdian));
                        } else {
                            tvBatteryTips.setVisibility(View.INVISIBLE);
                            tvBattery.setTextColor(Color.parseColor("#000000"));
                        }
                        tvBatteryTitle.setText(getString(R.string.text_battery2));
                        ivChongdian.setVisibility(View.GONE);
                        circleProgressBattery.setVisibility(View.VISIBLE);
                        tvHomeMode.setTextColor(Color.parseColor("#6A6B71"));
                        ivAddFood.setImageResource(R.mipmap.icon_add_food_unpress);
                        tvAddFood.setTextColor(Color.parseColor("#6A6B71"));

                    } else {

                        ivChongdian.setVisibility(View.VISIBLE);
                        circleProgressBattery.setVisibility(View.GONE);
                        tvBatteryTitle.setText("供电方式");
                        tvBatteryTips.setVisibility(View.INVISIBLE);
                        tvBattery.setText("电源供电中");
                        tvBattery.setTextColor(Color.parseColor("#000000"));
                    }

                    if (result.getData().getFeederLogList() != null && result.getData().getFeederLogList().size() > 0) {
                        if (result.getData().getFeederLogList().size() > 5) {
                            useRecordListAdapter.setData(result.getData().getFeederLogList().subList(0, 5));
                        } else {
                            useRecordListAdapter.setData(result.getData().getFeederLogList());
                        }
                        rvFoodRecords.setVisibility(View.VISIBLE);
                        emptyRecords.setVisibility(View.GONE);
                    } else {
                        rvFoodRecords.setVisibility(View.GONE);
                        emptyRecords.setVisibility(View.VISIBLE);
                    }

                    if (result.getData().bucketSurplus != null) {
                        if (!result.getData().bucketSurplus.equals("0")) {
                            if (!isShowed && !alertDialog.isShowing()) {
                                isShowed = true;
                                alertDialog.show();
                            }
                        }
                        tvBarrelMarginTips.setVisibility(result.getData().bucketSurplus.equals("0") ? View.INVISIBLE : View.VISIBLE);
                        tvBarrelMargin.setText(result.getData().bucketSurplus.equals("0") ? getString(R.string.text_adequate) : getString(R.string.text_insufficient));
                        tvBarrelMargin.setTextColor(result.getData().bucketSurplus.equals("0") ? Color.parseColor("#000000") : Color.parseColor("#FB7E37"));

                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewBarrelContent.getLayoutParams();
                        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                        params.height = DensityUtil.dip2px(FeedDevActivity.this, result.getData().bucketSurplus.equals("0") ? 40 : 10);
                        viewBarrelContent.setLayoutParams(params);
                    } else {
                        tvBarrelMarginTips.setVisibility(View.INVISIBLE);
                        tvBarrelMargin.setText(getString(R.string.text_adequate));
                        tvBarrelMargin.setTextColor(Color.parseColor("#000000"));

                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewBarrelContent.getLayoutParams();
                        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                        params.height = DensityUtil.dip2px(FeedDevActivity.this, 40);
                        viewBarrelContent.setLayoutParams(params);
                    }

                    Map<Integer, Integer> inDatas = new HashMap<>();
                    Map<Integer, Integer> outDatas = new HashMap<>();
                    allInFood = 0;
                    for (int i = 0; i < 12; i++) {
                        inDatas.put(i * 2, 0);
                    }
                    for (int i = 0; i < 12; i++) {
                        outDatas.put(i * 2, 0);
                    }
                    for (int i = 0; i < result.getData().getFeedingList().size(); i++) {
                        try {
                            Date date = simpleDateFormat.parse(result.getData().getFeedingList().get(i).date);//开始时间
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            allInFood += result.getData().getFeedingList().get(i).eatingAmount;

                            int index = 0;
                            int hour = calendar.get(Calendar.HOUR_OF_DAY);
                            if (hour == 0 || hour == 1) {
                                index = 0;
                            } else if (hour == 2 || hour == 3) {
                                index = 1;
                            } else if (hour == 4 || hour == 5) {
                                index = 2;
                            } else if (hour == 6 || hour == 7) {
                                index = 3;
                            } else if (hour == 8 || hour == 9) {
                                index = 4;
                            } else if (hour == 10 || hour == 11) {
                                index = 5;
                            } else if (hour == 12 || hour == 13) {
                                index = 6;
                            } else if (hour == 14 || hour == 15) {
                                index = 7;
                            } else if (hour == 16 || hour == 17) {
                                index = 8;
                            } else if (hour == 18 || hour == 19) {
                                index = 9;
                            } else if (hour == 20 || hour == 21) {
                                index = 10;
                            } else if (hour == 22 || hour == 23) {
                                index = 11;
                            } else {
                                index = 12;
                            }
                            if (inDatas.containsKey(index)) {
                                int count = inDatas.get(index);
                                count += result.getData().getFeedingList().get(i).eatingAmount;
                                inDatas.put(index, count);
                            } else {
                                inDatas.put(index, result.getData().getFeedingList().get(i).eatingAmount);
                            }

                            if (outDatas.containsKey(index)) {
                                int count = outDatas.get(index);
                                count += result.getData().getFeedingList().get(i).feedingAmount;
                                outDatas.put(index, count);
                            } else {
                                outDatas.put(index, result.getData().getFeedingList().get(i).feedingAmount);
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    mutableList = new ArrayList<>();
                    for (Integer key : inDatas.keySet()) {
                        BarEntry barEntry = new BarEntry(key, new float[]{(float) inDatas.get(key), (float) outDatas.get(key)});
                        mutableList.add(barEntry);
                    }
                    dataSet = new BarDataSet(mutableList, "");
                    dataSet.setColors(new int[]{R.color.color_tab_select2, R.color.color_tab_select}, FeedDevActivity.this);

                    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                    dataSets.add(dataSet);
                    barData = new BarData(dataSets);
                    dataSet.setValueTextSize(0);
                    mBarChart.setData(barData);
                    mBarChart.invalidate();
                } else {
                    showToast(result.getMsg());

                }
            }
        });
    }

    private void parseHomeMode(String mode) {
        //3单猫，4单狗，5两猫，6三猫
        if (mode.equals("0")) {
            tvHomeMode.setText(getString(R.string.text_cat_mode));
        } else if (mode.equals("1")) {
            tvHomeMode.setText(getString(R.string.text_dog_mode));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StyledDialog.init(this);
        initData();
        // getWaterDrinkData();
    }

    @OnClick({R.id.ly_add_food, R.id.ly_food_plan, R.id.tv_desiccan, R.id.tv_see_more, R.id.rv_food_records, R.id.ly_records, R.id.tv_home_mode, R.id.tv_add_food})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ly_food_plan:
                Intent intent = new Intent(FeedDevActivity.this, FeedPlanActivity.class);
                intent.putExtra("device", device);
                startActivity(intent);
                break;
            case R.id.ly_add_food:
                if (deviceDetail == null) {
                    showToast(getString(R.string.text_plan_null));
                } else {
                    if (deviceDetail.powerPlug == 0) {
                        AppDialog.showAlertDialog(FeedDevActivity.this, "电池模式下只执行现有的喂食计划").show();
                        return;
                    }
                }
                AppDialog  dialog = new AppDialog(FeedDevActivity.this, null, R.layout.dialog_select_food_size);
                dialog.showSetFoodSize(dialog,FeedDevActivity.this, 0, true,new AppDialog.FeedSizeSetListener() {
                    @Override
                    public void setSize(String size) {
                        HttpManage.getInstance().sendFeedingByHand(device.getDeviceId(), Integer.valueOf(size), new HttpManage.ResultCallback<String>() {
                            @Override
                            public void onError(Header[] headers, HttpManage.Error error) {
                                LogUtil.d("sendFeedingByHand onError");
                            }

                            @Override
                            public void onSuccess(int code, String response) {
                                LogUtil.d("sendFeedingByHand，response=" + response);
                                BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                                }.getType());
                                if (result.getCode() == 200) {

                                } else {
                                    showToast(result.getMsg());
                                }
                            }
                        });
                    }
                });
                break;
            case R.id.tv_desiccan:
                intent = new Intent(FeedDevActivity.this, DesiccantActivity.class);
                intent.putExtra("device", device);
                startActivity(intent);
                break;
            case R.id.tv_see_more:
                intent = new Intent(FeedDevActivity.this, FeedDatasActivity.class);
                intent.putExtra("device", device);
                startActivity(intent);
                break;
            case R.id.ly_records:
            case R.id.rv_food_records:
                if (emptyRecords.getVisibility() == View.VISIBLE) {
                    return;
                }
                intent = new Intent(FeedDevActivity.this, FeedRecordsActivity.class);
                intent.putExtra("device", device);
                startActivity(intent);
                break;
            case R.id.tv_home_mode:
                if (deviceDetail == null) {
                    showToast(getString(R.string.text_plan_null));
                } else {
                    if (deviceDetail.powerPlug == 0) {
                        AppDialog.showAlertDialog(FeedDevActivity.this, "电池模式下只执行现有的喂食计划").show();
                        return;
                    }
                }
                StyledDialog.buildBottomItemDialog(Arrays.asList(getResources().getStringArray(R.array.feed_select_home_mode_array)), getString(R.string.text_cancel), new MyItemDialogListener() {
                    @Override
                    public void onItemClick(CharSequence text, int position) {
                        String mode = "";
                        if (position == 0) {
                            mode = "0";
                        } else if (position == 1) {
                            mode = "1";
                        }
                        String finalMode = mode;
                        HttpManage.getInstance().feedSendHomeMode(device.getDeviceId(), mode, new HttpManage.ResultCallback<String>() {
                            @Override
                            public void onError(Header[] headers, HttpManage.Error error) {

                            }

                            @Override
                            public void onSuccess(int code, String response) {
                                LogUtil.d("sendHomeMode->" + response);
                                BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                                }.getType());
                                if (result.getCode() == 200) {
                                    parseHomeMode(finalMode);
                                }
                            }
                        });
                    }
                }).show();
                break;
        }
    }


    //去掉左右y轴，和中间的横线和竖线
    private void removeY() {
        mBarChart.getAxisLeft().setDrawZeroLine(true);
        //去掉左侧Y轴刻度
        //mBarChart.getAxisLeft().setDrawLabels(false);
        //去掉左侧Y轴
        //mBarChart.getAxisLeft().setDrawAxisLine(false);
        //去掉中间竖线
        mBarChart.getXAxis().setDrawGridLines(false);

        mBarChart.getAxisLeft().setAxisMinimum(0f);
        //mBarChart.getAxisLeft().setAxisMaximum(40);
        mBarChart.getAxisLeft().setLabelCount(5, true);
        mBarChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return (int) value + "g";
            }
        });
        //去掉中间横线
        //mBarChart.getAxisLeft().setDrawGridLines(false);
        //不使用右侧Y轴
        mBarChart.getAxisRight().setEnabled(false);
    }

    private void initX() {
        String[] array = {"00:00", "", "04:00"
                , "", "08:00"
                , "", "12:00", "", "16:00"
                , "", "20:00", "", "24:00"};

        xAxisList = new ArrayList<>(Arrays.asList(array));
        // Collections.addAll(xAxisList,array);
        IAxisValueFormatter iAxisValueFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int hour = (int) value;
                if (hour == 2
                        || hour == 6
                        || hour == 10
                        || hour == 14
                        || hour == 18
                        || hour == 22) {
                    return "";
                }
                return (hour < 10 ? "0" + hour : hour) + ":00";
            }
        };
        mBarChart.getXAxis().setValueFormatter(iAxisValueFormatter);

        //默认显示在顶端，这是设置到底部，符合我们正常视觉
        mBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        //去掉底部图例BarChatView 的提示，大家可以根据自己业务需求，对
        //Legend进行定制
        mBarChart.setLogEnabled(false);
        //xAxisList的长度要和list的长度一直，否则会数组越界
        mBarChart.getXAxis().setLabelCount(xAxisList.size(), false);
    }

    private void initBarChartView() {
        Description description = new Description();
        description.setText("");
        mBarChart.setDescription(description);
        mBarChart.setNoDataText("正在初始化...");
        mBarChart.getLegend().setEnabled(false);
        mBarChart.setHighlightFullBarEnabled(false);
        mBarChart.setDoubleTapToZoomEnabled(false);
        mBarChart.setClickable(false);
        removeY();
        initX();
    }




    private void initView() {
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_black_ic);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText(device.getDeviceName());

        Drawable drawable;
        if (device.getStatus().equals("0")) {
            // 使用代码设置drawableLeft
            drawable = getResources().getDrawable(
                    R.drawable.oval_online);
            // / 这一步必须要做,否则不会显示.
        } else {
            // 使用代码设置drawableLeft
            drawable = getResources().getDrawable(
                    R.drawable.oval_offline);
            // / 这一步必须要做,否则不会显示.
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                drawable.getMinimumHeight());
        tvTitle.setCompoundDrawables(drawable, null, null, null);

        rvFoodRecords.setLayoutManager(new LinearLayoutManager(FeedDevActivity.this));

        useRecordListAdapter = new UseRecordListAdapter(FeedDevActivity.this);
        rvFoodRecords.setAdapter(useRecordListAdapter);
        useRecordListAdapter.setClickListener(new UseRecordListAdapter.ClickListener() {
            @Override
            public void onClick() {
                if (emptyRecords.getVisibility() == View.VISIBLE) {
                    return;
                }
                Intent intent = new Intent(FeedDevActivity.this, FeedRecordsActivity.class);
                intent.putExtra("device", device);
                startActivity(intent);
            }
        });
        initBarChartView();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feed_dev;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRenameDevice(RenameDeviceEvent event) {
        device.setDeviceName(event.name);
        tvTitle.setText(device.getDeviceName());
    }

    /**
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDelDevice(DelDeviceEvent event) {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.setting:
                Intent intent = new Intent(FeedDevActivity.this, FeedDevSettingActivity.class);
                intent.putExtra("device", device);
                if (deviceDetail!=null){
                    intent.putExtra("powerPlug", deviceDetail.powerPlug);

                }
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
