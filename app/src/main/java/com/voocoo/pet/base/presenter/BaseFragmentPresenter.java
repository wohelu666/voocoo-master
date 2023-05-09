package com.voocoo.pet.base.presenter;


import androidx.annotation.StringRes;

import com.voocoo.pet.base.fragment.AbsBaseFragment;


/**
 * fragment 基础 Presenter
 *
 */
public class BaseFragmentPresenter<V extends AbsBaseFragment> extends BasePresenter<V> {
    public BaseFragmentPresenter(V fragment) {
        super(fragment);
    }

    protected String getString(@StringRes int stringRes) {
        return getView().getString(stringRes);
    }

    protected String getString(@StringRes int stringRes, Object... formatArgs) {
        return getView().getString(stringRes, formatArgs);
    }
}
