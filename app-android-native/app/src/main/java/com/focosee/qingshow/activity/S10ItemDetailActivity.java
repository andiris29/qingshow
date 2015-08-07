package com.focosee.qingshow.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.fragment.S11NewTradeFragment;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.widget.LoadingDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/3/13.
 */
public class S10ItemDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String INPUT_ITEM_ENTITY = "INPUT_ITEM_ENTITY";

    @InjectView(R.id.webview)
    WebView webview;
    @InjectView(R.id.s10_back_btn)
    ImageButton back;
    @InjectView(R.id.s10_bay)
    ImageView bay;
    @InjectView(R.id.container)
    FrameLayout container;

    private MongoItem itemEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s10_item_detail);
        ButterKnife.inject(this);
        DeployWebView(webview);

        itemEntity = (MongoItem) getIntent().getExtras().getSerializable(INPUT_ITEM_ENTITY);
        if (itemEntity != null) {
            loadWebView(itemEntity.source);
        }
    }

    private void DeployWebView(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
    }

    private void loadWebView(String url) {
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {

            final LoadingDialog dialog = new LoadingDialog(getSupportFragmentManager());

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dialog.dismiss();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                dialog.show("s10 web dialog");
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
                FragmentTransaction details = getSupportFragmentManager().beginTransaction().replace(R.id.container, new S11NewTradeFragment(), "details");
                details.addToBackStack(null);
                details.commit();
                break;
            case R.id.s10_back_btn:
                finish();
                break;
        }
    }
}

