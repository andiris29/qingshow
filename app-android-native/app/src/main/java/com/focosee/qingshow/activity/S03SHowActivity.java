package com.focosee.qingshow.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Response;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.constants.config.ShareConfig;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.EventModel;
import com.focosee.qingshow.model.GoToWhereAfterLoginModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.persist.SinaAccessTokenKeeper;
import com.focosee.qingshow.util.BitMapUtil;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.UmengCountUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.widget.ConfirmDialog;
import com.focosee.qingshow.widget.SharePopupWindow;
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
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

import static com.focosee.qingshow.R.id.s03_nickname;

public class S03SHowActivity extends BaseActivity implements IWXAPIEventHandler, IWeiboHandler.Response, View.OnClickListener {

    // Input data
    public static final String INPUT_SHOW_ENTITY_ID = "S03SHowActivity_input_show_entity_id";
    public final String TAG = "S03SHowActivity";
    private static final int shareMsgShowTime = 2000;//分享优惠显示时间
    @InjectView(R.id.S03_image_preground)
    SimpleDraweeView s03ImagePreground;
    @InjectView(R.id.S03_describe)
    TextView s03Describe;
    @InjectView(R.id.S03_back_btn)
    ImageButton S03BackBtn;
    @InjectView(R.id.S03_video_start_btn_real)
    ImageView s03VideoStartBtnReal;
    @InjectView(R.id.s03_portrait)
    SimpleDraweeView s03Portrait;
    @InjectView(s03_nickname)
    TextView s03Nickname;
    @InjectView(R.id.s03_del_btn)
    ImageView s03DelBtn;

    private MongoShow showDetailEntity;
    private MongoItem[] itemsData;
    private String videoUriString;
    private int playTime = 0;

    @InjectView(R.id.S03_video_pause)
    ImageView pauseImage;
    @InjectView(R.id.S03_video_view)
    VideoView videoView;
    @InjectView(R.id.S03_image)
    SimpleDraweeView image;
    @InjectView(R.id.S03_comment_text_view)
    TextView commentTextView;
    @InjectView(R.id.S03_like_btn)
    ImageView likeBtn;
    @InjectView(R.id.S03_like_text_view)
    TextView likeTextView;
    @InjectView(R.id.S03_item_text_view)
    TextView itemTextView;
    private SharePopupWindow sharePopupWindow;

    private IWeiboShareAPI mWeiboShareAPI;

    private String showId;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (S04CommentActivity.COMMENT_NUM_CHANGE.equals(intent.getAction())) {
                commentTextView.setText(String.valueOf(Integer.parseInt(commentTextView.getText().toString()) + intent.getIntExtra("value", 0)));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s03_show);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);

        if (!TextUtils.isEmpty(getIntent().getStringExtra(INPUT_SHOW_ENTITY_ID))) {
            showId = getIntent().getStringExtra(INPUT_SHOW_ENTITY_ID);
        }else showId = "";

        if (TextUtils.isEmpty(showId)) {
            Toast.makeText(S03SHowActivity.this, "未知错误，请重试！", Toast.LENGTH_SHORT).show();
            finish();
        }
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, ShareConfig.SINA_APP_KEY);
        mWeiboShareAPI.registerApp();

        S03BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S03SHowActivity.this.finish();
            }
        });

        getShowDetailFromNet();

        registerReceiver(receiver, new IntentFilter(S04CommentActivity.COMMENT_NUM_CHANGE));

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("showDetailEntity", showDetailEntity);
        getIntent().putExtras(outState);
    }

    @Override
    public void reconn() {
        getShowDetailFromNet();
    }

    private void getShowDetailFromNet() {

        final QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getShowDetailApi(showId), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "response:" + response);
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S03SHowActivity.this, MetadataParser.getError(response));
                    return;
                }
                showDetailEntity = ShowParser.parseQuery_parentCategoryString(response).get(0);
                showData();
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void clickLikeShowButton() {
        if (!QSModel.INSTANCE.loggedin()) {
            Toast.makeText(S03SHowActivity.this, R.string.need_login, Toast.LENGTH_SHORT).show();
            GoToWhereAfterLoginModel.INSTANCE.set_class(null);
            startActivity(new Intent(S03SHowActivity.this, U07RegisterActivity.class));
        }
        if (null == showDetailEntity.__context) return;
        likeBtn.setClickable(false);

        final int change = (showDetailEntity.__context.likedByCurrentUser) ? -1 : 1;
        String requestApi = (showDetailEntity.__context.likedByCurrentUser) ? QSAppWebAPI.getShowUnlikeApi() : QSAppWebAPI.getShowLikeApi();
        UserCommand.likeOrFollow(requestApi, showDetailEntity._id, new Callback() {
            @Override
            public void onComplete(JSONObject response) {
                showDetailEntity.__context.likedByCurrentUser = !showDetailEntity.__context.likedByCurrentUser;
                showMessage(S03SHowActivity.this, showDetailEntity.__context.likedByCurrentUser ? "添加收藏" : "取消收藏");
                setLikedImageButtonBackgroundImage();
                likeTextView.setText(String.valueOf(Integer.parseInt(likeTextView.getText().toString()) + change));
                likeBtn.setClickable(true);
                EventModel eventModel = new EventModel(U01UserActivity.class, null);
                eventModel.setFrom(S03SHowActivity.class);
                EventBus.getDefault().post(eventModel);
            }

            @Override
            public void onError(int errorCode) {
                ErrorHandler.handle(S03SHowActivity.this, errorCode);
            }
        });
    }

    private void setLikedImageButtonBackgroundImage() {
        if (null == showDetailEntity) {
            return;
        }
        if (null == showDetailEntity.__context) return;
        if (showDetailEntity.__context.likedByCurrentUser) {
            likeBtn.setImageResource(R.drawable.s03_like_btn_hover);
        } else {
            likeBtn.setImageResource(R.drawable.s03_like_btn);
        }
    }


    private void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        Log.i(context.getPackageName(), message);
    }

    private void showData() {

        commentTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/black_fangzheng_simple.TTF"));
        itemTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/black_fangzheng_simple.TTF"));
        likeTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/black_fangzheng_simple.TTF"));
        if (null == showDetailEntity)
            return;
        if (showDetailEntity.__context.likedByCurrentUser) {
            likeBtn.setImageResource(R.drawable.s03_like_btn_hover);
        }
        itemsData = showDetailEntity.itemRefs;

        videoUriString = showDetailEntity.video;

        if (null != videoUriString && !"".equals(videoUriString))
            s03VideoStartBtnReal.setVisibility(View.VISIBLE);

        s03ImagePreground.setImageURI(Uri.parse(ImgUtil.getImgSrc(showDetailEntity.coverForeground, ImgUtil.LARGE)));
        s03ImagePreground.setAspectRatio(ValueUtil.pre_img_AspectRatio);

        if (null != showDetailEntity.cover) {
            image.setImageURI(Uri.parse(showDetailEntity.cover));
            image.setAspectRatio(ValueUtil.match_img_AspectRatio);
        }

        if (null != showDetailEntity.__context)
            commentTextView.setText(String.valueOf(showDetailEntity.__context.numComments));

        likeTextView.setText(String.valueOf(0 == showDetailEntity.numLike ? 0 : showDetailEntity.numLike));

        if (null != showDetailEntity.itemRefs) {
            itemTextView.setText(String.valueOf(showDetailEntity.itemRefs.length));
        }

        if (!QSModel.INSTANCE.loggedin()) {
            showData_other();
        } else if (showDetailEntity.ownerRef._id.equals(QSModel.INSTANCE.getUser()._id)) {
            showData_self();
        } else {
            showData_other();
        }

        setLikedImageButtonBackgroundImage();
    }

    private void showData_self() {
        s03DelBtn.setVisibility(View.VISIBLE);
        s03Describe.setVisibility(View.VISIBLE);
        s03Describe.setText("发布日期：" + TimeUtil.parseDateString(showDetailEntity.create));
    }

    private void showData_other() {
        s03Portrait.setVisibility(View.VISIBLE);
        if (null != showDetailEntity.ownerRef)
            UserCommand.getPeople(new Callback() {
                @Override
                public void onComplete(JSONObject response) {
                    showDetailEntity.ownerRef = UserParser._parsePeoples(response).get(0);
                    if (null != showDetailEntity.ownerRef.portrait)
                        s03Portrait.setImageURI(Uri.parse(ImgUtil.getImgSrc(showDetailEntity.ownerRef.portrait, ImgUtil.PORTRAIT_LARGE)));
                    s03Nickname.setVisibility(View.VISIBLE);
                    s03Nickname.setText(showDetailEntity.ownerRef.nickname);
                }
            }, S03SHowActivity.this, showDetailEntity.ownerRef._id);
    }

    public void pauseVideo() {
        pauseImage.setVisibility(View.VISIBLE);
        videoView.buildDrawingCache();
        pauseImage.setImageBitmap(videoView.getDrawingCache());
        s03VideoStartBtnReal.setImageResource(R.drawable.s03_play_btn);
        videoView.pause();
    }

    public void startVideo() {
        pauseImage.setVisibility(View.GONE);
        videoView.setDrawingCacheEnabled(true);
        if (videoView.getVisibility() == View.VISIBLE) {
            videoView.start();
        } else {
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(Uri.parse(showDetailEntity.video));
            videoView.start();
        }
        s03VideoStartBtnReal.setImageResource(R.drawable.s03_pause_btn);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                s03VideoStartBtnReal.setImageResource(R.drawable.s03_play_btn);
            }
        });
    }

    public void onEventMainThread(EventModel<Integer> event) {
        if (event.tag == S03SHowActivity.class) {
            if (!showDetailEntity.__context.likedByCurrentUser) {
                clickLikeShowButton();
            }
        }
    }

    private void configVideo() {
        videoView.setDrawingCacheEnabled(true);
        videoView.setVideoPath(videoUriString);
        videoView.requestFocus();
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
        msg.thumbData = BitMapUtil.bmpToByteArray(thumb, false, Bitmap.CompressFormat.PNG);
        msg.setThumbImage(thumb);
        msg.title = ShareConfig.SHARE_TITLE;
        msg.description = ShareConfig.SHARE_DESCRIPTION;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
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

    @Override
    public void onClick(View v) {

        Intent intent;

        switch (v.getId()) {
            case R.id.S03_item_btn://搭配清单
                if (null == showDetailEntity.itemRefs || null == showDetailEntity.cover) return;
                intent = new Intent(S03SHowActivity.this, S07CollectActivity.class);
                Bundle bundle = new Bundle();
                ArrayList<MongoItem> itemList = new ArrayList<>();
                Collections.addAll(itemList, showDetailEntity.itemRefs);
                bundle.putSerializable(S07CollectActivity.INPUT_ITEMS, itemList);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.S03_comment_btn://评论
                if (null != showDetailEntity && null != showDetailEntity._id) {
                    if (S04CommentActivity.isOpened) return;
                    S04CommentActivity.isOpened = true;
                    intent = new Intent(S03SHowActivity.this, S04CommentActivity.class);
                    intent.putExtra(S04CommentActivity.INPUT_SHOW_ID, showDetailEntity._id);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else {
                    Toast.makeText(S03SHowActivity.this, "Plese NPC!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.S03_like_btn://收藏
                clickLikeShowButton();
                break;
            case R.id.S03_share_btn://分享
                if (!QSModel.INSTANCE.loggedin()) {
                    Toast.makeText(S03SHowActivity.this, R.string.need_login, Toast.LENGTH_SHORT).show();
                    GoToWhereAfterLoginModel.INSTANCE.set_class(null);
                    startActivity(new Intent(S03SHowActivity.this, U07RegisterActivity.class));
                }
                sharePopupWindow = new SharePopupWindow(S03SHowActivity.this, new ShareClickListener());
                sharePopupWindow.setAnimationStyle(R.style.popwin_anim_style);
                sharePopupWindow.showAtLocation(S03SHowActivity.this.findViewById(R.id.S03_share_btn), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.S03_video_start_btn_real://视频播放
                if (videoView.isPlaying()) {
                    pauseVideo();
                } else {
                    startVideo();
                }
                break;
            case R.id.s03_del_btn:
                final ConfirmDialog dialog = new ConfirmDialog();
                dialog.setTitle(getResources().getString(R.string.s20_dialog)).setConfirm(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideShow();
                        dialog.dismiss();
                    }
                }).setCancel(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }).show(getSupportFragmentManager());
                break;
            case R.id.s03_portrait:
                if(null == showDetailEntity.ownerRef)return;
                intent = new Intent(S03SHowActivity.this, U01UserActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("user", showDetailEntity.ownerRef);
                intent.putExtras(bundle1);
                startActivity(intent);
                break;
        }
    }

    private void hideShow() {
        Map<String, String> params = new HashMap<>();
        params.put("_id", showDetailEntity._id);
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getMatchHideApi(), new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "repsonse_del:" + response);
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S03SHowActivity.this, MetadataParser.getError(response));
                    return;
                }

                Toast.makeText(S03SHowActivity.this, R.string.delete_finish, Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post("refresh");
                finish();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

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
        if (null != getIntent().getExtras()) {
            if(null != getIntent().getExtras().get("showDetailEntity"))
                showDetailEntity = (MongoShow) getIntent().getExtras().get("showDetailEntity");
        }
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
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
