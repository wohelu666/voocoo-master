package com.voocoo.pet.modules.web;

import android.net.http.SslError;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
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

public class WebActivity extends AbsBaseActivity {

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    supportFinishAfterTransition();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        tvTitle.setTextColor(getColor(android.R.color.black));
        tvTitle.setText(getString(R.string.text_my_shop));

        webView.loadUrl(getIntent().getStringExtra("url"));
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//支持js
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//设置允许JS弹窗
        webSettings.setAllowFileAccess(true);//设置可以访问文件
        webSettings.supportMultipleWindows();//多窗口
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        //webview的缓存模式
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDomStorageEnabled(true);
        webSettings.setNeedInitialFocus(true);

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true);//将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true);// 缩放至屏幕的大小

        webSettings.setSupportZoom(false);  //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(false);//设置内置的缩放控件。
        webSettings.setDisplayZoomControls(false);//隐藏原生的缩放控件
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.cancel();
            }
        });
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
