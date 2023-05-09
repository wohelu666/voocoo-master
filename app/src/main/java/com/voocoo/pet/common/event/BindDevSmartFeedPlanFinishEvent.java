package com.voocoo.pet.common.event;

import com.voocoo.pet.entity.FeedPlan;

import java.util.List;

public class BindDevSmartFeedPlanFinishEvent {
    public List<FeedPlan.FeedPlanDiets> feedPlanDiets;

    public BindDevSmartFeedPlanFinishEvent(List<FeedPlan.FeedPlanDiets> feedPlanDiets) {
        this.feedPlanDiets = feedPlanDiets;
    }
}
