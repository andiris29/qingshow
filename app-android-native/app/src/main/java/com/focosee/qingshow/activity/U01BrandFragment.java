package com.focosee.qingshow.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.P03BrandListAdapter;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.mongo.MongoBrand;
import com.focosee.qingshow.entity.mongo.MongoPeople;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.BrandParser;
import com.focosee.qingshow.request.QSJsonObjectRequest;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rong5690001 on 2015/1/19.
 */
public class U01BrandFragment extends Fragment{

    private MPullRefreshListView mPullRefreshListView;
    private P03BrandListAdapter mAdapter;
    private ListView brandListView;

    private int _currentPageIndex = 1;
    private MongoPeople people;

    private static U01BrandFragment instance;

    public static U01BrandFragment newInstance(){

            if (instance == null) {
                instance = new U01BrandFragment();
            }

            return instance;
    }

    public U01BrandFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        people = ((U01PersonalActivity) getActivity()).getMongoPeople();
        if(people == null){
            people = new MongoPeople();
        }
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.activity_personal_pager_brand, container, false);

        mPullRefreshListView = (MPullRefreshListView) view.findViewById(R.id.pager_P04_item_list);
        mAdapter = new P03BrandListAdapter(getActivity(), new ArrayList<MongoBrand>(), ImageLoader.getInstance());

        brandListView = mPullRefreshListView.getRefreshableView();
        brandListView.setAdapter(mAdapter);

        mPullRefreshListView.setPullRefreshEnabled(false);
        mPullRefreshListView.setScrollLoadEnabled(true);

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                doGetMoreTask();
            }
        });

        brandListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent  = new Intent(getActivity(), P04BrandActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable(P04BrandActivity.INPUT_BRAND, mAdapter.getData().get(i));
                intent.putExtras(bundle);

                startActivity(intent);


            }
        });
        doRefreshTask();

        return view;
    }


    private void doRefreshTask() {
        _getDataFromNet(true, 1, 10);
    }

    private void doGetMoreTask() {
        _getDataFromNet(false, _currentPageIndex+1, 10);
    }

    private void _getDataFromNet(boolean refreshSign, int pageNo, int pageSize) {
        final boolean _tRefreshSign = refreshSign;
        QSJsonObjectRequest jor = new QSJsonObjectRequest(QSAppWebAPI.getBrandFollowedApi(people.get_id(), pageNo, pageSize), null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try{
                    ArrayList<MongoBrand> results = BrandParser.parseQueryBrands(response);
                    if (_tRefreshSign) {
                        ((TextView)getActivity().findViewById(R.id.brandCountTextView)).setText(MetadataParser.getNumTotal(response));
                        mAdapter.resetData(results);
                        _currentPageIndex = 1;
                    } else {
                        mAdapter.addData(results);
                        _currentPageIndex++;
                    }
                    mAdapter.notifyDataSetChanged();
                    mPullRefreshListView.onPullDownRefreshComplete();
                    mPullRefreshListView.onPullUpRefreshComplete();
                    mPullRefreshListView.setHasMoreData(true);

                }catch (Exception error){
                    Toast.makeText(getActivity(), "最后一页", Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onPullDownRefreshComplete();
                    mPullRefreshListView.onPullUpRefreshComplete();
                    mPullRefreshListView.setHasMoreData(true);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mPullRefreshListView.onPullDownRefreshComplete();
                mPullRefreshListView.onPullUpRefreshComplete();
                mPullRefreshListView.setHasMoreData(true);
                if(!AppUtil.checkNetWork(getActivity())) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("连接失败")
                            .setMessage("未连接网络或者信号不好。")
                            .setPositiveButton("重新连接", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    doRefreshTask();
                                }
                            }).show();
                }
            }
        });
        QSApplication.get().QSRequestQueue().add(jor);
    }



}
