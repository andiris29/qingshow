package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.focosee.qingshow.R;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/3/11.
 */
public class S11NewTradeActivity extends BaseActivity {

    private ImageView submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s11_trade);
        EventBus.getDefault().register(this);
        init();
    }

    private void init() {

        submit = (ImageView) findViewById(R.id.s11_submit_button);

        findViewById(R.id.s11_back_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void reconn() {

    }
}
