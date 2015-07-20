package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.fragment.WelComeFragment;

public class G02WelcomeActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private final int indicatorCount = 3;
    private ViewPager mViewPager;
    private View view;
    private LinearLayout indicatorLayout;
    private boolean isLastPagerSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_g02_welcome);

        findViewById(R.id.g02_go).setOnClickListener(this);

        findViewById(R.id.g02_dump).setOnClickListener(this);


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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_g02_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(isLastPagerSelected){
            startActivity(new Intent(G02WelcomeActivity.this, S01MatchShowsActivity.class));
            finish();
        }
        for (int i = 0; i < indicatorLayout.getChildCount(); i++) {
            if (i == position)
                ((ImageView) indicatorLayout.getChildAt(i)).setImageResource(R.drawable.point_white);
            else
                ((ImageView) indicatorLayout.getChildAt(i)).setImageResource(R.drawable.point_transparent);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if(position == indicatorCount)
            isLastPagerSelected = true;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(G02WelcomeActivity.this, S01MatchShowsActivity.class));
        finish();
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private final int count = 3;

        private final int[] titleArgs = {R.string.guide_pager1_title, R.string.guide_pager2_title, R.string.guide_pager3_title};
        private final int[] describeArgs = {R.string.guide_pager1_describe, R.string.guide_pager2_describe, R.string.guide_pager3_describe};
        private final int[] describeArgs1 = {R.string.guide_pager1_describe1, R.string.guide_pager2_describe1, R.string.guide_pager3_describe1};
        private final int[] backgroundArgs = {R.drawable.guide_page1_bg, R.drawable.guide_page2_bg, R.drawable.guide_page3_bg};
        private View[] views;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            views = new View[count];
        }


        @Override
        public int getCount() {
            return count + 1;
        }

        @Override
        public Fragment getItem(int position) {

            WelComeFragment fragment = WelComeFragment.newInstance(titleArgs[position % indicatorCount], describeArgs[position % indicatorCount], describeArgs1[position % indicatorCount], backgroundArgs[position % indicatorCount]);

            return fragment;
        }

    }
}
