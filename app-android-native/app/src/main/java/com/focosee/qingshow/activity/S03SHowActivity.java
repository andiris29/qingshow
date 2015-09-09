package com.focosee.qingshow.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import com.android.volley.Response;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.constants.config.ShareConfig;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.EventModel;
import com.focosee.qingshow.model.GoToWhereAfterLoginModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.util.ShareUtil;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.UmengCountUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.util.filter.Filter;
import com.focosee.qingshow.util.filter.FilterHepler;
import com.focosee.qingshow.util.people.PeopleHelper;
import com.focosee.qingshow.widget.ConfirmDialog;
import com.focosee.qingshow.widget.QSTextView;
import com.focosee.qingshow.widget.SharePopupWindow;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import static com.focosee.qingshow.R.id.s03_nickname;

public class S03SHowActivity extends MenuActivity implements IWeiboHandler.Response {

    // Input data
    public static final String INPUT_SHOW_ENTITY_ID = "S03SHowActivity_input_show_entity_id";
    public static final String CLASS_NAME = "class_name";
    @InjectView(R.id.S03_image_preground)
    SimpleDraweeView s03ImagePreground;
    @InjectView(R.id.S03_describe)
    TextView s03Describe;
    @InjectView(R.id.S03_back_btn)
    ImageView s03BackBtn;
    @InjectView(R.id.S03_video_start_btn_real)
    ImageView s03VideoStartBtnReal;
    @InjectView(R.id.s03_portrait)
    SimpleDraweeView s03Portrait;
    @InjectView(s03_nickname)
    TextView s03Nickname;
    @InjectView(R.id.s03_del_btn)
    ImageView s03DelBtn;
    @InjectView(R.id.s03_bonus)
    QSTextView s03Bonus;

    private MongoShow showDetailEntity;
    private List<MongoItem> itemsData;
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
    private String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s03_show);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        if (!TextUtils.isEmpty(getIntent().getStringExtra(INPUT_SHOW_ENTITY_ID))) {
            showId = getIntent().getStringExtra(INPUT_SHOW_ENTITY_ID);
        } else showId = "";
        className = getIntent().getStringExtra(CLASS_NAME);

        if (TextUtils.isEmpty(showId)) {
            finish();
        }
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, ShareConfig.SINA_APP_KEY);
        mWeiboShareAPI.registerApp();

        if (S20MatchPreviewActivity.class.getSimpleName().equals(className)) {
            s03BackBtn.setImageResource(R.drawable.nav_btn_menu_n);
            s03BackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuSwitch();
                }
            });
            initDrawer();
        } else {
            s03BackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    S03SHowActivity.this.finish();
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(INPUT_SHOW_ENTITY_ID, showId);
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
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S03SHowActivity.this, MetadataParser.getError(response));
                    return;
                }
                showDetailEntity = ShowParser.parseQuery_categoryString(response).get(0);
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
                setLikedImageButtonBackgroundImage();
                likeTextView.setText(String.valueOf(Integer.parseInt(likeTextView.getText().toString()) + change));
                likeBtn.setClickable(true);
                EventBus.getDefault().post(new ShowCollectionEvent(showDetailEntity.__context.likedByCurrentUser));
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

    private void showData() {
        if (null == showDetailEntity)
            return;

        itemsData = showDetailEntity.itemRefs;
        videoUriString = showDetailEntity.video;

        if (!TextUtils.isEmpty(videoUriString))
            s03VideoStartBtnReal.setVisibility(View.VISIBLE);

        s03ImagePreground.setImageURI(Uri.parse(ImgUtil.getImgSrc(showDetailEntity.coverForeground, ImgUtil.LARGE)));
        s03ImagePreground.setAspectRatio(ValueUtil.pre_img_AspectRatio);

        if (null != showDetailEntity.cover) {
            image.setImageURI(Uri.parse(showDetailEntity.cover));
            image.setAspectRatio(ValueUtil.match_img_AspectRatio);
        }

        if (null != showDetailEntity.__context) {
            commentTextView.setText(String.valueOf(showDetailEntity.__context.numComments));
            if (showDetailEntity.__context.likedByCurrentUser) {
                likeBtn.setImageResource(R.drawable.s03_like_btn_hover);
            }
        }

        likeTextView.setText(String.valueOf(0 == showDetailEntity.numLike ? 0 : showDetailEntity.numLike));

        if (null != showDetailEntity.itemRefs) {
            FilterHepler.filterList(showDetailEntity.itemRefs, new Filter() {
                @Override
                public <T> boolean filtrate(T t) {
                    MongoItem item = (MongoItem) t;
                    if (!TextUtils.isEmpty(item.delist))
                        return true;
                    return false;
                }
            });
            itemTextView.setText(String.valueOf(showDetailEntity.itemRefs.size()));
        }

        setLikedImageButtonBackgroundImage();

        if (QSModel.INSTANCE.loggedin()) {
            if (null != showDetailEntity.ownerRef) {
                if (showDetailEntity.ownerRef._id.equals(QSModel.INSTANCE.getUserId()) && (className.equals(U01UserActivity.class.getSimpleName()))) {
                    showData_self();
                    return;
                }
            }
        }
        showData_other();

        if(null != showDetailEntity.ownerRef){
            if(null != showDetailEntity.ownerRef.bonuses){
                s03Bonus.setText(getText(R.string.get_bonuses_label) + PeopleHelper.getTotalBonuses(showDetailEntity.ownerRef.bonuses));
            }
        }
    }

    private void showData_self() {
        s03DelBtn.setVisibility(View.VISIBLE);
        s03Describe.setVisibility(View.VISIBLE);
        s03Describe.setText("发布日期：" + TimeUtil.parseDateString(showDetailEntity.create));
    }

    private void showData_other() {
        s03Portrait.setVisibility(View.VISIBLE);
        s03Nickname.setVisibility(View.VISIBLE);
        if (null == showDetailEntity.ownerRef) return;
        s03Portrait.setImageURI(Uri.parse(ImgUtil.getImgSrc(showDetailEntity.ownerRef.portrait, ImgUtil.PORTRAIT_LARGE)));
        s03Nickname.setText(showDetailEntity.ownerRef.nickname);

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
        if (event.tag == S03SHowActivity.class.getSimpleName()) {
            if (!showDetailEntity.__context.likedByCurrentUser) {
                clickLikeShowButton();
            }
        }
    }

    public void onEventMainThread(S04PostCommentEvent event) {
        switch (event.action) {
            case S04PostCommentEvent.addComment:
                commentTextView.setText(String.valueOf(Integer.parseInt(commentTextView.getText().toString()) + 1));
                break;
            case S04PostCommentEvent.delComment:
                commentTextView.setText(String.valueOf(Integer.parseInt(commentTextView.getText().toString()) - 1));
                break;
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
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                UmengCountUtil.countShareShow(this, "weibo");
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
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
                itemList.addAll(showDetailEntity.itemRefs);
                bundle.putSerializable(S07CollectActivity.INPUT_ITEMS, itemList);
                intent.putExtras(bundle);
                startActivity(intent);
                return;
            case R.id.S03_comment_btn://评论
                if (null != showDetailEntity && null != showDetailEntity._id) {
                    if (S04CommentActivity.isOpened) return;
                    S04CommentActivity.isOpened = true;
                    intent = new Intent(S03SHowActivity.this, S04CommentActivity.class);
                    intent.putExtra(S04CommentActivity.INPUT_SHOW_ID, showDetailEntity._id);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
                return;
            case R.id.S03_like_btn://收藏
                clickLikeShowButton();
                return;
            case R.id.S03_share_btn://分享
                if (!QSModel.INSTANCE.loggedin()) {
                    Toast.makeText(S03SHowActivity.this, R.string.need_login, Toast.LENGTH_SHORT).show();
                    GoToWhereAfterLoginModel.INSTANCE.set_class(null);
                    startActivity(new Intent(S03SHowActivity.this, U07RegisterActivity.class));
                }
                sharePopupWindow = new SharePopupWindow(S03SHowActivity.this, new ShareClickListener(S03SHowActivity.this));
                sharePopupWindow.setAnimationStyle(R.style.popwin_anim_style);
                sharePopupWindow.showAtLocation(S03SHowActivity.this.findViewById(R.id.S03_share_btn), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                return;
            case R.id.S03_video_start_btn_real://视频播放
                if (videoView.isPlaying()) {
                    pauseVideo();
                } else {
                    startVideo();
                }
                return;
            case R.id.s03_del_btn:
                final ConfirmDialog dialog = new ConfirmDialog(this);
                dialog.setTitle(getResources().getString(R.string.s20_dialog));
                dialog.setConfirm(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideShow();
                        dialog.dismiss();
                    }
                });
                dialog.setCancel(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return;
            case R.id.s03_portrait:
                if (null == showDetailEntity.ownerRef) return;
                intent = new Intent(S03SHowActivity.this, U01UserActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("user", showDetailEntity.ownerRef);
                intent.putExtras(bundle1);
                startActivity(intent);
                return;
        }
        super.onClick(v);
    }

    private void hideShow() {
        Map<String, String> params = new HashMap<>();
        params.put("_id", showDetailEntity._id);
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getMatchHideApi(), new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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

        public Context context;

        public ShareClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.share_wechat:
                    ShareUtil.shareShowToWX(showDetailEntity._id, ValueUtil.SHARE_SHOW, context, false);
                    break;
                case R.id.share_wx_timeline:
                    ShareUtil.shareShowToWX(showDetailEntity._id, ValueUtil.SHARE_SHOW, context, true);
                    break;
                case R.id.share_sina:
                    ShareUtil.shareShowToSina(showDetailEntity._id, context, mWeiboShareAPI);
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        MobclickAgent.onPageStart("S03Show");
        MobclickAgent.onResume(this);
        reconn();
        super.onResume();
//        if(TextUtils.isEmpty(getIntent().getStringExtra(INPUT_SHOW_ENTITY_ID))) {
//            showId = getIntent().getStringExtra(INPUT_SHOW_ENTITY_ID);
//            getShowDetailFromNet();
//        }
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
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (className.equals(S20MatchPreviewActivity.class.getSimpleName())) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                menuSwitch();
            }
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (videoView.getVisibility() == View.VISIBLE) {
                    videoView.setVisibility(View.GONE);
                    s03VideoStartBtnReal.setImageResource(R.drawable.s03_play_btn);
                    return true;
                }
                finish();
            }
        }
        return true;
    }
}
