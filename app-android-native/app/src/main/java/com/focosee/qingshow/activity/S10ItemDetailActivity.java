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
public class S10ItemDetailActivity extends BaseActivity implements View.OnClickListener {

    private final int LOADING_FINISH = 0x1;
    private final int LOADING_START = 0x2;
    public static final String INPUT_ITEM_ENTITY = "INPUT_ITEM_ENTITY";
    public static final String OUTPUT_ITEM_ENTITY = "OUTPUT_ITEM_ENTITY";
    public static final String BONUSES_ITEMID = "BONUSES_ITEMID";
    public static final String PROMOTRER = "promoterRef";

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

    private boolean showble = false;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case LOADING_START:
                    if(null != dialog) {
                        if (!dialog.isShowing())
                            dialog.show();
                    }
                    return true;
                case LOADING_FINISH:
                    if(null != dialog){
                        if(dialog.isShowing()){
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
        if(null != getIntent().getExtras()){
            if(null != getIntent().getExtras().getSerializable(INPUT_ITEM_ENTITY)){
                itemEntity = (MongoItem) getIntent().getExtras().getSerializable(INPUT_ITEM_ENTITY);
                if (itemEntity != null) {
                    loadWebView(itemEntity.source);
                    if (itemEntity.readOnly || null != itemEntity.delist) {
                        bay.setVisibility(View.GONE);
                    }
                    return;
                } else {
                    bay.setVisibility(View.GONE);
                }
            }
        }

        itemEntity = new MongoItem();
        itemEntity._id = getIntent().getStringExtra(BONUSES_ITEMID);
        if(!TextUtils.isEmpty(itemEntity._id)){
            getItemFormNet(itemEntity._id);
        }
        dialog.show();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.s10_bay:
                if (!QSModel.INSTANCE.loggedin() || QSModel.INSTANCE.isGuest()) {
                    GoToWhereAfterLoginModel.INSTANCE.set_class(null);
                    startActivity(new Intent(S10ItemDetailActivity.this, U19LoginGuideActivity.class));
                    return;
                }
                dialog.show();
                showble = true;
                getItemFormNet(itemEntity._id);
                break;
            case R.id.s10_back_btn:
                finish();
                break;
        }
    }


    private void getItemFormNet(String id) {
        Map map = new HashMap();
        Log.d(S10ItemDetailActivity.class.getSimpleName(), "_id:" + id);
        map.put("_id", id);
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getItemSyncApi(), new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.i("S10ItemDetailActivity",response.toString());
                handler.sendEmptyMessage(LOADING_FINISH);
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

                if(showble) {
                    innerItemEntity = ItemFeedingParser.parseOne(response);
                    showble = false;
                }else{
                    itemEntity = ItemFeedingParser.parseOne(response);
                    loadWebView(itemEntity.source);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handler.sendEmptyMessage(LOADING_FINISH);
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
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

