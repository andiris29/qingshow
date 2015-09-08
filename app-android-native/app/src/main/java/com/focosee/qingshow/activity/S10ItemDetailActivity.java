package com.focosee.qingshow.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.fragment.S11NewTradeFragment;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.TradeParser;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.widget.LoadingDialogs;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/3/13.
 */
public class S10ItemDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String INPUT_ITEM_ENTITY = "INPUT_ITEM_ENTITY";
    public static final String BONUSES_TRADEID = "BONUSES_TRADEID";

    @InjectView(R.id.webview)
    WebView webview;
    @InjectView(R.id.s10_back_btn)
    ImageView back;
    @InjectView(R.id.s10_bay)
    ImageView bay;
    @InjectView(R.id.container)
    FrameLayout container;

    private MongoItem itemEntity;
    private LoadingDialogs dialog;
    private MongoTrade trade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s10_item_detail);
        ButterKnife.inject(this);
        DeployWebView(webview);
        dialog = new LoadingDialogs(this, R.style.dialog);
        if(null != getIntent().getExtras()){
            if(null != getIntent().getExtras().getSerializable(INPUT_ITEM_ENTITY)){
                itemEntity = (MongoItem) getIntent().getExtras().getSerializable(INPUT_ITEM_ENTITY);
                if (itemEntity != null) {
                    loadWebView(itemEntity.source);
                    if (itemEntity.readOnly) {
                        bay.setVisibility(View.GONE);
                    }
                } else {
                    bay.setVisibility(View.GONE);
                }
            }
        }

        trade = new MongoTrade();
        trade._id = getIntent().getStringExtra(BONUSES_TRADEID);
        if(!TextUtils.isEmpty(trade._id)){
            getTrade();
        }
        dialog.show();
    }

    private void getTrade(){

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getTradeApi(trade._id), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(!MetadataParser.hasError(response)){
                    if(dialog.isShowing())
                        dialog.dismiss();
                    trade = TradeParser.parseQuery(response).get(0);
                    itemEntity = trade.itemSnapshot;
                    loadWebView(itemEntity.source);
                    if (itemEntity.readOnly) {
                        bay.setVisibility(View.GONE);
                    }
                }
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
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
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(dialog.isShowing())
                    dialog.dismiss();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if(!dialog.isShowing())
                    dialog.show();
            }
        });
        webview.loadUrl(url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.s10_bay:
                if (itemEntity.skuProperties == null || itemEntity.skuProperties.size() == 0) {
                    break;
                }
                container.setVisibility(View.VISIBLE);
                FragmentTransaction details = getSupportFragmentManager().beginTransaction().replace(R.id.container, new S11NewTradeFragment(), "details" + System.currentTimeMillis());
                details.addToBackStack(null);
                details.commit();
                break;
            case R.id.s10_back_btn:
                finish();
                break;
        }
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

