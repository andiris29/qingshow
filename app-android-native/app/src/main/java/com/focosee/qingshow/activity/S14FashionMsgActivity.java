package com.focosee.qingshow.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S08TrendListAdapter;
import com.focosee.qingshow.model.vo.mongo.MongoPreview;
import com.focosee.qingshow.widget.indicator.NetworkImageIndicatorView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class S14FashionMsgActivity extends BaseActivity {

    private NetworkImageIndicatorView imageIndicatorView;
    private TextView describe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s14_fashion_msg_pictures);
        if(getIntent().getStringExtra("refCollection").equals(S08TrendListAdapter.PREVIEWS)){
            MongoPreview preview = (MongoPreview) getIntent().getExtras().getSerializable("entity");
            imageIndicatorView = (NetworkImageIndicatorView) findViewById(R.id.s14_image_indicator);
            describe = (TextView) findViewById(R.id.s14_describe);
            List<String> imageList = new ArrayList<String>();
            for (MongoPreview.Image image : preview.images){
                imageList.add(image.url);
            }
            imageIndicatorView.setupLayoutByImageUrl(imageList, ImageLoader.getInstance());
            imageIndicatorView.show();
            imageIndicatorView.getViewPager().setCurrentItem(0, false);
            describe.setText(preview.getDescription(0));

        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        findViewById(R.id.s14_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void reconn() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.meun_s14_fashion_msg, menu);
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
