package com.voocoo.pet.entity.Restful;

import java.io.Serializable;
import java.util.List;

public class FeedData implements Serializable {

    public List<FeedDataDetail> feedingOfDayList;
    public List<FeedDataDetail> feedingOfWeekList;
    public List<FeedDataDetail> feedingOfMonthList;
    public List<FeedDataDetail> feedingOfYearList;

    public class FeedDataDetail {
        public String date;
        public int eatingAmount;
        public int feedingAmount;
    }
}
