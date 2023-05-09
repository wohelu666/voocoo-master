package com.voocoo.pet.modules.dev.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyItemDialogListener;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.event.DelDeviceEvent;
import com.voocoo.pet.common.event.RenameDeviceEvent;
import com.voocoo.pet.common.utils.DensityUtil;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.widgets.AppDialog;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Device;
import com.voocoo.pet.entity.DeviceDetail;
import com.voocoo.pet.entity.Restful.WaterData;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.dev.adapter.UseRecordListAdapter;

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

public class WaterDevActivity extends AbsBaseActivity {

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;
    @BindView(R.id.top_mode)
    TextView tvMode;

    @BindView(R.id.iv_night)
    ImageView ivNight;
    @BindView(R.id.iv_ganying)
    ImageView ivGanying;
    @BindView(R.id.iv_smart)
    ImageView ivSmart;

    @BindView(R.id.tv_home_mode)
    TextView tvHomeMode;

    @BindView(R.id.tv_time)
    TextView tvTime;

    @BindView(R.id.drink_time_tag)
    ImageView ivDrinkTimeTag;
    @BindView(R.id.drink_time_value)
    TextView tvDrinkTimeValue;

    @BindView(R.id.stay_time_tag)
    ImageView ivStayTimeTag;
    @BindView(R.id.stay_time_value)
    TextView tvStayTimeValue;

    @BindView(R.id.tv_min)
    TextView tvMin;
   /* @BindView(R.id.tv_min_unit)
    TextView tvMinUnit;
    @BindView(R.id.tv_second)
    TextView tvSecond;*/

    @BindView(R.id.tv_clean_water_status)
    TextView tvCleanWaterStauts;
    @BindView(R.id.tv_clean_water_status_tips)
    TextView tvCleanWaterStautsTips;
    @BindView(R.id.iv_clean_water_status)
    ImageView ivCleanWaterStatus;

    @BindView(R.id.tv_waste_water_status)
    TextView tvWasteWaterStauts;
    @BindView(R.id.tv_waste_water_status_tips)
    TextView tvWasteWaterStautsTips;
    @BindView(R.id.iv_waste_water_status)
    ImageView ivWasteWaterStatus;

    @BindView(R.id.tv_filter_element)
    TextView tvFilterElement;
    @BindView(R.id.tv_battery)
    TextView tvBattery;

    @BindView(R.id.ly_records)
    View recordsView;
    @BindView(R.id.rv_water_records)
    RecyclerView rvWaterRecords;

    UseRecordListAdapter useRecordListAdapter;
    Device device;

    @BindView(R.id.barChart)
    BarChart mBarChart;
    @BindView(R.id.tv_drink_time)
    TextView tvDrinkTime;

    BarDataSet dataSet = null;
    BarData barData = null;
    List<BarEntry> mutableList = new ArrayList<>();
    List<String> xAxisList = new ArrayList<>();
    int allDrinkTime = 0;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private boolean isShowed = false;
    private boolean isShowed2 = false;
    AppDialog alertDialog1;
    AppDialog alertDialog2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        device = (Device) getIntent().getSerializableExtra("device");
        initView();

        alertDialog1 = AppDialog.showAlertDialog(WaterDevActivity.this, "饮水机废水箱严重满载，饮水机已停止出水，请及时倾倒，否则爱宠要口渴啦！");
        alertDialog2 = AppDialog.showAlertDialog(WaterDevActivity.this, "饮水机净水箱严重缺水，饮水机已停止出水，请及时添水，否则爱宠要口渴啦！");
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
                    parseMode(result.getData().getWorkingModel()); //感应模式，智能模式
                    parseNightMode(result.getData().nightModel);//夜间模式
                    parseHomeMode(result.getData().getFamilyModel()); //
                    parseCleanWater(result.getData().getPurifiedWaterSurplus());//净水
                    parseWasteWater(result.getData().getWasteWaterSurplus()); //废水


                    String content = result.getData().getDrinkingTimes() + "次";
                    Spannable textSpan = new SpannableStringBuilder(content);
                    textSpan.setSpan(new AbsoluteSizeSpan(DensityUtil.dip2px(WaterDevActivity.this, 16)),
                            content.indexOf("次"), content.indexOf("次")+1,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    tvTime.setText(textSpan);

                    if (result.getData().getDrinkingTimes() - result.getData().getDrinkingTimesYesterday() > 0) {
                        ivDrinkTimeTag.setImageResource(R.mipmap.icon_up);
                        tvDrinkTimeValue.setText("+"
                                + (result.getData().getDrinkingTimes() - result.getData().getDrinkingTimesYesterday()) + getString(R.string.text_times));
                    } else if (result.getData().getDrinkingTimes() - result.getData().getDrinkingTimesYesterday() < 0) {
                        ivDrinkTimeTag.setImageResource(R.mipmap.icon_down);
                        tvDrinkTimeValue.setText((result.getData().getDrinkingTimes() - result.getData().getDrinkingTimesYesterday()) + getString(R.string.text_times));
                    } else {
                        ivDrinkTimeTag.setVisibility(View.GONE);
                        tvDrinkTimeValue.setText("--");
                    }

                    if (result.getData().getAvgStayTime() - result.getData().getAvgStayTimeYesterday() > 0) {
                        ivStayTimeTag.setImageResource(R.mipmap.icon_up);
                        tvStayTimeValue.setText("+"
                                + (result.getData().getAvgStayTime() - result.getData().getAvgStayTimeYesterday()) + "s");
                    } else if (result.getData().getAvgStayTime() - result.getData().getAvgStayTimeYesterday() < 0) {
                        ivStayTimeTag.setImageResource(R.mipmap.icon_down);
                        tvStayTimeValue.setText(result.getData().getAvgStayTime() - result.getData().getAvgStayTimeYesterday() + "s");
                    } else {
                        ivStayTimeTag.setVisibility(View.GONE);
                        tvStayTimeValue.setText("--");
                    }



                    String stayTime = result.getData().getFormatAvgStayTime();
                    textSpan = new SpannableStringBuilder(stayTime);

                    if(stayTime.contains("小时")) {
                        textSpan.setSpan(new AbsoluteSizeSpan(DensityUtil.dip2px(WaterDevActivity.this, 16)),
                                stayTime.indexOf("小时"), stayTime.indexOf("小时")+1,
                                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        textSpan.setSpan(new AbsoluteSizeSpan(DensityUtil.dip2px(WaterDevActivity.this, 16)),
                                stayTime.indexOf("分"), stayTime.indexOf("分")+1,
                                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        textSpan.setSpan(new AbsoluteSizeSpan(DensityUtil.dip2px(WaterDevActivity.this, 16)),
                                stayTime.indexOf("秒"), stayTime.indexOf("秒")+1,
                                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        tvMin.setText(textSpan);
                    }else if(stayTime.contains("分")) {
                        textSpan.setSpan(new AbsoluteSizeSpan(DensityUtil.dip2px(WaterDevActivity.this, 16)),
                                stayTime.indexOf("分"), stayTime.indexOf("分")+1,
                                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        textSpan.setSpan(new AbsoluteSizeSpan(DensityUtil.dip2px(WaterDevActivity.this, 16)),
                                stayTime.indexOf("秒"), stayTime.indexOf("秒")+1,
                                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        tvMin.setText(textSpan);
                    }else{
                        textSpan.setSpan(new AbsoluteSizeSpan(DensityUtil.dip2px(WaterDevActivity.this, 16)),
                                stayTime.indexOf("秒"), stayTime.indexOf("秒")+1,
                                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        tvMin.setText(textSpan);
                    }
                   /* if (stayTime.contains("分")) {
                        tvMin.setText(stayTime.substring(0, stayTime.indexOf("分")));
                        tvSecond.setText(stayTime.substring(stayTime.indexOf("分"), stayTime.indexOf("秒")));
                    } else {
                        tvSecond.setText(stayTime.substring(0, stayTime.indexOf("秒")));
                        tvMin.setVisibility(View.GONE);
                        tvMinUnit.setVisibility(View.GONE);
                    }*/

                    tvFilterElement.setText(getString(R.string.text_residual_filter_element, (int) ((result.getData().getFilterSurplusDays() / 90f) * 100) + "%"));
                    tvBattery.setText(result.getData().getElectricity().equals("0") ? getString(R.string.text_battery, 0 + "%")
                            : result.getData().getElectricity().equals("1") ? getString(R.string.text_battery, 25 + "%")
                            : result.getData().getElectricity().equals("2") ? getString(R.string.text_battery, 50 + "%")
                            : result.getData().getElectricity().equals("3") ? getString(R.string.text_battery, 75 + "%")
                            : getString(R.string.text_battery, 100 + "%"));

                    if (result.getData().getWaterLogVoList() != null && result.getData().getWaterLogVoList().size() > 0) {
                       /* if (result.getData().getDrinkingList().size() > 5) {
                            useRecordListAdapter.setData(result.getData().getDrinkingList().subList(0, 5));
                        } else {
                            useRecordListAdapter.setData(result.getData().getDrinkingList());
                        }*/
                        useRecordListAdapter.setData(result.getData().getWaterLogVoList());
                        recordsView.setVisibility(View.VISIBLE);
                    } else {
                        recordsView.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void parseCleanWater(String purifiedWaterSurplus) {
        if (purifiedWaterSurplus.equals("1")) {
            tvCleanWaterStauts.setText(getString(R.string.text_clean_water_status2));
            tvCleanWaterStautsTips.setVisibility(View.VISIBLE);
            tvCleanWaterStautsTips.setText(getString(R.string.text_clean_water_status_tips));
            ivCleanWaterStatus.setImageResource(R.mipmap.icon_water2);
        } else if (purifiedWaterSurplus.equals("0")) {
            tvCleanWaterStauts.setText(getString(R.string.text_clean_water_status3));
            tvCleanWaterStautsTips.setVisibility(View.VISIBLE);
            tvCleanWaterStautsTips.setText(getString(R.string.text_clean_water_status_tips));
            ivCleanWaterStatus.setImageResource(R.mipmap.icon_water1);

            if (!isShowed2 && !alertDialog2.isShowing()) {
                isShowed2 = true;
                alertDialog2.show();
            }
        } else {
            tvCleanWaterStauts.setText(getString(R.string.text_clean_water_status1));
            tvCleanWaterStautsTips.setVisibility(View.INVISIBLE);
            ivCleanWaterStatus.setImageResource(R.mipmap.icon_water3);
        }
    }

    private void parseWasteWater(String wasteWaterSurplus) {
        if (wasteWaterSurplus.equals("0")) {
            tvWasteWaterStauts.setText(getString(R.string.text_waste_water_status1));
            tvWasteWaterStautsTips.setVisibility(View.INVISIBLE);
            ivWasteWaterStatus.setImageResource(R.mipmap.icon_grid_water1);
        } else {
            tvWasteWaterStauts.setText(getString(R.string.text_waste_water_status2));
            tvWasteWaterStautsTips.setVisibility(View.VISIBLE);
            tvWasteWaterStautsTips.setText(getString(R.string.text_waste_water_status_tips));
            ivWasteWaterStatus.setImageResource(R.mipmap.icon_grid_water2);

            if (!isShowed && !alertDialog1.isShowing()) {
                isShowed = true;
                alertDialog1.show();
            }
        }
    }

    private void parseHomeMode(String mode) {
        //3单猫，4单狗，5两猫，6三猫
        if (mode.equals("3")) {
            tvHomeMode.setText(getString(R.string.text_one_cat_mode));
        } else if (mode.equals("4")) {
            tvHomeMode.setText(getString(R.string.text_one_dog_mode));
        } else if (mode.equals("5")) {
            tvHomeMode.setText(getString(R.string.text_two_cat_mode));
        } else if (mode.equals("6")) {
            tvHomeMode.setText(getString(R.string.text_three_cat_mode));
        }
    }

    private void parseNightMode(String nightmode) {
        if (nightmode.equals("0")) {
            ivNight.setImageResource(R.mipmap.icon_night_sel);
        } else {
            ivNight.setImageResource(R.mipmap.icon_night);
        }
    }

    private void parseMode(String mode) {
        if (mode.equals("1")) {
            tvMode.setText(getString(R.string.text_ganying_mode));
            ivGanying.setImageResource(R.mipmap.icon_ganying_sel);
            ivSmart.setImageResource(R.mipmap.icon_smart);
        } else {
            tvMode.setText(getString(R.string.text_smart_mode));
            ivGanying.setImageResource(R.mipmap.icon_ganying);
            ivSmart.setImageResource(R.mipmap.icon_smart_sel);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StyledDialog.init(this);
        initData();
        getWaterDrinkData();
    }

    private void getWaterDrinkData() {
        HttpManage.getInstance().waterDrinkData(device.getDeviceId(), 0, new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {

            }

            @Override
            public void onSuccess(int code, String response) {
                LogUtil.d("waterDrinkData->" + response);
                BaseEntity<WaterData> result = new Gson().fromJson(response, new TypeToken<BaseEntity<WaterData>>() {
                }.getType());
                Map<Integer, Integer> drinkDatas = new HashMap<>();
                allDrinkTime = 0;
                for (int i = 0; i < 25; i++) {
                    drinkDatas.put(i, 0);
                }
                for (int i = 0; i < result.getData().drinkingOfDayList.size(); i++) {
                    try {
                        Date date = simpleDateFormat.parse(result.getData().drinkingOfDayList.get(i).date);//开始时间
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        allDrinkTime += result.getData().drinkingOfDayList.get(i).drinkingcounts;
                        if (drinkDatas.containsKey(calendar.get(Calendar.HOUR_OF_DAY))) {
                            int count = drinkDatas.get(calendar.get(Calendar.HOUR_OF_DAY));
                            count += result.getData().drinkingOfDayList.get(i).drinkingcounts;
                            drinkDatas.put(calendar.get(Calendar.HOUR_OF_DAY), count);
                        } else {
                            drinkDatas.put(calendar.get(Calendar.HOUR_OF_DAY), result.getData().drinkingOfDayList.get(i).drinkingcounts);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                tvDrinkTime.setText(allDrinkTime + "");
                mutableList = new ArrayList<>();
                for (Integer key : drinkDatas.keySet()) {
                    BarEntry barEntry = new BarEntry(key, drinkDatas.get(key));
                    mutableList.add(barEntry);
                }
                dataSet = new BarDataSet(mutableList, "");
                dataSet.setColor(Color.parseColor("#FB7E37"));
                dataSet.setValueTextSize(0);
                barData = new BarData(dataSet);
                mBarChart.setData(barData);
                mBarChart.invalidate();
            }
        });
    }

    @OnClick({R.id.tv_see_more, R.id.ly_records, R.id.rv_water_records, R.id.tv_filter_element, R.id.tv_home_mode, R.id.ly_night, R.id.ly_ganying, R.id.ly_smart})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_see_more:
                Intent intent = new Intent(WaterDevActivity.this, WaterDatasActivity.class);
                intent.putExtra("device", device);
                startActivity(intent);
                break;
            case R.id.ly_night:
                intent = new Intent(WaterDevActivity.this, DevSettingActivity.class);
                intent.putExtra("device", device);
                startActivity(intent);
                break;
            case R.id.ly_records:
                intent = new Intent(WaterDevActivity.this, WaterRecordsActivity.class);
                intent.putExtra("device", device);
                startActivity(intent);
                break;
            case R.id.tv_filter_element:
                intent = new Intent(WaterDevActivity.this, FilterElementActivity.class);
                intent.putExtra("device", device);
                startActivity(intent);
                break;
            case R.id.tv_home_mode:
                StyledDialog.buildBottomItemDialog(Arrays.asList(getResources().getStringArray(R.array.select_home_mode_array)), getString(R.string.text_cancel), new MyItemDialogListener() {
                    @Override
                    public void onItemClick(CharSequence text, int position) {
                        String mode = "";
                        if (position == 0) {
                            mode = "3";
                        } else if (position == 1) {
                            mode = "4";
                        } else if (position == 2) {
                            mode = "5";
                        } else {
                            mode = "6";
                        }
                        String finalMode = mode;
                        HttpManage.getInstance().sendHomeMode(device.getDeviceId(), mode, new HttpManage.ResultCallback<String>() {
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
                                } else {
                                    showToast(result.getMsg());
                                }
                            }
                        });
                    }
                }).show();
                break;
            case R.id.ly_ganying:
                HttpManage.getInstance().sendInductionMode(device.getDeviceId(), new HttpManage.ResultCallback<String>() {
                    @Override
                    public void onError(Header[] headers, HttpManage.Error error) {

                    }

                    @Override
                    public void onSuccess(int code, String response) {
                        LogUtil.d("sendInductionMode->" + response);
                        BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                        }.getType());
                        if (result.getCode() == 200) {
                            parseMode("1");
                        }
                    }
                });
                break;
            case R.id.ly_smart:
                HttpManage.getInstance().sendSmartMode(device.getDeviceId(), new HttpManage.ResultCallback<String>() {
                    @Override
                    public void onError(Header[] headers, HttpManage.Error error) {

                    }

                    @Override
                    public void onSuccess(int code, String response) {
                        LogUtil.d("sendSmartMode->" + response);
                        BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                        }.getType());
                        if (result.getCode() == 200) {
                            parseMode("2");
                        }
                    }
                });
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
        mBarChart.getAxisLeft().setAxisMaximum(4f);
        mBarChart.getAxisLeft().setLabelCount(4, true);//设置纵坐标的个数
        /*if (allDrinkTime >= 5) {
            mBarChart.getAxisLeft().setLabelCount(5, false);//设置纵坐标的个数
        } else {
            mBarChart.getAxisLeft().setLabelCount(allDrinkTime, false);//设置纵坐标的个数
        }*/
        mBarChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return (int) value + "min";
            }
        });
        //去掉中间横线
        //mBarChart.getAxisLeft().setDrawGridLines(false);
        //不使用右侧Y轴
        mBarChart.getAxisRight().setEnabled(false);
    }

    private void initX() {
        String[] array = {"00:00", "", "", "", "04:00"
                , "", "", "", "08:00", "", ""
                , "", "12:00", "", "", "", "16:00"
                , "", "", "", "20:00", "", ""
                , "", "24:00"};

        xAxisList = new ArrayList<>(Arrays.asList(array));
        // Collections.addAll(xAxisList,array);
        IAxisValueFormatter iAxisValueFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xAxisList.get(((int) value));
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

        if (device.getStatus().equals("0")) {
            // 使用代码设置drawableleft
            Drawable drawable = getResources().getDrawable(
                    R.drawable.oval_online);
            // / 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            tvTitle.setCompoundDrawables(drawable, null, null, null);
        } else {
            // 使用代码设置drawableleft
            Drawable drawable = getResources().getDrawable(
                    R.drawable.oval_offline);
            // / 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            tvTitle.setCompoundDrawables(drawable, null, null, null);
        }

        rvWaterRecords.setLayoutManager(new LinearLayoutManager(WaterDevActivity.this));

        useRecordListAdapter = new UseRecordListAdapter(WaterDevActivity.this);
        rvWaterRecords.setAdapter(useRecordListAdapter);
        useRecordListAdapter.setClickListener(new UseRecordListAdapter.ClickListener() {
            @Override
            public void onClick() {
                Intent intent = new Intent(WaterDevActivity.this, WaterRecordsActivity.class);
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
        return R.layout.activity_water_dev;
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
                Intent intent = new Intent(WaterDevActivity.this, DevSettingActivity.class);
                intent.putExtra("device", device);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
