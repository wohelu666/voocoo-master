package com.voocoo.pet.modules.dev.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

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
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Device;
import com.voocoo.pet.entity.DeviceDetail;
import com.voocoo.pet.entity.FeedCompareData;
import com.voocoo.pet.entity.Restful.FeedData;
import com.voocoo.pet.entity.Restful.WaterData;
import com.voocoo.pet.http.HttpManage;

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

public class FeedDatasActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.drink_time_tag)
    ImageView ivDrinkTimeTag;
    @BindView(R.id.drink_time_value)
    TextView tvDrinkTimeValue;
    @BindView(R.id.tv_today)
    TextView tvToday;
    @BindView(R.id.tv_week_data)
    TextView tvWeekData;

    @BindView(R.id.stay_time_tag)
    ImageView ivStayTimeTag;
    @BindView(R.id.stay_time_value)
    TextView tvStayTimeValue;

    Device device;

    @BindView(R.id.barChart)
    BarChart mBarChart;
    @BindView(R.id.tv_out_food)
    TextView tvOutFood;

    @BindView(R.id.tv_day)
    TextView tvDay;
    @BindView(R.id.tv_week)
    TextView tvWeek;
    @BindView(R.id.tv_month)
    TextView tvMonth;
    @BindView(R.id.tv_year)
    TextView tvYear;

    @BindView(R.id.tv_out_food_title)
    TextView tvOutFoodTitle;

    BarDataSet dataSet = null;
    BarData barData = null;
    List<BarEntry> mutableList = new ArrayList<>();
    List<String> xAxisList = new ArrayList<>();
    int allOutFood = 0;

    int dataType = 0;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public List<FeedData.FeedDataDetail> feedOfWeekList;
    public List<FeedData.FeedDataDetail> feedOfMonthList;
    public List<FeedData.FeedDataDetail> feedOfYearList;

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
                tvOutFoodTitle.setText(getString(R.string.text_today_eat_food));
                break;
            case R.id.tv_week:
                selDataType(1);
                getWaterDrinkData();
                tvOutFoodTitle.setText(getString(R.string.text_week_eat_food));
                break;
            case R.id.tv_month:
                selDataType(2);
                getWaterDrinkData();
                tvOutFoodTitle.setText(getString(R.string.text_month_eat_food));
                break;
            case R.id.tv_year:
                selDataType(3);
                getWaterDrinkData();
                tvOutFoodTitle.setText(getString(R.string.text_year_eat_food));
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
            // mBarChart.getAxisLeft().setAxisMaximum(15f);
        } else {
            mBarChart.getAxisLeft().setAxisMinimum(0f);
            // mBarChart.getAxisLeft().setAxisMaximum(4);
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
                return (int) value + "g";
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
                    if (((int) value) >= feedOfMonthList.size()) {
                        return "";
                    }

                    String date = feedOfMonthList.get(((int) value)).date;
                    String[] dateArray = date.split("-");
                    if ((int) Integer.valueOf(dateArray[2]) == 1 || (int) Integer.valueOf(dateArray[2]) == 8
                            || (int) Integer.valueOf(dateArray[2]) == 15 || (int) Integer.valueOf(dateArray[2]) == 22
                            || (int) Integer.valueOf(dateArray[2]) == 29 || (int) Integer.valueOf(dateArray[2]) == 31) {
                        return Integer.valueOf(dateArray[2]) + "日";
                    }
                    return "";
                } else if (dataType == 3) {
                    if (((int) value) >= feedOfYearList.size()) {
                        return "";
                    }
                    String date = feedOfYearList.get(((int) value)).date;
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
            mBarChart.getXAxis().setLabelCount(feedOfWeekList.size(), false);
        } else if (dataType == 2) {
            mBarChart.getXAxis().setLabelCount(feedOfMonthList.size(), false);
        } else if (dataType == 3) {
            mBarChart.getXAxis().setLabelCount(feedOfYearList.size(), false);
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
        HttpManage.getInstance().getFeedCompareData(device.getDeviceId(), new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {

            }

            @Override
            public void onSuccess(int code, String response) {
                LogUtil.d("getFeedCompareData->" + response);
                BaseEntity<FeedCompareData> result = new Gson().fromJson(response, new TypeToken<BaseEntity<FeedCompareData>>() {
                }.getType());

                if (result.getCode() == 200) {
                    if (result.getData().eatingAmount - result.getData().eatingAmountYesterday > 0) {
                        ivDrinkTimeTag.setImageResource(R.mipmap.icon_up);
                        tvDrinkTimeValue.setText("+"
                                + result.getData().eatingCompareYesterday + "g");
                    } else if (result.getData().eatingAmount - result.getData().eatingAmountYesterday < 0) {
                        ivDrinkTimeTag.setImageResource(R.mipmap.icon_down);
                        tvDrinkTimeValue.setText(result.getData().eatingCompareYesterday + "g");
                    } else {
                        ivDrinkTimeTag.setVisibility(View.GONE);
                        tvDrinkTimeValue.setText("--");
                    }

                    if (result.getData().eatingAmountWeek - result.getData().eatingAmountLastWeek > 0) {
                        ivStayTimeTag.setImageResource(R.mipmap.icon_up);
                        tvStayTimeValue.setText("+"
                                + result.getData().eatingCompareWeek + "g");
                    } else if (result.getData().eatingAmountWeek - result.getData().eatingAmountLastWeek < 0) {
                        ivStayTimeTag.setImageResource(R.mipmap.icon_down);
                        tvStayTimeValue.setText(result.getData().eatingCompareWeek + "g");
                    } else {
                        ivStayTimeTag.setVisibility(View.GONE);
                        tvStayTimeValue.setText("--");
                    }

                    tvToday.setText(result.getData().eatingAmount + "");
                    tvWeekData.setText(result.getData().eatingAmountWeek + "");
                }
            }
        });
    }


    private void getWaterDrinkData() {
        showLoading();
        HttpManage.getInstance().feederData(device.getDeviceId(), dataType, new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                dismissLoading();
            }

            @Override
            public void onSuccess(int code, String response) {
                dismissLoading();
                LogUtil.d("feederData->" + response);
                BaseEntity<FeedData> result = new Gson().fromJson(response, new TypeToken<BaseEntity<FeedData>>() {
                }.getType());
                if (dataType == 0) {
                    allOutFood = 0;
                    Map<Integer, Integer> inDatas = new HashMap<>();
                    Map<Integer, Integer> outDatas = new HashMap<>();
                    for (int i = 0; i < 25; i++) {
                        inDatas.put(i, 0);
                        outDatas.put(i, 0);
                    }
                    for (int i = 0; i < result.getData().feedingOfDayList.size(); i++) {
                        allOutFood += result.getData().feedingOfDayList.get(i).feedingAmount;
                        try {
                            Date date = simpleDateFormat.parse(result.getData().feedingOfDayList.get(i).date);//开始时间
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            if (inDatas.containsKey(calendar.get(Calendar.HOUR_OF_DAY))) {
                                int count = inDatas.get(calendar.get(Calendar.HOUR_OF_DAY));
                                count += result.getData().feedingOfDayList.get(i).eatingAmount;
                                inDatas.put(calendar.get(Calendar.HOUR_OF_DAY), count);
                            } else {
                                inDatas.put(calendar.get(Calendar.HOUR_OF_DAY), result.getData().feedingOfDayList.get(i).eatingAmount);
                            }

                            if (outDatas.containsKey(calendar.get(Calendar.HOUR_OF_DAY))) {
                                int count = outDatas.get(calendar.get(Calendar.HOUR_OF_DAY));
                                count += result.getData().feedingOfDayList.get(i).feedingAmount;
                                outDatas.put(calendar.get(Calendar.HOUR_OF_DAY), count);
                            } else {
                                outDatas.put(calendar.get(Calendar.HOUR_OF_DAY), result.getData().feedingOfDayList.get(i).feedingAmount);
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
                    dataSet.setColors(new int[]{R.color.color_tab_select2, R.color.color_tab_select}, FeedDatasActivity.this);

                    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                    dataSets.add(dataSet);
                    barData = new BarData(dataSets);
                    dataSet.setValueTextSize(0);
                    mBarChart.setData(barData);

                    if (dataType == 0) {
                        mBarChart.getXAxis().setLabelCount(xAxisList.size(), false);
                    } else if (dataType == 1) {
                        mBarChart.getXAxis().setLabelCount(feedOfWeekList.size(), false);
                    } else if (dataType == 2) {
                        mBarChart.getXAxis().setLabelCount(feedOfMonthList.size(), false);
                    } else if (dataType == 3) {
                        mBarChart.getXAxis().setLabelCount(feedOfYearList.size(), false);
                    }
                    mBarChart.invalidate();

                    tvOutFood.setText(allOutFood + "");
                } else if (dataType == 1) {
                    allOutFood = 0;
                    Map<Integer, Integer> inDatas = new HashMap<>();
                    Map<Integer, Integer> outDatas = new HashMap<>();
                    feedOfWeekList = result.getData().feedingOfWeekList;
                    for (int i = 0; i < result.getData().feedingOfWeekList.size(); i++) {
                        allOutFood += result.getData().feedingOfWeekList.get(i).feedingAmount;
                        if (inDatas.containsKey(i)) {
                            int count = inDatas.get(i);
                            count += result.getData().feedingOfWeekList.get(i).eatingAmount;
                            inDatas.put(i, count);
                        } else {
                            inDatas.put(i, result.getData().feedingOfWeekList.get(i).eatingAmount);
                        }
                    }
                    for (int i = 0; i < result.getData().feedingOfWeekList.size(); i++) {
                        if (outDatas.containsKey(i)) {
                            int count = outDatas.get(i);
                            count += result.getData().feedingOfWeekList.get(i).feedingAmount;
                            outDatas.put(i, count);
                        } else {
                            outDatas.put(i, result.getData().feedingOfWeekList.get(i).feedingAmount);
                        }
                    }

                    mutableList = new ArrayList<>();
                    for (Integer key : inDatas.keySet()) {
                        BarEntry barEntry = new BarEntry(key, new float[]{(float) inDatas.get(key), (float) outDatas.get(key)});
                        mutableList.add(barEntry);
                    }
                    dataSet = new BarDataSet(mutableList, "");
                    dataSet.setColors(new int[]{R.color.color_tab_select2, R.color.color_tab_select}, FeedDatasActivity.this);

                    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                    dataSets.add(dataSet);
                    barData = new BarData(dataSets);
                    dataSet.setValueTextSize(0);
                    mBarChart.setData(barData);

                    if (dataType == 0) {
                        mBarChart.getXAxis().setLabelCount(xAxisList.size(), false);
                    } else if (dataType == 1) {
                        mBarChart.getXAxis().setLabelCount(feedOfWeekList.size(), false);
                    } else if (dataType == 2) {
                        mBarChart.getXAxis().setLabelCount(feedOfMonthList.size(), false);
                    } else if (dataType == 3) {
                        mBarChart.getXAxis().setLabelCount(feedOfYearList.size(), false);
                    }
                    mBarChart.invalidate();
                    tvOutFood.setText(allOutFood + "");
                } else if (dataType == 2) {
                    allOutFood = 0;
                    Map<Integer, Integer> inDatas = new HashMap<>();
                    Map<Integer, Integer> outDatas = new HashMap<>();
                    feedOfMonthList = result.getData().feedingOfMonthList;
                    for (int i = 0; i < result.getData().feedingOfMonthList.size(); i++) {
                        allOutFood += result.getData().feedingOfMonthList.get(i).feedingAmount;
                        if (inDatas.containsKey(i)) {
                            int count = inDatas.get(i);
                            count += result.getData().feedingOfMonthList.get(i).eatingAmount;
                            inDatas.put(i, count);
                        } else {
                            inDatas.put(i, result.getData().feedingOfMonthList.get(i).eatingAmount);
                        }
                    }
                    for (int i = 0; i < result.getData().feedingOfMonthList.size(); i++) {
                        if (outDatas.containsKey(i)) {
                            int count = outDatas.get(i);
                            count += result.getData().feedingOfMonthList.get(i).feedingAmount;
                            outDatas.put(i, count);
                        } else {
                            outDatas.put(i, result.getData().feedingOfMonthList.get(i).feedingAmount);
                        }
                    }
                    mutableList = new ArrayList<>();
                    for (Integer key : inDatas.keySet()) {
                        BarEntry barEntry = new BarEntry(key, new float[]{(float) inDatas.get(key), (float) outDatas.get(key)});
                        mutableList.add(barEntry);
                    }
                    dataSet = new BarDataSet(mutableList, "");
                    dataSet.setColors(new int[]{R.color.color_tab_select2, R.color.color_tab_select}, FeedDatasActivity.this);

                    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                    dataSets.add(dataSet);
                    barData = new BarData(dataSets);
                    dataSet.setValueTextSize(0);
                    mBarChart.setData(barData);

                    if (dataType == 0) {
                        mBarChart.getXAxis().setLabelCount(xAxisList.size(), false);
                    } else if (dataType == 1) {
                        mBarChart.getXAxis().setLabelCount(feedOfWeekList.size(), false);
                    } else if (dataType == 2) {
                        mBarChart.getXAxis().setLabelCount(feedOfMonthList.size(), false);
                    } else if (dataType == 3) {
                        mBarChart.getXAxis().setLabelCount(feedOfYearList.size(), false);
                    }
                    mBarChart.invalidate();
                    tvOutFood.setText(allOutFood + "");
                } else {
                    allOutFood = 0;
                    Map<Integer, Integer> inDatas = new HashMap<>();
                    Map<Integer, Integer> outDatas = new HashMap<>();
                    feedOfYearList = result.getData().feedingOfYearList;
                    for (int i = 0; i < result.getData().feedingOfYearList.size(); i++) {
                        allOutFood += result.getData().feedingOfYearList.get(i).feedingAmount;
                        if (inDatas.containsKey(i)) {
                            int count = inDatas.get(i);
                            count += result.getData().feedingOfYearList.get(i).eatingAmount;
                            inDatas.put(i, count);
                        } else {
                            inDatas.put(i, result.getData().feedingOfYearList.get(i).eatingAmount);
                        }
                    }
                    for (int i = 0; i < result.getData().feedingOfYearList.size(); i++) {
                        if (outDatas.containsKey(i)) {
                            int count = outDatas.get(i);
                            count += result.getData().feedingOfYearList.get(i).feedingAmount;
                            outDatas.put(i, count);
                        } else {
                            outDatas.put(i, result.getData().feedingOfYearList.get(i).feedingAmount);
                        }
                    }
                    mutableList = new ArrayList<>();
                    for (Integer key : inDatas.keySet()) {
                        BarEntry barEntry = new BarEntry(key, new float[]{(float) inDatas.get(key), (float) outDatas.get(key)});
                        mutableList.add(barEntry);
                    }
                    dataSet = new BarDataSet(mutableList, "");
                    dataSet.setColors(new int[]{R.color.color_tab_select2, R.color.color_tab_select}, FeedDatasActivity.this);

                    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                    dataSets.add(dataSet);
                    barData = new BarData(dataSets);
                    dataSet.setValueTextSize(0);
                    mBarChart.setData(barData);

                    if (dataType == 0) {
                        mBarChart.getXAxis().setLabelCount(xAxisList.size(), false);
                    } else if (dataType == 1) {
                        mBarChart.getXAxis().setLabelCount(feedOfWeekList.size(), false);
                    } else if (dataType == 2) {
                        mBarChart.getXAxis().setLabelCount(feedOfMonthList.size(), false);
                    } else if (dataType == 3) {
                        mBarChart.getXAxis().setLabelCount(feedOfYearList.size(), false);
                    }
                    mBarChart.invalidate();
                    tvOutFood.setText(allOutFood + "");
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
        tvTitle.setText(getString(R.string.text_feed_datas));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feed_datas;
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
