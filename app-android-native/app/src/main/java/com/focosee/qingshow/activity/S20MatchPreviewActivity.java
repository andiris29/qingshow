package com.focosee.qingshow.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.QSMultipartEntity;
import com.focosee.qingshow.httpapi.request.QSMultipartRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.S03Model;
import com.focosee.qingshow.model.S20Bitmap;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.BitMapUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2015/7/9.
 */
public class S20MatchPreviewActivity extends BaseActivity {

    @InjectView(R.id.image)
    ImageView image;

    private Bitmap bitmap;
    private List<String> itemRefs;

    private MongoShow show;

    @Override
    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s20_preview);
        ButterKnife.inject(this);
        itemRefs = getIntent().getStringArrayListExtra(S20MatcherActivity.S20_ITEMREFS);
        initImage();
    }

    private void initImage() {
        bitmap = S20Bitmap.INSTANCE.getBitmap();
        if (bitmap != null) {
            image.setImageBitmap(bitmap);
        }
    }

    @OnClick(R.id.submitBtn)
    public void sublit() {
        saveMatch();
    }

    @OnClick(R.id.back)
    public void back(){
        this.finish();
    }

    private void saveMatch() {
        Map map = new HashMap();
        try {
            JSONArray jsonArray = new JSONArray(QSGsonFactory.create().toJson(itemRefs));
            map.put("itemRefs", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getMatchSaveApi(), new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                show = ShowParser.parse(response);
                uploadImage();
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }


    private void uploadImage() {
        QSMultipartRequest multipartRequest = new QSMultipartRequest(Request.Method.POST,
                QSAppWebAPI.getUpdateMatchCoverApi(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                show = ShowParser.parse(response);
                S03Model.INSTANCE.setShow(show);
                Intent intent = new Intent(S20MatchPreviewActivity.this,S03SHowActivity.class);
                startActivity(intent);
                S20MatchPreviewActivity.this.finish();
            }
        }, null);
        QSMultipartEntity multipartEntity = multipartRequest.getMultiPartEntity();
        multipartEntity.addBinaryPart("cover", BitMapUtil.bmpToByteArray(bitmap, false, Bitmap.CompressFormat.JPEG));
        multipartEntity.addStringPart("_id", show._id);
        RequestQueueManager.INSTANCE.getQueue().add(multipartRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }
}
