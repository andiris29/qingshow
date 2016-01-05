package com.focosee.qingshow.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ItemFeedingParser;
import com.focosee.qingshow.model.GoToWhereAfterLoginModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.widget.ConfirmDialog;
import com.focosee.qingshow.widget.LoadingDialogs;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/3/13.
 */
public class S10ItemDetailActivity extends BaseActivity {

    private final int LOADING_FINISH = 0x1;
    private final int LOADING_START = 0x2;
    public static final String INPUT_ITEM_ENTITY = "INPUT_ITEM_ENTITY";
    public static final String BONUSES_ITEMID = "BONUSES_ITEMID";
    public static final String PROMOTRER = "promoterRef";

    @InjectView(R.id.webview)
    WebView webview;
    @InjectView(R.id.s10_back_btn)
    ImageView back;

    private MongoItem itemEntity, innerItemEntity;
    private LoadingDialogs dialog;


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING_START:
                    if (null != dialog) {
                        if (!dialog.isShowing())
                            dialog.show();
                    }
                    return true;
                case LOADING_FINISH:
                    if (null != dialog) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                    return true;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s10_item_detail);
        ButterKnife.inject(this);
        DeployWebView(webview);
        dialog = new LoadingDialogs(this, R.style.dialog);
        if (null != getIntent().getExtras()) {
            if (null != getIntent().getExtras().getSerializable(INPUT_ITEM_ENTITY)) {
                itemEntity = (MongoItem) getIntent().getExtras().getSerializable(INPUT_ITEM_ENTITY);
                if (itemEntity != null) {
                    loadWebView(itemEntity.source);
                    return;
                }
            }
        }

        itemEntity = new MongoItem();
        itemEntity._id = getIntent().getStringExtra(BONUSES_ITEMID);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S10ItemDetailActivity.this.finish();
            }
        });
    }

    @Override
    public void reconn() {

    }

    private void DeployWebView(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDisplayZoomControls(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void loadWebView(String url) {
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                handler.sendEmptyMessage(LOADING_FINISH);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                handler.sendEmptyMessage(LOADING_START);
            }
        });
        webview.loadUrl(url);
    }
    public void onClick(View view){
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}

