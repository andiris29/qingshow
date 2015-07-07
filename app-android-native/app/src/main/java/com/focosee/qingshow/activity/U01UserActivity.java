package com.focosee.qingshow.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.fragment.U01BaseFragment;
import com.focosee.qingshow.activity.fragment.U01CollectionFragment;
import com.focosee.qingshow.activity.fragment.U01FansFragment;
import com.focosee.qingshow.activity.fragment.U01FollowerFragment;
import com.focosee.qingshow.activity.fragment.U01MatchFragment;
import com.focosee.qingshow.activity.fragment.U01RecommFragment;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.U01Model;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.widget.MViewPager_NoScroll;

import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class U01UserActivity extends MenuActivity {

    public static final int POS_MATCH = 0;
    public static final int POS_RECOMM = 1;
    public static final int POS_COLL = 2;
    public static final int POS_FOLLOW = 3;
    public static final int POS_FANS = 4;
    public static final int PAGER_NUM = 5;

    @InjectView(R.id.user_bg)
    SimpleDraweeView userBg;

    private final float DAMP = 3.0f;
    @InjectView(R.id.user_match_layout)
    RelativeLayout userMatchLayout;
    @InjectView(R.id.user_head_layout)
    RelativeLayout userHeadLayout;
    @InjectView(R.id.bg)
    ImageView bg;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.user_hw)
    TextView userHw;
    @InjectView(R.id.center_layout)
    LinearLayout centerLayout;
    @InjectView(R.id.user_follow_btn)
    ImageView userFollowBtn;
    @InjectView(R.id.user_match)
    ImageView userMatch;
    @InjectView(R.id.user_recomm)
    ImageView userRecomm;
    @InjectView(R.id.user_recomm_layout)
    RelativeLayout userRecommLayout;
    @InjectView(R.id.user_collection)
    ImageView userCollection;
    @InjectView(R.id.user_collection_layout)
    RelativeLayout userCollectionLayout;
    @InjectView(R.id.user_follow)
    ImageView userFollow;
    @InjectView(R.id.user_follow_layout)
    RelativeLayout userFollowLayout;
    @InjectView(R.id.user_fans)
    ImageView userFans;
    @InjectView(R.id.user_fans_layout)
    RelativeLayout userFansLayout;
    @InjectView(R.id.user_tab_layout)
    LinearLayout userTabLayout;
    @InjectView(R.id.U01_head_relative)
    RelativeLayout u01HeadRelative;
    @InjectView(R.id.navigation_btn_match)
    ImageButton navigationBtnMatch;
    @InjectView(R.id.navigation_btn_good_match)
    ImageButton navigationBtnGoodMatch;
    @InjectView(R.id.u01_people)
    ImageButton u01People;
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

    private List<MongoShow> datas;
    private UserPagerAdapter pagerAdapter;
    private EventBus eventBus;

    private boolean isFirstFocus = true;

    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u01_base);
        ButterKnife.inject(this);
        userNavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuSwitch();
            }
        });

        if(U01Model.INSTANCE.getUser() == QSModel.INSTANCE.getUser()) {//进入自己的页面时不显示关注按钮
            userFollowBtn.setVisibility(View.GONE);
        }else{
            userFollowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        datas = new LinkedList<>();
        initUserInfo();

        pagerAdapter = new UserPagerAdapter(getSupportFragmentManager());
        userViewPager.setAdapter(pagerAdapter);
        userViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIndicatorBackground(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        userViewPager.setOffscreenPageLimit(5);
        userViewPager.setCurrentItem(POS_MATCH);
        userViewPager.setScrollble(false);
//
//        initRectcler();
//        loadDataFormNet();

    }

    private void initUserInfo() {

        if (U01Model.INSTANCE.getUser() == null) {
            U01Model.INSTANCE.setUser(QSModel.INSTANCE.getUser());
        }
        MongoPeople user = U01Model.INSTANCE.getUser();

        userName.setText(user.nickname);
        userHw.setText((null == user.height ? "0" : user.height) + "cm," + (null == user.weight ? "0" : user.weight) + "kg");
        if (user.portrait != null) {
            userHead.setImageURI(Uri.parse(user.portrait));
        }
        if (null != user.background)
            userBg.setImageURI(Uri.parse(ImgUtil.getImgSrc(user.background, -1)));
    }

    private View view;
    private U01BaseFragment[] fragments = new U01BaseFragment[PAGER_NUM];
    private RecyclerView[] recyclerViews = new RecyclerView[PAGER_NUM];
    private RecyclerView preRecyclerView = null;

    private void initRecyclerViews(RecyclerView recyclerView) {
        recyclerViews[Integer.parseInt(String.valueOf(recyclerView.getTag()))] = recyclerView;
    }

    private void initRectcler(RecyclerView recyclerView) {

        //reset
        if (null == recyclerView) return;
        userHeadLayout.setY(0);
//        preRecyclerView.scrollToPosition(0);
        view = null;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (null == view && null != recyclerView.getChildAt(0))
                    view = recyclerView.getChildAt(0);
                if (view == recyclerView.getChildAt(0))
                    userHeadLayout.setY(view.getBottom() - view.getHeight());
                else
                    userHeadLayout.setY(-userHeadLayout.getHeight());
            }
        });

        preRecyclerView = recyclerView;
    }

    private int currentPos = 0;

    public void onEventMainThread(RecyclerView recyclerView) {
        if (null == recyclerViews[POS_MATCH])
            initRectcler(recyclerView);
        initRecyclerViews(recyclerView);
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
        preRecyclerView.scrollToPosition(0);
        userViewPager.setCurrentItem(pos);
        fragments[pos].getRecyclerPullToRefreshView().doPullRefreshing(true, 200);
        this.pos = pos;
        initRectcler(recyclerViews[pos]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }

    public class UserPagerAdapter extends FragmentPagerAdapter {

        public UserPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            U01BaseFragment fragment = null;
            switch (pos) {
                case POS_MATCH:
                    fragment = U01MatchFragment.newInstance(U01UserActivity.this);
                    break;
                case POS_RECOMM:
                    fragment = U01RecommFragment.newInstance(U01UserActivity.this);
                    break;
                case POS_COLL:
                    fragment = U01CollectionFragment.newInstance(U01UserActivity.this);
                    break;
                case POS_FOLLOW:
                    fragment = U01FollowerFragment.newInstance(U01UserActivity.this);
                    break;
                case POS_FANS:
                    fragment = U01FansFragment.newInstance(U01UserActivity.this);
                    break;
                default:
                    fragment = U01MatchFragment.newInstance(U01UserActivity.this);
            }
            if (fragment == null) {
                fragment = U01MatchFragment.newInstance(U01UserActivity.this);
            }
            fragments[pos] = fragment;
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

}
