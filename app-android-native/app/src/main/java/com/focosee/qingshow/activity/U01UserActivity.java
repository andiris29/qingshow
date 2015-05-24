package com.focosee.qingshow.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.U01PushAdapter;
import com.focosee.qingshow.model.vo.mongo.Bean;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class U01UserActivity extends BaseActivity {
    @InjectView(R.id.recycler)
    RecyclerView recyclerView;

    private List<Bean> list;

    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_u01_base);
        ButterKnife.inject(this);
        initData();
        initView();
    }


    private void initView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
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
        recyclerView.setAdapter(new U01PushAdapter(list, this,
                R.layout.item_u01_push, R.layout.item_u01_loading));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                float offset = recyclerView.computeVerticalScrollOffset();
            }
        });


    }

    private void initData() {
        list = new ArrayList<Bean>();

        for (int i = 0; i < 20; i++) {

            if (i % 2 != 0) {
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
