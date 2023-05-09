package com.voocoo.pet.modules.pet;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.voocoo.pet.R;
import com.voocoo.pet.base.fragment.AbsBaseFragment;
import com.voocoo.pet.base.presenter.BaseFragmentPresenter;
import com.voocoo.pet.common.event.AddPetControlEvent;
import com.voocoo.pet.common.utils.LogUtil;
import com.zkk.view.rulerview.RulerView;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;

public class AddPetStepThreeFragment extends AbsBaseFragment {

    @BindView(R.id.wv_year)
    WheelView wvYear;
    @BindView(R.id.wv_month)
    WheelView wvMonth;
    @BindView(R.id.wv_date)
    WheelView wvDate;

    @BindView(R.id.tv_days)
    TextView tvDays;

    @BindView(R.id.ruler_weight)
    RulerView ruler_weight;
    @BindView(R.id.tv_weight)
    TextView tvWeight;

    float weight = 0;
    int age = 0;

    boolean isControl1;
    boolean isControl2;

    public float getWeight() {
        return weight;
    }

    public int getAge() {
        return age;
    }

    public String getAgeStr() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//年-月-日 时-分
        int year = wvYear.getCurrentItem() + 1949;
        int month = wvMonth.getCurrentItem() + 1;
        int date = wvDate.getCurrentItem() + 1;

        Calendar sel = Calendar.getInstance();
        sel.set(Calendar.YEAR, year);
        sel.set(Calendar.MONTH, month - 1);
        sel.set(Calendar.DATE, date);
        return dateFormat.format(sel.getTime());
    }
    @Override
    protected int initLayoutID() {
        return R.layout.fragment_add_pet_step_three;
    }

    public static AddPetStepThreeFragment newInstance() {
        return new AddPetStepThreeFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {

        ArrayList<String> years = new ArrayList<>();

        for (int i = 1949; i < Calendar.getInstance().get(Calendar.YEAR) + 1; i++) {
            years.add(i + "");
        }

        wvYear.setAdapter(new ArrayWheelAdapter<>(years));
        wvYear.setCyclic(false);
        wvYear.setTextSize(15);
        wvYear.setCurrentItem(Calendar.getInstance().get(Calendar.YEAR) - 1949);
        wvYear.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                setDayText();
                isControl1=true;
                EventBus.getDefault().post(new AddPetControlEvent());
            }
        });

        ArrayList<String> month = new ArrayList<>();

        for (int i = 1; i < 13; i++) {
            month.add(i + "");
        }

        wvMonth.setAdapter(new ArrayWheelAdapter<>(month));
        wvMonth.setCyclic(false);
        wvMonth.setTextSize(15);
        wvMonth.setCurrentItem(Calendar.getInstance().get(Calendar.MONTH));
        wvMonth.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                setDayText();
                isControl1=true;
                EventBus.getDefault().post(new AddPetControlEvent());
            }
        });

        ArrayList<String> days = new ArrayList<>();

        for (int i = 1; i < 31; i++) {
            days.add(i + "");
        }

        wvDate.setAdapter(new ArrayWheelAdapter<>(days));
        wvDate.setCyclic(false);
        wvDate.setTextSize(15);
        wvDate.setCurrentItem(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - 1);
        wvDate.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                setDayText();
                isControl1=true;
                EventBus.getDefault().post(new AddPetControlEvent());
            }
        });

        setDayText();

        tvWeight.setText(0 + "");
        ruler_weight.setOnValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                tvWeight.setText(value + "");
                weight=value;
                isControl2=true;
                EventBus.getDefault().post(new AddPetControlEvent());
            }
        });
        ruler_weight.setValue(0, 0, 100, (float) 0.1);
    }


    public boolean isControl1() {
        return isControl1;
    }

    public boolean isControl2() {
        return isControl2;
    }

    /**
     * 获取两个日期之间的间隔天数
     *
     * @return
     */
    public static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    private String getFormatAge(int day) {
        int year = day / 365;
        int month = (day - year * 365) / 30;
        int date = day - ((year * 365) + (month * 30));
        String content = "";
        if (year > 0) {
            content += year + "岁";
        }
        if (month > 0) {
            content += month + "月";
        }
        if (date > 0) {
            content += date + "天";
        }
        return content;
    }

    public void setDayText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//年-月-日 时-分
        int year = wvYear.getCurrentItem() + 1949;
        int month = wvMonth.getCurrentItem() + 1;
        int date = wvDate.getCurrentItem() + 1;

        Calendar sel = Calendar.getInstance();
        sel.set(Calendar.YEAR, year);
        sel.set(Calendar.MONTH, month - 1);
        sel.set(Calendar.DATE, date);
        LogUtil.d(dateFormat.format(sel.getTime()));

        age = getGapCount(sel.getTime(), Calendar.getInstance().getTime());
        tvDays.setText(getFormatAge(age));
    }

    @Override
    protected BaseFragmentPresenter createPresenter() {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
