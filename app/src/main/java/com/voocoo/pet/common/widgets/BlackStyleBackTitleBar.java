package com.voocoo.pet.common.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.voocoo.pet.R;
import com.voocoo.pet.common.utils.CommonUtil;


public class BlackStyleBackTitleBar extends Toolbar implements View.OnClickListener {
    private ImageView ivBack;
    private Toolbar toolbar;
    private TextView tvTitleBar, tvAction;
    private OnBackTitleListener onBackTitleListener = new OnBackTitleListener();

    public BlackStyleBackTitleBar(@NonNull Context context) {
        this(context, null);
    }

    public BlackStyleBackTitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlackStyleBackTitleBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        setId(R.id.top_toolbar);
        inflate(context, R.layout.view_black_title_bar, this);
        toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitleBar = (TextView) findViewById(R.id.tv_title);
        tvAction = (TextView) findViewById(R.id.tv_action);
        ivBack.setOnClickListener(this);
        tvAction.setOnClickListener(this);
        setFitsSystemWindows(true);//设置透明状态栏

        LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new LayoutParams(context, attrs);
        }
        layoutParams.width = LayoutParams.MATCH_PARENT;
        TypedArray app = context.obtainStyledAttributes(
                attrs, R.styleable.AppCompatTheme, defStyleAttr, 0);
        float h = app.getDimension(R.styleable.AppCompatTheme_actionBarSize, 0);
        layoutParams.height = (int) h;
        app.recycle();
        setLayoutParams(layoutParams);
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(
                    attrs, R.styleable.BackTitleBar, defStyleAttr, 0);
            try {
                boolean showBack = a.getBoolean(R.styleable.BackTitleBar_showBack, true);
                if (showBack) {
                    ivBack.setVisibility(VISIBLE);
                } else {
                    ivBack.setVisibility(INVISIBLE);
                }
                String title = a.getString(R.styleable.BackTitleBar_titleText);
                tvTitleBar.setText(title);
                String actionText = a.getString(R.styleable.BackTitleBar_actionText);
                if (!TextUtils.isEmpty(actionText)) {
                    tvAction.setText(actionText);
                    tvAction.setVisibility(VISIBLE);
                } else {
                    tvAction.setVisibility(GONE);
                }
                int actionIconRes = a.getResourceId(R.styleable.BackTitleBar_actionIcon, 0);
                if (actionIconRes != 0) {
                    if (TextUtils.isEmpty(actionText)) {
                        tvAction.setText("");
                    }
                    tvAction.setVisibility(VISIBLE);
                    tvAction.setCompoundDrawablesWithIntrinsicBounds(0, 0, actionIconRes, 0);
                } else {
                    tvAction.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
                int actionBg = a.getResourceId(R.styleable.BackTitleBar_actionBg, 0);
                if (actionBg != 0) {
                    tvAction.setBackgroundResource(actionBg);
                    LayoutParams lp = (LayoutParams) tvAction.getLayoutParams();
                    lp.setMargins(0, 0, CommonUtil.convertDIP2PX(context, 10), 0);
                } else {
                    tvAction.setBackgroundDrawable(null);
                }
                int actionTextColor = a.getColor(R.styleable.BackTitleBar_actionTextColor, 0);
                if (actionTextColor != 0) {
                    tvAction.setTextColor(actionTextColor);
                } else {
                    tvAction.setTextColor(getResources().getColor(R.color.color_ffffff));
                }
                int textColor = a.getColor(R.styleable.BackTitleBar_titleTextColor, getResources().getColor(R.color.color_ffffff));
                tvTitleBar.setTextColor(textColor);
                int background = a.getResourceId(R.styleable.BackTitleBar_background, 0);

                toolbar.setBackgroundColor( getResources().getColor(R.color.colorAccent));
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (onBackTitleListener != null) {
            switch (v.getId()) {
                case R.id.iv_back:
                    onBackTitleListener.onBack(this, ivBack);
                    break;
                case R.id.tv_action:
                    onBackTitleListener.onClickActionText(this, tvAction);
                    break;
            }
        }
    }

    public void setBgColor(int colorRes) {
        setBackgroundColor(getResources().getColor(colorRes));
    }

    public void setTitle(int titleRes) {
        tvTitleBar.setText(titleRes);
    }

    public void setTitle(String title) {
        tvTitleBar.setText(title);
    }

    public void setActionText(int actionRes) {
        tvAction.setText(actionRes);
    }

    public void setActionText(String actionText) {
        showActionText(true);
        tvAction.setText(actionText);
    }

    public void showActionText(boolean isShow) {
        tvAction.setVisibility(isShow ? VISIBLE : GONE);
    }

    /**
     * 设置Action的icon，文本
     *
     * @param actionRes    图片资源
     * @param actionStrRes 文本资源
     */
    public void setActionIcon(int actionRes, int actionStrRes) {
        String actionText = "";
        if (actionStrRes != 0) {
            actionText = getResources().getString(actionStrRes);
        }
        setActionIcon(actionRes, actionText);
    }

    /**
     * 设置Action的icon，文本
     *
     * @param actionRes  图片资源
     * @param actionText 文本
     */
    public void setActionIcon(int actionRes, String actionText) {
        if (TextUtils.isEmpty(actionText)) {
            tvAction.setText("");
        } else {
            tvAction.setText(actionText);
        }
        tvAction.setVisibility(VISIBLE);
        tvAction.setCompoundDrawablesWithIntrinsicBounds(0, 0, actionRes, 0);
    }


    public OnBackTitleListener getOnBackTitleListener() {
        return onBackTitleListener;
    }

    public void setOnBackTitleListener(OnBackTitleListener onBackTitleListener) {
        this.onBackTitleListener = onBackTitleListener;
    }

    public static class OnBackTitleListener {
        public void onBack(BlackStyleBackTitleBar titleBar, ImageView ivBack) {
            if (titleBar != null && titleBar.getContext() instanceof Activity) {
                ((Activity) titleBar.getContext()).onBackPressed();
            }
        }

        public void onClickActionText(BlackStyleBackTitleBar titleBar, TextView tvAction) {
        }

    }
}