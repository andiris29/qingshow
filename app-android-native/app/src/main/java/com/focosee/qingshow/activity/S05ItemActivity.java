package com.focosee.qingshow.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.entity.mongo.MongoItem;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.widget.MCircularImageView;
import com.focosee.qingshow.widget.MVerticalViewPager;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class S05ItemActivity extends BaseActivity {
    //是否第一次加载webView
    private boolean isFirstLoad = true;
    public static final String INPUT_ITEMS = "S05ItemActivity_input_items";

    private MVerticalViewPager mVerticalViewPager;

    private ArrayList<MongoItem> items;

    private RelativeLayout mWebViewRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s05_item);

        items = (ArrayList<MongoItem>) getIntent().getExtras().getSerializable(INPUT_ITEMS);

        findViewById(R.id.S05_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S05ItemActivity.this.finish();
            }
        });

        mVerticalViewPager = (MVerticalViewPager) findViewById(R.id.S05_view_pager);
        View[] views = new View[items.size()];
        for (int i = 0; i < views.length; i++) {
            views[i] = LayoutInflater.from(this).inflate(R.layout.pager_s05_item, null);
            ImageLoader.getInstance().displayImage(items.get(i).getSource(), (ImageView)((View)views[i]).findViewById(R.id.pager_s05_background), AppUtil.getShowDisplayOptions());
        }
        mVerticalViewPager.setViews(views);
        mVerticalViewPager.setOnPageChangeListener(new MVerticalViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                showItemAtIndex(position);
            }
        });

        mWebViewRelativeLayout = (RelativeLayout) findViewById(R.id.s05_webview_relative);


        ((ImageButton)findViewById(R.id.s05_webview_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebViewRelativeLayout.setVisibility(View.GONE);
            }
        });
        showItemAtIndex(0);
    }

    public void showItemAtIndex(final int index) {
        ImageLoader.getInstance().displayImage(items.get(index).getBrandPortrait(), (MCircularImageView)findViewById(R.id.S05_portrait), AppUtil.getPortraitDisplayOptions());
        ((TextView)findViewById(R.id.S05_item_name)).setText(items.get(index).getItemName());
        ((TextView)findViewById(R.id.S05_origin_price)).setText(items.get(index).getOriginPrice());
        ((TextView)findViewById(R.id.S05_origin_price)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        ((TextView)findViewById(R.id.S05_now_price)).setText(items.get(index).getPrice());
        ((Button)findViewById(R.id.S05_buy_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(items.get(index).getSource()));
                startActivity(intent);*/
                mWebViewRelativeLayout.setVisibility(View.VISIBLE);

                if(!isFirstLoad){
                    return;
                }

                WebView mWebView = (WebView) findViewById(R.id.s05_webview);
                mWebView.setVisibility(View.VISIBLE);
                Log.d("S05ItemActivity", items.get(index).getSource().toString());
                WebSettings webSettings = mWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                mWebView.setWebViewClient(new WebViewClient() {

                    final ProgressDialog progressDialog = new ProgressDialog(S05ItemActivity.this);
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
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
                mWebView.loadUrl(items.get(index).getSource().toString());

                isFirstLoad = false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_s05_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
