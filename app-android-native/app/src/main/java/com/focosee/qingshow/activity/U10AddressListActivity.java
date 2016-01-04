package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.fragment.S17ReceiptFragment;
import com.focosee.qingshow.adapter.U10AddressListAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by Administrator on 2015/3/16.
 */
public class U10AddressListActivity extends BaseActivity {

    private RecyclerView addresslist;
    private U10AddressListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private MongoPeople people;
    public String fromWhere = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_personal_addresslist);

        Intent intent = getIntent();

        fromWhere = intent.getStringExtra(S17ReceiptFragment.TO_U10);

        people = QSModel.INSTANCE.getUser();
        if (null == people) {
            people = new MongoPeople();
            people._id = QSModel.INSTANCE.getUserId();
            getPeopleFromNet();
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
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        addresslist.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        addresslist.setHasFixedSize(true);
        //创建并设置Adapter
        mAdapter = new U10AddressListAdapter(people, new LinkedList<MongoPeople.Receiver>(), U10AddressListActivity.this, R.layout.item_addreslist);
        if (null != people.receivers)
            mAdapter.addDataAtTop(people.receivers);
        addresslist.setAdapter(mAdapter);

    }

    public void refresh() {
        getPeopleFromNet();
    }

    public void getPeopleFromNet() {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getPeopleQueryApi(people._id), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(U10AddressListActivity.this, MetadataParser.getError(response));
                    return;
                }
                LinkedList<MongoPeople> users = UserParser._parsePeoples(response);
                people = users.get(0);
                mAdapter.addDataAtTop(people.receivers);
                mAdapter.notifyDataSetChanged();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        refresh();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void reconn() {
        refresh();
    }
}
