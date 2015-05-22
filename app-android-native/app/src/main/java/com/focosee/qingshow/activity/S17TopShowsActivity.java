package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S17TopAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoShow;

import org.json.JSONObject;

import java.util.LinkedList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by DylanJiang on 15/4/30.
 */

public class S17TopShowsActivity extends BaseActivity{

    @InjectView(R.id.s17_recycler)
    RecyclerView recyclerView;

    @InjectView(R.id.title)
    TextView title;

    private LinkedList<MongoShow> data;
    private S17TopAdapter adapter;

    @Override
    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s17_topshows);
        ButterKnife.inject(this);

        title.setText(R.string.s17_title_name);
        data = new LinkedList<MongoShow>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new S17TopAdapter(data,this,R.layout.item_s17);
        recyclerView.setAdapter(adapter);
        getDataFormNet();
    }

    private void getDataFormNet(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(QSAppWebAPI.getTopApi(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S17TopShowsActivity.this, MetadataParser.getError(response));
                    return;
                }
                data = ShowParser.parseQuery(response);
                adapter.notifyDataSetChanged();

            }
        },null);
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }
}
