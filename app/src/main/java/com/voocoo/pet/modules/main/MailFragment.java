package com.voocoo.pet.modules.main;

import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.voocoo.pet.R;
import com.voocoo.pet.base.fragment.AbsBaseFragment;
import com.voocoo.pet.base.presenter.BaseFragmentPresenter;

import butterknife.BindView;

public class MailFragment extends AbsBaseFragment {

    @BindView(R.id.webview)
    WebView webView;

    @Override
    protected int initLayoutID() {
        return R.layout.fragment_mail;
    }

    public static MailFragment newInstance() {
        return new MailFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        webView.loadUrl("https://shop91447720.m.youzan.com/v2/feature/uckUdCS8AK");
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
        webView.setWebViewClient(new WebViewClient(){
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
    protected BaseFragmentPresenter createPresenter() {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
