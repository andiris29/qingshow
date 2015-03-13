package com.focosee.qingshow.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import com.focosee.qingshow.R;

/**
 * Created by Chenhr on 15/1/28.
 */
public class G01WebViewActivity extends BaseActivity{

    public static final String URL = "WebViewActivity";

    private WebView mWebView;

    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.webview_activity);

        backBtn = (ImageView) findViewById(R.id.webview_back);

        mWebView = (WebView) findViewById(R.id.webview_webview);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                G01WebViewActivity.this.finish();
            }
        });

        Intent intent = getIntent();
        String url = intent.getStringExtra(URL);
//        Uri uri = Uri.parse(url);
//        Intent intentGo = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(intentGo);
        loadWebView(url);
    }

    @Override
    public void reconn() {

    }

    private void loadWebView(String url){
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setBlockNetworkLoads(true);
        mWebView.setWebViewClient(new WebViewClient() {

            final ProgressDialog progressDialog = new ProgressDialog(G01WebViewActivity.this);

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if(url != null) return null;
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.hide();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressDialog.setMessage("加载中...");
                progressDialog.show();
            }
        });

        mWebView.loadUrl(url);
    }

}
