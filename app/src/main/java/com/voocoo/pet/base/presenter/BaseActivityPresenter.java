package com.voocoo.pet.base.presenter;


import androidx.annotation.StringRes;

import com.voocoo.pet.base.activity.AbsBaseActivity;


/**
 * activity基础 Presenter
 */
public class BaseActivityPresenter<V extends AbsBaseActivity> extends BasePresenter<V> {
    public BaseActivityPresenter(V activity) {
        super(activity);
    }

    protected String getString(@StringRes int stringRes) {
        return getView().getString(stringRes);
    }

    protected String getString(@StringRes int stringRes, Object... formatArgs) {
        return getView().getString(stringRes, formatArgs);
    }
}
