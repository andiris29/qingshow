package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.allthelucky.common.view.ImageIndicatorView;
import com.allthelucky.common.view.network.NetworkImageIndicatorView;
import com.android.volley.Request;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class S03SHowActivity extends Activity {

    // Input data
    public static final String INPUT_SHOW_ENTITY_ID = "asfadf";
    public final String TAG = "S03SHowActivity";

    private String showId;
    private ShowDetailEntity showDetailEntity;
    private ArrayList<ShowDetailEntity.RefItem> itemsData;
    private String videoUriString;
    private Uri videoUri = null;

    // Component declaration
    private RelativeLayout mRelativeLayout;
    private NetworkImageIndicatorView imageIndicatorView;
    private VideoView videoView;
//    private SurfaceView surfaceView;
//    private MediaPlayer mediaPlayer;

    private MediaController mediaController;

    private MRoundImageView modelImage;
    private TextView modelInformation;
    private TextView modelAgeHeight;
    private TextView modelSignature;
    private TextView commentTextView;
    private TextView likeTextView;
    private TextView itemTextView;

    // like image button
    private ImageButton likedImageButton;

    private LinearLayout buttomLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s03_show);

        likedImageButton = (ImageButton) findViewById(R.id.S03_like_btn);

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

    private void clickLikeShowButton() {

        Map<String, String> likeData = new HashMap<String, String>();
        likeData.put("_id", showDetailEntity.get_id());
        JSONObject jsonObject = new JSONObject(likeData);

        String requestApi = (showDetailEntity.likedByCurrentUser()) ? QSAppWebAPI.getShowUnlikeApi() : QSAppWebAPI.getShowLikeApi();

        MJsonObjectRequest jsonObjectRequest = new MJsonObjectRequest(Request.Method.POST, requestApi, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.get("metadata").toString().equals("{}")) {
                        showMessage(S03SHowActivity.this, showDetailEntity.likedByCurrentUser() ? "取消点赞成功" : "点赞成功");
                        showDetailEntity.setLikedByCurrentUser(!showDetailEntity.likedByCurrentUser());
                        setLikedImageButtonBackgroundImage();
                    } else {
                        handleResponseError(response);
//                        showMessage(S03SHowActivity.this, showDetailEntity.likedByCurrentUser() ? "取消点赞失败" : "点赞失败" + response.toString() + response.get("metadata").toString().length());
                    }
                } catch (Exception e) {
                    showMessage(S03SHowActivity.this, e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showMessage(S03SHowActivity.this, error.toString());
            }
        });

        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void setLikedImageButtonBackgroundImage() {
        if (null == showDetailEntity) {
            return;
        }
        if (showDetailEntity.likedByCurrentUser()) {
            likedImageButton.setBackgroundResource(R.drawable.s03_like_btn_hover);
        } else {
            likedImageButton.setBackgroundResource(R.drawable.s03_like_btn);
        }
        likeTextView.setText(showDetailEntity.getShowLikeNumber());
    }

    private void handleResponseError(JSONObject response) {
        try {
            int errorCode = response.getJSONObject("metadata").getInt("error");
            String errorMessage = showDetailEntity.likedByCurrentUser() ? "取消点赞失败" : "点赞失败";
            switch (errorCode) {
                case 1012:
                    errorMessage = "请先登录！";
                    break;
                case 1000:
                    errorMessage = "服务器错误，请稍微重试！";
                    break;
                default:
                    errorMessage = String.valueOf(errorCode) + response.toString();
                    break;
            }
            showMessage(S03SHowActivity.this, errorMessage);
        } catch (Exception e) {
            showMessage(S03SHowActivity.this, e.toString() + response.toString());
        }
    }

    private void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        Log.i(context.getPackageName(), message);
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

        buttomLayout = (LinearLayout) findViewById(R.id.S03_model_LinearLayout);
    }

    private void showData() {
        if (null == showDetailEntity)
            return;

        itemsData = showDetailEntity.getItemsList();

        videoUriString = showDetailEntity.getShowVideo();

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
                if (S07CollectActivity.isOpened) return;
                S07CollectActivity.isOpened = true;
                Intent intent = new Intent(S03SHowActivity.this, S07CollectActivity.class);
                intent.putExtra(S07CollectActivity.INPUT_BACK_IMAGE, showDetailEntity.getCover());
//                intent.putExtra(S07CollectActivity.INPUT_BRAND_TEXT, showDetailEntity.getBrandNameText());
                Bundle bundle = new Bundle();
                bundle.putSerializable(S07CollectActivity.INPUT_ITEMS, showDetailEntity.getItemsList());
                //bundle.putSerializable(S07CollectActivity.INPUT_BRAND_ENTITY, showDetailEntity.get());
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
                clickLikeShowButton();
            }
        });

        findViewById(R.id.S03_share_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(Intent.ACTION_SEND);
//                intent.setType("image/*");
//                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
//                intent.putExtra(Intent.EXTRA_TEXT, "测试内容!!!");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(Intent.createChooser(intent, getTitle()));

                ShareSDK.initSDK(S03SHowActivity.this);
                OnekeyShare oks = new OnekeyShare();
                //关闭sso授权
                oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字
                oks.setNotification(R.drawable.app_icon, getString(R.string.app_name));
                // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
                oks.setTitle(getString(R.string.share));
                // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                oks.setTitleUrl("http://sharesdk.cn");
                // text是分享文本，所有平台都需要这个字段
                oks.setText("欢迎大家过来使用~");
                // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//                oks.setImagePath(getResources().getResourceName(R.drawable.app_icon));//确保SDcard下面存在此张图片
                // url仅在微信（包括好友和朋友圈）中使用
                oks.setUrl("http://sharesdk.cn");
                // comment是我对这条分享的评论，仅在人人网和QQ空间使用
                oks.setComment("快来试用吧~");
                // site是分享此内容的网站名称，仅在QQ空间使用
                oks.setSite(getString(R.string.app_name));
                // siteUrl是分享此内容的网站地址，仅在QQ空间使用
                oks.setSiteUrl("http://sharesdk.cn");
// 启动分享GUI
                oks.show(S03SHowActivity.this);
            }
        });

        findViewById(R.id.S03_video_start_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVideo();
            }
        });

        this.buttomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(S03SHowActivity.this, P02ModelActivity.class);
//                intent.putExtra(P02ModelActivity.INPUT_MODEL, showDetailEntity);
//                startActivity(intent);
            }
        });

        this.imageIndicatorView.setOnItemChangeListener(new ImageIndicatorView.OnItemChangeListener() {
            @Override
            public void onPosition(int position, int totalCount) {
                Log.d(TAG, "position: " + position % totalCount);
                findViewById(R.id.S03_before_video_view).setVisibility(View.VISIBLE);
                if(position % totalCount == 0)
                    findViewById(R.id.S03_before_video_without_back).setVisibility(View.VISIBLE);
                else
                    findViewById(R.id.S03_before_video_without_back).setVisibility(View.GONE);
            }
        });

        setLikedImageButtonBackgroundImage();
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
        this.imageIndicatorView.getViewPager().setCurrentItem(urlList.length * 100, true);
    }

    private void configVideo() {
        videoView.setDrawingCacheEnabled(true);
        videoView.setVideoPath(videoUriString);
        videoView.requestFocus();
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pauseVideo();
                return false;
            }
        });
    }

    private boolean isFirstStart = true;

    private void startVideo() {
        if (isFirstStart) {
            configVideo();
            isFirstStart = false;
        }
        findViewById(R.id.S03_before_video_view).setVisibility(View.GONE);
//        imageIndicatorView.setVisibility(View.GONE);
//        videoView.setVisibility(View.VISIBLE);
        videoView.start();
    }

    private void pauseVideo() {

//        MediaMetadataRetriever rev = new MediaMetadataRetriever();
//        rev.setDataSource(this, Uri.parse(videoUriString));
//        Bitmap bitmap = rev.getFrameAtTime(videoView.getCurrentPosition() * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

        videoView.pause();

//        View view = findViewById(R.id.S03_relative_layout).getRootView();
//        view.setDrawingCacheEnabled(true);
//        view.buildDrawingCache();
//        Bitmap bitmap = view.getDrawingCache();

//        videoView.buildDrawingCache();
        Bitmap bitmapInput = videoView.getDrawingCache();
//        Bitmap bitmapInput = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        Bitmap bitmap = Bitmap.createBitmap(bitmapInput);
//        Bitmap bitmap = Surface.screenshot((int) dims[0], (int) dims[1]);
//
//        Canvas canvas = new Canvas(bitmapInput);
//        canvas.drawBitmap(bitmap, 0, 0, null);

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
//        savePic(bitmapInput, "test.png");


        this.imageIndicatorView.addBitmapAtFirst(bitmap, ImageLoader.getInstance(), AppUtil.getShowDisplayOptions());
        this.imageIndicatorView.show();

        findViewById(R.id.S03_before_video_view).setVisibility(View.VISIBLE);
    }

    // 保存到sdcard
    private void savePic(Bitmap b, String strFileName) {
        File f = new File("/sdcard/Note/" + strFileName + ".jpg");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            Log.i("test", e.toString());
        }
        b.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            Log.i("test", e.toString());
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            Log.i("test", e.toString());
        }
    }

    private void showIcon(boolean display) {

    }

    private int getWinWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    private int getWinHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }
}
