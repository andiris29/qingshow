package com.focosee.qingshow.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.EventModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.U01Model;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.CommUtil;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.widget.MViewPager_NoScroll;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class U01UserActivity extends MenuActivity {

    private static final String TAG = "U01UserActivity";

    public static final String NEW_RECOMMANDATIONS = "NEW_RECOMMANDATIONS";

    public static final int POS_MATCH = 0;
    public static final int POS_RECOMM = 1;
    public static final int POS_COLL = 2;
    public static final int POS_FOLLOW = 3;
    public static final int POS_FANS = 4;
    public static final int PAGER_NUM = 5;

    @InjectView(R.id.user_bg)
    ImageView userBg;

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
    @InjectView(R.id.user_nav_btn)
    ImageView userNavBtn;
    @InjectView(R.id.circle_tip)
    View circleTip;

    private List<MongoShow> datas;
    private UserPagerAdapter pagerAdapter;
    private EventBus eventBus;
    private MongoPeople user;

    private BackBtnListener btnListener;

    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u01_base);
        ButterKnife.inject(this);
        try {
            user = (MongoPeople)CommUtil.deepCopy(U01Model.INSTANCE.getUser());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initUserInfo();
        if (user._id.equals(QSModel.INSTANCE.getUser()._id)) {//进入自己的页面时不显示关注按钮
            userFollowBtn.setVisibility(View.GONE);
            userNavBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuSwitch();
                }
            });
            btnListener = new BackBtnListener() {
                @Override
                public boolean onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK) {
                    menuSwitch();
                }
                return true;
                }
            };
        } else {
            userNavBtn.setImageResource(R.drawable.s03_back_btn);
            userNavBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            btnListener = new BackBtnListener() {
                @Override
                public boolean onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU) {
                    menuSwitch();
                }
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
                        String msg = "";
                        if (user.__context.followedByCurrentUser) {
                            msg = "取消关注";
                            userFollowBtn.setImageResource(R.drawable.follow_btn);
                        } else {
                            msg = "添加关注";
                            userFollowBtn.setImageResource(R.drawable.unfollow_btn);
                        }
                        fragments[POS_FANS].refresh();
                        UserCommand.refresh();
                        user.__context.followedByCurrentUser = !user.__context.followedByCurrentUser;
                        Toast.makeText(U01UserActivity.this, msg, Toast.LENGTH_SHORT).show();
                        userFollowBtn.setEnabled(true);
                        EventModel eventModel = new EventModel(U01UserActivity.class, null);
                        eventModel.setFrom(U01UserActivity.class);
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
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        datas = new LinkedList<>();
        pagerAdapter = new UserPagerAdapter(getSupportFragmentManager());
        userViewPager.setAdapter(pagerAdapter);
        userViewPager.setOffscreenPageLimit(5);
        userViewPager.setCurrentItem(POS_MATCH);
        userViewPager.setScrollble(false);

        initDrawer();
    }

    private void initUserInfo() {

        if (null == user) {
            return;
        }

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getPeopleQueryApi(user._id), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, "response:" + response);
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(U01UserActivity.this, MetadataParser.getError(response));
                    return;
                }
                LinkedList<MongoPeople> users = UserParser._parsePeoples(response);
                user = users.get(0);
                userName.setText(user.nickname);
                userHw.setText(user.height + "cm," + user.weight + "kg");
                if (user.portrait != null)
                    userHead.setImageURI(Uri.parse(user.portrait));
                ImageLoader.getInstance().displayImage(user.background, userBg, AppUtil.getModelBackgroundDisplayOptions());
                if (user.__context.followedByCurrentUser)
                    userFollowBtn.setImageResource(R.drawable.unfollow_btn);
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private View view;
    private U01BaseFragment[] fragments = new U01BaseFragment[PAGER_NUM];
    private RecyclerView[] recyclerViews = new RecyclerView[PAGER_NUM];
    private RecyclerView preRecyclerView = null;

    private void initRecyclerViews(RecyclerView recyclerView) {
        if (null == recyclerViews[Integer.parseInt(String.valueOf(recyclerView.getTag()))])
            recyclerViews[Integer.parseInt(String.valueOf(recyclerView.getTag()))] = recyclerView;
    }

    private void initRectcler(RecyclerView recyclerView) {

        if (null == recyclerView || preRecyclerView == recyclerView) return;
        view = null;
        if (null != recyclerView.getChildAt(0)) {
            view = recyclerView.getChildAt(0);
        }

        int span = recyclerView.getLayoutManager() instanceof LinearLayoutManager ? 1 : 2;

        boolean isShort = recyclerView.getHeight() > recyclerView.getChildAt(recyclerView.getChildCount() - 1).getHeight()
                * (recyclerView.getChildCount() - 1) / span + userHeadLayout.getBottom() + userHeadLayout.getY();
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (userHeadLayout.getY() != 0) {
            if (isShort) {//短列表
                userHeadLayout.setY(0);
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
                if (view == recyclerView.getChildAt(0))
                    userHeadLayout.setY(view.getBottom() - view.getHeight());
                else
                    userHeadLayout.setY(-userHeadLayout.getHeight());
            }
        });

        preRecyclerView = recyclerView;
    }

    public void onEventMainThread(EventModel eventModel) {
        //TODO recyclerviews
        if (eventModel.tag != U01UserActivity.class) return;
        if (eventModel.from == S03SHowActivity.class)
            fragments[POS_COLL].refresh();
        if (eventModel.from == U01UserActivity.class) {
            fragments[POS_FOLLOW].refresh();
        }
        if (null == recyclerViews[POS_MATCH])
            initRectcler((RecyclerView) eventModel.msg);
        initRecyclerViews((RecyclerView) eventModel.msg);
    }

    int pos = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_match_layout:
                tabOnclick(POS_MATCH);
                return;
            case R.id.user_recomm_layout:
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
        super.onClick(v);

    }

    private void tabOnclick(int pos) {

        userViewPager.setCurrentItem(pos);
        fragments[pos].getRecyclerPullToRefreshView().doPullRefreshing(true, 0);
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

    @Override
    protected void onResume() {
        super.onResume();
        if(null != getIntent().getExtras()){
            user = (MongoPeople)getIntent().getExtras().get("user");
        }
        boolean hasNew = getIntent().getBooleanExtra(NEW_RECOMMANDATIONS, false);
        if (hasNew) {
            circleTip.setVisibility(View.VISIBLE);
        }
        System.out.println("onResume");
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
        userMatch.setImageResource(R.drawable.match);
        userRecomm.setImageResource(R.drawable.recommend);
        userCollection.setImageResource(R.drawable.collection);
        userFollow.setImageResource(R.drawable.follow);
        userFans.setImageResource(R.drawable.fans);

        userMatchText.setTextColor(getResources().getColor(R.color.darker_gray));
        userRecommText.setTextColor(getResources().getColor(R.color.darker_gray));
        userCollectionText.setTextColor(getResources().getColor(R.color.darker_gray));
        userFollowText.setTextColor(getResources().getColor(R.color.darker_gray));
        userFansText.setTextColor(getResources().getColor(R.color.darker_gray));

        if (pos == 0) {
            userMatch.setImageResource(R.drawable.match_pink);
            userMatchText.setTextColor(getResources().getColor(R.color.s21_pink));
        } else if (pos == 1) {
            circleTip.setVisibility(View.GONE);
            userRecomm.setImageResource(R.drawable.recommend_pink);
            userRecommText.setTextColor(getResources().getColor(R.color.s21_pink));
        } else if (pos == 2) {
            userCollection.setImageResource(R.drawable.collection_pink);
            userCollectionText.setTextColor(getResources().getColor(R.color.s21_pink));
        } else if (pos == 3) {
            userFollow.setImageResource(R.drawable.follow_pink);
            userFollowText.setTextColor(getResources().getColor(R.color.s21_pink));
        } else if (pos == 4) {
            userFans.setImageResource(R.drawable.fans_pink);
            userFansText.setTextColor(getResources().getColor(R.color.s21_pink));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return btnListener.onKeyDown(keyCode, event);
    }

    public interface BackBtnListener {
        public boolean onKeyDown(int keyCode, KeyEvent event);
    }
}
