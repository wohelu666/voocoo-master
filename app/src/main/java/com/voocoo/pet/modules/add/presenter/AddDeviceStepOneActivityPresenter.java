package com.voocoo.pet.modules.add.presenter;

import android.content.Intent;

import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.modules.add.activity.AddDeviceStepOneActivity;
import com.voocoo.pet.modules.add.activity.AddDeviceStepTwoActivity;
import com.voocoo.pet.modules.add.contract.AddDeviceStepOneActivityContract;

public class AddDeviceStepOneActivityPresenter extends BaseActivityPresenter<AddDeviceStepOneActivity> implements AddDeviceStepOneActivityContract.Presenter {

    public AddDeviceStepOneActivityPresenter(AddDeviceStepOneActivity activity) {
        super(activity);
    }

    @Override
    public void next() {
        Intent intent=new Intent(getContext(), AddDeviceStepTwoActivity.class);
        getContext().startActivity(intent);
    }
}
