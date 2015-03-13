package com.focosee.qingshow.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.focosee.qingshow.R;

/**
 * Created by Administrator on 2015/3/13.
 */
public class S10ItemDetailActivity extends BaseActivity {

    private ViewPager viewPager;
    private TextView discount;
    private TextView description;
    private TextView price;
    private TextView sourcePrice;
    private ImageView watch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s10_item_detail);
        init();
    }

    private void init() {
        viewPager = (ViewPager) findViewById(R.id.s10_item_viewpager);
        discount = (TextView) findViewById(R.id.s10_item_description);
        price = (TextView) findViewById(R.id.s10_item_price);
        sourcePrice = (TextView) findViewById(R.id.s10_item_source_price);
        watch = (ImageView) findViewById(R.id.s10_watch);

        findViewById(R.id.s10_bay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S10ItemDetailActivity.this, S11NewTradeActivity.class);
//                intent.putExtra();
                S10ItemDetailActivity.this.startActivity(intent);
            }
        });

        findViewById(R.id.s10_back_btn).setOnClickListener(new View.OnClickListener() {
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
