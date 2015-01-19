package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.focosee.qingshow.R;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.entity.People;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class U01PersonalActivity extends FragmentActivity {
    private static final int PAGER_NUM = 3;

    private static final int PAGER_COLLECTION = 0;
    private static final int PAGER_RECOMMEND = 1;
    private static final int PAGER_WATCH = 2;
    private static final int PAGER_CHOOSE = 3;

    private TextView backTextView;
    private TextView settingsTextView;
    private Context context;

    private ViewPager personalViewPager;
    private PersonalPagerAdapter personalPagerAdapter;

    private RelativeLayout matchRelativeLayout;
    private RelativeLayout watchRelativeLayout;
    private RelativeLayout fansRelativeLayout;
    private RelativeLayout followRelativeLayout;

    private LinearLayout line1;
    private LinearLayout line2;
    private LinearLayout line3;
    private LinearLayout line4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        context = getApplicationContext();

        backTextView = (TextView) findViewById(R.id.backTextView);
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        settingsTextView = (TextView) findViewById(R.id.settingsTextView);
        settingsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(U01PersonalActivity.this, U02SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        TextView nameTextView = (TextView) findViewById(R.id.nameTextView);
        TextView heightAndWeightTextView = (TextView) findViewById(R.id.heightAndWeightTextView);
        People people = QSApplication.get().getPeople();
        if (people != null) {
            if (people.name != null) nameTextView.setText(people.name);
            if (people.height != null && people.weight != null)
                heightAndWeightTextView.setText(people.height + "cm/" + people.weight + "kg");
        } else {
            Intent intent = new Intent(U01PersonalActivity.this, U06LoginActivity.class);
            startActivity(intent);
            Toast.makeText(context, "请登录账号", Toast.LENGTH_LONG).show();
            finish();
        }

        ImageView portraitImageView = (ImageView) findViewById(R.id.avatorImageView);
        if (QSApplication.get().getPeople() != null) {
            String portraitUrl = QSApplication.get().getPeople().portrait;
            if (portraitUrl != null && !portraitUrl.equals("")) {
                Picasso.with(context).load(portraitUrl).into(portraitImageView);
            }
        }

        matchRelativeLayout = (RelativeLayout) findViewById(R.id.matchRelativeLayout);
        watchRelativeLayout = (RelativeLayout) findViewById(R.id.watchRelativeLayout);
        fansRelativeLayout = (RelativeLayout) findViewById(R.id.fansRelativeLayout);
        followRelativeLayout = (RelativeLayout) findViewById(R.id.followRelativeLayout);

        line1 = (LinearLayout) findViewById(R.id.u01_line_toleftRecommend);
        line2 = (LinearLayout) findViewById(R.id.u01_line_toleftAttention);
        line3 = (LinearLayout) findViewById(R.id.u01_line_toleftAddAttention);

        personalViewPager = (ViewPager) findViewById(R.id.personalViewPager);

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
                case PAGER_CHOOSE:
                    fragment = U01ChooseFragment.newInstance();
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
        followRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_default_activity_personal));

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
            followRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_chosen_activity_personal));
        }
    }

    private void setIndicatorListener() {
        matchRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                personalViewPager.setCurrentItem(0);
            }
        });
        watchRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                personalViewPager.setCurrentItem(1);
            }
        });
        fansRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                personalViewPager.setCurrentItem(2);
            }
        });
        followRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                personalViewPager.setCurrentItem(3);
            }
        });
    }

    private void setTabCount() {

    }

}
