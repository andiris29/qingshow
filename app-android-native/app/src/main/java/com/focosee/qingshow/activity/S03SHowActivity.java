package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.allthelucky.common.view.ImageIndicatorView;
import com.allthelucky.common.view.network.NetworkImageIndicatorView;
import com.focosee.qingshow.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class S03SHowActivity extends Activity {

    public static final String INPUT_POSTERS = "asfadf";

    private NetworkImageIndicatorView imageIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s03_show);

        Intent intent = getIntent();
        List<String> urlList = intent.getStringArrayListExtra(S03SHowActivity.INPUT_POSTERS);

        this.imageIndicatorView = (NetworkImageIndicatorView) findViewById(R.id.S03_image_indicator);

        this.imageIndicatorView.setOnItemChangeListener(new ImageIndicatorView.OnItemChangeListener() {
            @Override
            public void onPosition(int position, int totalCount) {

            }
        });
        this.imageIndicatorView.setOnItemChangeListener(new ImageIndicatorView.OnItemChangeListener() {
            @Override
            public void onPosition(int position, int totalCount) {

            }
        });

        this.initView(urlList);
    }

    private void initView(List<String> urlList) {
        this.imageIndicatorView.setupLayoutByImageUrl(urlList, ImageLoader.getInstance());
        this.imageIndicatorView.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_s03_show, menu);
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
