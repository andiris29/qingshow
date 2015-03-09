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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.HeadScrollAdapter;
import com.focosee.qingshow.adapter.P01ModelListAdapter;
import com.focosee.qingshow.constants.code.RolesCode;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.PeopleParser;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.widget.ILoadingLayout;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zenan on 12/27/14.
 */
public class U01WatchFragment extends Fragment{

    public static String ACTION_MESSAGE = "refresh_U01WatchFragment";
    public ListView followerListView;
    public MPullRefreshListView followerPullRefreshListView;
    private P01ModelListAdapter followerPeopleListAdapter;
    private boolean noMoreData = false;

    private int pageIndex = 1;
    private MongoPeople people;
    private HeadScrollAdapter headScrollAdapter;

    private static U01WatchFragment instance;
    private QSJsonObjectRequest jsonObjectRequest;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(ACTION_MESSAGE.equals(intent.getAction())){
                doFollowersRefreshDataTask();
            }
        }
    };

    public static U01WatchFragment newInstance() {

//            if (instance == null) {
                instance = new U01WatchFragment();
//            }
            return instance;

    }

    public U01WatchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        activity.registerReceiver(receiver, new IntentFilter(ACTION_MESSAGE));
        super.onAttach(activity);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_personal_pager_watch, container, false);
        headScrollAdapter = new HeadScrollAdapter(((U01PersonalActivity) getActivity()).headRelativeLayout, getActivity());
        followerPullRefreshListView = (MPullRefreshListView) view.findViewById(R.id.pager_P02_item_list);
//        followerPullRefreshListView.setOnScrollListener(headScrollAdapter);
        followerListView = followerPullRefreshListView.getRefreshableView();
//        followerListView.setOnTouchListener(headScrollAdapter);
        followerListView.setPadding(0, headScrollAdapter.headHeight, 0, 0);
        ArrayList<MongoPeople> followerPeopleList = new ArrayList<MongoPeople>();
        followerPeopleListAdapter = new P01ModelListAdapter(getActivity(), followerPeopleList, ImageLoader.getInstance(), U01PersonalActivity.peopleType);
        followerPeopleListAdapter.setU01PersonActivity((U01PersonalActivity)getActivity());
        followerListView.setAdapter(followerPeopleListAdapter);
        followerPullRefreshListView.setScrollLoadEnabled(true);
        followerPullRefreshListView.setPullRefreshEnabled(false);
        followerPullRefreshListView.setPullLoadEnabled(true);
        followerPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                doFollowersLoadMoreTask();
            }
        });

        followerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), P02ModelActivity.class);

                MongoPeople itemEntity = followerPeopleListAdapter.getItemData(position);

                Bundle bundle = new Bundle();
                bundle.putSerializable(P02ModelActivity.INPUT_MODEL, itemEntity);
                bundle.putSerializable(U01PersonalActivity.U01PERSONALACTIVITY_PEOPLE, itemEntity);

                int[] roles = followerPeopleListAdapter.getItemData(position).getRoles();

                for(int role : roles){
                    if(role == RolesCode.MODEL.getIndex()){
                        intent.setClass(getActivity(), P02ModelActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        return;
                    }
                }
                intent.setClass(getActivity(), U01PersonalActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
        doFollowersRefreshDataTask();
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

    private void doFollowersRefreshDataTask() {
        jsonObjectRequest = new QSJsonObjectRequest(Request.Method.GET,
                QSAppWebAPI.getPeopleQueryFollowedApi(people.get_id(),1, 10), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ((TextView) getActivity().findViewById(R.id.followedCountTextView)).setText(MetadataParser.getNumTotal(response));
                if (MetadataParser.hasError(response)) {
                    noMoreData = true;
                    followerPullRefreshListView.onPullDownRefreshComplete();
                    if(followerPullRefreshListView.getFooterLoadingLayout() != null)
                        followerPullRefreshListView.getFooterLoadingLayout().setState(ILoadingLayout.State.NONE);
                    return;
                }

                pageIndex = 1;

                ArrayList<MongoPeople> modelShowEntities = PeopleParser.parseQueryFollowed(response);
                followerPeopleListAdapter.resetData(modelShowEntities);
                followerPeopleListAdapter.notifyDataSetChanged();
                followerPullRefreshListView.onPullUpRefreshComplete();
                followerPullRefreshListView.onPullDownRefreshComplete();
                followerPullRefreshListView.setHasMoreData(true);
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void doFollowersLoadMoreTask() {
        jsonObjectRequest = new QSJsonObjectRequest(Request.Method.GET,
                QSAppWebAPI.getPeopleQueryFollowedApi(people.get_id(), pageIndex + 1, 10), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    if(noMoreData){
                        return;
                    }
                    Toast.makeText(getActivity(), R.string.no_more_data, Toast.LENGTH_SHORT).show();
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
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    @Override
    public void onPause() {
        if(null != jsonObjectRequest)
            RequestQueueManager.INSTANCE.getQueue().cancelAll(jsonObjectRequest);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(receiver);
        if(null != jsonObjectRequest)
            RequestQueueManager.INSTANCE.getQueue().cancelAll(jsonObjectRequest);
        super.onDestroyView();
    }
}
