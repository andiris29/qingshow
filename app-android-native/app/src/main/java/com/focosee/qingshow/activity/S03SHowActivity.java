package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.allthelucky.common.view.ImageIndicatorView;
import com.allthelucky.common.view.network.NetworkImageIndicatorView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.ShowDetailEntity;
import com.focosee.qingshow.entity.ShowListEntity;
import com.focosee.qingshow.widget.MCircularImageView;
import com.focosee.qingshow.widget.MHorizontalScrollView;
import com.focosee.qingshow.widget.MRelativeLayout_3_4;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class S03SHowActivity extends Activity {

    // Input data
    public static final String INPUT_SHOW_ENTITY_ID = "asfadf";

    private String showId;
    private ShowDetailEntity showDetailEntity;
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
    private ImageView nav_menu_back;
    private ImageView nav_menu_account;

    private MHorizontalScrollView itemScrollView;
    private LinearLayout itemContainer;

    private TextView description;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s03_show);

        nav_menu_account = (ImageView) findViewById(R.id.S03_title_account);
        nav_menu_back = (ImageView) findViewById(R.id.S03_title_back);

        nav_menu_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S03SHowActivity.this.finish();
            }
        });
        nav_menu_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S03SHowActivity.this, U06LoginActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        showId = intent.getStringExtra(S03SHowActivity.INPUT_SHOW_ENTITY_ID);
        getShowDetailFromNet();

        matchUI();
        showData();

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

        ((ImageButton)findViewById(R.id.S03_message_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S03SHowActivity.this, S04CommentActivity.class);
                intent.putExtra(S04CommentActivity.INPUT_SHOW_ID, showEntity._id);
                startActivity(intent);
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
            startActivity(intent);
        }
    };

    private void getShowDetailFromNet() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(QSAppWebAPI.getShowDetailApi(showId), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                showDetailEntity = ShowDetailEntity.getShowDetailFromResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("S03ShowActivity", error.toString());
            }
        });
    }

    private void matchUI() {
        this.mRelativeLayout_3_4 = (MRelativeLayout_3_4) findViewById(R.id.S03_relative_layout);
        this.imageIndicatorView = (NetworkImageIndicatorView) findViewById(R.id.S03_image_indicator);
        this.videoView = (VideoView) findViewById(R.id.S03_video_view);

        modelImage = (MCircularImageView) findViewById(R.id.S03_item_show_model_image);
        modelName = (TextView) findViewById(R.id.S03_item_show_model_name);
        modelAge = (TextView) findViewById(R.id.S03_item_show_model_age);
        modelStatus = (TextView) findViewById(R.id.S03_item_show_model_status);
        modelLoveNumber = (TextView) findViewById(R.id.S03_item_show_love);

        itemScrollView = (MHorizontalScrollView) findViewById(R.id.S03_item_scroll_view);
        itemContainer = (LinearLayout) findViewById(R.id.S03_item_container);

        description = (TextView) findViewById(R.id.S03_item_show_description);
    }

    private void showData() {

        itemsData = showDetailEntity.getItemsList();

        videoUri = showDetailEntity.getShowVideo();

        ImageLoader.getInstance().displayImage(showDetailEntity.getModelPhoto(), modelImage);

        modelName.setText(showDetailEntity.getModelName());

        modelAge.setText(ShowDetailEntity.getAge());

        modelStatus.setText(showEntity.getModelStatus());

        modelLoveNumber.setText(showEntity.getShowNumLike());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getWinWidth()/3, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < showEntity.getItemsList().size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ImageLoader.getInstance().displayImage(showEntity.getItem(i).cover, imageView);
            imageView.setOnClickListener(mImageClickListener);
            itemContainer.addView(imageView);
        }

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

    private int getWinWidth(){
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    private int getWinHeight(){
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }
}
