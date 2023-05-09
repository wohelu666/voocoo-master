package com.voocoo.pet.common.event;

import com.voocoo.pet.entity.FeedPlan;

public class DelFeedPlanFinishEvent {
    public FeedPlan.FeedPlanDiets feedPlanDiets;

    public DelFeedPlanFinishEvent(FeedPlan.FeedPlanDiets feedPlanDiets) {
        this.feedPlanDiets = feedPlanDiets;
    }
}
