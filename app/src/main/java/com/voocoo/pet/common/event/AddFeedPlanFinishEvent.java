package com.voocoo.pet.common.event;

import com.voocoo.pet.entity.FeedPlan;

public class  AddFeedPlanFinishEvent {
    public FeedPlan.FeedPlanDiets feedPlanDiets;

    public AddFeedPlanFinishEvent(FeedPlan.FeedPlanDiets feedPlanDiets) {
        this.feedPlanDiets = feedPlanDiets;
    }
}
