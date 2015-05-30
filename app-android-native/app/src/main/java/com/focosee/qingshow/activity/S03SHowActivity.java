package com.focosee.qingshow.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.constants.config.ShareConfig;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
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
import com.focosee.qingshow.widget.MFullScreenVideoView;
import com.focosee.qingshow.widget.MRoundImageView;
import com.focosee.qingshow.widget.SharePopupWindow;
import com.focosee.qingshow.widget.indicator.ImageIndicatorView;
import com.focosee.qingshow.widget.indicator.NetworkImageIndicatorView;
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
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class S03SHowActivity extends BaseActivity implements IWXAPIEventHandler, IWeiboHandler.Response {

    // Input data
    public static final String INPUT_SHOW_ENTITY_ID = "S03SHowActivity_input_show_entity_id";
    public static final String INPUT_SHOW_LIST_ENTITY = "S03SHowActivity_input_show_list_entity";
    public static String ACTION_MESSAGE = "";//动态变化的
    public final String TAG = "S03SHowActivity";
    private static final int shareMsgShowTime = 2000;//分享优惠显示时间
    private int position;

    private String showId;
    private MongoShow showListEntity;
    private MongoShow showDetailEntity;// TODO remove the duplicated one
    private MongoItem[] itemsData;
    private String videoUriString;
    private int playTime = 0;

    private VideoView videoView;
    @InjectView(R.id.S03_image)
    SimpleDraweeView image;
    @InjectView(R.id.S03_comment_text_view)
    TextView commentTextView;
    @InjectView(R.id.S03_like_text_view)
    TextView likeTextView;
    @InjectView(R.id.S03_item_text_view)
    TextView itemTextView;
    private SharePopupWindow sharePopupWindow;

    private IWeiboShareAPI mWeiboShareAPI;

    // like image button
    @InjectView(R.id.S03_like_btn)
    ImageView likedImageButton;
    @InjectView(R.id.S03_video_start_btn)
    ImageView playImageButton;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (S04CommentActivity.COMMENT_NUM_CHANGE.equals(intent.getAction())) {
                commentTextView.setText(String.valueOf(Integer.parseInt(commentTextView.getText().toString()) + intent.getIntExtra("value", 0)));
            }
        }
    };

//    public void setState(State state) {
//        this.mState = state;
//        onStateChanged(state);
//    }

//    public void onStateChanged(State state) {
//        switch (state) {
//            case RESET:
//                onReset();
//                break;
//            case AFTER_PLAY:
//                onAfterPlay();
//                break;
//            case START_PLAY:
//                onStartPlay();
//                break;
//        }
//    }

//    private void onStartPlay() {
//        if (isFirstStart) {
//            configVideo();
//            imageIndicatorView.addViewAtFirst(videoView, false);
//            isFirstStart = false;
//            imageIndicatorView.getViewPager().setCurrentItem(0, false);
//            imageIndicatorView.show();
//        }
//        imageIndicatorView.getIndicateLayout().setVisibility(View.INVISIBLE);
//        showOneView(beforeLayout, playImageButton.getId());
//        findViewById(R.id.S03_back_btn).setVisibility(View.INVISIBLE);
//        imageIndicatorView.getViewPager().setScrollEnabled(false);
//        isPlayed = true;
//        videoView.start();
//    }
//
//    private void onAfterPlay() {
//        videoView.pause();
//
//        imageIndicatorView.getIndicateLayout().setVisibility(View.VISIBLE);
//        playImageButton.setImageResource(R.drawable.s03_play_btn);
//        findViewById(R.id.S03_back_btn).setVisibility(View.VISIBLE);
//        imageIndicatorView.getViewPager().setScrollEnabled(true);
//        showAllView(beforeLayout);
//
//    }
//
//    private void onReset() {
//        if (isPlayed) {
//            imageIndicatorView.removeViewItemAtIndex(0);
//            imageIndicatorView.show();
//            findViewById(R.id.S03_before_video_without_back).setVisibility(View.VISIBLE);
//            isPlayed = false;
//            isFirstStart = true;
//        }
//
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s03_show);
        ButterKnife.inject(this);
        final TextView shareMsgTextView = (TextView)findViewById(R.id.S03_share_msg);

        shareMsgTextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                shareMsgTextView.setVisibility(View.GONE);
            }
        }, shareMsgShowTime);


        likedImageButton = (ImageView) findViewById(R.id.S03_like_btn);
        playImageButton = (ImageView) findViewById(R.id.S03_video_start_btn);

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
        if (null != intent.getSerializableExtra(S03SHowActivity.INPUT_SHOW_LIST_ENTITY)) {
            showListEntity = (MongoShow) intent.getSerializableExtra(S03SHowActivity.INPUT_SHOW_LIST_ENTITY);
            position = intent.getIntExtra("position", 0);
        }
        if (null != intent.getSerializableExtra(S03SHowActivity.INPUT_SHOW_ENTITY_ID)) {
            showId = intent.getStringExtra(S03SHowActivity.INPUT_SHOW_ENTITY_ID);
            position = intent.getIntExtra("position", 0);
        }

        System.out.println("showId:" + showId);
        getShowDetailFromNet();

        matchUI();

        registerReceiver(receiver, new IntentFilter(S04CommentActivity.COMMENT_NUM_CHANGE));

    }

    @Override
    public void reconn() {
        getShowDetailFromNet();
    }

    private void getShowDetailFromNet() {
        final QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getShowDetailApi(showId), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(S03SHowActivity.this, MetadataParser.getError(response));
                    return;
                }
                showDetailEntity = ShowParser.parseQuery(response).get(0);
                showData();
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void clickLikeShowButton() {
        if (null == showDetailEntity.__context)return;
        likedImageButton.setClickable(false);
        Map<String, String> likeData = new HashMap<>();
        likeData.put("_id", showDetailEntity._id);
        JSONObject jsonObject = new JSONObject(likeData);

        String requestApi = (showDetailEntity.__context.likedByCurrentUser) ? QSAppWebAPI.getShowUnlikeApi() : QSAppWebAPI.getShowLikeApi();
        final int change = (showDetailEntity.__context.likedByCurrentUser) ? 1 : -1;

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, requestApi, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (!MetadataParser.hasError(response)) {
                    showMessage(S03SHowActivity.this, showDetailEntity.__context.likedByCurrentUser ? "取消点赞成功" : "点赞成功");
                    showDetailEntity.__context.likedByCurrentUser = !showDetailEntity.__context.likedByCurrentUser;
                    Intent intent = new Intent(ACTION_MESSAGE);
                    intent.putExtra("position", position);
                    sendBroadcast(intent);
                    setLikedImageButtonBackgroundImage();
                    likeTextView.setText(String.valueOf(Integer.parseInt(likeTextView.getText().toString()) + change));
                    likedImageButton.setClickable(true);
                    UserCommand.refresh();
                } else {
                    handleResponseError(response);
                }
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void setLikedImageButtonBackgroundImage() {
        if (null == showDetailEntity) {
            return;
        }
        if(null == showDetailEntity.__context)return;
        if (showDetailEntity.__context.likedByCurrentUser) {
            likedImageButton.setBackgroundResource(R.drawable.s03_like_btn_hover);
        } else {
            likedImageButton.setBackgroundResource(R.drawable.s03_like_btn);
        }

    }

    private void handleResponseError(JSONObject response) {
//        int errorCode = MetadataParser.getError(response);
//        String errorMessage = showDetailEntity.likedByCurrentUser() ? "取消点赞失败" : "点赞失败";
//        switch (errorCode) {
//            case 1012:
//                errorMessage = "请先登录！";
//                break;
//            case 1000:
//                errorMessage = "服务器错误，请稍后重试！";
//                break;
//            default:
//                errorMessage = String.valueOf(errorCode) + response.toString();
//                break;
//        }
//        showMessage(S03SHowActivity.this, errorMessage);
    }

    private void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        Log.i(context.getPackageName(), message);
    }

    private void matchUI() {
        this.videoView = new MFullScreenVideoView(this);
        videoView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    private void showData() {
        if (null == showDetailEntity)
            return;
        if (null == showDetailEntity.__context)return;
        if(showDetailEntity.__context.likedByCurrentUser){
            likedImageButton.setImageResource(R.drawable.s03_like_btn_hover);
        }
        itemsData = showDetailEntity.itemRefs;

        videoUriString = showDetailEntity.video;

        image.setImageURI(Uri.parse(showDetailEntity.cover));

        commentTextView.setText(String.valueOf(showDetailEntity.__context.numComments));

        likeTextView.setText(String.valueOf(showDetailEntity.numLike));

        itemTextView.setText(String.valueOf(showDetailEntity.itemRefs.length));

//        this.initPosterView(showDetailEntity.getPosters());

        findViewById(R.id.S03_item_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (S07CollectActivity.isOpened) return;
//                if(null == showDetailEntity.getItemsList() || null == showDetailEntity.getCover())return;
//                S07CollectActivity.isOpened = true;
//                Intent intent = new Intent(S03SHowActivity.this, S07CollectActivity.class);
//                intent.putExtra(S07CollectActivity.INPUT_BACK_IMAGE, ImgUtil.imgTo2x(showDetailEntity.getCover()));
//                Bundle bundle = new Bundle();
//                bundle.putSerializable(S07CollectActivity.INPUT_ITEMS, showDetailEntity.getItemsList());
//                intent.putExtras(bundle);
//                startActivity(intent);
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
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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
//                if (videoView.isPlaying()) pauseVideo();
//                else startVideo();

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

    private void configVideo() {
        videoView.setDrawingCacheEnabled(true);
        videoView.setVideoPath(videoUriString);
        videoView.requestFocus();
    }

    private boolean isFirstStart = true;

    private void showOneView(ViewGroup viewGroup, int id) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i).getId() != id)
                viewGroup.getChildAt(i).setVisibility(View.INVISIBLE);
        }
    }

    private void showAllView(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            viewGroup.getChildAt(i).setVisibility(View.VISIBLE);
        }
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
        QSApplication.instance().getWxApi().handleIntent(intent, this);
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    private void shareToSina() {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = ShareConfig.SHARE_TITLE;
        mediaObject.description = ShareConfig.SHARE_DESCRIPTION;
        mediaObject.setThumbImage(BitmapFactory.decodeResource(getResources(), ShareConfig.IMG));
        mediaObject.actionUrl = ShareConfig.SHARE_SHOW_URL + showDetailEntity._id;
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
            public void onWeiboException(WeiboException arg0) {
            }

            @Override
            public void onComplete(Bundle bundle) {
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
                UmengCountUtil.countShareShow(this, "weibo");
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


    private void shareToWX(boolean isTimelineCb) {

        WXWebpageObject webpage = new WXWebpageObject();
        WXMediaMessage msg;
        webpage.webpageUrl = ShareConfig.SHARE_SHOW_URL + showDetailEntity._id;

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
        UmengCountUtil.countShareShow(this, "weixin");
        QSApplication.instance().getWxApi().sendReq(req);
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


    class ShareClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
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
        MobclickAgent.onPageStart("S03Show");
        MobclickAgent.onResume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView.isShown()) {
            playTime = videoView.getCurrentPosition();
            UmengCountUtil.countPlayVideo(this, showId, playTime);
        }
        MobclickAgent.onPageEnd("S03Show");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
