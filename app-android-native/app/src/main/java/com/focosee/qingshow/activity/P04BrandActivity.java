package com.focosee.qingshow.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.HeadScrollAdapter;
import com.focosee.qingshow.adapter.P02ModelFollowPeopleListAdapter;
import com.focosee.qingshow.adapter.P04BrandItemListAdapter;
import com.focosee.qingshow.adapter.P04BrandViewPagerAdapter;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.code.RolesCode;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.model.vo.mongo.MongoBrand;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.BrandParser;
import com.focosee.qingshow.httpapi.response.dataparser.ItemFeedingParser;
import com.focosee.qingshow.httpapi.response.dataparser.PeopleParser;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.DensityUtil;
import com.focosee.qingshow.widget.MImageView_OriginSize;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.MPullRefreshMultiColumnListView;
import com.focosee.qingshow.widget.MViewPager_NoScroll;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AbsListView;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.huewu.pla.lib.internal.PLA_ListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class P04BrandActivity extends BaseActivity{
    public static final String INPUT_BRAND = "P04BrandActivity_input_brand";
    public static final String INPUT_ITEM = "P04BrandActivity_input_item";
    public static final String INPUT_BRAND_ID = "p04BrandActivity_input_brand";
    private static final String TAG = "P04BrandActivity";

    private RelativeLayout headRelativeLayout;
    private MViewPager_NoScroll viewPager;
    private MPullRefreshListView latestPullRefreshListView;
    private ListView latestListView;
    private MPullRefreshListView discountPullRefreshListView;
    private ListView discountListView;
    private MPullRefreshMultiColumnListView showPullRefreshListView;
    private MultiColumnListView showListView;
    private MPullRefreshListView fansPullRefreshListView;
    private ListView fansListView;

    private RelativeLayout newRelativeLayout;
    private RelativeLayout discountRelativeLayout;
    private RelativeLayout showRelativeLayout;
    private RelativeLayout fansRelativeLayout;
    private RelativeLayout followRelativeLayout;
    private ImageView followSignText;
    private ImageView bgImage;

    private TextView newNumTotal;
    private TextView discountNumTotal;
    private TextView showNumTotal;
    private TextView fansNumTotal;

    private View line1;
    private View line2;
    private View line3;
    private View line4;

    private P04BrandViewPagerAdapter viewPagerAdapter;
    private P04BrandItemListAdapter newestBrandItemListAdapter;
    private P04BrandItemListAdapter discountBrandItemListAdapter;
    private P04BrandItemListAdapter showBrandItemListAdapter;
    private P02ModelFollowPeopleListAdapter fansListAdapter;

    private ArrayList<View> pagerViewList;

    private MongoBrand brandEntity;
    private MongoItem additionalItemEntity;
    private int pageIndex = 1;

    private HeadScrollAdapter headScrollAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p04_brand);
        headRelativeLayout = (RelativeLayout) findViewById(R.id.P04_head_relative);
        headScrollAdapter = new HeadScrollAdapter(headRelativeLayout, this);
        brandEntity = (null != getIntent().getExtras().getSerializable(INPUT_BRAND)) ? ((MongoBrand)getIntent().getExtras().getSerializable(INPUT_BRAND)) : null;
        additionalItemEntity = (null != getIntent().getExtras().getSerializable(INPUT_ITEM)) ? (MongoItem) getIntent().getExtras().getSerializable(INPUT_ITEM): null;


        if(null != additionalItemEntity && null == brandEntity) {

             brandEntity = additionalItemEntity.getBrandRef();
        }

        getBrandFromNet();

        findViewById(R.id.P04_back_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                P04BrandActivity.this.finish();
            }
        });

        newRelativeLayout = (RelativeLayout) findViewById(R.id.P04_new_relativeLayout);
        discountRelativeLayout = (RelativeLayout) findViewById(R.id.P04_discount_relativeLayout);
        showRelativeLayout = (RelativeLayout) findViewById(R.id.P04_show_relativeLayout);
        fansRelativeLayout = (RelativeLayout) findViewById(R.id.P04_fans_relativeLayout);
        followRelativeLayout = (RelativeLayout) findViewById(R.id.P04_follow_relativeLayout);

        line1 = findViewById(R.id.p04_line_toleftDiscount);
        line2 = findViewById(R.id.p04_line_toleftShow);
        line3 = findViewById(R.id.p04_line_toleftFans);
        line4 = findViewById(R.id.p04_line_toleftFollows);

        newNumTotal = (TextView) findViewById(R.id.P04_brand_newest_number_text_view);
        discountNumTotal = (TextView) findViewById(R.id.P04_brand_discount_number_text_view);
        showNumTotal = (TextView) findViewById(R.id.P04_brand_show_number_text_view);
        fansNumTotal = (TextView) findViewById(R.id.P04_brand_fans_number_text_view);

        viewPager = (MViewPager_NoScroll) findViewById(R.id.P04_content_viewPager);
        viewPager.setScrollble(false);

        followSignText = (ImageView) findViewById(R.id.P04_follow_sign_text);
        bgImage = (ImageView) findViewById(R.id.P04_back_image_view);

        newNumTotal.setText("0");
        discountNumTotal.setText("0");
        showNumTotal.setText("0");
        fansNumTotal.setText("0");

        ImageLoader.getInstance().displayImage((null != brandEntity) ? (null == brandEntity.getBrandLogo()) ? "" : brandEntity.getBrandLogo() : ""
                , (ImageView) findViewById(R.id.P04_brand_portrait), AppUtil.getPortraitDisplayOptions());
        ((TextView)findViewById(R.id.P04_brand_name)).setText((null != brandEntity) ? brandEntity.getBrandName() : "未定义");
        //((TextView)findViewById(R.id.P04_brand_url)).setText((null != brandEntity) ? brandEntity.getBrandName() : "未定义");

        ImageLoader.getInstance().displayImage((null != brandEntity) ? brandEntity.background : "", bgImage);

        if(brandEntity.getModelIsFollowedByCurrentUser()){
            followSignText.setBackgroundResource(R.drawable.badge_unfollow_btn);
        }

        constructViewPager();

        setIndicatorListener();

        // new brand list page;
        configNewestShowListPage();

        // discount brand list page;
        configDiscountShowListPage();

        // show list page;
        configShowsListPage();

        // fans list page;
        configFansListPage();

        viewPager.setCurrentItem(0);

    }

    @Override
    public void reconn() {
        doNewestRefreshDataTask();
        doDiscountRefreshDataTask();
        doShowRefreshTask();
        doFollowersRefreshDataTask();
    }

    private void constructViewPager() {
        pagerViewList = new ArrayList<View>();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pagerViewList.add(inflater.inflate(R.layout.pager_p04_brand_item, null));
        pagerViewList.add(inflater.inflate(R.layout.pager_p04_brand_item, null));
        pagerViewList.add(inflater.inflate(R.layout.pager_p04_show_item, null));
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
        showRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_default_activity_personal));
        fansRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_default_activity_personal));
        followRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_default_activity_personal));

        line1.setVisibility(View.GONE);
        line2.setVisibility(View.GONE);
        line3.setVisibility(View.GONE);
        line4.setVisibility(View.GONE);

        latestListView.setPadding(0, headScrollAdapter.headHeight, 0, 0);
        discountListView.setPadding(0, headScrollAdapter.headHeight, 0, 0);
        showListView.setPadding(0, headScrollAdapter.headHeight, 0, 0);
        fansListView.setPadding(0, headScrollAdapter.headHeight, 0, 0);

        if (pos == 0) {
            newRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_chosen_activity_personal));
            line2.setVisibility(View.VISIBLE);
            line3.setVisibility(View.VISIBLE);
            line4.setVisibility(View.VISIBLE);
        } else if (pos == 1) {
            discountRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_chosen_activity_personal));
            line3.setVisibility(View.VISIBLE);
            line4.setVisibility(View.VISIBLE);
        } else if (pos == 2) {
            showRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_chosen_activity_personal));
            line1.setVisibility(View.VISIBLE);
            line4.setVisibility(View.VISIBLE);
        } else if (pos == 3) {
            fansRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_chosen_activity_personal));
            line1.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
        } else if (pos == 4) {
            //followRelativeLayout.setBackgroundColor(getResources().getColor(R.color.indicator_bg_chosen_activity_personal));
        }
    }

    private void setIndicatorListener() {
        newRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                headRelativeLayout.setY(0);
                latestListView.setPadding(0, headScrollAdapter.headHeight, 0, 0);
                viewPager.setCurrentItem(0);
            }
        });
        discountRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                headRelativeLayout.setY(0);
                discountListView.setPadding(0, headScrollAdapter.headHeight, 0, 0);
                viewPager.setCurrentItem(1);
            }
        });
        showRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                headRelativeLayout.setY(0);
                showListView.setPadding(0, headScrollAdapter.headHeight, 0, 0);
                viewPager.setCurrentItem(2);
            }
        });
        fansRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                headRelativeLayout.setY(0);
                fansListView.setPadding(0, headScrollAdapter.headHeight, 0, 0);
                viewPager.setCurrentItem(3);
            }
        });
        followRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(QSModel.INSTANCE.loggedin()) {
                    followOrUnfollowTask();
                }else{
                    Toast.makeText(P04BrandActivity.this, "请先登录！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(P04BrandActivity.this, U06LoginActivity.class);
                    P04BrandActivity.this.startActivity(intent);
                }
            }
        });

    }

    private void getBrandFromNet() {
        if(null == brandEntity)return;
        String _ids = brandEntity.get_id();
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.GET, QSAppWebAPI.getBrandQueryApi(_ids), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    Toast.makeText(P04BrandActivity.this, "店铺不存在了！", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                ArrayList<MongoBrand> modelShowEntities = BrandParser.parseQueryBrands(response);
                if(null == modelShowEntities) {
                    finish();
                    return;
                }
                brandEntity = modelShowEntities.get(0);
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void configNewestShowListPage() {
        latestPullRefreshListView = (MPullRefreshListView) pagerViewList.get(0).findViewById(R.id.pager_P04_item_list);
        latestPullRefreshListView.setOnScrollListener(headScrollAdapter);
        latestListView = latestPullRefreshListView.getRefreshableView();
        latestListView.setOnTouchListener(headScrollAdapter);

        ArrayList<MongoItem> newestBrandItemDataList = new ArrayList<MongoItem>();
        if (null != additionalItemEntity) {
            newestBrandItemDataList.add(additionalItemEntity);
        }
        newestBrandItemListAdapter = new P04BrandItemListAdapter(this, getScreenSize(), newestBrandItemDataList);

        latestListView.setAdapter(newestBrandItemListAdapter);
        latestPullRefreshListView.setScrollLoadEnabled(true);
        latestPullRefreshListView.setPullRefreshEnabled(false);
        latestPullRefreshListView.setPullLoadEnabled(true);

        if (null != brandEntity) {
            latestPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    //doNewestRefreshDataTask();
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
                    intent.putExtra(S03SHowActivity.INPUT_SHOW_ENTITY_ID, ((MongoShow)newestBrandItemListAdapter.getItem(position)).get_id());
                    startActivity(intent);
                }
            });
            doNewestRefreshDataTask();
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
        discountPullRefreshListView.setOnScrollListener(headScrollAdapter);
        discountListView = discountPullRefreshListView.getRefreshableView();
        discountListView.setOnTouchListener(headScrollAdapter);
        discountBrandItemListAdapter = new P04BrandItemListAdapter(this, getScreenSize(), new ArrayList<MongoItem>());

        discountListView.setAdapter(discountBrandItemListAdapter);
        discountPullRefreshListView.setScrollLoadEnabled(true);
        discountPullRefreshListView.setPullRefreshEnabled(false);
        discountPullRefreshListView.setPullLoadEnabled(true);

        if (null != brandEntity) {
            discountPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    //doDiscountRefreshDataTask();
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
                    intent.putExtra(S03SHowActivity.INPUT_SHOW_ENTITY_ID, ((MongoShow)discountBrandItemListAdapter.getItem(position)).get_id());
                    startActivity(intent);
                }
            });

            doDiscountRefreshDataTask();
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

    private void configShowsListPage() {
        showPullRefreshListView = (MPullRefreshMultiColumnListView) pagerViewList.get(2).findViewById(R.id.pager_P04_item_list);
        showPullRefreshListView.setOnScrollListener(headScrollAdapter);
        showListView = showPullRefreshListView.getRefreshableView();
        showListView.setOnTouchListener(headScrollAdapter);
        showBrandItemListAdapter = new P04BrandItemListAdapter(this, getScreenSize(), new ArrayList<MongoItem>());

        showListView.setAdapter(showBrandItemListAdapter);
        showPullRefreshListView.setScrollLoadEnabled(true);
        showPullRefreshListView.setPullRefreshEnabled(false);
        showPullRefreshListView.setPullLoadEnabled(true);

        if (null != brandEntity) {
            showPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<MultiColumnListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<MultiColumnListView> refreshView) {
                    //doShowRefreshTask();
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<MultiColumnListView> refreshView) {
                    doShowLoadMoreTask();
                }
            } );

            showListView.setOnItemClickListener(new PLA_AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {

                }
            });
            doShowRefreshTask();
        } else {
            showPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<MultiColumnListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<MultiColumnListView> refreshView) {
                    showPullRefreshListView.onPullDownRefreshComplete();
                    showPullRefreshListView.setHasMoreData(false);
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<MultiColumnListView> refreshView) {
                    showPullRefreshListView.onPullUpRefreshComplete();
                    showPullRefreshListView.setHasMoreData(false);
                }
            });
        }
    }

    private void configFansListPage() {
        fansPullRefreshListView = (MPullRefreshListView) pagerViewList.get(3).findViewById(R.id.pager_P02_item_list);
        fansListView = fansPullRefreshListView.getRefreshableView();
        ArrayList<MongoPeople> followerPeopleList = new ArrayList<MongoPeople>();
        fansListAdapter = new P02ModelFollowPeopleListAdapter(this, followerPeopleList);

        fansListView.setAdapter(fansListAdapter);
        fansPullRefreshListView.setScrollLoadEnabled(true);
        fansPullRefreshListView.setPullRefreshEnabled(false);
        fansPullRefreshListView.setPullLoadEnabled(true);

        if (null != brandEntity) {
            fansPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    //doFollowersRefreshDataTask();
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    doFollowersLoadMoreTask();
                }
            });

            fansListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    MongoPeople people = fansListAdapter.getData().get(i);
                    intent.setClass(P04BrandActivity.this, U01PersonalActivity.class);
                    bundle.putSerializable(U01PersonalActivity.U01PERSONALACTIVITY_PEOPLE, people);
                    for (int role : people.getRoles()) {
                        if ( role == RolesCode.MODEL.getIndex()){
                            intent.setClass(P04BrandActivity.this, P02ModelActivity.class);
                            bundle.putSerializable(P02ModelActivity.INPUT_MODEL, people);
                        }
                    }
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            });
            doFollowersRefreshDataTask();
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
        Log.d(TAG, "news:"+QSAppWebAPI.getBrandMatchApi(String.valueOf(brandEntity.get_id()), "1"));
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, QSAppWebAPI.getBrandMatchApi(String.valueOf(brandEntity.get_id()), "1"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    latestPullRefreshListView.onPullUpRefreshComplete();
                    latestPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex = 1;

                ArrayList<MongoItem> brandItemsEntities = ItemFeedingParser.parse(response);
                if (null != additionalItemEntity) {
                    brandItemsEntities.add(0, additionalItemEntity);
                }
                newNumTotal.setText(MetadataParser.getNumTotal(response));

                newestBrandItemListAdapter.resetData(brandItemsEntities);
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
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void doNewestLoadMoreTask() {
        if(null == brandEntity)return;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, QSAppWebAPI.getBrandMatchApi(String.valueOf(brandEntity.get_id()), String.valueOf(pageIndex + 1)), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    Toast.makeText(P04BrandActivity.this, "没有更多数据了！", Toast.LENGTH_SHORT).show();
                    latestPullRefreshListView.onPullUpRefreshComplete();
                    latestPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex++;

                ArrayList<MongoItem> brandItemsEntities = ItemFeedingParser.parse(response);

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
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void doDiscountRefreshDataTask() {
        if(null == brandEntity)return;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, QSAppWebAPI.getBrandDiscountApi(String.valueOf(brandEntity.get_id()), "1"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    discountPullRefreshListView.onPullUpRefreshComplete();
                    discountPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex = 1;

                ArrayList<MongoItem> modelShowEntities = ItemFeedingParser.parse(response);
                if (null != additionalItemEntity) {
                    modelShowEntities.add(0, additionalItemEntity);
                }
                discountNumTotal.setText(MetadataParser.getNumTotal(response));

                discountBrandItemListAdapter.resetData(modelShowEntities);
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
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void doDiscountLoadMoreTask() {
        if(null == brandEntity)return;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, QSAppWebAPI.getBrandDiscountApi(String.valueOf(brandEntity.get_id()), String.valueOf(pageIndex + 1)), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    Toast.makeText(P04BrandActivity.this, "没有更多数据了！", Toast.LENGTH_SHORT).show();
                    discountPullRefreshListView.onPullUpRefreshComplete();
                    discountPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex++;

                ArrayList<MongoItem> modelShowEntities = ItemFeedingParser.parse(response);

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
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void doShowRefreshTask(){ showsGetNetTask("1", true);}

    private void doShowLoadMoreTask(){ showsGetNetTask(String.valueOf(pageIndex), false);}

    private void showsGetNetTask(String _pageIndex, final boolean _isRefresh){
        if(null == brandEntity)return;
        final boolean _refreshTag = _isRefresh;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, QSAppWebAPI.getBrandShowApi(String.valueOf(brandEntity.get_id()), _pageIndex), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    if(pageIndex == 1) {

                    }else{
                        Toast.makeText(P04BrandActivity.this, "没有更多数据！", Toast.LENGTH_SHORT).show();
                    }
                    showPullRefreshListView.onPullUpRefreshComplete();
                    showPullRefreshListView.setHasMoreData(false);
                    return;
                }

                ArrayList<MongoItem> modelShowEntities = ItemFeedingParser.parse(response);
                if(_isRefresh){
                    showBrandItemListAdapter.resetData(modelShowEntities);
                    pageIndex = 1;
                }else{
                    showBrandItemListAdapter.addData(modelShowEntities);
                    ++pageIndex;
                }
                showBrandItemListAdapter.notifyDataSetChanged();
                showPullRefreshListView.onPullUpRefreshComplete();
                showPullRefreshListView.setHasMoreData(true);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showPullRefreshListView.onPullUpRefreshComplete();
                handleErrorMsg(error);
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void doFollowersRefreshDataTask() {
        if(null == brandEntity)return;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, QSAppWebAPI.getBrandFollowersApi(brandEntity.get_id(), 1), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                fansNumTotal.setText(MetadataParser.getNumTotal(response));
                if (MetadataParser.hasError(response)) {
                    fansPullRefreshListView.onPullUpRefreshComplete();
                    fansPullRefreshListView.setHasMoreData(false);
                    fansListAdapter.resetData(null);
                    fansListAdapter.notifyDataSetChanged();
                    return;
                }

                pageIndex = 1;

                ArrayList<MongoPeople> peopleEntities = PeopleParser.parseQueryFollowers(response);

                fansListAdapter.resetData(peopleEntities);
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
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void doFollowersLoadMoreTask() {
        if(null == brandEntity)return;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, QSAppWebAPI.getBrandFollowersApi(brandEntity.get_id(), pageIndex + 1), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    fansPullRefreshListView.onPullUpRefreshComplete();
                    fansPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex++;

                ArrayList<MongoPeople> modelShowEntities = PeopleParser.parseQueryFollowers(response);

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
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void followOrUnfollowTask() {
        if(null == brandEntity)return;
        if (brandEntity.getModelIsFollowedByCurrentUser()) {
            __unFollowModel();
        } else {
            __followModel();
        }
        sendBroadcast(new Intent(P03BrandListActivity.ACTION_REFRESH));
    }

    private void __followModel() {
        Map<String, String> followData = new HashMap<String, String>();
        followData.put("_id", brandEntity.get_id());
        JSONObject jsonObject = new JSONObject(followData);

        QSJsonObjectRequest mJsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getBrandFollowApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (!MetadataParser.hasError(response)) {
                        brandEntity.setModelIsFollowedByCurrentUser(true);
                        followSignText.setBackgroundResource(R.drawable.badge_unfollow_btn);
                        showMessage(P04BrandActivity.this, "关注成功");
                        doFollowersRefreshDataTask();
                        UserCommand.refresh();
                        sendBroadcast(new Intent(U01PersonalActivity.USER_UPDATE));
                        sendBroadcast(new Intent(U01BrandFragment.ACTION_MESSAGE));
                    }else{
                        showMessage(P04BrandActivity.this, "关注失败" + response);
                    }
                }catch (Exception e) {
                    showMessage(P04BrandActivity.this, "关注失败");
                }
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(mJsonObjectRequest);
    }

    private void __unFollowModel() {
        Map<String, String> followData = new HashMap<String, String>();
        followData.put("_id", brandEntity.get_id());
        JSONObject jsonObject = new JSONObject(followData);

        QSJsonObjectRequest mJsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getBrandUnfollowApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (!MetadataParser.hasError(response)) {
                        brandEntity.setModelIsFollowedByCurrentUser(false);
                        followSignText.setBackgroundResource(R.drawable.badge_follow_btn);
                        showMessage(P04BrandActivity.this, "取消关注成功");
                        doFollowersRefreshDataTask();
                        UserCommand.refresh();
                        sendBroadcast(new Intent(U01PersonalActivity.USER_UPDATE));
                        sendBroadcast(new Intent(U01BrandFragment.ACTION_MESSAGE));
                    }else{
                        showMessage(P04BrandActivity.this, "取消关注失败");
                    }
                }catch (Exception e) {
                    showMessage(P04BrandActivity.this, "取消关注失败");
                }
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(mJsonObjectRequest);
    }

    private void handleErrorMsg(VolleyError error) {
        Log.i("P04BrandActivity", error.toString());
    }

    private void showMessage(Context context, String message) {
        Log.i(context.getPackageName(), message);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private Point getScreenSize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("P04Brand"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("P04Brand"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}
