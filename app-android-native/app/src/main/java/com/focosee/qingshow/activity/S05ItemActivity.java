package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.entity.ShowDetailEntity;
import com.focosee.qingshow.util.AppUtil;
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

        mVerticalViewPager = (MVerticalViewPager) findViewById(R.id.S05_view_pager);
        View[] views = new View[items.size()];
        for (int i = 0; i < views.length; i++) {
            views[i] = LayoutInflater.from(this).inflate(R.layout.pager_s05_item, null);
            ImageLoader.getInstance().displayImage(items.get(i).getCover(), (ImageView)((View)views[i]).findViewById(R.id.pager_s05_background), AppUtil.getShowDisplayOptions());
        }
        mVerticalViewPager.setViews(views);
        mVerticalViewPager.setOnPageChangeListener(new MVerticalViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                showItemAtIndex(position);
            }
        });

        showItemAtIndex(0);
    }

    public void showItemAtIndex(final int index) {
        ImageLoader.getInstance().displayImage(items.get(index).getCover(), (MCircularImageView)findViewById(R.id.S05_portrait), AppUtil.getPortraitDisplayOptions());
        ((TextView)findViewById(R.id.S05_item_name)).setText(items.get(index).getItemName());
        ((TextView)findViewById(R.id.S05_origin_price)).setText(items.get(index).getOriginPrice());
        ((TextView)findViewById(R.id.S05_origin_price)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        ((TextView)findViewById(R.id.S05_now_price)).setText(items.get(index).getPrice());
        ((Button)findViewById(R.id.S05_buy_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(items.get(index).getSource()));
                startActivity(intent);
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
