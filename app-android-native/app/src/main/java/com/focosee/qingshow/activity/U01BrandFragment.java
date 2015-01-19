package com.focosee.qingshow.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.P03BrandListAdapter;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.BrandEntity;
import com.focosee.qingshow.request.MJsonObjectRequest;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;
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

    public static U01BrandFragment newInstance(){
        return new U01BrandFragment();
    }

    public U01BrandFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.activity_personal_pager_brand, container, false);

        mPullRefreshListView = (MPullRefreshListView) view.findViewById(R.id.pager_P04_item_list);
        mAdapter = new P03BrandListAdapter(getActivity(), new ArrayList<BrandEntity>(), ImageLoader.getInstance());

        brandListView = mPullRefreshListView.getRefreshableView();
        brandListView.setAdapter(mAdapter);

        mPullRefreshListView.setPullRefreshEnabled(true);
        mPullRefreshListView.setScrollLoadEnabled(true);
        mPullRefreshListView.setScrollLoadEnabled(true);

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                doRefreshTask();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                doGetMoreTask();
            }
        });

        return view;
    }


    private void doRefreshTask() {
        _getDataFromNet(true);
    }

    private void doGetMoreTask() {
        _getDataFromNet(false);
    }

    private void _getDataFromNet(boolean refreshSign) {
        final boolean _tRefreshSign = refreshSign;
        MJsonObjectRequest jor = new MJsonObjectRequest(QSAppWebAPI.getBrandFollowedApi(QSApplication.get().QSUserId(getActivity())), null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try{
                    ArrayList<BrandEntity> results = BrandEntity.getBrandListFromResponse(response);
                    if (_tRefreshSign) {
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
                    Log.i("test", "error" + error.toString());
                    //Toast.makeText(getApplication(), "Error:" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "最后一页", Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onPullDownRefreshComplete();
                    mPullRefreshListView.onPullUpRefreshComplete();
                    mPullRefreshListView.setHasMoreData(true);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(S08TrendActivity.this, "Error:"+error.toString(), Toast.LENGTH_LONG).show();
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
        //Toast.makeText(this,jor.get,Toast.LENGTH_LONG).show();
        QSApplication.get().QSRequestQueue().add(jor);
    }




}
