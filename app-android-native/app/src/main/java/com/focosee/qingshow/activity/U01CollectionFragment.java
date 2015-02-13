package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.ClassifyWaterfallAdapter;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.mongo.MongoPeople;
import com.focosee.qingshow.entity.mongo.MongoShow;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.FeedingParser;
import com.focosee.qingshow.request.QSJsonObjectRequest;
import com.focosee.qingshow.widget.MPullRefreshMultiColumnListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by zenan on 12/27/14.
 */
public class U01CollectionFragment extends Fragment {

    public static String ACTION_MESSAGE = "U01CollectionFragment_actionMessage";
    private int currentPageIndex = 1;

    private MPullRefreshMultiColumnListView latestPullRefreshListView;
    private MultiColumnListView latestListView;
    private ClassifyWaterfallAdapter itemListAdapter;
    private MongoPeople people;
    private static U01CollectionFragment instance;

    public static U01CollectionFragment newInstance() {

            //if (instance == null) {
                instance = new U01CollectionFragment();
            //}

            return instance;
    }

    public U01CollectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            people = (MongoPeople) savedInstanceState.get("people");
        } else {
            if(getActivity() instanceof U01PersonalActivity){
                people = ((U01PersonalActivity)getActivity()).getMongoPeople();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_personal_pager_collection, container, false);

        latestPullRefreshListView = (MPullRefreshMultiColumnListView) view.findViewById(R.id.pager_P02_item_list);
        latestListView = latestPullRefreshListView.getRefreshableView();

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
//        getFragmentManager().putFragment(outState, "mContent", mContent);
    }

    private void doShowsRefreshDataTask() {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.GET,
                QSAppWebAPI.getFeedingLikeApi(people.get_id(), 1, 10), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ((TextView)getActivity().findViewById(R.id.likeCountTextView)).setText(MetadataParser.getNumTotal(response));
                if (MetadataParser.hasError(response)) {
                    latestPullRefreshListView.onPullUpRefreshComplete();
                    latestPullRefreshListView.setHasMoreData(false);
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
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void doShowsLoadMoreTask(String pageNo, String pageSize) {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.GET,
                QSAppWebAPI.getFeedingLikeApi(people.get_id(), Integer.valueOf(pageNo), Integer.valueOf(pageSize)), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
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
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void handleErrorMsg(VolleyError error) {
        Log.i("P02ModelActivity", error.toString());
    }

}
