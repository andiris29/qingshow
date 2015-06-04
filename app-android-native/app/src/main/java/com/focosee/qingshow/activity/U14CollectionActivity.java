package com.focosee.qingshow.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.U14CollectionAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import org.json.JSONObject;
import java.util.LinkedList;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class U14CollectionActivity extends MenuActivity {

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
                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(U14CollectionActivity.this, MetadataParser.getError(response));
                    return;
                }
                LinkedList<MongoShow> shows = ShowParser.parseQuery(response);
                adapter.refreshDatas(shows);
                Toast.makeText(U14CollectionActivity.this, R.string.load_finish, Toast.LENGTH_SHORT).show();

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
