package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.ClassifyWaterfallAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.FeedingParser;
import com.focosee.qingshow.widget.ILoadingLayout;
import com.focosee.qingshow.widget.MPullRefreshMultiColumnListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AbsListView;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by zenan on 12/27/14.
 */
public class U01RecommendFragment extends Fragment implements View.OnTouchListener, PLA_AbsListView.OnScrollListener{
    public static String ACTION_MESSAGE = "U01RecommendFragment_actionMessage";
    private int currentPageIndex = 1;

    public MPullRefreshMultiColumnListView latestPullRefreshListView;
    public MultiColumnListView latestListView;
    private ClassifyWaterfallAdapter itemListAdapter;
    private MongoPeople people;
    private boolean noMoreData = false;

    private static U01RecommendFragment instance;
    /**
     * 当关注或取消关注时。接收广播，刷新列表
     */
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(ACTION_MESSAGE.equals(intent.getAction())){
                doShowsRefreshDataTask();
            }
        }
    };

    public static U01RecommendFragment newInstance() {

//            if (instance == null) {
                instance = new U01RecommendFragment();
//            }

            return instance;
    }

    public U01RecommendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(null != savedInstanceState){
            people = (MongoPeople) savedInstanceState.get("people");
        } else {
            if (getActivity() instanceof U01PersonalActivity) {
                people = ((U01PersonalActivity) getActivity()).getMongoPeople();
            }
        }
        if (getArguments() != null) {

        }
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_MESSAGE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_personal_pager_recommend, container, false);

        latestPullRefreshListView = (MPullRefreshMultiColumnListView) view.findViewById(R.id.pager_P02_item_list);
        latestPullRefreshListView.setOnScrollListener(this);
        latestListView = latestPullRefreshListView.getRefreshableView();
        latestListView.setOnTouchListener(this);
        latestListView.setPadding(0, U01PersonalActivity.headHeight, 0, 0);

        itemListAdapter = new ClassifyWaterfallAdapter(getActivity(), R.layout.item_showlist, ImageLoader.getInstance());
        latestListView.setAdapter(itemListAdapter);
        latestPullRefreshListView.setScrollLoadEnabled(true);
        latestPullRefreshListView.setPullRefreshEnabled(false);
        latestPullRefreshListView.setPullLoadEnabled(true);
        latestPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<MultiColumnListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<MultiColumnListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<MultiColumnListView> refreshView) {
                doShowsLoadMoreTask(String.valueOf(currentPageIndex), String.valueOf(10));
            }
        });

        latestListView.setOnItemClickListener(new PLA_AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), S03SHowActivity.class);
                S03SHowActivity.ACTION_MESSAGE = ACTION_MESSAGE;
                intent.putExtra(S03SHowActivity.INPUT_SHOW_ENTITY_ID, ((MongoShow)itemListAdapter.getItem(position))._id);
                startActivity(intent);
            }
        });

        doShowsRefreshDataTask();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("people", people);
    }


    private void doShowsRefreshDataTask() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                QSAppWebAPI.getFeedingRecommendationApi(people.get_id(), 1, 10), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ((TextView)getActivity().findViewById(R.id.recommendCountTextView)).setText(MetadataParser.getNumTotal(response));
                if (MetadataParser.hasError(response)) {
                    noMoreData = true;
                    latestPullRefreshListView.onPullUpRefreshComplete();
                    if(latestPullRefreshListView.getFooterLoadingLayout() != null)
                        latestPullRefreshListView.getFooterLoadingLayout().setState(ILoadingLayout.State.NONE);
                    return;
                }

                currentPageIndex = 1;

                LinkedList<MongoShow> modelShowEntities = FeedingParser.parse(response);

                itemListAdapter.addItemTop(modelShowEntities);
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

    private void doShowsLoadMoreTask(String pageNo, String pageSize) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                QSAppWebAPI.getFeedingRecommendationApi(people.get_id(), Integer.parseInt(pageNo)
                        , Integer.parseInt(pageSize)), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    if(noMoreData){
                        return;
                    }
                    Toast.makeText(getActivity(), "没有更多数据了！", Toast.LENGTH_SHORT).show();
                    latestPullRefreshListView.onPullUpRefreshComplete();
                    latestPullRefreshListView.setHasMoreData(false);
                    return;
                }

                currentPageIndex++;

                LinkedList<MongoShow> modelShowEntities = FeedingParser.parse(response);

                itemListAdapter.addItemLast(modelShowEntities);
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

    private void handleErrorMsg(VolleyError error) {
        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("P02ModelActivity", error.toString());
    }


    @Override
    public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        ((U01PersonalActivity) getActivity()).onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ((U01PersonalActivity) getActivity()).onTouch(v, event);
        return false;
    }
}
