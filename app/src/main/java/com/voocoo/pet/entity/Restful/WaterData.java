package com.voocoo.pet.entity.Restful;

import java.io.Serializable;
import java.util.List;

public class WaterData implements Serializable {

    public List<WaterDataDetail> drinkingOfDayList;
    public List<WaterDataDetail> drinkingOfWeekList;
    public List<WaterDataDetail> drinkingOfMonthList;
    public List<WaterDataDetail> drinkingOfYearList;

    public class WaterDataDetail{
        public String date;
        public int drinkingcounts;
        public int drinkingSeconds;
    }
}
