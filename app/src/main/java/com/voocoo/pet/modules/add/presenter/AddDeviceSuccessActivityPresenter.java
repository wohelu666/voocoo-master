package com.voocoo.pet.modules.add.presenter;

import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.modules.add.activity.AddDeviceSuccessActivity;
import com.voocoo.pet.modules.add.contract.AddDeviceSuccessActivityContract;

public class AddDeviceSuccessActivityPresenter extends BaseActivityPresenter<AddDeviceSuccessActivity> implements AddDeviceSuccessActivityContract.Presenter {
    public AddDeviceSuccessActivityPresenter(AddDeviceSuccessActivity activity) {
        super(activity);
    }
}
