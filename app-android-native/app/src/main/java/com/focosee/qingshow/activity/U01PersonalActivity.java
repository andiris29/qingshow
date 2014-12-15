package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.P03BrandListAdapter;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.BrandEntity;
import com.focosee.qingshow.widget.MPullRefreshMultiColumnListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.huewu.pla.lib.MultiColumnListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;


public class U01PersonalActivity extends Activity {
    private TextView settingsTextView;
    private Context context;
    private LayoutInflater inflater;

    private MPullRefreshMultiColumnListView pullRefreshListView;
    private MultiColumnListView multiColumnListView;

    private P03BrandListAdapter adapter;

    private ViewPager personalViewPager;

    private ArrayList<View> pagerViewList;

    private RelativeLayout matchRelativeLayout;
    private RelativeLayout watchRelativeLayout;
    private RelativeLayout fansRelativeLayout;
    private RelativeLayout followRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        context = getApplicationContext();

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        settingsTextView = (TextView) findViewById(R.id.settingsTextView);
        settingsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(U01PersonalActivity.this, U02SettingsActivity.class);
                startActivity(intent);
            }
        });

        matchRelativeLayout = (RelativeLayout)findViewById(R.id.matchRelativeLayout);
        watchRelativeLayout = (RelativeLayout)findViewById(R.id.watchRelativeLayout);
        fansRelativeLayout = (RelativeLayout)findViewById(R.id.fansRelativeLayout);
        followRelativeLayout = (RelativeLayout)findViewById(R.id.followRelativeLayout);


        personalViewPager = (ViewPager) findViewById(R.id.personalViewPager);

        pagerViewList = new ArrayList<View>();
        View view = inflater.inflate(R.layout.activity_personal_pager_match, null);
        pagerViewList.add(view);
        pagerViewList.add(inflater.inflate(R.layout.activity_personal_pager_watch, null));
        pagerViewList.add(inflater.inflate(R.layout.activity_personal_pager_following, null));
        pagerViewList.add(inflater.inflate(R.layout.activity_personal_pager_follow, null));

        personalViewPager.setAdapter(personalPagerAdapter);
        personalViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setIndicatorBackground(position);
                if (position == 0) {

                } else if (position == 1) {

                } else if (position == 2) {

                } else if (position == 3) {

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


        pullRefreshListView = (MPullRefreshMultiColumnListView)
                pagerViewList.get(0).findViewById(R.id.P03_brand_list_list_view);
        multiColumnListView = pullRefreshListView.getRefreshableView();
        pullRefreshListView.setPullRefreshEnabled(true);
        pullRefreshListView.setPullLoadEnabled(true);
        adapter = new P03BrandListAdapter(this, new ArrayList<BrandEntity>(), ImageLoader.getInstance());

        multiColumnListView.setAdapter(adapter);

        pullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<MultiColumnListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<MultiColumnListView> refreshView) {
                refreshData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<MultiColumnListView> refreshView) {
                loadMoreData();
            }
        });
        pullRefreshListView.doPullRefreshing(true, 0);
    }

    private void setIndicatorBackground(int pos) {
        matchRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_default_activity_personal));
        watchRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_default_activity_personal));
        fansRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_default_activity_personal));
        followRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_default_activity_personal));
        if (pos == 0) {
            matchRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_chosen_activity_personal));
        } else if (pos == 1) {
            watchRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_chosen_activity_personal));
        } else if (pos == 2) {
            fansRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_chosen_activity_personal));
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

    private void loadMoreData() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(QSAppWebAPI.getShowListApi(0, 0),null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayList<BrandEntity> moreData = __createFakeData();
                adapter.addData(moreData);
                adapter.notifyDataSetChanged();

                pullRefreshListView.onPullUpRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleErrorMsg(error);
            }
        });
        QSApplication.QSRequestQueue().add(jsonObjectRequest);
    }

    private void refreshData() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(QSAppWebAPI.getShowListApi(0,0),null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayList<BrandEntity> newData = __createFakeData();
                adapter.resetData(newData);
                adapter.notifyDataSetChanged();

                pullRefreshListView.onPullDownRefreshComplete();
                pullRefreshListView.setHasMoreData(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleErrorMsg(error);
            }
        });
        QSApplication.QSRequestQueue().add(jsonObjectRequest);
    }

    private void handleErrorMsg(VolleyError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("P03BrandListActivity", error.toString());
    }

    private ArrayList<BrandEntity> __createFakeData() {
        ArrayList<BrandEntity> tempData = new ArrayList<BrandEntity>();
        for (int i = 0; i < 5; i++) {
            BrandEntity brandEntity = new BrandEntity();
            brandEntity.name = "品牌" + String.valueOf(i);
            brandEntity.logo = "http://img2.imgtn.bdimg.com/it/u=2439868726,3891592022&fm=21&gp=0.jpg";
            brandEntity.slogan = "http://img1.imgtn.bdimg.com/it/u=3411049717,3668206888&fm=21&gp=0.jpg";
            tempData.add(brandEntity);
        }
        return tempData;
    }

    private PagerAdapter personalPagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return pagerViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(pagerViewList.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(pagerViewList.get(position));
            return pagerViewList.get(position);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_personal, menu);
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
}
