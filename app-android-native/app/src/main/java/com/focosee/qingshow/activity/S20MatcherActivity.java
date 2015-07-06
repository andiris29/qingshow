package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.FrameLayout;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S20SelectAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ItemFeedingParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.widget.QSCanvasView;
import com.focosee.qingshow.widget.QSImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/7/1.
 */
public class S20MatcherActivity extends BaseActivity {

    @InjectView(R.id.canvas)
    QSCanvasView canvas;
    @InjectView(R.id.selectRV)
    RecyclerView selectRV;
    @InjectView(R.id.selectBtn)
    Button selectBtn;
    @InjectView(R.id.submitBtn)
    Button submitBtn;

    private S20SelectAdapter adapter;
    private List<MongoItem> datas;

    @Override
    public void reconn() {

    }

    Handler addHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.i("tag","handler");
            addItemsToCanvas("http://trial01.focosee.com/cs/0080703027/a.jpg");
            return true;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s20_matcher);
        ButterKnife.inject(this);
        initSelectRV();
        getDataFormNet(1,10,"5593b88838dadbed5a998b8c");
        addItemsToCanvas("http://trial01.focosee.com/cs/0080703027/a.jpg");
    }

    private void addItemsToCanvas(String url) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        QSImageView itemView = new QSImageView(this);
        ImageLoader.getInstance().displayImage(url,itemView.getImageView());
//        itemView.getImageView().setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
        itemView.setLayoutParams(layoutParams);
        canvas.attach(itemView);
    }

    private void initSelectRV(){
        datas = new LinkedList<>();
        adapter = new S20SelectAdapter(datas,this,R.layout.item_s20_select);
        selectRV.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        selectRV.setAdapter(adapter);
    }

    private void getDataFormNet(int pageNo, int pageSize, String categoryRef) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(QSAppWebAPI.getQueryItems(pageNo,pageSize,categoryRef), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S20MatcherActivity.this, MetadataParser.getError(response));
                    return;
                }
                datas = ItemFeedingParser.parse(response);

//                adapter.notifyDataSetChanged();
            }
        }, null);
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }


}
