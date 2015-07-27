package com.focosee.qingshow.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.focosee.qingshow.R;

import dmax.dialog.SpotsDialog;

/**
 * Created by Administrator on 2015/7/24.
 */
public class PushWebActivity extends BaseActivity {
    
    public static final String URL = "WebViewActivity";

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.webview_activity);
        mWebView = (WebView) findViewById(R.id.webview_webview);


        Intent intent = getIntent();
        String url = intent.getStringExtra(URL);
        loadWebView(url);
    }

    @Override
    public void reconn() {

    }

    private void loadWebView(String url){
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

            final SpotsDialog dialog = new SpotsDialog(PushWebActivity.this,getResources().getString(R.string.web_view));
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dialog.hide();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                dialog.show();
            }
        });

        mWebView.loadUrl(url);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String url = intent.getStringExtra(URL);
        loadWebView(url);
    }
}
