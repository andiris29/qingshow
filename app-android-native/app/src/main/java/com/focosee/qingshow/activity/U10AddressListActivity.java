package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.U10AddressListAdapter;

import java.util.List;

import static com.focosee.qingshow.adapter.U10AddressListAdapter.*;

/**
 * Created by Administrator on 2015/3/16.
 */
public class U10AddressListActivity extends BaseActivity {

    private RecyclerView addresslist;
    private U10AddressListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_personal_addresslist);

        findViewById(R.id.person_addresslist_back_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addresslist = (RecyclerView) findViewById(R.id.person_addresslist_recycleview);
        findViewById(R.id.person_addresslist_btn_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(U10AddressListActivity.this, U11EditAddressActivity.class));
            }
        });

//创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        addresslist.setLayoutManager(mLayoutManager);
//如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        addresslist.setHasFixedSize(true);
//创建并设置Adapter
        mAdapter = new U10AddressListAdapter(this);
        mAdapter.setOnViewHolderListener(new OnViewHolderListener() {
            @Override
            public void onRequestedLastItem() {
                mAdapter.setAddcount(mAdapter.getItemCount() + 10);
                Toast.makeText(U10AddressListActivity.this, "loadMore", Toast.LENGTH_SHORT).show();
            }
        });
        addresslist.setAdapter(mAdapter);
        addresslist.addItemDecoration(mAdapter.getItemDecoration(20));
    }

    @Override
    public void reconn() {

    }
}
