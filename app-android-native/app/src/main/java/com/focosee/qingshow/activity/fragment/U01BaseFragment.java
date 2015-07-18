package com.focosee.qingshow.activity.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alipay.sdk.widget.Loading;
import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.widget.LoadingLayout;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.focosee.qingshow.widget.RecyclerPullToRefreshView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class U01BaseFragment extends Fragment {

    @InjectView(R.id.fragment_u01_recyclerview)
    RecyclerPullToRefreshView recyclerPullToRefreshView;
    RecyclerView recyclerView;
    MongoPeople user;

    int currentPageN0 = 1;

    public U01BaseFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (MongoPeople)getArguments().getSerializable("user");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_u01, container, false);
        ButterKnife.inject(this, view);
        recyclerView = recyclerPullToRefreshView.getRefreshableView();

        recyclerPullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                refresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                loadMore();
            }
        });
        return view;
    }

    public RecyclerPullToRefreshView getRecyclerPullToRefreshView(){
        return this.recyclerPullToRefreshView;
    }

    public abstract void refresh();

    public abstract void loadMore();


}
