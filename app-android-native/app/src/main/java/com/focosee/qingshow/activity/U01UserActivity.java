package com.focosee.qingshow.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.Response;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.fragment.U01BaseFragment;
import com.focosee.qingshow.activity.fragment.U01CollectionFragment;
import com.focosee.qingshow.activity.fragment.U01FansFragment;
import com.focosee.qingshow.activity.fragment.U01FollowerFragment;
import com.focosee.qingshow.activity.fragment.U01MatchFragment;
import com.focosee.qingshow.activity.fragment.U01RecommFragment;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.constants.config.QSPushAPI;
import com.focosee.qingshow.constants.config.UserConfig;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.EventModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.aggregation.BonusAmount;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.receiver.PushGuideEvent;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.util.bonus.BonusHelper;
import com.focosee.qingshow.util.user.UnreadHelper;
import com.focosee.qingshow.widget.LoadingDialogs;
import com.focosee.qingshow.widget.MViewPager_NoScroll;
import com.focosee.qingshow.widget.MenuView;
import com.focosee.qingshow.widget.QSTextView;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class U01UserActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "U01UserActivity";

    public static final String NEW_RECOMMANDATIONS = "NEW_RECOMMANDATIONS";

    public static final int POS_MATCH = 0;
    public static final int POS_RECOMM = 1;
    public static final int POS_COLL = 2;
    public static final int POS_FOLLOW = 3;
    public static final int POS_FANS = 4;
    public static final int PAGER_NUM = 5;

    @InjectView(R.id.user_bg)
    SimpleDraweeView userBg;

    @InjectView(R.id.user_head_layout)
    RelativeLayout userHeadLayout;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.user_hw)
    TextView userHw;
    @InjectView(R.id.user_follow_btn)
    ImageView userFollowBtn;
    @InjectView(R.id.user_match)
    ImageView userMatch;
    @InjectView(R.id.user_recomm)
    ImageView userRecomm;
    @InjectView(R.id.user_collection)
    ImageView userCollection;
    @InjectView(R.id.user_follow)
    ImageView userFollow;
    @InjectView(R.id.user_fans)
    ImageView userFans;
    @InjectView(R.id.user_viewPager)
    MViewPager_NoScroll userViewPager;
    @InjectView(R.id.user_match_text)
    TextView userMatchText;
    @InjectView(R.id.user_recomm_text)
    TextView userRecommText;
    @InjectView(R.id.user_collection_text)
    TextView userCollectionText;
    @InjectView(R.id.user_follow_text)
    TextView userFollowText;
    @InjectView(R.id.user_fans_text)
    TextView userFansText;
    @InjectView(R.id.user_head)
    SimpleDraweeView userHead;
    @InjectView(R.id.iv_rank_gold)
    ImageView ivRank;
    @InjectView(R.id.user_nav_btn)
    ImageView userNavBtn;
    @InjectView(R.id.circle_tip)
    View circleTip;
    @InjectView(R.id.u01_backTop_btn)
    ImageButton u01BackTopBtn;
    @InjectView(R.id.user_bonuses)
    QSTextView userBonuses;
    @InjectView(R.id.container)
    FrameLayout container;
    @InjectView(R.id.u01_setting)
    ImageView setting;
    @InjectView(R.id.bonus)
    TextView bonus;


    private List<MongoShow> datas;
    private UserPagerAdapter pagerAdapter;
    private EventBus eventBus;
    private MongoPeople user;

    private BackBtnListener btnListener;
    private MenuView menuView;

    private boolean isMyself = true;

    private LoadingDialogs dialogs;

    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u01_base);
        ButterKnife.inject(this);
        dialogs = new LoadingDialogs(this);
        userMatchText.setActivated(true);
        userMatch.setActivated(true);
        user = (MongoPeople) getIntent().getExtras().get("user");
        initUserInfo();
        if (user._id.equals(QSModel.INSTANCE.getUserId())) {//进入自己的页面时不显示关注按钮
            mySelf();
        } else {
            isMyself = false;
            others();
        }
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        datas = new LinkedList<>();
        pagerAdapter = new UserPagerAdapter(getSupportFragmentManager());
        userViewPager.setAdapter(pagerAdapter);
        userViewPager.setOffscreenPageLimit(5);
        userViewPager.setCurrentItem(POS_MATCH);
        userViewPager.setScrollble(false);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(U01UserActivity.this, U02SettingsActivity.class);
                startActivity(intent);
            }
        });

        bonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                U01UserActivity.this.startActivity(new Intent(U01UserActivity.this, U15BonusActivity.class));
            }
        });
    }

    private void mySelf() {
        userFollowBtn.setVisibility(View.GONE);
        userNavBtn.setVisibility(View.VISIBLE);
        userNavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNavBtn.setImageResource(R.drawable.menu_gray);
                menuView = new MenuView();
                menuView.show(getSupportFragmentManager(), U01UserActivity.class.getSimpleName(), container);
            }
        });
        btnListener = new BackBtnListener() {
            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU) {
                    menuView = new MenuView();
                    menuView.show(getSupportFragmentManager(), U01UserActivity.class.getSimpleName(), container);
                }
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Intent home = new Intent(Intent.ACTION_MAIN);
                    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    home.addCategory(Intent.CATEGORY_HOME);
                    startActivity(home);
                }
                return true;
            }
        };
    }

    private void others() {
        userNavBtn.setVisibility(View.VISIBLE);
        userNavBtn.setImageResource(R.drawable.back_gray);
        userNavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnListener = new BackBtnListener() {
            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                }
                return true;
            }
        };
        userFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userFollowBtn.setEnabled(false);
                String url = user.__context.followedByCurrentUser ? QSAppWebAPI.getPeopleUnfollowApi() : QSAppWebAPI.getPeopleFollowApi();
                UserCommand.likeOrFollow(url, user._id, new Callback() {
                    @Override
                    public void onComplete(JSONObject response) {
                        super.onComplete();
                        if (user.__context.followedByCurrentUser) {
                            userFollowBtn.setImageResource(R.drawable.follow_btn);
                        } else {
                            userFollowBtn.setImageResource(R.drawable.unfollow_btn);
                        }
                        fragments[POS_FANS].refresh();
                        UserCommand.refresh();
                        user.__context.followedByCurrentUser = !user.__context.followedByCurrentUser;
                        userFollowBtn.setEnabled(true);
                        EventModel eventModel = new EventModel(U01UserActivity.class.getSimpleName(), null);
                        eventModel.setFrom(U01UserActivity.class.getSimpleName());
                        EventBus.getDefault().post(eventModel);
                    }

                    @Override
                    public void onError(int errorCode) {
                        ErrorHandler.handle(U01UserActivity.this, errorCode);
                        userFollowBtn.setEnabled(true);
                    }
                });
            }
        });
    }

    private void initUserInfo() {

        if (null == user) {
            user = new MongoPeople();
            user._id = QSModel.INSTANCE.getUserId();
        }
        getUserFromNet(user._id);
    }

    private void getUserFromNet(String uId) {
        if(null != dialogs)
            if(!dialogs.isShowing())
                dialogs.show();
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getPeopleQueryApi(uId), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("test_i","response --> "+response.toString());
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(U01UserActivity.this, MetadataParser.getError(response));
                    return;
                }
                LinkedList<MongoPeople> users = UserParser._parsePeoples(response);
                user = users.get(0);
                setUserBaseMInfo();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void setUserBaseMInfo() {

        userName.setText(user.nickname);
        float totalBonuses = 0f;
        if(null != QSModel.INSTANCE.getUser() &&  null != QSModel.INSTANCE.getUser().__context) {
            if(null != QSModel.INSTANCE.getUser().__context.bonusAmountByStatus) {
                BonusAmount bonusAmount = QSModel.INSTANCE.getUser().__context.bonusAmountByStatus;
                if (null != bonusAmount) {
                    Map<String, Number> bonuses = bonusAmount.bonuses;
                    if (bonuses != null) {
                        if (bonuses.containsKey("0")) {
                            totalBonuses += bonuses.get("0").floatValue();
                        }
                        if (bonuses.containsKey("1")) {
                            totalBonuses += bonuses.get("1").floatValue();
                        }
                        if (bonuses.containsKey("2")) {
                            totalBonuses += bonuses.get("2").floatValue();
                        }
                    }
                }
            }
        }
        userBonuses.setText(getString(R.string.get_bonuses_label) + StringUtil.FormatPrice(totalBonuses));
        userHw.setText(StringUtil.formatHeightAndWeight(user.height, user.weight));
        if("0".equals(user.rank)){
            ivRank.setVisibility(View.VISIBLE);
        }else {
            ivRank.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(user.portrait)){
            userHead.setImageURI(Uri.parse(user.portrait));
        }else {
            userHead.setImageURI(Uri.parse(UserConfig.USER_PORTRAIT_50));
        }
        if (!TextUtils.isEmpty(user.background)){
            userBg.setImageURI(Uri.parse(user.background));
        }else {
            userBg.setImageURI(Uri.parse(UserConfig.USER_HEAD_BG));
        }
        if (null != user.__context)
            if (user.__context.followedByCurrentUser)
                userFollowBtn.setImageResource(R.drawable.unfollow_btn);
    }

    private View view;
    private U01BaseFragment[] fragments = new U01BaseFragment[PAGER_NUM];
    private RecyclerView[] recyclerViews = new RecyclerView[PAGER_NUM];
    private RecyclerView preRecyclerView = null;

    private void initRecyclerViews(RecyclerView recyclerView) {
        if (null == recyclerViews[Integer.parseInt(String.valueOf(recyclerView.getTag()))])
            recyclerViews[Integer.parseInt(String.valueOf(recyclerView.getTag()))] = recyclerView;
    }


    private void initRectcler(final RecyclerView recyclerView) {

        if (null == recyclerView || preRecyclerView == recyclerView) return;
        view = null;
        if (null != recyclerView.getChildAt(0)) {
            view = recyclerView.getChildAt(0);
        }

        u01BackTopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(0);
            }
        });

        int span = recyclerView.getLayoutManager() instanceof LinearLayoutManager ? 1 : 2;

        boolean isShort = recyclerView.getHeight() > recyclerView.getChildAt(recyclerView.getChildCount() - 1).getHeight()
                * (recyclerView.getChildCount() - 1) / span + userHeadLayout.getY() + userHeadLayout.getHeight();

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        if (userHeadLayout.getY() != 0) {
            if (isShort) {
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(userHeadLayout, View.TRANSLATION_Y, 0);
                objectAnimator.setDuration(200);
                objectAnimator.start();
                recyclerView.scrollToPosition(0);
            } else {
                layoutManager.scrollToPositionWithOffset(0, (int) userHeadLayout.getY());
            }
        } else {
            recyclerView.scrollToPosition(0);
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition() != 0) {
                    u01BackTopBtn.setVisibility(View.VISIBLE);
                } else {
                    u01BackTopBtn.setVisibility(View.GONE);
                }
                if (view == recyclerView.getChildAt(0)) {
                    userHeadLayout.setY(view.getBottom() - view.getHeight());
                } else
                    userHeadLayout.setY(-userHeadLayout.getHeight());
            }
        });

        preRecyclerView = recyclerView;
    }

    public void onEventMainThread(EventModel eventModel) {
        if (!eventModel.tag.equals(U01UserActivity.class.getSimpleName())) return;
        if (null == recyclerViews[POS_MATCH])
            initRectcler((RecyclerView) eventModel.msg);
        initRecyclerViews((RecyclerView) eventModel.msg);
    }

    public void onEventMainThread(ShowCollectionEvent eventModel) {
        if (null == fragments) return;
        if (fragments.length < POS_COLL) return;
        fragments[POS_COLL].refresh();
    }

    public void onEventMainThread(PushGuideEvent event){
        if(event.unread){
            userNavBtn.setImageResource(R.drawable.menu_gray_dot);
        }else{
            if(!UnreadHelper.hasUnread())
                userNavBtn.setImageResource(R.drawable.menu_gray);
        }
    }

    public void onEventMainThread(String event){
        if(ValueUtil.U01_LOADING_FINISH.equals(event)){
            if(dialogs.isShowing()) dialogs.dismiss();
        }
    }

    int pos = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_match_layout:
                tabOnclick(POS_MATCH);
                return;
            case R.id.user_recomm_layout:
                if(isMyself)
                    UnreadHelper.userReadNotificationCommand(QSPushAPI.NEW_RECOMMANDATIONS);
                circleTip.setVisibility(View.GONE);
                tabOnclick(POS_RECOMM);
                return;
            case R.id.user_collection_layout:
                tabOnclick(POS_COLL);
                return;
            case R.id.user_follow_layout:
                tabOnclick(POS_FOLLOW);
                return;
            case R.id.user_fans_layout:
                tabOnclick(POS_FANS);
                return;
        }
    }

    private void tabOnclick(int pos) {

        userViewPager.setCurrentItem(pos);
        this.pos = pos;
        setIndicatorBackground(pos);
        initRectcler(recyclerViews[pos]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }

    // 将数据保存到outState对象中, 该对象会在重建activity时传递给onCreate方法
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("user", user);
        getIntent().putExtras(outState);
    }


    public class UserPagerAdapter extends FragmentPagerAdapter {

        public UserPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            U01BaseFragment fragment = null;
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            switch (pos) {
                case POS_MATCH:
                    fragment = U01MatchFragment.newInstance();
                    break;
                case POS_RECOMM:
                    fragment = U01RecommFragment.newInstance();
                    break;
                case POS_COLL:
                    fragment = U01CollectionFragment.newInstance();
                    break;
                case POS_FOLLOW:
                    fragment = U01FollowerFragment.newInstance();
                    break;
                case POS_FANS:
                    fragment = U01FansFragment.newInstance();
                    break;
                default:
                    fragment = U01MatchFragment.newInstance();
            }
            if (fragment == null) {
                fragment = U01MatchFragment.newInstance();
            }
            fragments[pos] = fragment;
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return PAGER_NUM;
        }
    }

    private void setIndicatorBackground(int pos) {
        userMatch.setActivated(false);
        userMatchText.setActivated(false);
        userRecomm.setActivated(false);
        userRecommText.setActivated(false);
        userCollection.setActivated(false);
        userCollectionText.setActivated(false);
        userFollow.setActivated(false);
        userFollowText.setActivated(false);
        userFans.setActivated(false);
        userFansText.setActivated(false);
        switch (pos) {
            case POS_MATCH:
                userMatch.setActivated(true);
                userMatchText.setActivated(true);
                return;
            case POS_RECOMM:
                userRecomm.setActivated(true);
                userRecommText.setActivated(true);
                return;
            case POS_COLL:
                userCollection.setActivated(true);
                userCollectionText.setActivated(true);
                return;
            case POS_FOLLOW:
                userFollow.setActivated(true);
                userFollowText.setActivated(true);
                return;
            case POS_FANS:
                userFans.setActivated(true);
                userFansText.setActivated(true);
                return;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return btnListener.onKeyDown(keyCode, event);
    }

    public interface BackBtnListener {
        public boolean onKeyDown(int keyCode, KeyEvent event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != getIntent().getExtras()) {
            if (null != getIntent().getExtras().get("user"))
                user = (MongoPeople) getIntent().getExtras().get("user");
        }
        MobclickAgent.onResume(this);
        if(UnreadHelper.hasUnread() && isMyself){
            userNavBtn.setImageResource(R.drawable.menu_gray_dot);
            if(UnreadHelper.hasMyNotificationCommand(QSPushAPI.NEW_RECOMMANDATIONS)) {
                circleTip.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
