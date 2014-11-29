package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.allthelucky.common.view.ImageIndicatorView;
import com.allthelucky.common.view.network.NetworkImageIndicatorView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.entity.ShowEntity;
import com.focosee.qingshow.widget.MCircularImageView;
import com.focosee.qingshow.widget.MRelativeLayout_3_4;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;

public class S03SHowActivity extends Activity {

    // Input data
    public static final String INPUT_SHOW_ENTITY = "asfadf";

    private ShowEntity showEntity;
    private String videoUri;

    // Component declaration
    private MRelativeLayout_3_4 mRelativeLayout_3_4;
    private NetworkImageIndicatorView imageIndicatorView;
    private VideoView videoView;
    private MCircularImageView modelImage;
    private TextView modelName;
    private TextView modelAge;
    private TextView modelStatus;
    private TextView modelLoveNumber;

    private ImageView itemIMG1;
    private ImageView itemIMG2;
    private ImageView itemIMG3;

    private TextView description;

    private ArrayList<ShowEntity.RefItem> itemsData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s03_show);

        Intent intent = getIntent();
        showEntity = (ShowEntity) intent.getSerializableExtra(S03SHowActivity.INPUT_SHOW_ENTITY);
        itemsData = showEntity.getItemsList();
//        itemUrlList = (ArrayList)showEntity.getItemUrlList();
//        itemDescriptionList = (ArrayList)showEntity.getItemDescriptionList();
//        itemBrandList = (ArrayList)showEntity.getItemBrandList();

        matchUI();
        configData();

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

        this.initView(showEntity.getPosters());
    }

    private ImageView.OnClickListener mImageClickListener = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(S03SHowActivity.this, S05ProductActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(S05ProductActivity.INPUT_ITEM_LIST, itemsData);
            intent.putExtras(bundle);
//            intent.putStringArrayListExtra(S05ProductActivity.INPUT_URL_LIST, itemUrlList);
//            intent.putStringArrayListExtra(S05ProductActivity.INPUT_DESCRIPTION_LIST, itemDescriptionList);
//            intent.putStringArrayListExtra(S05ProductActivity.INPUT_BRAND_LIST, itemBrandList);
            startActivity(intent);
        }
    };

    private void matchUI() {
        this.mRelativeLayout_3_4 = (MRelativeLayout_3_4) findViewById(R.id.S03_relative_layout);
        this.imageIndicatorView = (NetworkImageIndicatorView) findViewById(R.id.S03_image_indicator);
        this.videoView = (VideoView) findViewById(R.id.S03_video_view);

        modelImage = (MCircularImageView) findViewById(R.id.S03_item_show_model_image);
        modelName = (TextView) findViewById(R.id.S03_item_show_model_name);
        modelAge = (TextView) findViewById(R.id.S03_item_show_model_age);
        modelStatus = (TextView) findViewById(R.id.S03_item_show_model_status);
        modelLoveNumber = (TextView) findViewById(R.id.S03_item_show_love);

        itemIMG1 = (ImageView) findViewById(R.id.S03_item_show_img1);
        itemIMG2 = (ImageView) findViewById(R.id.S03_item_show_img2);
        itemIMG3 = (ImageView) findViewById(R.id.S03_item_show_img3);

        description = (TextView) findViewById(R.id.S03_item_show_description);
    }

    private void configData() {

        videoUri = showEntity.getShowVideo();

        ImageLoader.getInstance().displayImage(showEntity.getModelImgSrc(), modelImage);

        modelName.setText(showEntity.getModelName());

        modelAge.setText(showEntity.getAge());

        modelStatus.setText(showEntity.getModelStatus());

        modelLoveNumber.setText(showEntity.getShowNumLike());

        ImageLoader.getInstance().displayImage(showEntity.getItem(0).cover, itemIMG1);

        ImageLoader.getInstance().displayImage(showEntity.getItem(1).cover, itemIMG2);

        ImageLoader.getInstance().displayImage(showEntity.getItem(2).cover, itemIMG3);

        itemIMG1.setOnClickListener(mImageClickListener);
        itemIMG2.setOnClickListener(mImageClickListener);
        itemIMG3.setOnClickListener(mImageClickListener);

        description.setText(showEntity.getAllItemDescription());
    }

    private String arrayToString(String[] input) {
        String result = "";
        for (String str : input)
            result += str + " ";
        return result;
    }

    private void initView(String[] urlList) {
        Log.i("app", urlList.toString());
        this.imageIndicatorView.setupLayoutByImageUrl(Arrays.asList(urlList), ImageLoader.getInstance());
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
