package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.fragment.WelComeFragment;
import com.focosee.qingshow.widget.MVerticalViewPager;

public class G02WelcomeActivity extends Activity implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewPager = new ViewPager(this);
        setContentView(mViewPager);

        view = LayoutInflater.from(this).inflate(R.layout.activity_g02_welcome, null);

        view.findViewById(R.id.g02_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(G02WelcomeActivity.this, S12TopicListActivity.class));
            }
        });

        view.findViewById(R.id.g02_dump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(G02WelcomeActivity.this, S12TopicListActivity.class));
            }
        });




//        mViewPager.setAdapter(new ViewPagerAdapter());
//
//        mViewPager.setCurrentItem(0);

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

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private final int count = 3;

        private final String[] titleArgs = {"倾秀", "�����Ĵ���", "���Ի�����"};
        private final String[] describeArgs = {"һ��Ϊ����Ҷ��Ƶķ�װ����Ӧ��ƽ̨", "��ʹû��Ů������Ҳ������Ů��ķ緶", "ÿ�����ͺ�����Ĵ�����������Ѷ"};
        private final int[] backgroundArgs = {R.drawable.guide_page1_bg, R.drawable.guide_page2_bg, R.drawable.guide_page3_bg};
        private View[] views;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            views = new View[count];
        }


        @Override
        public int getCount() {
            return count;
        }

        @Override        public boolean isViewFromObject(View view, Object object) {
            return false;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if(null == views[position % count]) return;
            container.removeView(views[position % count]);
        }

        @Override
        public Fragment getItem(int position) {

//            WelComeFragment fragment = WelComeFragment.newInstance();

            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            if(null != views[0]){
                container.addView(views[position % count]);
                return views[position % count];
            }

            ((TextView) view.findViewById(R.id.g02_title)).setText(titleArgs[position % count]);
            ((TextView) view.findViewById(R.id.g02_describe)).setText(describeArgs[position % count]);
            ((ImageView) view.findViewById(R.id.g02_backgroud)).setImageResource(backgroundArgs[position % count]);
            container.addView(view);

            return view;
        }
    }
}
