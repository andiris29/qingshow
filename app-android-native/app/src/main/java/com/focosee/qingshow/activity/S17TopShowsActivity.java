package com.focosee.qingshow.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.focosee.qingshow.util.BitMapUtil;
import com.focosee.qingshow.util.QSComponent;
import com.focosee.qingshow.util.ShowUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by DylanJiang on 15/4/30.
 */

public class S17TopShowsActivity extends MenuActivity implements OnClickListener {

    @InjectView(R.id.s17_recycler)
    RecyclerView recyclerView;

    @InjectView(R.id.title)
    TextView title;

    private boolean isFirstFocus = true;

    private LinkedList<MongoShow> data;
    private S17TopAdapter adapter;


    private Timer timer = new Timer(true);
    private TimerTask timerTask;
    private long time = 2000;
    private int count = 0;

    @Override
    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s17_topshows);
        ButterKnife.inject(this);

        title.setText(R.string.s17_title_name);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new S17TopAdapter(new ArrayList<>(), this, R.layout.item_s17);
        recyclerView.setAdapter(adapter);
        getDataFormNet();
        initShowVerison(title);
    }


    private void initShowVerison(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++count;
                if (null != timerTask) {
                    timerTask.cancel();
                }
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        count = 0;
                    }
                };
                timer.schedule(timerTask, time / 5);
                String version = "";
                try {
                    version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (count == 5)
                    QSComponent.showDialag(S17TopShowsActivity.this, "当前版本是：" + version);
            }
        });
    }


    private void getDataFormNet() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(QSAppWebAPI.getTopApi(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S17TopShowsActivity.this, MetadataParser.getError(response));
                    return;
                }
                data = ShowParser.parseQuery(response);
                adapter.addDataAtTop(ShowUtil.formentShow(data));
                adapter.notifyDataSetChanged();

            }
        }, null);
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }
}
