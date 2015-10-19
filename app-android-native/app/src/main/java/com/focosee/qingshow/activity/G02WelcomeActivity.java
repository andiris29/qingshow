package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.fragment.WelComeFragment;

public class G02WelcomeActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    private final int indicatorCount = 3;
    private ViewPager mViewPager;
    private View view;
    private LinearLayout indicatorLayout;
    private boolean isLastPagerSelected = false;
    private boolean misScrolled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_g02_welcome);

        mViewPager = (ViewPager) findViewById(R.id.g02_viewpager);

        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(this);

        mViewPager.setCurrentItem(0);

        indicatorLayout = (LinearLayout) findViewById(R.id.g02_point_onthebotton);

        initIndicator();

    }

    private void initIndicator() {

        for (int i = 0; i < indicatorCount; i++) {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 25);
            params.setMargins(5, 0, 5, 0);
            params.weight = 1;
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            indicatorLayout.addView(imageView);

        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        if(isLastPagerSelected){
//            startActivity(new Intent(G02WelcomeActivity.this, S01MatchShowsActivity.class));
//            finish();
//        }
        for (int i = 0; i < indicatorLayout.getChildCount(); i++) {
            if (i == position)
                ((ImageView) indicatorLayout.getChildAt(i)).setImageResource(R.drawable.point_white);
            else
                ((ImageView) indicatorLayout.getChildAt(i)).setImageResource(R.drawable.point_transparent);
        }
    }

    @Override
    public void onPageSelected(int position) {
//        if(position == indicatorCount)
//            isLastPagerSelected = true;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_DRAGGING:
                misScrolled = false;
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                misScrolled = true;
                break;
            case ViewPager.SCROLL_STATE_IDLE:
                if (mViewPager.getCurrentItem() == indicatorCount - 1 && !misScrolled) {
                    startActivity(new Intent(this, S20MatcherActivity.class));
                    G02WelcomeActivity.this.finish();
                }
                misScrolled = true;
                break;
        }
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private final int count = 3;
        private final int[] backgroundArgs = {R.drawable.welcome1, R.drawable.welcome2, R.drawable.welcome3};
        private View[] views;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            views = new View[count];
        }


        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Fragment getItem(int position) {

            WelComeFragment fragment = WelComeFragment.newInstance(backgroundArgs[position]);

            return fragment;
        }

    }
}
