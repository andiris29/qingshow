package com.focosee.qingshow.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.QSMultipartEntity;
import com.focosee.qingshow.httpapi.request.QSMultipartRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.S20Bitmap;
import com.focosee.qingshow.util.BitMapUtil;

import org.json.JSONObject;

import java.util.List;

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

    @Override
    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s20_preview);
        ButterKnife.inject(this);
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
        uploadImage();
    }

    private void saveMatch() {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getMatchSaveApi(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }


    private void uploadImage() {
        QSMultipartRequest multipartRequest = new QSMultipartRequest(Request.Method.POST,
                QSAppWebAPI.getUpdateMatchCoverApi(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, null);
        QSMultipartEntity multipartEntity = multipartRequest.getMultiPartEntity();
        multipartEntity.addBinaryPart("uploadCover", BitMapUtil.bmpToByteArray(bitmap, false, Bitmap.CompressFormat.JPEG));
        RequestQueueManager.INSTANCE.getQueue().add(multipartRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!bitmap.isRecycled()){
            bitmap.recycle();
        }
    }
}
