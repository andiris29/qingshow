package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.P02ModelFollowPeopleListAdapter;
import com.focosee.qingshow.adapter.P02ModelItemListAdapter;
import com.focosee.qingshow.adapter.P02ModelViewPagerAdapter;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.FollowPeopleEntity;
import com.focosee.qingshow.entity.ModelEntity;
import com.focosee.qingshow.entity.ModelShowEntity;
import com.focosee.qingshow.entity.People;
import com.focosee.qingshow.request.MJsonObjectRequest;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class P02ModelActivity extends Activity {

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
    private ModelEntity modelEntity;
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

        modelEntity = (ModelEntity)getIntent().getExtras().getSerializable(INPUT_MODEL);

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

        ArrayList<ModelShowEntity> itemEntities = new ArrayList<ModelShowEntity>();
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

        latestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(P02ModelActivity.this, S03SHowActivity.class);
                intent.putExtra(S03SHowActivity.INPUT_SHOW_ENTITY_ID, ((ModelShowEntity)itemListAdapter.getItem(position)).get_id());
                startActivity(intent);
            }
        });

        doShowsRefreshDataTask();

        // followed list page;
        followedPullRefreshListView = (MPullRefreshListView) pagerViewList.get(1).findViewById(R.id.pager_P02_item_list);
        followedListView = followedPullRefreshListView.getRefreshableView();
        ArrayList<FollowPeopleEntity> followedPeopleList = new ArrayList<FollowPeopleEntity>();
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
        ArrayList<FollowPeopleEntity> followerPeopleList = new ArrayList<FollowPeopleEntity>();
        followerPeopleListAdapter = new P02ModelFollowPeopleListAdapter(this, followerPeopleList);

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
                intent.putExtra(U01PersonalActivity.U01PERSONALACTIVITY_PEOPLE_ID, ((FollowPeopleEntity)followerPeopleListAdapter.getItem(position))._id);
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
                followOrUnfollowTask();
            }
        });
    }

    private void doShowsRefreshDataTask() {
        MJsonObjectRequest jsonObjectRequest = new MJsonObjectRequest(Request.Method.GET, QSAppWebAPI.getModelShowsApi(String.valueOf(modelEntity.get_id()), "1"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ((TextView) findViewById(R.id.P02_show_number_text_view)).setText(getTotalDataFromResponse(response));
                if (checkErrorExist(response)) {
                    latestPullRefreshListView.onPullUpRefreshComplete();
                    latestPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex = 1;

                ArrayList<ModelShowEntity> modelShowEntities = ModelShowEntity.getModelShowEntities(response);

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
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void doShowsLoadMoreTask() {
        MJsonObjectRequest jsonObjectRequest = new MJsonObjectRequest(Request.Method.GET, QSAppWebAPI.getModelShowsApi(String.valueOf(modelEntity.get_id()), String.valueOf(pageIndex + 1)), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //((TextView) findViewById(R.id.P02_show_number_text_view)).setText(getTotalDataFromResponse(response));
                if (checkErrorExist(response)) {
                    Toast.makeText(P02ModelActivity.this, "没有更多数据了！", Toast.LENGTH_SHORT).show();
                    latestPullRefreshListView.onPullUpRefreshComplete();
                    latestPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex++;

                ArrayList<ModelShowEntity> modelShowEntities = ModelShowEntity.getModelShowEntities(response);

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
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void doFollowedRefreshDataTask() {
        MJsonObjectRequest jsonObjectRequest = new MJsonObjectRequest(Request.Method.GET, QSAppWebAPI.getQueryPeopleFollowedApi(String.valueOf(modelEntity.get_id()), "1"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ((TextView)findViewById(R.id.P02_followed_number_text_view)).setText(getTotalDataFromResponse(response));
                if (checkErrorExist(response)) {
                    followedPullRefreshListView.onPullUpRefreshComplete();
                    followedPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex = 1;

                ArrayList<FollowPeopleEntity> modelShowEntities = FollowPeopleEntity.getFollowPeopleList(response);

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
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void doFollowedLoadMoreTask() {
        MJsonObjectRequest jsonObjectRequest = new MJsonObjectRequest(Request.Method.GET, QSAppWebAPI.getQueryPeopleFollowedApi(String.valueOf(modelEntity.get_id()), String.valueOf(pageIndex + 1)), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (checkErrorExist(response)) {
                    Toast.makeText(P02ModelActivity.this, "没有更多数据了！", Toast.LENGTH_SHORT).show();
                    followedPullRefreshListView.onPullUpRefreshComplete();
                    followedPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex++;

                ArrayList<FollowPeopleEntity> modelShowEntities = FollowPeopleEntity.getFollowPeopleList(response);

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
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void doFollowersRefreshDataTask() {
        MJsonObjectRequest jsonObjectRequest = new MJsonObjectRequest(Request.Method.GET, QSAppWebAPI.getQueryPeopleFollowerApi(String.valueOf(modelEntity.get_id()), "1"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ((TextView)findViewById(R.id.P02_follower_number_text_view)).setText(getTotalDataFromResponse(response));
                if (checkErrorExist(response)) {
                    followedPeopleListAdapter.notifyDataSetChanged();
                    followerPullRefreshListView.onPullUpRefreshComplete();
                    followerPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex = 1;


                ArrayList<FollowPeopleEntity> modelShowEntities = FollowPeopleEntity.getFollowPeopleList(response);

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
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void doFollowersLoadMoreTask() {
        MJsonObjectRequest jsonObjectRequest = new MJsonObjectRequest(Request.Method.GET, QSAppWebAPI.getQueryPeopleFollowerApi(String.valueOf(modelEntity.get_id()), String.valueOf(pageIndex + 1)), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ((TextView)findViewById(R.id.P02_follower_number_text_view)).setText(getTotalDataFromResponse(response));
                if (checkErrorExist(response)) {
                    Toast.makeText(P02ModelActivity.this, "没有更多数据了！", Toast.LENGTH_SHORT).show();
                    followerPullRefreshListView.onPullUpRefreshComplete();
                    followerPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex++;


                ArrayList<FollowPeopleEntity> modelShowEntities = FollowPeopleEntity.getFollowPeopleList(response);

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
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void followOrUnfollowTask() {
        if (modelEntity.getModelIsFollowedByCurrentUser()) {
            __unFollowModel();
        } else {
            __followModel();
        }
        QSApplication.get().refreshPeople(this);
    }

    private void __followModel() {
        Map<String, String> followData = new HashMap<String, String>();
        followData.put("_id", modelEntity.get_id());
        JSONObject jsonObject = new JSONObject(followData);

        MJsonObjectRequest mJsonObjectRequest = new MJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getPeopleFollowApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.get("metadata").toString().equals("{}")) {
                        showMessage(P02ModelActivity.this, "关注成功");
                        modelEntity.setModelIsFollowedByCurrentUser(true);
                        followSignText.setBackgroundResource(R.drawable.badge_unfollow_btn);
                        doFollowersRefreshDataTask();
                    }else{
                        showMessage(P02ModelActivity.this, "关注失败" + response.toString() + response.get("metadata").toString().length());
                    }
                }catch (Exception e) {
                    showMessage(P02ModelActivity.this, e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showMessage(P02ModelActivity.this, error.toString());
            }
        });

        QSApplication.get().QSRequestQueue().add(mJsonObjectRequest);
    }

    private void __unFollowModel() {
        Map<String, String> followData = new HashMap<String, String>();
        followData.put("_id", modelEntity.get_id());
        JSONObject jsonObject = new JSONObject(followData);

        MJsonObjectRequest mJsonObjectRequest = new MJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getPeopleUnfollowApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.get("metadata").toString().equals("{}")) {
                        showMessage(P02ModelActivity.this, "取消关注成功");
                        modelEntity.setModelIsFollowedByCurrentUser(false);
                        followSignText.setBackgroundResource(R.drawable.badge_follow_btn);
                        doFollowersRefreshDataTask();
                    }else{
                        showMessage(P02ModelActivity.this, "取消关注失败" + response.toString() + response.get("metadata").toString().length());
                    }
                }catch (Exception e) {
                    showMessage(P02ModelActivity.this, e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showMessage(P02ModelActivity.this, error.toString());
            }
        });

        QSApplication.get().QSRequestQueue().add(mJsonObjectRequest);
    }

    private boolean checkErrorExist(JSONObject response) {
        try {
            return ((JSONObject)response.get("metadata")).has("error");
        }catch (Exception e) {
            return true;
        }
    }

    private void handleErrorMsg(VolleyError error) {
        Log.i("P02ModelActivity", error.toString());
    }

    private void showMessage(Context context, String message) {
        //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        Log.i(context.getPackageName(), message);
    }

    private String getTotalDataFromResponse(JSONObject response) {
        try {
            return ((JSONObject)response.get("metadata")).get("numTotal").toString();
        } catch (Exception e) {
            return "0";
        }
    }

}
