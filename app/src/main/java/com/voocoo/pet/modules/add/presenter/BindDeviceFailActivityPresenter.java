package com.voocoo.pet.modules.add.presenter;

import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.modules.add.activity.BindDeviceFailActivity;
import com.voocoo.pet.modules.add.contract.BindDeviceFailActivityContract;

public class BindDeviceFailActivityPresenter extends BaseActivityPresenter<BindDeviceFailActivity> implements BindDeviceFailActivityContract.Presenter {
    public BindDeviceFailActivityPresenter(BindDeviceFailActivity activity) {
        super(activity);
    }
}
