package com.focosee.qingshow.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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
import com.focosee.qingshow.activity.fragment.S11NewTradeFragment;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ItemFeedingParser;
import com.focosee.qingshow.httpapi.response.dataparser.TradeParser;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.widget.ConfirmDialog;
import com.focosee.qingshow.widget.LoadingDialogs;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/3/13.
 */
public class S10ItemDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String INPUT_ITEM_ENTITY = "INPUT_ITEM_ENTITY";
    public static final String OUTPUT_ITEM_ENTITY = "OUTPUT_ITEM_ENTITY";
    public static final String BONUSES_ITEMID = "BONUSES_ITEMID";

    @InjectView(R.id.webview)
    WebView webview;
    @InjectView(R.id.s10_back_btn)
    ImageView back;
    @InjectView(R.id.s10_bay)
    ImageView bay;
    @InjectView(R.id.container)
    FrameLayout container;

    private MongoItem itemEntity, innerItemEntity;
    private LoadingDialogs dialog;

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
                    if (itemEntity.readOnly || !TextUtils.isEmpty(itemEntity.delist)) {
                        bay.setVisibility(View.GONE);
                    }
                } else {
                    bay.setVisibility(View.GONE);
                }
            }
        }

        itemEntity = new MongoItem();
        itemEntity._id = getIntent().getStringExtra(BONUSES_ITEMID);
        if(!TextUtils.isEmpty(itemEntity._id)){
            getItem();
        }
        dialog.show();
    }

    private void getItem(){

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getItemQueryApi(itemEntity._id), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(!MetadataParser.hasError(response)){
                    if(dialog.isShowing())
                        dialog.dismiss();
                    itemEntity = ItemFeedingParser.parse(response).get(0);
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
                dialog.show();
                getItemFormNet(itemEntity._id);
                break;
            case R.id.s10_back_btn:
                finish();
                break;
        }
    }

    private void getItemFormNet(String id) {
        Map map = new HashMap();
        map.put("_id", id);
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getItemSyncApi(), new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("S10ItemDetailActivity",response.toString());
                dialog.dismiss();
                if (MetadataParser.hasError(response)) {
                    final ConfirmDialog confirmDialog = new ConfirmDialog(S10ItemDetailActivity.this);
                    confirmDialog.setTitle(getResources().getString(R.string.s10_scale_close));
                    View.OnClickListener onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bay.setVisibility(View.GONE);
                            confirmDialog.dismiss();
                        }
                    };

                    confirmDialog.setConfirm(onClickListener);
                    confirmDialog.setCancel(onClickListener);
                    confirmDialog.show();
                    return;
                }

                innerItemEntity = ItemFeedingParser.parseOne(response);
                showNext(innerItemEntity);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void showNext(MongoItem item){

        if (item.skuProperties == null || item.skuProperties.size() == 0) {
            return;
        }
        container.setVisibility(View.VISIBLE);
        getIntent().putExtra(OUTPUT_ITEM_ENTITY, item);
        FragmentTransaction details = getSupportFragmentManager().beginTransaction().replace(R.id.container, new S11NewTradeFragment(), "details" + System.currentTimeMillis());
        details.addToBackStack(null);
        details.commit();
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

