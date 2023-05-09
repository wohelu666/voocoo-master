package com.voocoo.pet.base.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.utils.StatusBarCompat;
import com.voocoo.pet.common.utils.ToastUtil;
import com.voocoo.pet.common.widgets.AppDialog;

import butterknife.ButterKnife;


/**
 * 基础Activity
 */

public abstract class AbsBaseActivity<P extends BaseActivityPresenter> extends AppCompatActivity {
    protected P presenter;
    private AppDialog loadDialog;

    protected abstract int getLayoutId();

    protected abstract boolean isDarkMode();

    @Override
    protected void onStart() {
        StatusBarCompat.appOverlayStatusBar(this, true, true);
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        presenter = createPresenter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Nullable
    protected abstract P createPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
        }
    }

    /**
     * @return EditText失去焦点时是否隐藏键盘
     */
    public boolean isAutoHideKeyboard() {
        return true;
    }
/*

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isAutoHideKeyboard() && isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());

                if (hideKeyListener != null) {
                    hideKeyListener.onKeyHide();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
*/

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    public void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    HideKeyListener hideKeyListener;

    public void setOnHideKeyListener(HideKeyListener hideKeyListener) {
        this.hideKeyListener = hideKeyListener;
    }

    public interface HideKeyListener {
        void onKeyHide();
    }

    public void showToast(String str) {
        ToastUtil.getInstance().shortToast(str);
    }

    public void showLoading() {
        if (loadDialog == null) {
            loadDialog = AppDialog.loading(this);
        }
        loadDialog.show();
    }

    public void dismissLoading() {
        if (loadDialog != null)
            loadDialog.dismiss();
    }

    public void showPromptDialog(String str) {
        AppDialog.showPromptDialog(this, str).show();
    }

    public void showSuccessDialog(String str, String btnStr, View.OnClickListener listener) {
    }

}
