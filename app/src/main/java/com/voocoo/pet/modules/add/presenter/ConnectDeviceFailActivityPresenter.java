package com.voocoo.pet.modules.add.presenter;

import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.modules.add.activity.ConnectDeviceFailActivity;
import com.voocoo.pet.modules.add.contract.ConnectDeviceFailActivityContract;

public class ConnectDeviceFailActivityPresenter extends BaseActivityPresenter<ConnectDeviceFailActivity> implements ConnectDeviceFailActivityContract.Presenter {
    public ConnectDeviceFailActivityPresenter(ConnectDeviceFailActivity activity) {
        super(activity);
    }
}
