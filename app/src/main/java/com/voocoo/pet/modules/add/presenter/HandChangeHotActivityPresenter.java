package com.voocoo.pet.modules.add.presenter;

import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.modules.add.activity.HandChangeHotActivity;
import com.voocoo.pet.modules.add.contract.HandChangeHotActivityContract;

public class HandChangeHotActivityPresenter extends BaseActivityPresenter<HandChangeHotActivity> implements HandChangeHotActivityContract.Presenter {
    public HandChangeHotActivityPresenter(HandChangeHotActivity activity) {
        super(activity);
    }
}
