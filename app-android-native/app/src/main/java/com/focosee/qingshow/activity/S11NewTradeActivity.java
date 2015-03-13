package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.view.View;

import com.focosee.qingshow.R;

/**
 * Created by Administrator on 2015/3/11.
 */
public class S11NewTradeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s11_trade);
        init();
    }

    private void init() {
        findViewById(R.id.s11_back_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void reconn() {

    }
}
