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
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.focosee.qingshow.R;
import com.focosee.qingshow.constants.code.PeopleTypeInU01PersonalActivity;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.widget.MRoundImageView;
import com.focosee.qingshow.widget.MViewPager_NoScroll;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;


public class U01PersonalActivity extends FragmentActivity{
    private static final String TAG = "U01PersonalActivity";
    public static final String U01PERSONALACTIVITY_PEOPLE = "U01PersonalActivity_people";
    private static final int PAGER_NUM = 4;

    public static final String LOGOUT_ACTOIN = "logout_action";
    public static final String USER_UPDATE = "user_update";

    private static final int PAGER_COLLECTION = 0;
    private static final int PAGER_RECOMMEND = 1;
    private static final int PAGER_WATCH = 2;
    private static final int PAGE_BRAND = 3;

    public RelativeLayout headRelativeLayout;
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
        ImageLoader.getInstance().displayImage(people.getBackground(), backgroundIV, AppUtil.getModelBackgroundDisplayOptions());

        TextView nameTextView = (TextView) findViewById(R.id.nameTextView);
        TextView heightAndWeightTextView = (TextView) findViewById(R.id.heightAndWeightTextView);
        nameTextView.setText(people.getName());
        if (people.height != null && people.weight != null)
            heightAndWeightTextView.setText(people.getHeight() + people.getWeight());

        ImageView portraitImageView = (ImageView) findViewById(R.id.avatorImageView);
        ImageLoader.getInstance().displayImage(people.portrait, portraitImageView, AppUtil.getPortraitDisplayOptions());

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

        headRelativeLayout.setY(0);//重置刊头位置

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

}
