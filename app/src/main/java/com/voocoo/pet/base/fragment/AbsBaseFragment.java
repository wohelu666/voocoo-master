package com.voocoo.pet.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseFragmentPresenter;

import butterknife.ButterKnife;


/**
 * 基础Fragment
 */

public abstract class AbsBaseFragment<P extends BaseFragmentPresenter> extends Fragment {
    protected P presenter;
    private View view;

    @LayoutRes
    protected abstract int initLayoutID();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        presenter = createPresenter();
        if (view == null) {
            view = inflater.inflate(initLayoutID(), container, false);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        try {
            super.onDestroyView();
            presenter.detachView();
        } catch (Exception e) {

        }
    }

    protected abstract P createPresenter();

    public AbsBaseActivity getAct() {
        return (AbsBaseActivity) getActivity();
    }
}
