package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.focosee.qingshow.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2016/1/4.
 */
public class S25ShowHrefActivity extends BaseActivity implements OnClickListener{
    @InjectView(R.id.left_btn)
    ImageView ivBack;
    @InjectView(R.id.title)
    TextView tvTitle;
    @InjectView(R.id.wv_s25_showhref)
    WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s25_showhref);
        ButterKnife.inject(this);
        tvTitle.setText(R.string.s25_title);
        ivBack.setOnClickListener(this);
        WebSettings webSettings =   wv.getSettings();
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setBlockNetworkImage(false);
        String url = getIntent().getExtras().getString("url");
        wv.loadUrl(url);
    }

    @Override
    public void reconn() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_btn:
                finish();
                break;
        }
    }
}
