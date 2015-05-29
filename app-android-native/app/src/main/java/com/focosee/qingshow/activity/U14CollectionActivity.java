package com.focosee.qingshow.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.U14CollectionAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.model.QSModel;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class U14CollectionActivity extends Activity {

    @InjectView(R.id.u14_recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.left_btn)
    ImageView backBtn;
    @InjectView(R.id.right_btn)
    ImageView rightBtn;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.title_layout)
    LinearLayout layout;
    U14CollectionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u14_collection);
        ButterKnife.inject(this);
        backBtn.setImageResource(R.drawable.nav_btn_menu_n);
        rightBtn.setVisibility(View.INVISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText(R.string.title_collection);
        layout.setBackgroundColor(Color.WHITE);
        adapter = new U14CollectionAdapter(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(adapter.getItemDecoration(10));

        getDatasFromNet();


    }

    private void getDatasFromNet(){
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getFeedingLikeApi(QSModel.INSTANCE.getUser()._id), null, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                System.out.println("repsonse:" + response);
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_u14_collection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
