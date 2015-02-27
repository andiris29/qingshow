package com.focosee.qingshow.activity;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.allthelucky.common.view.ImageIndicatorView;
import com.allthelucky.common.view.network.NetworkImageIndicatorView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.focosee.qingshow.R;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.constants.config.ShareConfig;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.persist.SinaAccessTokenKeeper;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.BitMapUtil;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.util.UmengCountUtil;
import com.focosee.qingshow.widget.MRoundImageView;
import com.focosee.qingshow.widget.SharePopupWindow;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class S03SHowActivity extends BaseActivity implements IWXAPIEventHandler ,IWeiboHandler.Response {

    // Input data
    public static final String INPUT_SHOW_ENTITY_ID = "S03SHowActivity_input_show_entity_id";
    public static final String INPUT_SHOW_LIST_ENTITY = "S03SHowActivity_input_show_list_entity";
    public static String ACTION_MESSAGE = "";
    public final String TAG = "S03SHowActivity";
    private int position;

    private String showId;
    private MongoShow showListEntity;
    private MongoShow showDetailEntity;// TODO remove the duplicated one
    private ArrayList<MongoItem> itemsData;
    private String videoUriString;
    private int playTime = 0;

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
    private SharePopupWindow sharePopupWindow;

    private IWXAPI wxApi;

    private IWeiboShareAPI mWeiboShareAPI;


    // like image button
    private ImageView likedImageButton;
    private ImageView playImageButton;

    private LinearLayout buttomLayout;
    private RelativeLayout beforeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s03_show);


        likedImageButton = (ImageView) findViewById(R.id.S03_like_btn);
        playImageButton = (ImageView) findViewById(R.id.S03_video_start_btn);
        beforeLayout = (RelativeLayout) findViewById(R.id.S03_before_video_without_back);

        wxApi = WXAPIFactory.createWXAPI(this, ShareConfig.WX_APP_KEY, true);
        wxApi.registerApp(ShareConfig.WX_APP_KEY);


       // mSsoHandler = new SsoHandler(this, mAuthInfo);
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, ShareConfig.SINA_APP_KEY);
        mWeiboShareAPI.registerApp();

        findViewById(R.id.S03_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S03SHowActivity.this.finish();
            }
        });

        Intent intent = getIntent();
        if(null != intent.getSerializableExtra(S03SHowActivity.INPUT_SHOW_LIST_ENTITY)){
            showListEntity = (MongoShow) intent.getSerializableExtra(S03SHowActivity.INPUT_SHOW_LIST_ENTITY);
            position = intent.getIntExtra("position", 0);
        }
        showId = intent.getStringExtra(S03SHowActivity.INPUT_SHOW_ENTITY_ID);
        //if(null == intent.getSerializableExtra(S03SHowActivity.INPUT_SHOW_ENTITY)){
        getShowDetailFromNet();
        //}else {
        //    showDetailEntity = (MongoShowD) intent.getSerializableExtra(S03SHowActivity.INPUT_SHOW_ENTITY);
        //}

        matchUI();

    }

    private void getShowDetailFromNet() {
        final QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getShowDetailApi(showId), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("S03ShowActivity", response.toString());
                showDetailEntity = ShowParser.parseQuery(response).get(0);
                showData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("S03ShowActivity", error.toString());
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void clickLikeShowButton() {
        likedImageButton.setClickable(false);
        Map<String, String> likeData = new HashMap<String, String>();
        likeData.put("_id", showDetailEntity.get_id());
        JSONObject jsonObject = new JSONObject(likeData);

        String requestApi = (showDetailEntity.likedByCurrentUser()) ? QSAppWebAPI.getShowUnlikeApi() : QSAppWebAPI.getShowLikeApi();

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, requestApi, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (!MetadataParser.hasError(response)) {
                        showMessage(S03SHowActivity.this, showDetailEntity.likedByCurrentUser() ? "取消点赞成功" : "点赞成功");
                        showDetailEntity.setLikedByCurrentUser(!showDetailEntity.likedByCurrentUser());
                        if(showDetailEntity.likedByCurrentUser()) {//发送广播，更新首页的numLike
                            Intent intent = new Intent(ACTION_MESSAGE);
                            intent.putExtra("position", position);
                            sendBroadcast(intent);
                        }
                        setLikedImageButtonBackgroundImage();
                        likedImageButton.setClickable(true);
                        showListEntity.numLike = showDetailEntity.numLike;
                    } else {
                        handleResponseError(response);
//                        showMessage(S03SHowActivity.this, showDetailEntity.likedByCurrentUser() ? "取消点赞失败" : "点赞失败");
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

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
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
            int errorCode = MetadataParser.getError(response);
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

        commentTextView.setText(showDetailEntity.getShowCommentNumber());

        likeTextView.setText(showDetailEntity.getShowLikeNumber());

        itemTextView.setText(showDetailEntity.getItemsCount());

        this.initPosterView(showDetailEntity.getPosters());

        modelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S03SHowActivity.this, P02ModelActivity.class);
                intent.putExtra(P02ModelActivity.INPUT_MODEL, showDetailEntity.modelRef);
                S03SHowActivity.this.startActivity(intent);

            }
        });

        findViewById(R.id.S03_item_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (S07CollectActivity.isOpened) return;
                S07CollectActivity.isOpened = true;
                Intent intent = new Intent(S03SHowActivity.this, S07CollectActivity.class);
                intent.putExtra(S07CollectActivity.INPUT_BACK_IMAGE, ImgUtil.imgTo2x(showDetailEntity.getCover()));
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
                    if (S04CommentActivity.isOpened) return;
                    S04CommentActivity.isOpened = true;
                    Intent intent = new Intent(S03SHowActivity.this, S04CommentActivity.class);
                    intent.putExtra(S04CommentActivity.INPUT_SHOW_ID, showDetailEntity._id);
                    startActivity(intent);
                } else {
                    Toast.makeText(S03SHowActivity.this, "Plese NPC!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        likedImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickLikeShowButton();
            }
        });

        findViewById(R.id.S03_share_btn).setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                sharePopupWindow = new SharePopupWindow(S03SHowActivity.this, new ShareClickListener());
                sharePopupWindow.setAnimationStyle(R.style.popwin_anim_style);
                sharePopupWindow.showAtLocation(S03SHowActivity.this.findViewById(R.id.S03_share_btn), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

            }
        });

        playImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playImageButton.setImageResource(R.drawable.s03_pause_btn);
                if(videoView.isPlaying()) pauseVideo();
                else startVideo();

            }
        });

        this.buttomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        this.imageIndicatorView.setOnItemChangeListener(new ImageIndicatorView.OnItemChangeListener() {
            @Override
            public void onPosition(int position, int totalCount) {
                if(videoView.getVisibility() == View.GONE) return;
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
        this.imageIndicatorView.setupLayoutByImageUrl(Arrays.asList(urlList), ImageLoader.getInstance());
        this.imageIndicatorView.show();
        this.imageIndicatorView.getViewPager().setCurrentItem(urlList.length * 100, true);
    }

    private void configVideo() {
        videoView.setDrawingCacheEnabled(true);
        videoView.setVideoPath(videoUriString);
        videoView.requestFocus();
    }

    private boolean isFirstStart = true;

    private void startVideo() {
        if (isFirstStart) {
            configVideo();
            isFirstStart = false;
        }
        showOneView(beforeLayout, playImageButton.getId());
        imageIndicatorView.setVisibility(View.INVISIBLE);
        findViewById(R.id.S03_back_btn).setVisibility(View.INVISIBLE);
        videoView.setVisibility(View.VISIBLE);
        videoView.start();
    }

    private void showOneView(ViewGroup viewGroup,int id){
        for (int i = 0;i < viewGroup.getChildCount();i++){
            if(viewGroup.getChildAt(i).getId() != id)  viewGroup.getChildAt(i).setVisibility(View.INVISIBLE);
        }
    }

    private void showAllView(ViewGroup viewGroup){
        for (int i = 0;i < viewGroup.getChildCount();i++){
             viewGroup.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }

    private void pauseVideo() {

        videoView.pause();

        MediaMetadataRetriever rev = new MediaMetadataRetriever();
        Bitmap bitmap = null;
        try {
            rev.setDataSource(videoUriString,new HashMap<String, String>());
            bitmap = rev.getFrameAtTime(videoView.getCurrentPosition() * 1000,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        }catch (Exception e){

        }
        imageIndicatorView.addBitmapAtFirst(bitmap);
        imageIndicatorView.show();
        imageIndicatorView.setVisibility(View.VISIBLE);
        playImageButton.setImageResource(R.drawable.s03_play_btn);
        findViewById(R.id.S03_back_btn).setVisibility(View.VISIBLE);
        showAllView(beforeLayout);

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


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        wxApi.handleIntent(intent, this);
        mWeiboShareAPI.handleWeiboResponse(intent,this);
    }

    private void shareToSina(){
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title =ShareConfig.SHARE_TITLE;
        mediaObject.description =ShareConfig.SHARE_DESCRIPTION;
        mediaObject.setThumbImage(BitmapFactory.decodeResource(getResources(), ShareConfig.IMG));
        mediaObject.actionUrl =  ShareConfig.SHARE_SHOW_URL +showDetailEntity.get_id();
        mediaObject.defaultText = ShareConfig.SHARE_DESCRIPTION;

        weiboMessage.mediaObject = mediaObject;

        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        AuthInfo authInfo = new AuthInfo(this, ShareConfig.SINA_APP_KEY, ShareConfig.SINA_REDIRECT_URL, ShareConfig.SCOPE);
        Oauth2AccessToken accessToken = SinaAccessTokenKeeper.readAccessToken(getApplicationContext());
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }
        mWeiboShareAPI.sendRequest(this, request, authInfo, token, new WeiboAuthListener() {

            @Override
            public void onWeiboException( WeiboException arg0 ) {
            }

            @Override
            public void onComplete( Bundle bundle ) {
                // TODO Auto-generated method stub
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                SinaAccessTokenKeeper.writeAccessToken(getApplicationContext(), newToken);
            }

            @Override
            public void onCancel() {
            }
        });

    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                UmengCountUtil.countShareShow(this,"weibo");
                Log.i("tag", "ERR_OK");
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                Log.i("tag", "ERR_CANCEL");
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                Log.i("tag", "ERR_FAIL");
                break;
        }
    }



    private void shareToWX(boolean isTimelineCb){

        WXWebpageObject webpage = new WXWebpageObject();
        WXMediaMessage msg;
        webpage.webpageUrl = ShareConfig.SHARE_SHOW_URL +showDetailEntity.get_id();

        msg = new WXMediaMessage();
        msg.mediaObject = webpage;
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), ShareConfig.IMG);
        msg.thumbData = BitMapUtil.bmpToByteArray(thumb, false);
        msg.setThumbImage(thumb);
        msg.title = ShareConfig.SHARE_TITLE;
        msg.description = ShareConfig.SHARE_DESCRIPTION;


        final SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = isTimelineCb ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        UmengCountUtil.countShareShow(this,"weixin");
        wxApi.sendReq(req);
    }


    @Override
    public void onReq(BaseReq req) {
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                Log.i("tag", "COMMAND_GETMESSAGE_FROM_WX");
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                Log.i("tag", "COMMAND_SHOWMESSAGE_FROM_WX");
                break;
            default:
                break;
        }
    }

    @Override
    public void onResp(BaseResp resp) {


        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Log.i("tag", "ERR_OK");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Log.i("tag", "ERR_USER_CANCEL");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Log.i("tag", "ERR_AUTH_DENIED");
                break;
            default:
                Log.i("tag", "ERR_OK");
                break;
        }

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (mSsoHandler != null) {
//            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
//    }


    class ShareClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.share_wechat:
                    shareToWX(false);
                    break;
                case R.id.share_wx_timeline:
                    shareToWX(true);
                    break;
                case R.id.share_sina:
                    shareToSina();
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("S03Show"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        playTime = videoView.getCurrentPosition();
        UmengCountUtil.countPlayVideo(this,showId,playTime);
        MobclickAgent.onPageEnd("S03Show"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}
