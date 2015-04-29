package com.focosee.qingshow.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.U01PushAdapter;
import com.focosee.qingshow.model.vo.mongo.Bean;
import com.focosee.qingshow.widget.RecyclerPullToRefreshView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/4/29.
 */
public class U01FavoriteFragment extends Fragment {

    private RecyclerPullToRefreshView pullToRefreshView;
    private RecyclerView recyclerView;

    private List<Bean> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pullToRefreshView = (RecyclerPullToRefreshView) inflater.inflate(R.layout.fragment_u01_base, container, false);
        initData();
        initView();
        return pullToRefreshView;
    }

    private void initView() {
        recyclerView = pullToRefreshView.getRefreshableView();
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (0 == position) {
                    return 2;
                }
                return 1;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new U01PushAdapter(list, getActivity(), R.layout.item_u01_push, R.layout.item_u01_nonehead));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                float offset = recyclerView.computeVerticalScrollOffset();
                EventBus.getDefault().post(new U01ScrollEvent(offset));
            }
        });


    }


    private void initData() {
        list = new ArrayList<Bean>();

        for (int i = 0; i < 20; i++) {

            if (i % 2 == 0) {
                Bean b = new Bean();
                b.url = "http://trial01.focosee.com/demo6/1107a50100.jpg";
                b.text = "123";
                list.add(b);
            } else {
                Bean b = new Bean();
                b.url = "http://trial01.focosee.com/demo6/1211b60100.jpg";
                b.text = "456";
                list.add(b);
            }
        }
    }

}
