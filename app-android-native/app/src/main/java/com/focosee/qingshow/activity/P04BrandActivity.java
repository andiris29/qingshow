package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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
import com.focosee.qingshow.adapter.P04BrandItemListAdapter;
import com.focosee.qingshow.adapter.P04BrandViewPagerAdapter;
import com.focosee.qingshow.adapter.P04FansListAdapter;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.BrandEntity;
import com.focosee.qingshow.entity.FollowPeopleEntity;
import com.focosee.qingshow.entity.ModelShowEntity;
import com.focosee.qingshow.entity.ShowDetailEntity;
import com.focosee.qingshow.request.MJsonObjectRequest;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class P04BrandActivity extends Activity {
    public static final String INPUT_BRAND = "P04BrandActivity_input_brand";
    public static final String INPUT_ITEM = "P04BrandActivity_input_item";

    private ViewPager viewPager;
    private MPullRefreshListView latestPullRefreshListView;
    private ListView latestListView;
    private MPullRefreshListView discountPullRefreshListView;
    private ListView discountListView;
    private MPullRefreshListView fansPullRefreshListView;
    private ListView fansListView;

    private RelativeLayout newRelativeLayout;
    private RelativeLayout discountRelativeLayout;
    private RelativeLayout fansRelativeLayout;
    private RelativeLayout followRelativeLayout;
    private ImageView followSignText;

    private LinearLayout line1;
    private LinearLayout line2;
    private LinearLayout line3;

    private P04BrandViewPagerAdapter viewPagerAdapter;
    private P04BrandItemListAdapter newestBrandItemListAdapter;
    private P04BrandItemListAdapter discountBrandItemListAdapter;
    private P04FansListAdapter fansListAdapter;

    private ArrayList<View> pagerViewList;

    private BrandEntity brandEntity;
    private ShowDetailEntity.RefItem additionalItemEntity;
    private int pageIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p04_brand);

        brandEntity = (null != getIntent().getExtras().getSerializable(INPUT_BRAND)) ? ((BrandEntity)getIntent().getExtras().getSerializable(INPUT_BRAND))
                                                                                        : null;
        additionalItemEntity = (null != getIntent().getExtras().getSerializable(INPUT_ITEM)) ? (ShowDetailEntity.RefItem) getIntent().getExtras().getSerializable(INPUT_ITEM)
                                                                                        : null;

        findViewById(R.id.P04_back_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                P04BrandActivity.this.finish();
            }
        });

        newRelativeLayout = (RelativeLayout) findViewById(R.id.P04_new_relativeLayout);
        discountRelativeLayout = (RelativeLayout) findViewById(R.id.P04_discount_relativeLayout);
        fansRelativeLayout = (RelativeLayout) findViewById(R.id.P04_fans_relativeLayout);
        followRelativeLayout = (RelativeLayout) findViewById(R.id.P04_follow_relativeLayout);

        line1 = (LinearLayout) findViewById(R.id.p04_line_toleftDiscount);
        line2 = (LinearLayout) findViewById(R.id.p04_line_toleftFans);
        line3 = (LinearLayout) findViewById(R.id.p04_line_toleftFollows);

        ((TextView) findViewById(R.id.P04_brand_newest_number_text_view)).setText((null != brandEntity) ? brandEntity.getNewestNumber() : "0");
        ((TextView) findViewById(R.id.P04_brand_discount_number_text_view)).setText((null != brandEntity) ? brandEntity.getDiscountNumber() : "0");
        ((TextView) findViewById(R.id.P04_brand_fans_number_text_view)).setText((null != brandEntity) ? brandEntity.getFansNumber() : "0");

        viewPager = (ViewPager) findViewById(R.id.P04_content_viewPager);

        ImageLoader.getInstance().displayImage((null != brandEntity) ? brandEntity.getBrandSlogan() : "", (ImageView) findViewById(R.id.P04_brand_portrait), AppUtil.getPortraitDisplayOptions());
        ((TextView)findViewById(R.id.P04_brand_name)).setText((null != brandEntity) ? brandEntity.getBrandName() : "未定义");
        ((TextView)findViewById(R.id.P04_brand_url)).setText((null != brandEntity) ? brandEntity.getBrandName() : "未定义");
        followSignText = (ImageView) findViewById(R.id.P04_follow_sign_text);

        constructViewPager();

        setIndicatorListener();

        // new brand list page;
        configNewestShowListPage();

        // discount brand list page;
        configDiscountShowListPage();

        // fans list page;
        configFansListPage();

    }

    private void constructViewPager() {
        pagerViewList = new ArrayList<View>();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pagerViewList.add(inflater.inflate(R.layout.pager_p04_brand_item, null));
        pagerViewList.add(inflater.inflate(R.layout.pager_p04_brand_item, null));
        pagerViewList.add(inflater.inflate(R.layout.pager_p02_model_item, null));
        viewPagerAdapter = new P04BrandViewPagerAdapter(pagerViewList);

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

    private void configNewestShowListPage() {
        latestPullRefreshListView = (MPullRefreshListView) pagerViewList.get(0).findViewById(R.id.pager_P04_item_list);
        latestListView = latestPullRefreshListView.getRefreshableView();

        ArrayList<ShowDetailEntity.RefItem> newestBrandItemDataList = new ArrayList<ShowDetailEntity.RefItem>();
        if (null != additionalItemEntity) {
            newestBrandItemDataList.add(additionalItemEntity);
        }
        newestBrandItemListAdapter = new P04BrandItemListAdapter(this, newestBrandItemDataList);

        latestListView.setAdapter(newestBrandItemListAdapter);
        latestPullRefreshListView.setScrollLoadEnabled(true);
        latestPullRefreshListView.setPullRefreshEnabled(true);
        latestPullRefreshListView.setPullLoadEnabled(true);

        if (null != brandEntity) {
            latestPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    doNewestRefreshDataTask();
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    doNewestLoadMoreTask();
                }
            });
            latestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(P04BrandActivity.this, S03SHowActivity.class);
                    intent.putExtra(S03SHowActivity.INPUT_SHOW_ENTITY_ID, ((ModelShowEntity)newestBrandItemListAdapter.getItem(position)).get_id());
                    startActivity(intent);
                }
            });
            latestPullRefreshListView.doPullRefreshing(true, 0);
        } else {
            latestPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    latestPullRefreshListView.onPullDownRefreshComplete();
                    latestPullRefreshListView.setHasMoreData(false);
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    latestPullRefreshListView.onPullUpRefreshComplete();
                    latestPullRefreshListView.setHasMoreData(false);
                }
            });
        }

    }

    private void configDiscountShowListPage() {
        discountPullRefreshListView = (MPullRefreshListView) pagerViewList.get(1).findViewById(R.id.pager_P04_item_list);
        discountListView = discountPullRefreshListView.getRefreshableView();

        discountBrandItemListAdapter = new P04BrandItemListAdapter(this, new ArrayList<ShowDetailEntity.RefItem>());

        discountListView.setAdapter(discountBrandItemListAdapter);
        discountPullRefreshListView.setScrollLoadEnabled(true);
        discountPullRefreshListView.setPullRefreshEnabled(true);
        discountPullRefreshListView.setPullLoadEnabled(true);

        if (null != brandEntity) {
            discountPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    doDiscountRefreshDataTask();
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    doDiscountLoadMoreTask();
                }
            });
            discountListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(P04BrandActivity.this, S03SHowActivity.class);
                    intent.putExtra(S03SHowActivity.INPUT_SHOW_ENTITY_ID, ((ModelShowEntity)discountBrandItemListAdapter.getItem(position)).get_id());
                    startActivity(intent);
                }
            });

            discountPullRefreshListView.doPullRefreshing(true, 0);
        } else {
            discountPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    discountPullRefreshListView.onPullDownRefreshComplete();
                    discountPullRefreshListView.setHasMoreData(false);
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    discountPullRefreshListView.onPullUpRefreshComplete();
                    discountPullRefreshListView.setHasMoreData(false);
                }
            });
        }
    }

    private void configFansListPage() {
        fansPullRefreshListView = (MPullRefreshListView) pagerViewList.get(2).findViewById(R.id.pager_P02_item_list);
        fansListView = fansPullRefreshListView.getRefreshableView();
        ArrayList<FollowPeopleEntity> followerPeopleList = new ArrayList<FollowPeopleEntity>();
        fansListAdapter = new P04FansListAdapter(this, followerPeopleList);

        fansListView.setAdapter(fansListAdapter);
        fansPullRefreshListView.setScrollLoadEnabled(true);
        fansPullRefreshListView.setPullRefreshEnabled(true);
        fansPullRefreshListView.setPullLoadEnabled(true);

        if (null != brandEntity) {
            fansPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    doFollowersRefreshDataTask();
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    doFollowersLoadMoreTask();
                }
            });

            fansListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
            fansPullRefreshListView.doPullRefreshing(true, 0);
        } else {
            fansPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    fansPullRefreshListView.onPullDownRefreshComplete();
                    fansPullRefreshListView.setHasMoreData(false);
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    fansPullRefreshListView.onPullUpRefreshComplete();
                    fansPullRefreshListView.setHasMoreData(false);
                }
            });
        }
    }



    private void doNewestRefreshDataTask() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, QSAppWebAPI.getBrandMatchApi(String.valueOf(brandEntity.get_id()), "1"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (checkErrorExist(response)) {
                    try {
                        Toast.makeText(P04BrandActivity.this, ((JSONObject) response.get("metadata")).get("devInfo").toString(), Toast.LENGTH_SHORT).show();
                    }catch (JSONException e) {
                        Toast.makeText(P04BrandActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    latestPullRefreshListView.onPullDownRefreshComplete();
                    latestPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex = 1;

                ArrayList<ShowDetailEntity.RefItem> brandItemsEntities = ShowDetailEntity.RefItem.getItemEntities(response);

                if (null != additionalItemEntity) {
                    brandItemsEntities.add(0, additionalItemEntity);
                }

                newestBrandItemListAdapter.resetData(brandItemsEntities);
                newestBrandItemListAdapter.notifyDataSetChanged();
                latestPullRefreshListView.onPullDownRefreshComplete();
                latestPullRefreshListView.setHasMoreData(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                latestPullRefreshListView.onPullDownRefreshComplete();
                handleErrorMsg(error);
            }
        });
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void doNewestLoadMoreTask() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, QSAppWebAPI.getBrandMatchApi(String.valueOf(brandEntity.get_id()), String.valueOf(pageIndex + 1)), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (checkErrorExist(response)) {
                    try {
                        Toast.makeText(P04BrandActivity.this, ((JSONObject)response.get("metadata")).get("devInfo").toString(), Toast.LENGTH_SHORT).show();
                    }catch (JSONException e) {
                        Toast.makeText(P04BrandActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    latestPullRefreshListView.onPullUpRefreshComplete();
                    latestPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex++;

                ArrayList<ShowDetailEntity.RefItem> brandItemsEntities = ShowDetailEntity.RefItem.getItemEntities(response);

                newestBrandItemListAdapter.addData(brandItemsEntities);
                newestBrandItemListAdapter.notifyDataSetChanged();
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

    private void doDiscountRefreshDataTask() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, QSAppWebAPI.getBrandDiscountApi(String.valueOf(brandEntity.get_id()), "1"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (checkErrorExist(response)) {
                    try {
                        Toast.makeText(P04BrandActivity.this, ((JSONObject)response.get("metadata")).get("devInfo").toString(), Toast.LENGTH_SHORT).show();
                    }catch (JSONException e) {
                        Toast.makeText(P04BrandActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    discountPullRefreshListView.onPullDownRefreshComplete();
                    discountPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex = 1;

                ArrayList<ShowDetailEntity.RefItem> modelShowEntities = ShowDetailEntity.RefItem.getItemEntities(response);

                discountBrandItemListAdapter.resetData(modelShowEntities);
                discountBrandItemListAdapter.notifyDataSetChanged();
                discountPullRefreshListView.onPullDownRefreshComplete();
                discountPullRefreshListView.setHasMoreData(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                discountPullRefreshListView.onPullDownRefreshComplete();
                handleErrorMsg(error);
            }
        });
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void doDiscountLoadMoreTask() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, QSAppWebAPI.getBrandDiscountApi(String.valueOf(brandEntity.get_id()), String.valueOf(pageIndex + 1)), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (checkErrorExist(response)) {
                    try {
                        Toast.makeText(P04BrandActivity.this, ((JSONObject)response.get("metadata")).get("devInfo").toString(), Toast.LENGTH_SHORT).show();
                    }catch (JSONException e) {
                        Toast.makeText(P04BrandActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    discountPullRefreshListView.onPullUpRefreshComplete();
                    discountPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex++;

                ArrayList<ShowDetailEntity.RefItem> modelShowEntities = ShowDetailEntity.RefItem.getItemEntities(response);

                discountBrandItemListAdapter.addData(modelShowEntities);
                discountBrandItemListAdapter.notifyDataSetChanged();
                discountPullRefreshListView.onPullUpRefreshComplete();
                discountPullRefreshListView.setHasMoreData(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                discountPullRefreshListView.onPullUpRefreshComplete();
                handleErrorMsg(error);
            }
        });
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void doFollowersRefreshDataTask() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, QSAppWebAPI.getQueryPeopleFollowerApi(String.valueOf(brandEntity.get_id()), "1"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (checkErrorExist(response)) {
                    try {
                        Toast.makeText(P04BrandActivity.this, ((JSONObject)response.get("metadata")).get("devInfo").toString(), Toast.LENGTH_SHORT).show();
                    }catch (JSONException e) {
                        Toast.makeText(P04BrandActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    fansPullRefreshListView.onPullDownRefreshComplete();
                    fansPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex = 1;

                ArrayList<FollowPeopleEntity> modelShowEntities = FollowPeopleEntity.getFollowPeopleList(response);

                fansListAdapter.resetData(modelShowEntities);
                fansListAdapter.notifyDataSetChanged();
                fansPullRefreshListView.onPullDownRefreshComplete();
                fansPullRefreshListView.setHasMoreData(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                fansPullRefreshListView.onPullDownRefreshComplete();
                handleErrorMsg(error);
            }
        });
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void doFollowersLoadMoreTask() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, QSAppWebAPI.getQueryPeopleFollowerApi(String.valueOf(brandEntity.get_id()), String.valueOf(pageIndex + 1)), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (checkErrorExist(response)) {
                    try {
                        Toast.makeText(P04BrandActivity.this, ((JSONObject)response.get("metadata")).get("devInfo").toString(), Toast.LENGTH_SHORT).show();
                    }catch (JSONException e) {
                        Toast.makeText(P04BrandActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    fansPullRefreshListView.onPullUpRefreshComplete();
                    fansPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex++;

                ArrayList<FollowPeopleEntity> modelShowEntities = FollowPeopleEntity.getFollowPeopleList(response);

                fansListAdapter.addData(modelShowEntities);
                fansListAdapter.notifyDataSetChanged();
                fansPullRefreshListView.onPullUpRefreshComplete();
                fansPullRefreshListView.setHasMoreData(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                fansPullRefreshListView.onPullUpRefreshComplete();
                handleErrorMsg(error);
            }
        });
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void followOrUnfollowTask() {
        if (brandEntity.getModelIsFollowedByCurrentUser()) {
            __unFollowModel();
        } else {
            __followModel();
        }
    }

    private void __followModel() {
        Map<String, String> followData = new HashMap<String, String>();
        followData.put("_id", brandEntity.get_id());
        JSONObject jsonObject = new JSONObject(followData);

        MJsonObjectRequest mJsonObjectRequest = new MJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getPeopleFollowApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.get("metadata").toString().equals("{}")) {
                        showMessage(P04BrandActivity.this, "关注成功");
                        brandEntity.setModelIsFollowedByCurrentUser(true);
                        followSignText.setBackgroundResource(R.drawable.badge_unfollow_btn2);
                    }else{
                        showMessage(P04BrandActivity.this, "关注失败" + response.toString() + response.get("metadata").toString().length());
                    }
                }catch (Exception e) {
                    showMessage(P04BrandActivity.this, e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showMessage(P04BrandActivity.this, error.toString());
            }
        });

        QSApplication.get().QSRequestQueue().add(mJsonObjectRequest);
    }

    private void __unFollowModel() {
        Map<String, String> followData = new HashMap<String, String>();
        followData.put("_id", brandEntity.get_id());
        JSONObject jsonObject = new JSONObject(followData);

        MJsonObjectRequest mJsonObjectRequest = new MJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getPeopleUnfollowApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.get("metadata").toString().equals("{}")) {
                        showMessage(P04BrandActivity.this, "取消关注成功");
                        brandEntity.setModelIsFollowedByCurrentUser(false);
                        followSignText.setBackgroundResource(R.drawable.badge_follow_btn2);
                    }else{
                        showMessage(P04BrandActivity.this, "取消关注失败" + response.toString() + response.get("metadata").toString().length());
                    }
                }catch (Exception e) {
                    showMessage(P04BrandActivity.this, e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showMessage(P04BrandActivity.this, error.toString());
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
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("P04BrandActivity", error.toString());
    }

    private void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        Log.i(context.getPackageName(), message);
    }
}
