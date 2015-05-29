package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S18DateAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoShow;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by DylanJiang on 15/5/28.
 */
public class S18ShowByDateActivity extends BaseActivity{

    @InjectView(R.id.s18_recycler)
    RecyclerView recyclerView;

    private String date = "";
    S18DateAdapter adapter;

    @Override
    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        date = getIntent().getStringExtra("dateforS18");
        setContentView(R.layout.activity_s18_showsfordata);
        ButterKnife.inject(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new S18DateAdapter(new LinkedList<>(),this,R.layout.item_u01_date,R.layout.item_s18);
        recyclerView.setAdapter(adapter);
        getDataFormNet();
    }

    private void getDataFormNet(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(QSAppWebAPI.getBydateApi(date), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S18ShowByDateActivity.this, MetadataParser.getError(response));
                    return;
                }
                LinkedList<MongoShow> data = ShowParser.parseQuery(response);

                adapter.addDataAtTop(data);
                adapter.notifyDataSetChanged();

            }
        },null);
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }
}
