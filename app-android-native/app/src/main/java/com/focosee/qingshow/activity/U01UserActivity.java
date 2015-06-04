package com.focosee.qingshow.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.U01PushAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.BitMapUtil;
import com.focosee.qingshow.util.adapter.DividerGridItemDecoration;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class U01UserActivity extends MenuActivity implements View.OnClickListener{

    @InjectView(R.id.recycler)
    RecyclerView recyclerView;
    @InjectView(R.id.user_bg)
    SimpleDraweeView userBg;


    private final float DAMP = 3.0f;

    private List<MongoShow> datas;
    private U01PushAdapter adapter;

    private boolean isFirstFocus = true;

    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u01_base);
        ButterKnife.inject(this);
        datas = new LinkedList<>();
        initUserInfo();
        initRectcler();
        loadDataFormNet();

    }

    private void loadDataFormNet() {
        QSJsonObjectRequest objectRequest = new QSJsonObjectRequest(QSAppWebAPI.getUserRecommendationApi()
                , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(U01UserActivity.this, MetadataParser.getError(response));
                    return;
                }
                datas = ShowParser.parseQuery(response);
                adapter.addDataAtTop(datas);
                adapter.notifyDataSetChanged();

            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(objectRequest);
    }

    private void initUserInfo() {
        MongoPeople user = QSModel.INSTANCE.getUser();
        if (user == null){
            return;
        }
        if (null != user.background)
            userBg.setImageURI(Uri.parse(user.background));

    }

    private void initRectcler() {
        adapter = new U01PushAdapter(datas, this,
                R.layout.item_u01_push, R.layout.item_u01_header, R.layout.item_u01_date);
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
        recyclerView.setAdapter(adapter);
//        recyclerView.addItemDecoration(new DividerGridItemDecoration(this,R.drawable.item_decoration));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                float offset = recyclerView.computeVerticalScrollOffset();
                userBg.setY(-offset / DAMP);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
