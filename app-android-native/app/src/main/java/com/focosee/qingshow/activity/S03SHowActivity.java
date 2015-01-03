package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.allthelucky.common.view.ImageIndicatorView;
import com.allthelucky.common.view.network.NetworkImageIndicatorView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.ShowDetailEntity;
import com.focosee.qingshow.request.MJsonObjectRequest;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.widget.MRoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class S03SHowActivity extends Activity {

    // Input data
    public static final String INPUT_SHOW_ENTITY_ID = "asfadf";

    private String showId;
    private ShowDetailEntity showDetailEntity;
    private ArrayList<ShowDetailEntity.RefItem> itemsData;
    private String videoUri;

    // Component declaration
    private RelativeLayout mRelativeLayout;
    private NetworkImageIndicatorView imageIndicatorView;
    private VideoView videoView;
    private MRoundImageView modelImage;
    private TextView modelInformation;
    private TextView modelAgeHeight;
    private TextView modelSignature;
    private TextView commentTextView;
    private TextView likeTextView;
    private TextView itemTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s03_show);

        findViewById(R.id.S03_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S03SHowActivity.this.finish();
            }
        });

        Intent intent = getIntent();
        showId = intent.getStringExtra(S03SHowActivity.INPUT_SHOW_ENTITY_ID);
        getShowDetailFromNet();

        matchUI();

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

    }

    private void getShowDetailFromNet() {
        final MJsonObjectRequest jsonObjectRequest = new MJsonObjectRequest(QSAppWebAPI.getShowDetailApi(showId), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("S03ShowActivity", response.toString());
                showDetailEntity = ShowDetailEntity.getShowDetailFromResponse(response);
                showData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("S03ShowActivity", error.toString());
            }
        });
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void matchUI() {
        this.mRelativeLayout = (RelativeLayout) findViewById(R.id.S03_relative_layout);
        this.imageIndicatorView = (NetworkImageIndicatorView) findViewById(R.id.S03_image_indicator);
        this.videoView = (VideoView) findViewById(R.id.S03_video_view);

        modelImage = (MRoundImageView) findViewById(R.id.S03_model_portrait);
        modelInformation = (TextView) findViewById(R.id.S03_model_name);
        modelAgeHeight = (TextView) findViewById(R.id.S03_model_age_height);
        modelSignature = (TextView) findViewById(R.id.S03_model_signature);

        commentTextView = (TextView) findViewById(R.id.S03_comment_text_view);
        likeTextView = (TextView) findViewById(R.id.S03_like_text_view);
        itemTextView = (TextView) findViewById(R.id.S03_item_text_view);
    }

    private void showData() {
        if (null == showDetailEntity)
            return;

        itemsData = showDetailEntity.getItemsList();

        videoUri = showDetailEntity.getShowVideo();

        ImageLoader.getInstance().displayImage(showDetailEntity.getModelPhoto(), modelImage, AppUtil.getPortraitDisplayOptions());

        modelInformation.setText(showDetailEntity.getModelName());

        modelAgeHeight.setText(showDetailEntity.getModelWeightHeight());

        modelSignature.setText(showDetailEntity.getModelStatus());

        commentTextView.setText(showDetailEntity.getShowCommentNumber());

        likeTextView.setText(showDetailEntity.getShowLikeNumber());

        itemTextView.setText(showDetailEntity.getItemsCount());

        this.initPosterView(showDetailEntity.getPosters());

        findViewById(R.id.S03_item_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S03SHowActivity.this, S07CollectActivity.class);
                intent.putExtra(S07CollectActivity.INPUT_BACK_IMAGE, showDetailEntity.getCover());
//                intent.putExtra(S07CollectActivity.INPUT_BRAND_TEXT, showDetailEntity.getBrandNameText());
                Bundle bundle = new Bundle();
                bundle.putSerializable(S07CollectActivity.INPUT_ITEMS, showDetailEntity.getItemsList());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        findViewById(R.id.S03_comment_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != showDetailEntity && null != showDetailEntity._id) {
                    Intent intent = new Intent(S03SHowActivity.this, S04CommentActivity.class);
                    intent.putExtra(S04CommentActivity.INPUT_SHOW_ID, showDetailEntity._id);
                    startActivity(intent);
                } else {
                    Toast.makeText(S03SHowActivity.this, "Plese NPC!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.S03_like_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.S03_share_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                intent.putExtra(Intent.EXTRA_TEXT, "测试内容!!!");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, getTitle()));
            }
        });

        findViewById(R.id.S03_video_start_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVideo();
            }
        });
    }

    private String arrayToString(String[] input) {
        String result = "";
        for (String str : input)
            result += str + " ";
        return result;
    }

    private void initPosterView(String[] urlList) {
        this.imageIndicatorView.setupLayoutByImageUrl(Arrays.asList(urlList), ImageLoader.getInstance(), AppUtil.getShowDisplayOptions());
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
