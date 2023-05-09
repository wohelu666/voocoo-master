package com.voocoo.pet.modules.dev.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Device;
import com.voocoo.pet.entity.DeviceDetail;
import com.voocoo.pet.entity.Restful.WaterData;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.dev.adapter.UseRecordListAdapter;

import org.apache.http.Header;

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

public class WaterDatasActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.tv_tag)
    TextView tvTag;

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

    Device device;

    @BindView(R.id.barChart)
    BarChart mBarChart;
    @BindView(R.id.tv_drink_time)
    TextView tvDrinkTime;

    @BindView(R.id.tv_day)
    TextView tvDay;
    @BindView(R.id.tv_week)
    TextView tvWeek;
    @BindView(R.id.tv_month)
    TextView tvMonth;
    @BindView(R.id.tv_year)
    TextView tvYear;

    BarDataSet dataSet = null;
    BarData barData = null;
    List<BarEntry> mutableList = new ArrayList<>();
    List<String> xAxisList = new ArrayList<>();
    int allDrinkTime = 0;

    int dataType = 0;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public List<WaterData.WaterDataDetail> drinkingOfWeekList;
    public List<WaterData.WaterDataDetail> drinkingOfMonthList;
    public List<WaterData.WaterDataDetail> drinkingOfYearList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        device = (Device) getIntent().getSerializableExtra("device");
        initView();
        initData();
        getWaterDrinkData();
        initBarChartView();
        selDataType(0);
    }

    @OnClick({R.id.tv_day, R.id.tv_week, R.id.tv_month, R.id.tv_year})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_day:
                selDataType(0);
                getWaterDrinkData();
                tvTag.setText(getString(R.string.text_cat_today_drink));
                break;
            case R.id.tv_week:
                selDataType(1);
                getWaterDrinkData();
                tvTag.setText(getString(R.string.text_cat_week_drink));
                break;
            case R.id.tv_month:
                selDataType(2);
                getWaterDrinkData();
                tvTag.setText(getString(R.string.text_cat_month_drink));
                break;
            case R.id.tv_year:
                selDataType(3);
                getWaterDrinkData();
                tvTag.setText(getString(R.string.text_cat_year_drink));
                break;
        }
    }


    private void initDataType() {
        tvDay.setBackground(null);
        tvWeek.setBackground(null);
        tvMonth.setBackground(null);
        tvYear.setBackground(null);
        tvDay.setTextColor(Color.parseColor("#6A6B71"));
        tvWeek.setTextColor(Color.parseColor("#6A6B71"));
        tvMonth.setTextColor(Color.parseColor("#6A6B71"));
        tvYear.setTextColor(Color.parseColor("#6A6B71"));
    }

    private void selDataType(int type) {
        dataType = type;
        initDataType();
        if (type == 0) {
            tvDay.setBackgroundResource(R.drawable.bg_sel_data_type);
            tvDay.setTextColor(Color.parseColor("#ffffff"));
        } else if (type == 1) {
            tvWeek.setBackgroundResource(R.drawable.bg_sel_data_type);
            tvWeek.setTextColor(Color.parseColor("#ffffff"));
        } else if (type == 2) {
            tvMonth.setBackgroundResource(R.drawable.bg_sel_data_type);
            tvMonth.setTextColor(Color.parseColor("#ffffff"));
        } else if (type == 3) {
            tvYear.setBackgroundResource(R.drawable.bg_sel_data_type);
            tvYear.setTextColor(Color.parseColor("#ffffff"));
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

        if (dataType != 0) {
            mBarChart.getAxisLeft().setAxisMinimum(0f);
            mBarChart.getAxisLeft().setAxisMaximum(15f);
        } else {
            mBarChart.getAxisLeft().setAxisMinimum(0f);
            mBarChart.getAxisLeft().setAxisMaximum(4);
        }
        if (dataType == 0) {
            /*if (allDrinkTime >= 5) {
                mBarChart.getAxisLeft().setLabelCount(3, true);//设置纵坐标的个数
            } else {
                mBarChart.getAxisLeft().setLabelCount(allDrinkTime, true);//设置纵坐标的个数
            }*/
            mBarChart.getAxisLeft().setLabelCount(4, true);//设置纵坐标的个数
        } else {
            mBarChart.getAxisLeft().setLabelCount(4, true);//设置纵坐标的个数
        }
        mBarChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (dataType == 0) {
                    return (int) value + "min";
                } else {
                    return (int) value + "次";
                }
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
                if (dataType == 0) {
                    if (((int) value) >= xAxisList.size()) {
                        return "";
                    }
                    return xAxisList.get(((int) value));
                } else if (dataType == 1) {
                   /* if (((int) value) >= drinkingOfWeekList.size()) {
                        return "";
                    }
                    String date = drinkingOfWeekList.get(((int) value)).date;
                    String[] dateArray = date.split("-");
                    return dateArray[1] + "-" + dateArray[2];*/
                    if (value == 0) {
                        return getString(R.string.text_day1);
                    } else if (value == 1) {
                        return getString(R.string.text_day2);
                    } else if (value == 2) {
                        return getString(R.string.text_day3);
                    } else if (value == 3) {
                        return getString(R.string.text_day4);
                    } else if (value == 4) {
                        return getString(R.string.text_day5);
                    } else if (value == 5) {
                        return getString(R.string.text_day6);
                    } else {
                        return getString(R.string.text_day7);
                    }
                } else if (dataType == 2) {
                    if (((int) value) >= drinkingOfMonthList.size()) {
                        return "";
                    }

                    String date = drinkingOfMonthList.get(((int) value)).date;
                    String[] dateArray = date.split("-");
                    if ((int) Integer.valueOf(dateArray[2]) == 1 || (int) Integer.valueOf(dateArray[2]) == 8
                            || (int) Integer.valueOf(dateArray[2]) == 15 || (int) Integer.valueOf(dateArray[2]) == 22
                            || (int) Integer.valueOf(dateArray[2]) == 29 || (int) Integer.valueOf(dateArray[2]) == 31) {
                        return Integer.valueOf(dateArray[2]) + "日";
                    }
                    return "";
                } else if (dataType == 3) {
                    if (((int) value) >= drinkingOfYearList.size()) {
                        return "";
                    }
                    String date = drinkingOfYearList.get(((int) value)).date;
                    String[] dateArray = date.split("-");
                    return Integer.valueOf(dateArray[1]) + "";
                } else {
                    return "";
                }
            }
        };
        mBarChart.getXAxis().setValueFormatter(iAxisValueFormatter);

        //默认显示在顶端，这是设置到底部，符合我们正常视觉
        mBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        //去掉底部图例BarChatView 的提示，大家可以根据自己业务需求，对
        //Legend进行定制
        mBarChart.setLogEnabled(false);
        //xAxisList的长度要和list的长度一直，否则会数组越界
        if (dataType == 0) {
            mBarChart.getXAxis().setLabelCount(xAxisList.size(), false);
        } else if (dataType == 1) {
            mBarChart.getXAxis().setLabelCount(drinkingOfWeekList.size(), false);
        } else if (dataType == 2) {
            mBarChart.getXAxis().setLabelCount(drinkingOfMonthList.size(), false);
        } else if (dataType == 3) {
            mBarChart.getXAxis().setLabelCount(drinkingOfYearList.size(), false);
        }
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


    private void initData() {
        HttpManage.getInstance().devDetail(device.getDeviceId(), new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {

            }

            @Override
            public void onSuccess(int code, String response) {
                LogUtil.d("devDetail->" + response);
                BaseEntity<DeviceDetail> result = new Gson().fromJson(response, new TypeToken<BaseEntity<DeviceDetail>>() {
                }.getType());

                if (result.getCode() == 200) {
                    tvTime.setText(result.getData().getDrinkingTimes() + "次");

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
                    tvMin.setText(stayTime);
                    /*if (stayTime.contains("分")) {
                        tvMin.setText(stayTime.substring(0, stayTime.indexOf("分")));
                        tvSecond.setText(stayTime.substring(stayTime.indexOf("分"), stayTime.indexOf("秒")));
                    } else {
                        tvSecond.setText(stayTime.substring(0, stayTime.indexOf("秒")));
                        tvMin.setVisibility(View.GONE);
                        tvMinUnit.setVisibility(View.GONE);
                    }*/
                }
            }
        });
    }


    private void getWaterDrinkData() {
        showLoading();
        HttpManage.getInstance().waterDrinkData(device.getDeviceId(), dataType, new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                dismissLoading();
            }

            @Override
            public void onSuccess(int code, String response) {
                dismissLoading();
                LogUtil.d("waterDrinkData->" + response);
                BaseEntity<WaterData> result = new Gson().fromJson(response, new TypeToken<BaseEntity<WaterData>>() {
                }.getType());
                if (dataType == 0) {
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
                    if (dataType == 0) {
                        mBarChart.getXAxis().setLabelCount(xAxisList.size(), false);
                    } else if (dataType == 1) {
                        mBarChart.getXAxis().setLabelCount(drinkingOfWeekList.size(), false);
                    }
                    mBarChart.invalidate();
                } else if (dataType == 1) {
                    Map<Integer, Integer> drinkDatas = new HashMap<>();
                    allDrinkTime = 0;
                    drinkingOfWeekList = result.getData().drinkingOfWeekList;
                    for (int i = 0; i < result.getData().drinkingOfWeekList.size(); i++) {
                        allDrinkTime += result.getData().drinkingOfWeekList.get(i).drinkingcounts;
                        if (drinkDatas.containsKey(i)) {
                            int count = drinkDatas.get(i);
                            count += result.getData().drinkingOfWeekList.get(i).drinkingcounts;
                            drinkDatas.put(i, count);
                        } else {
                            drinkDatas.put(i, result.getData().drinkingOfWeekList.get(i).drinkingcounts);
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

                    if (dataType == 0) {
                        mBarChart.getXAxis().setLabelCount(xAxisList.size(), false);
                    } else if (dataType == 1) {
                        mBarChart.getXAxis().setLabelCount(drinkingOfWeekList.size(), false);
                    } else if (dataType == 2) {
                        mBarChart.getXAxis().setLabelCount(drinkingOfMonthList.size(), false);
                    } else if (dataType == 3) {
                        mBarChart.getXAxis().setLabelCount(drinkingOfYearList.size(), false);
                    }
                    mBarChart.invalidate();
                } else if (dataType == 2) {
                    Map<Integer, Integer> drinkDatas = new HashMap<>();
                    allDrinkTime = 0;
                    drinkingOfMonthList = result.getData().drinkingOfMonthList;
                    for (int i = 0; i < result.getData().drinkingOfMonthList.size(); i++) {
                        allDrinkTime += result.getData().drinkingOfMonthList.get(i).drinkingcounts;
                        if (drinkDatas.containsKey(i)) {
                            int count = drinkDatas.get(i);
                            count += result.getData().drinkingOfMonthList.get(i).drinkingcounts;
                            drinkDatas.put(i, count);
                        } else {
                            drinkDatas.put(i, result.getData().drinkingOfMonthList.get(i).drinkingcounts);
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

                    if (dataType == 0) {
                        mBarChart.getXAxis().setLabelCount(xAxisList.size(), false);
                    } else if (dataType == 1) {
                        mBarChart.getXAxis().setLabelCount(drinkingOfWeekList.size(), false);
                    } else if (dataType == 2) {
                        mBarChart.getXAxis().setLabelCount(drinkingOfMonthList.size(), false);
                    } else if (dataType == 3) {
                        mBarChart.getXAxis().setLabelCount(drinkingOfYearList.size(), false);
                    }
                    mBarChart.invalidate();
                } else {
                    Map<Integer, Integer> drinkDatas = new HashMap<>();
                    allDrinkTime = 0;
                    drinkingOfYearList = result.getData().drinkingOfYearList;
                    for (int i = 0; i < result.getData().drinkingOfYearList.size(); i++) {
                        allDrinkTime += result.getData().drinkingOfYearList.get(i).drinkingcounts;
                        if (drinkDatas.containsKey(i)) {
                            int count = drinkDatas.get(i);
                            count += result.getData().drinkingOfYearList.get(i).drinkingcounts;
                            drinkDatas.put(i, count);
                        } else {
                            drinkDatas.put(i, result.getData().drinkingOfYearList.get(i).drinkingcounts);
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

                    if (dataType == 0) {
                        mBarChart.getXAxis().setLabelCount(xAxisList.size(), false);
                    } else if (dataType == 1) {
                        mBarChart.getXAxis().setLabelCount(drinkingOfWeekList.size(), false);
                    } else if (dataType == 2) {
                        mBarChart.getXAxis().setLabelCount(drinkingOfMonthList.size(), false);
                    } else if (dataType == 3) {
                        mBarChart.getXAxis().setLabelCount(drinkingOfYearList.size(), false);
                    }
                    mBarChart.invalidate();
                }
                removeY();
            }
        });
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_black_ic);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText(getString(R.string.text_water_datas));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_water_datas;
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
