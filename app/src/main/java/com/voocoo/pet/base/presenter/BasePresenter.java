package com.voocoo.pet.base.presenter;

import android.app.Activity;
import android.content.Context;


import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.fragment.AbsBaseFragment;
import com.voocoo.pet.common.utils.IntentUtil;

import java.lang.ref.WeakReference;


/**
 * 基础 Presenter
 */

public class BasePresenter<V> {
    protected WeakReference<V> weakReference;

    public BasePresenter(V t) {
        weakReference = new WeakReference<>(t);
    }


    protected V getView() {
        if (weakReference == null || weakReference.get() == null)
            throw new NullPointerException("view is null!");
        return weakReference.get();
    }

    protected Context getContext(){
        if(getView() instanceof AbsBaseActivity){
            return (Context) getView();
        }else if(getView() instanceof AbsBaseFragment){
            return ((AbsBaseFragment) getView()).getContext();
        }

        return null;
    }

    /**
     * 在某种情况下，如Activity不一定调到onDestroy(),则弱引用也有可能导致内存泄漏，这时你得主动去detach掉
     */
    public void detachView() {
        if (weakReference != null) {
            weakReference.clear();
            weakReference = null;
        }
    }

    public void finish(){
        if(weakReference.get() instanceof Activity){
            IntentUtil.closeActivity(((Activity) weakReference.get()));
        }else if(weakReference.get() instanceof AbsBaseFragment){
            IntentUtil.closeActivity(((AbsBaseFragment) weakReference.get()).getActivity());
        }
    }

    /**
     * 显示loading
     */
    public void showLoading(){
        if(weakReference.get() instanceof Activity){
            ((AbsBaseActivity) weakReference.get()).showLoading();
        }else if(weakReference.get() instanceof AbsBaseFragment){
            ((AbsBaseActivity)((AbsBaseFragment) weakReference.get()).getActivity()).showLoading();
        }
    }
    /**
     * 关闭loading
     */
    public void dismissLoading(){
        if(weakReference.get() instanceof AbsBaseActivity){
            ((AbsBaseActivity) weakReference.get()).dismissLoading();
        }else if(weakReference.get() instanceof AbsBaseFragment){
            ((AbsBaseActivity)((AbsBaseFragment) weakReference.get()).getActivity()).dismissLoading();
        }
    }
}
