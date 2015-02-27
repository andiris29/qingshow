package com.focosee.qingshow.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.P02ModelFollowPeopleListAdapter;
import com.focosee.qingshow.adapter.P02ModelItemListAdapter;
import com.focosee.qingshow.adapter.P02ModelViewPagerAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.FeedingParser;
import com.focosee.qingshow.httpapi.response.dataparser.PeopleParser;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class P02ModelActivity extends BaseActivity {

    private static final String TAG = "P02ModelActivity";

    public static final String INPUT_MODEL = "P02ModelActivity_input_model";
    private ViewPager viewPager;
    private MPullRefreshListView latestPullRefreshListView;
    private ListView latestListView;
    private MPullRefreshListView followedPullRefreshListView;
    private MPullRefreshListView followerPullRefreshListView;
    private ListView followedListView;
    private ListView followerListView;
    private RelativeLayout newRelativeLayout;
    private RelativeLayout discountRelativeLayout;
    private RelativeLayout fansRelativeLayout;
    private RelativeLayout followRelativeLayout;
    private ImageView followSignText;
    private P02ModelViewPagerAdapter viewPagerAdapter;
    private P02ModelItemListAdapter itemListAdapter;
    private P02ModelFollowPeopleListAdapter followedPeopleListAdapter;
    private P02ModelFollowPeopleListAdapter followerPeopleListAdapter;
    private MongoPeople modelEntity;
    private int pageIndex = 1;
    private LinearLayout line1;
    private LinearLayout line2;
    private LinearLayout line3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p02_model);

        ((ImageButton) findViewById(R.id.P02_back_image_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                P02ModelActivity.this.finish();
            }
        });

        newRelativeLayout = (RelativeLayout) findViewById(R.id.P02_newRelativeLayout);
        discountRelativeLayout = (RelativeLayout) findViewById(R.id.P02_discountRelativeLayout);
        fansRelativeLayout = (RelativeLayout) findViewById(R.id.P02_fansRelativeLayout);
        followRelativeLayout = (RelativeLayout) findViewById(R.id.P02_followRelativeLayout);

        line1 = (LinearLayout) findViewById(R.id.p02_line_toleftDiscount);
        line2 = (LinearLayout) findViewById(R.id.p02_line_toleftFans);
        line3 = (LinearLayout) findViewById(R.id.p02_line_toleftFollows);

        viewPager = (ViewPager) findViewById(R.id.P02_personalViewPager);

        modelEntity = (MongoPeople)getIntent().getExtras().getSerializable(INPUT_MODEL);

        ImageLoader.getInstance().displayImage(modelEntity.getPortrait(), (ImageView)findViewById(R.id.P02_model_image_view), AppUtil.getPortraitDisplayOptions());
        ((TextView) findViewById(R.id.P02_model_name_text_view)).setText(String.valueOf(modelEntity.getName()));
        ((TextView) findViewById(R.id.P02_model_job_text_view)).setText(String.valueOf(modelEntity.getJob()));
        ((TextView) findViewById(R.id.P02_model_height_weight_text_view)).setText(String.valueOf(modelEntity.getHeightWeight()));
        ImageLoader.getInstance().displayImage(modelEntity.getBackground(), (ImageView)findViewById(R.id.P02_back_image_view), AppUtil.getModelBackgroundDisplayOptions());
        ((TextView) findViewById(R.id.P02_show_number_text_view)).setText(String.valueOf(modelEntity.getNumberShows()));
        ((TextView)findViewById(R.id.P02_followed_number_text_view)).setText(String.valueOf(modelEntity.getNumberFollowers()));
        ((TextView)findViewById(R.id.P02_follower_number_text_view)).setText(String.valueOf(modelEntity.getNumberFollowers()));
        followSignText = (ImageView) findViewById(R.id.P02_follow_sign_text);

        if(null != modelEntity && modelEntity.getModelIsFollowedByCurrentUser()){
            followSignText.setBackgroundResource(R.drawable.badge_unfollow_btn);
        }

        ArrayList<View> pagerViewList = new ArrayList<View>();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pagerViewList.add(inflater.inflate(R.layout.pager_p02_model_item, null));
        pagerViewList.add(inflater.inflate(R.layout.pager_p02_model_item, null));
        pagerViewList.add(inflater.inflate(R.layout.pager_p02_model_item, null));
//        pagerViewList.add(inflater.inflate(R.layout.activity_personal_pager_follow, null));
        viewPagerAdapter = new P02ModelViewPagerAdapter(pagerViewList);

        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setIndicatorBackground(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setIndicatorListener();

        // shows list page;
        latestPullRefreshListView = (MPullRefreshListView) pagerViewList.get(0).findViewById(R.id.pager_P02_item_list);
        latestListView = latestPullRefreshListView.getRefreshableView();

        LinkedList<MongoShow> itemEntities = new LinkedList<MongoShow>();
        itemListAdapter = new P02ModelItemListAdapter(this, itemEntities);

        latestListView.setAdapter(itemListAdapter);
        latestPullRefreshListView.setScrollLoadEnabled(true);
        latestPullRefreshListView.setPullRefreshEnabled(false);
        latestPullRefreshListView.setPullLoadEnabled(true);
        latestPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //doShowsRefreshDataTask();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                doShowsLoadMoreTask();
            }
        });

        doShowsRefreshDataTask();

        // followed list page;
        followedPullRefreshListView = (MPullRefreshListView) pagerViewList.get(1).findViewById(R.id.pager_P02_item_list);
        followedListView = followedPullRefreshListView.getRefreshableView();
        ArrayList<MongoPeople> followedPeopleList = new ArrayList<MongoPeople>();
        followedPeopleListAdapter = new P02ModelFollowPeopleListAdapter(this, followedPeopleList);

        followedListView.setAdapter(followedPeopleListAdapter);
        followedPullRefreshListView.setScrollLoadEnabled(true);
        followedPullRefreshListView.setPullRefreshEnabled(false);
        followedPullRefreshListView.setPullLoadEnabled(true);
        followedPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                doFollowedRefreshDataTask();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                doFollowedLoadMoreTask();
            }
        });

        followedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        doFollowedRefreshDataTask();

        // followers list page;
        followerPullRefreshListView = (MPullRefreshListView) pagerViewList.get(2).findViewById(R.id.pager_P02_item_list);
        followerListView = followerPullRefreshListView.getRefreshableView();
        ArrayList<MongoPeople> followerPeopleList = new ArrayList<MongoPeople>();
        followerPeopleListAdapter = new P02ModelFollowPeopleListAdapter(this, followerPeopleList);
        followerPeopleListAdapter.setP02ModelActivity(this);
        followerListView.setAdapter(followerPeopleListAdapter);
        followerPullRefreshListView.setScrollLoadEnabled(true);
        followerPullRefreshListView.setPullRefreshEnabled(false);
        followerPullRefreshListView.setPullLoadEnabled(true);
        followerPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                doFollowersRefreshDataTask();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                doFollowersLoadMoreTask();
            }
        });

        followerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(P02ModelActivity.this, U01PersonalActivity.class);
                intent.putExtra(U01PersonalActivity.U01PERSONALACTIVITY_PEOPLE, ((MongoPeople)followerPeopleListAdapter.getItem(position)));
                startActivity(intent);
            }
        });
        doFollowersRefreshDataTask();
    }

    private void setIndicatorBackground(int pos) {
        newRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_default_activity_personal));
        discountRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_default_activity_personal));
        fansRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_default_activity_personal));
        followRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_default_activity_personal));

        line1.setVisibility(View.GONE);
        line2.setVisibility(View.GONE);
        line3.setVisibility(View.GONE);

        if (pos == 0) {
            newRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_chosen_activity_personal));
            line2.setVisibility(View.VISIBLE);
            line3.setVisibility(View.VISIBLE);
        } else if (pos == 1) {
            discountRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_chosen_activity_personal));
            line3.setVisibility(View.VISIBLE);
        } else if (pos == 2) {
            fansRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_chosen_activity_personal));
            line1.setVisibility(View.VISIBLE);
        } else if (pos == 3) {
            followRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_chosen_activity_personal));
        }
    }

    private void setIndicatorListener() {
        newRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
            }
        });
        discountRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
            }
        });
        fansRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(2);
            }
        });
        followRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(QSModel.INSTANCE.loggedin()) {
                    followOrUnfollowTask();
                } else {
                    Toast.makeText(P02ModelActivity.this, "请先登录！", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(P02ModelActivity.this, U06LoginActivity.class));
                }
            }
        });
    }

    private void doShowsRefreshDataTask() {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.GET, QSAppWebAPI.getModelShowsApi(String.valueOf(modelEntity.get_id()), "1"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ((TextView) findViewById(R.id.P02_show_number_text_view)).setText(MetadataParser.getNumTotal(response));
                if (MetadataParser.hasError(response)) {
                    latestPullRefreshListView.onPullUpRefreshComplete();
                    latestPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex = 1;

                LinkedList<MongoShow> modelShowEntities = FeedingParser.parse(response);

                itemListAdapter.resetData(modelShowEntities);
                itemListAdapter.notifyDataSetChanged();
                latestPullRefreshListView.onPullUpRefreshComplete();
                latestPullRefreshListView.setHasMoreData(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                latestPullRefreshListView.onPullUpRefreshComplete();
                handleErrorMsg(error);
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void doShowsLoadMoreTask() {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.GET, QSAppWebAPI.getModelShowsApi(String.valueOf(modelEntity.get_id()), String.valueOf(pageIndex + 1)), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //((TextView) findViewById(R.id.P02_show_number_text_view)).setText(getNumTotal(response));
                if (MetadataParser.hasError(response)) {
                    Toast.makeText(P02ModelActivity.this, "没有更多数据了！", Toast.LENGTH_SHORT).show();
                    latestPullRefreshListView.onPullUpRefreshComplete();
                    latestPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex++;

                LinkedList<MongoShow> modelShowEntities = FeedingParser.parse(response);

                itemListAdapter.addData(modelShowEntities);
                itemListAdapter.notifyDataSetChanged();
                latestPullRefreshListView.onPullUpRefreshComplete();
                latestPullRefreshListView.setHasMoreData(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                latestPullRefreshListView.onPullUpRefreshComplete();
                handleErrorMsg(error);
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void doFollowedRefreshDataTask() {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.GET, QSAppWebAPI.getQueryPeopleFollowedApi(String.valueOf(modelEntity.get_id()), "1"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ((TextView)findViewById(R.id.P02_followed_number_text_view)).setText(MetadataParser.getNumTotal(response));
                if (MetadataParser.hasError(response)) {
                    followedPullRefreshListView.onPullUpRefreshComplete();
                    followedPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex = 1;

                ArrayList<MongoPeople> modelShowEntities = PeopleParser.parseQueryFollowed(response);

                followedPeopleListAdapter.resetData(modelShowEntities);
                followedPeopleListAdapter.notifyDataSetChanged();
                followedPullRefreshListView.onPullUpRefreshComplete();
                followedPullRefreshListView.setHasMoreData(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                followedPullRefreshListView.onPullUpRefreshComplete();
                handleErrorMsg(error);
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void doFollowedLoadMoreTask() {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.GET, QSAppWebAPI.getQueryPeopleFollowedApi(String.valueOf(modelEntity.get_id()), String.valueOf(pageIndex + 1)), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    Toast.makeText(P02ModelActivity.this, "没有更多数据了！", Toast.LENGTH_SHORT).show();
                    followedPullRefreshListView.onPullUpRefreshComplete();
                    followedPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex++;

                ArrayList<MongoPeople> modelShowEntities = PeopleParser.parseQueryFollowed(response);

                followedPeopleListAdapter.addData(modelShowEntities);
                followedPeopleListAdapter.notifyDataSetChanged();
                followedPullRefreshListView.onPullUpRefreshComplete();
                followedPullRefreshListView.setHasMoreData(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                followedPullRefreshListView.onPullUpRefreshComplete();
                handleErrorMsg(error);
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void doFollowersRefreshDataTask() {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.GET, QSAppWebAPI.getQueryPeopleFollowerApi(String.valueOf(modelEntity.get_id()), "1"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ((TextView)findViewById(R.id.P02_follower_number_text_view)).setText(MetadataParser.getNumTotal(response));
                if (MetadataParser.hasError(response)) {
                    followedPeopleListAdapter.notifyDataSetChanged();
                    followerPullRefreshListView.onPullUpRefreshComplete();
                    followerPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex = 1;

                ArrayList<MongoPeople> modelShowEntities = PeopleParser.parseQueryFollowers(response);

                followerPeopleListAdapter.resetData(modelShowEntities);
                followerPeopleListAdapter.notifyDataSetChanged();
                followerPullRefreshListView.onPullUpRefreshComplete();
                followerPullRefreshListView.setHasMoreData(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                followerPullRefreshListView.onPullUpRefreshComplete();
                handleErrorMsg(error);
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void doFollowersLoadMoreTask() {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.GET, QSAppWebAPI.getQueryPeopleFollowerApi(String.valueOf(modelEntity.get_id()), String.valueOf(pageIndex + 1)), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ((TextView)findViewById(R.id.P02_follower_number_text_view)).setText(MetadataParser.getNumTotal(response));
                if (MetadataParser.hasError(response)) {
                    Toast.makeText(P02ModelActivity.this, "没有更多数据了！", Toast.LENGTH_SHORT).show();
                    followerPullRefreshListView.onPullUpRefreshComplete();
                    followerPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex++;


                ArrayList<MongoPeople> modelShowEntities = PeopleParser.parseQueryFollowers(response);

                followerPeopleListAdapter.addData(modelShowEntities);
                followerPeopleListAdapter.notifyDataSetChanged();
                followerPullRefreshListView.onPullUpRefreshComplete();
                followerPullRefreshListView.setHasMoreData(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                followerPullRefreshListView.onPullUpRefreshComplete();
                handleErrorMsg(error);
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void followOrUnfollowTask() {
        if (modelEntity.getModelIsFollowedByCurrentUser()) {
            __unFollowModel();
        } else {
            __followModel();
        }
    }

    private void __followModel() {
        Map<String, String> followData = new HashMap<String, String>();
        followData.put("_id", modelEntity.get_id());
        JSONObject jsonObject = new JSONObject(followData);

        QSJsonObjectRequest mJsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getPeopleFollowApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                    if (!MetadataParser.hasError(response)) {
                        showMessage(P02ModelActivity.this, "关注成功");
                        modelEntity.setModelIsFollowedByCurrentUser(true);
                        followSignText.setBackgroundResource(R.drawable.badge_unfollow_btn);
                        doFollowersRefreshDataTask();
                        sendBroadcast(new Intent(U01PersonalActivity.USER_UPDATE));
                    }else{
                        showMessage(P02ModelActivity.this, "关注失败" + response);
                    }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showMessage(P02ModelActivity.this, error.toString());
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(mJsonObjectRequest);
    }

    private void __unFollowModel() {
        Map<String, String> followData = new HashMap<String, String>();
        followData.put("_id", modelEntity.get_id());
        JSONObject jsonObject = new JSONObject(followData);

        QSJsonObjectRequest mJsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getPeopleUnfollowApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                    if (!MetadataParser.hasError(response)) {
                        showMessage(P02ModelActivity.this, "取消关注成功");
                        modelEntity.setModelIsFollowedByCurrentUser(false);
                        followSignText.setBackgroundResource(R.drawable.badge_follow_btn);
                        doFollowersRefreshDataTask();
                        sendBroadcast(new Intent(U01PersonalActivity.USER_UPDATE));
                    }else{
                        showMessage(P02ModelActivity.this, "取消关注失败");
                    }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showMessage(P02ModelActivity.this, error.toString());
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(mJsonObjectRequest);
    }

    private void handleErrorMsg(VolleyError error) {
        Log.i("P02ModelActivity", error.toString());
    }

    private void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("P02Model"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("P02Model"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

    public void refreshWatchNum() {
        TextView tv = (TextView) findViewById(R.id.P02_follower_number_text_view);
        tv.setText(String.valueOf(Integer.parseInt(tv.getText().toString()) - 1));
    }

}
