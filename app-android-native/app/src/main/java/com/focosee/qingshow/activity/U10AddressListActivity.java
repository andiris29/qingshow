package com.focosee.qingshow.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import com.focosee.qingshow.Listener.EndlessRecyclerOnScrollListener;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.fragment.U11AddressEditFragment;
import com.focosee.qingshow.adapter.U10AddressListAdapter;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;

/**
 * Created by Administrator on 2015/3/16.
 */
public class U10AddressListActivity extends BaseActivity {

    private RecyclerView addresslist;
    private U10AddressListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private MongoPeople people;
    private int current_pageNo;
    public String fromWhere = "";
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(U11AddressEditFragment.ASK_REFRESH.equals(intent.getAction())){
                people = QSModel.INSTANCE.getUser();
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_personal_addresslist);

        Intent intent = getIntent();

//        fromWhere = intent.getStringExtra();

        people = QSModel.INSTANCE.getUser();
        if(null == people){
            finish();
        }

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
        mAdapter.resetData(people.receivers);
        addresslist.setAdapter(mAdapter);
        addresslist.addItemDecoration(mAdapter.getItemDecoration(20));
        addresslist.setOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
//                Toast.makeText(U10AddressListActivity.this, "loadMore", Toast.LENGTH_SHORT).show();
            }
        });

        registerReceiver(receiver, new IntentFilter(U11AddressEditFragment.ASK_REFRESH));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        people = QSModel.INSTANCE.getUser();
    }

    @Override
    public void reconn() {

    }
}
