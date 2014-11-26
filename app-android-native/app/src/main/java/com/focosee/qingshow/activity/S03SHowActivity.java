package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.allthelucky.common.view.ImageIndicatorView;
import com.allthelucky.common.view.network.NetworkImageIndicatorView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.widget.MRelativeLayout_3_4;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class S03SHowActivity extends Activity {

    public static final String INPUT_POSTERS = "asfadf";
    public static final String INPUT_VIDEO = "asfasdfadf";

    private String videoUri;

    private MRelativeLayout_3_4 mRelativeLayout_3_4;
    private NetworkImageIndicatorView imageIndicatorView;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s03_show);

        Intent intent = getIntent();
        List<String> urlList = intent.getStringArrayListExtra(S03SHowActivity.INPUT_POSTERS);
        videoUri = intent.getStringExtra(S03SHowActivity.INPUT_VIDEO);


        this.mRelativeLayout_3_4 = (MRelativeLayout_3_4) findViewById(R.id.S03_relative_layout);
        this.imageIndicatorView = (NetworkImageIndicatorView) findViewById(R.id.S03_image_indicator);
        this.videoView = (VideoView) findViewById(R.id.S03_video_view);

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
        Log.i("app", urlList.toString());
        this.imageIndicatorView.setupLayoutByImageUrl(urlList, ImageLoader.getInstance());
        this.imageIndicatorView.getStartButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVideo();
            }
        });
        this.imageIndicatorView.show();
    }

    private void startVideo() {
        imageIndicatorView.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        Uri uri = Uri.parse(videoUri);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.start();
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
