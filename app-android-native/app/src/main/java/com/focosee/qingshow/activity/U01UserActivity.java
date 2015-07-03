package com.focosee.qingshow.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.android.volley.Response;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.U01MatchAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.ImgUtil;
import org.json.JSONObject;
import java.util.LinkedList;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class U01UserActivity extends MenuActivity{

    @InjectView(R.id.recycler)
    RecyclerView recyclerView;
    @InjectView(R.id.user_bg)
    SimpleDraweeView userBg;


    private final float DAMP = 3.0f;
    @InjectView(R.id.user_match_layout)
    RelativeLayout userMatchLayout;
    @InjectView(R.id.user_head)
    RelativeLayout userHead;

    private List<MongoShow> datas;
    private U01MatchAdapter adapter;

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
        if (user == null) {
            return;
        }
        if (null != user.background)
            userBg.setImageURI(Uri.parse(ImgUtil.getImgSrc(user.background, -1)));
    }

    float preOffset = 0;
    View view;
    private void initRectcler() {
        adapter = new U01MatchAdapter(datas, this,
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
                if(null == view && null != recyclerView.getChildAt(0))
                    view = recyclerView.getChildAt(0);
                float offset = recyclerView.computeVerticalScrollOffset();
                if (view == recyclerView.getChildAt(0))
                    userHead.setY(view.getBottom()-view.getHeight());
                else
                    userHead.setY(-userHead.getHeight());
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.user_match_layout:
                Toast.makeText(this, "match", Toast.LENGTH_SHORT).show();
                break;
            case R.id.user_recomm_layout:
                Toast.makeText(this, "recomm", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
