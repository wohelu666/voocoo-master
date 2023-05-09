package com.voocoo.pet.common.event;

import com.voocoo.pet.entity.FeedPlan;

public class UpdateFeedPlanFinishEvent {
    public FeedPlan.FeedPlanDiets feedPlanDiets;

    public UpdateFeedPlanFinishEvent(FeedPlan.FeedPlanDiets feedPlanDiets) {
        this.feedPlanDiets = feedPlanDiets;
    }
}
