package com.focosee.qingshow.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.focosee.qingshow.R;
import com.focosee.qingshow.constants.code.PeopleTypeInU01PersonalActivity;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.util.DensityUtil;
import com.focosee.qingshow.widget.MRoundImageView;
import com.focosee.qingshow.widget.MViewPager_NoScroll;
import com.huewu.pla.lib.internal.PLA_AbsListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;


public class U01PersonalActivity extends FragmentActivity implements AbsListView.OnScrollListener, View.OnTouchListener, PLA_AbsListView.OnScrollListener{
    private static final String TAG = "U01PersonalActivity";
    public static final String U01PERSONALACTIVITY_PEOPLE = "U01PersonalActivity_people";
    private static final int PAGER_NUM = 4;

    public static final String LOGOUT_ACTOIN = "logout_action";
    public static final String USER_UPDATE = "user_update";

    private static final int PAGER_COLLECTION = 0;
    private static final int PAGER_RECOMMEND = 1;
    private static final int PAGER_WATCH = 2;
    private static final int PAGE_BRAND = 3;

    private RelativeLayout headRelativeLayout;
    private ImageView backTextView;
    private ImageView settingsTextView;
    private ImageView backgroundIV;
    private Context context;

    private MViewPager_NoScroll personalViewPager;
    private PersonalPagerAdapter personalPagerAdapter;

    private RelativeLayout matchRelativeLayout;
    private RelativeLayout watchRelativeLayout;
    private RelativeLayout fansRelativeLayout;
    private RelativeLayout brandRelativeLayout;

    private LinearLayout line1;
    private LinearLayout line2;
    private LinearLayout line3;

    private MongoPeople people;

    public static int peopleType = PeopleTypeInU01PersonalActivity.MYSELF.getIndex();

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(LOGOUT_ACTOIN.equals(intent.getAction())){
                finish();
            }
            if(USER_UPDATE.equals(intent.getAction()) && peopleType == PeopleTypeInU01PersonalActivity.MYSELF.getIndex()){
                people = QSModel.INSTANCE.getUser();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        context = getApplicationContext();
        Intent mIntent = getIntent();
        headRelativeLayout = (RelativeLayout) findViewById(R.id.U01_head_relative);
        headHeight = headRelativeLayout.getLayoutParams().height;

        if (null != mIntent.getSerializableExtra(U01PERSONALACTIVITY_PEOPLE)) {
            people = (MongoPeople) mIntent.getSerializableExtra(U01PERSONALACTIVITY_PEOPLE);
        } else if (null != mIntent.getSerializableExtra(P02ModelActivity.INPUT_MODEL)) {
            people = (MongoPeople) mIntent.getSerializableExtra(P02ModelActivity.INPUT_MODEL);
        } else {//本人
            people = QSModel.INSTANCE.getUser();
        }

        if (people != null) {
            if (!QSModel.INSTANCE.loggedin() || !people.get_id().equals(QSModel.INSTANCE.getUser()._id)){
                peopleType = PeopleTypeInU01PersonalActivity.OTHERS.getIndex();
            }else {
                if (people.get_id().equals(QSModel.INSTANCE.getUser().get_id())) {
                    peopleType = PeopleTypeInU01PersonalActivity.MYSELF.getIndex();
                }
            }
        } else {
            Intent intent = new Intent(U01PersonalActivity.this, U06LoginActivity.class);
            startActivity(intent);
            Toast.makeText(context, "请登录账号", Toast.LENGTH_LONG).show();
            finish();
        }

        matchUI();
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                peopleType = PeopleTypeInU01PersonalActivity.MYSELF.getIndex();
                finish();
            }
        });

        settingsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(U01PersonalActivity.this, U02SettingsActivity.class);
                startActivity(intent);
            }
        });

        backgroundIV = (ImageView) findViewById(R.id.activity_personal_background);
        ImageLoader.getInstance().displayImage(people.getBackground(), backgroundIV);

        TextView nameTextView = (TextView) findViewById(R.id.nameTextView);
        TextView heightAndWeightTextView = (TextView) findViewById(R.id.heightAndWeightTextView);
        nameTextView.setText(people.getName());
        if (people.height != null && people.weight != null)
            heightAndWeightTextView.setText(people.getHeight() + people.getWeight());

        MRoundImageView portraitImageView = (MRoundImageView) findViewById(R.id.avatorImageView);
        if (people != null) {
            String portraitUrl = people.getPortrait();
            if (portraitUrl != null && !portraitUrl.equals("")) {
//                Picasso.with(context).load(portraitUrl).into(portraitImageView);
                ImageLoader.getInstance().displayImage(portraitUrl, portraitImageView);
            }
        }

        personalPagerAdapter = new PersonalPagerAdapter(getSupportFragmentManager());
        personalViewPager.setAdapter(personalPagerAdapter);
        personalViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setIndicatorBackground(position);
                if (position == 0) {

                } else if (position == 1) {

                } else if (position == 2) {

                } else if (position == 3) {

                } else {

                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        setIndicatorListener();
        personalViewPager.setOffscreenPageLimit(4);
        personalViewPager.setCurrentItem(0);

        registerReceiver(receiver, new IntentFilter(LOGOUT_ACTOIN));
        registerReceiver(receiver, new IntentFilter(USER_UPDATE));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    public void matchUI() {
        backTextView = (ImageView) findViewById(R.id.activity_personal_backTextView);
        settingsTextView = (ImageView) findViewById(R.id.settingsTextView);
        if (peopleType != PeopleTypeInU01PersonalActivity.MYSELF.getIndex()) {//不是本人
            settingsTextView.setVisibility(View.GONE);
        }
        matchRelativeLayout = (RelativeLayout) findViewById(R.id.matchRelativeLayout);
        watchRelativeLayout = (RelativeLayout) findViewById(R.id.watchRelativeLayout);
        fansRelativeLayout = (RelativeLayout) findViewById(R.id.fansRelativeLayout);
        brandRelativeLayout = (RelativeLayout) findViewById(R.id.brandRelativeLayout);

        line1 = (LinearLayout) findViewById(R.id.u01_line_toleftRecommend);
        line2 = (LinearLayout) findViewById(R.id.u01_line_toleftAttention);
        line3 = (LinearLayout) findViewById(R.id.u01_line_toleftBrand);

        line1.setVisibility(View.GONE);

        personalViewPager = (MViewPager_NoScroll) findViewById(R.id.personalViewPager);
        personalViewPager.setScrollble(false);
    }



    public class PersonalPagerAdapter extends FragmentPagerAdapter {

        public PersonalPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            Fragment fragment = null;
            switch (pos) {
                case PAGER_COLLECTION:
                    fragment = U01CollectionFragment.newInstance();
                    break;
                case PAGER_RECOMMEND:
                    fragment = U01RecommendFragment.newInstance();
                    break;
                case PAGER_WATCH:
                    fragment = U01WatchFragment.newInstance();
                    break;
                case PAGE_BRAND:
                    fragment = U01BrandFragment.newInstance();
                    break;
                default:
                    fragment = U01CollectionFragment.newInstance();
            }
            if (fragment == null) {
                fragment = U01CollectionFragment.newInstance();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return PAGER_NUM;
        }
    }

    private void setIndicatorBackground(int pos) {
        matchRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_default_activity_personal));
        watchRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_default_activity_personal));
        fansRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_default_activity_personal));
        brandRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_default_activity_personal));

        line1.setVisibility(View.GONE);
        line2.setVisibility(View.GONE);
        line3.setVisibility(View.GONE);

        if (pos == 0) {
            matchRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_chosen_activity_personal));
            line2.setVisibility(View.VISIBLE);
            line3.setVisibility(View.VISIBLE);
        } else if (pos == 1) {
            watchRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_chosen_activity_personal));
            line3.setVisibility(View.VISIBLE);
        } else if (pos == 2) {
            fansRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_chosen_activity_personal));
            line1.setVisibility(View.VISIBLE);
        } else if (pos == 3) {
            brandRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_chosen_activity_personal));
            line1.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
        }
    }

    private void setIndicatorListener() {
        matchRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                headRelativeLayout.setY(0);
                personalViewPager.setCurrentItem(0);
            }
        });
        watchRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                headRelativeLayout.setY(0);
                personalViewPager.setCurrentItem(1);
            }
        });
        fansRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                headRelativeLayout.setY(0);
                personalViewPager.setCurrentItem(2);
            }
        });
        brandRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headRelativeLayout.setY(0);
                personalViewPager.setCurrentItem(3);
            }
        });
    }

    public MongoPeople getMongoPeople() {
        return people;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U01User"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U01User"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

    private int firstVisibleItem;
    private int padding;
    public static int headHeight;
    private boolean isHeadMove = false;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
        padding = headRelativeLayout.getLayoutParams().height+(int)headRelativeLayout.getY();
        padding = padding > headHeight ? headHeight : padding;
        padding = padding < 0 ? 0 : padding;
        if(isHeadMove) {
            if (null != view.getChildAt(0)) {
                if (padding > 0 && padding <= headHeight) {

//                    System.out.println("viewChildTop:" + view.getChildAt(0).getTop());
//                    System.out.println("headTop:" + headRelativeLayout.getTop());
//                    System.out.println("headY:" + headRelativeLayout.getY());

//                    view.getChildAt(0).setY(view.getY());
                    view.setPadding(0, padding, 0, 0);

                    headRelativeLayout.setY((view.getChildAt(0).getY() - headRelativeLayout.getLayoutParams().height) > 0 ? 0 : view.getChildAt(0).getY() - headRelativeLayout.getLayoutParams().height);
//                viewPager.setY(view.getChildAt(firstVisibleItem).getY());


//                System.out.println("滚动到第一个");
//                System.out.println("firstViewY:" + view.getChildAt(firstVisibleItem).getY());
                }else {
                    isHeadMove = false;
                    if (view.getPaddingTop() != 0 && padding == 0)
                        view.setPadding(0, padding, 0, 0);
                }
            }
        }
        System.out.println("padding：" + padding);
    }

    private VelocityTracker mVelocityTracker;

    private float speedTouch;
    private float offSetTouch;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //final float scale = getResources().getDisplayMetrics().density;
//        System.out.println("相对于屏幕左上角的Y:" + (230 * scale + 0.5f));5
        if(null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
            isHeadMove = true;
        }

        mVelocityTracker.addMovement(event);

        final VelocityTracker velocityTracker = mVelocityTracker;
        // 1000 provides pixels per second
        velocityTracker.computeCurrentVelocity(1, (float)0.8); //设置maxVelocity值为0.1时，速率大于0.01时，显示的速率都是0.01,速率小于0.01时，显示正常
        speedTouch = velocityTracker.getYVelocity();

        velocityTracker.computeCurrentVelocity(1000); //设置units的值为1000，意思为一秒时间内运动了多少个像素
        offSetTouch = velocityTracker.getYVelocity();
        if(offSetTouch > 0 && offSetTouch > 300) {//向下滑动

//            if(speedTouch == (float) 0.8) {//快速滑动
////                if(headRelativeLayout.getY() != (float)0){
////                    headRelativeLayout.setY(0);
////                    isHeadMove = true;
////                }
////                if(v.getPaddingTop() != headHeight){
////                    v.setPadding(0, headHeight, 0, 0);
////                }
//
//            } else {//触摸滑动
            if(padding < headHeight){
                headRelativeLayout.setY(0);
                padding = headHeight;
                v.setPadding(0, padding, 0, 0);
                isHeadMove = false;
            }
//            }

        } else if (offSetTouch < -300) {//向上滑动
            isHeadMove = true;
//            if(speedTouch == (float)-0.8) {//快速滑动
//
//            }
        }
        return false;
    }

    @Override
    public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
        padding = headRelativeLayout.getLayoutParams().height+(int)headRelativeLayout.getY();
        padding = padding > headHeight ? headHeight : padding;
        padding = padding < 0 ? 0 : padding;
        if(isHeadMove) {
            if (null != view.getChildAt(0)) {
                if (padding > 0 && padding <= headHeight) {
                    System.out.println("U01Activity_padding: " + padding);

//                    System.out.println("viewChildTop:" + view.getChildAt(0).getTop());
//                    System.out.println("headTop:" + headRelativeLayout.getTop());
//                    System.out.println("headY:" + headRelativeLayout.getY());

//                    view.getChildAt(0).setY(view.getY());
                    view.setPadding(0, padding, 0, 0);

                    headRelativeLayout.setY((DensityUtil.dip2px(this, view.getChildAt(0).getTop()) - headRelativeLayout.getLayoutParams().height) > 0 ? 0 : DensityUtil.dip2px(this, view.getChildAt(0).getTop()) - headRelativeLayout.getLayoutParams().height);
//                viewPager.setY(view.getChildAt(firstVisibleItem).getY());


//                System.out.println("滚动到第一个");
//                System.out.println("firstViewY:" + view.getChildAt(firstVisibleItem).getY());
                }else {
                    isHeadMove = false;
                    if (view.getPaddingTop() != 0 && padding == 0)
                        view.setPadding(0, padding, 0, 0);
                }
            }
        }
        System.out.println("padding--U01：" + padding);
    }
}
