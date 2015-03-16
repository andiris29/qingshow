package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.U09TradeListAdapter;
import com.focosee.qingshow.adapter.U10AddressListAdapter;

/**
 * Created by Administrator on 2015/3/13.
 */
public class U09TradeListActivity extends BaseActivity{

    private RecyclerView tradelist;
    private U09TradeListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_person_tradelist);

        findViewById(R.id.person_activity_back_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tradelist = (RecyclerView) findViewById(R.id.person_activity_tradelist_recycleview);

        mAdapter = new U09TradeListAdapter(this);

//创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        tradelist.setLayoutManager(mLayoutManager);
//如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        tradelist.setHasFixedSize(true);
//创建并设置Adapter
        mAdapter.setOnViewHolderListener(new U09TradeListAdapter.OnViewHolderListener() {
            @Override
            public void onRequestedLastItem() {
                Toast.makeText(U09TradeListActivity.this, "loadMore", Toast.LENGTH_SHORT).show();
            }
        });
        tradelist.setAdapter(mAdapter);
        tradelist.addItemDecoration(mAdapter.getItemDecoration(20));

    }

    @Override
    public void reconn() {

    }
}
