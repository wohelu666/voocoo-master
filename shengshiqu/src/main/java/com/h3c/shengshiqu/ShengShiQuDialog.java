package com.h3c.shengshiqu;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.h3c.shengshiqu.widget.ShengShiQuPicker;

/**
 * Created by H3c on 16/8/23.
 */

public class ShengShiQuDialog extends DialogFragment implements View.OnClickListener {
    public ShengShiQuPicker picker;
    public String[] mData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(inflateLayout(), null);
        initView(view);

        // 去掉系统默认的一个主题样式Title
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    public int inflateLayout() {
        return R.layout.dialog_shengshiqu;
    }

    public void initView(View view) {
        view.findViewById(R.id.dialog_SSQ_doneBtn).setOnClickListener(this);
        view.findViewById(R.id.dialog_SSQ_cancelBtn).setOnClickListener(this);
        picker = (ShengShiQuPicker) view.findViewById(R.id.dialog_SSQPicker);
        picker.setData(mData);
    }

    @Override
    public void onResume() {
        if(isMatchParentWidth()) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;

            if (window != null) {
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                params.gravity = Gravity.BOTTOM;
                window.setAttributes(params);
            }
        }

        super.onResume();
    }

    public boolean isMatchParentWidth() {
        return true;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.dialog_SSQ_doneBtn) {
            String[] result = picker.getResult();
            if(mListener != null) {
                mListener.onSSQDialogResult(result[0], result[1], result[2]);
            }
        }

        dismiss();
    }

    public ShengShiQuDialogListener mListener;
    public void setDataResultListener(ShengShiQuDialogListener l) {
        this.mListener = l;
    }

    public void setData(String[] data) {
        mData = data;
        if(picker != null) {
            picker.setData(mData);
        }
    }

    public interface ShengShiQuDialogListener {
        void onSSQDialogResult(String province, String city, String district);
    }
}
