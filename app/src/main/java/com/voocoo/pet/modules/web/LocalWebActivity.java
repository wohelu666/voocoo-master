package com.voocoo.pet.modules.web;

import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;

import butterknife.BindView;

public class LocalWebActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.webview)
    WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }


    /**
     * 开启时，关闭来源
     *
     * @param context
     */
    public static void open(Context context, String title, String file) {
        Intent intent = new Intent(context, LocalWebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("file", file);
        context.startActivity(intent);
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        tvTitle.setTextColor(getColor(android.R.color.black));
        String title = getIntent().getStringExtra("title");
        tvTitle.setText(title);

        webView.loadUrl("file:////android_asset/" + getIntent().getStringExtra("file"));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @Nullable
    @Override
    protected BaseActivityPresenter createPresenter() {
        return null;
    }
}
