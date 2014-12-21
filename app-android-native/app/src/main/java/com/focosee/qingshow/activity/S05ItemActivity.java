package com.focosee.qingshow.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.entity.ShowDetailEntity;
import com.focosee.qingshow.widget.MCircularImageView;
import com.focosee.qingshow.widget.MVerticalViewPager;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class S05ItemActivity extends Activity {
    public static final String INPUT_ITEMS = "S05ItemActivity_input_items";

    private MVerticalViewPager mVerticalViewPager;

    private ArrayList<ShowDetailEntity.RefItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s05_item);

        items = (ArrayList<ShowDetailEntity.RefItem>) getIntent().getExtras().getSerializable(INPUT_ITEMS);

        findViewById(R.id.S05_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S05ItemActivity.this.finish();
            }
        });

        ImageLoader.getInstance().displayImage("", (MCircularImageView)findViewById(R.id.S05_portrait));
        ((TextView)findViewById(R.id.S05_item_name)).setText("测试的物品");
        ((TextView)findViewById(R.id.S05_origin_price)).setText("￥360.00");
        ((TextView)findViewById(R.id.S05_now_price)).setText("￥169.00");

        mVerticalViewPager = (MVerticalViewPager) findViewById(R.id.S05_view_pager);
        View[] views = new View[items.size()];
        for (int i = 0; i < views.length; i++) {
            views[i] = LayoutInflater.from(this).inflate(R.layout.pager_s05_item, null);
            ImageLoader.getInstance().displayImage(items.get(i).getCover(), (ImageView)((View)views[i]).findViewById(R.id.pager_s05_background));
        }
        mVerticalViewPager.setViews(views);
        mVerticalViewPager.setOnPageChangeListener(new MVerticalViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

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
