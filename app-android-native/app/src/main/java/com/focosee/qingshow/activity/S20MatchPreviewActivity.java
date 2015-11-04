package com.focosee.qingshow.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.QSMultipartEntity;
import com.focosee.qingshow.httpapi.request.QSMultipartRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.httpapi.response.error.QSResponseErrorListener;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.S20Bitmap;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.BitMapUtil;
import com.focosee.qingshow.widget.LoadingDialogs;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
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
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.submitBtn)
    Button submitBtn;
    @InjectView(R.id.rootView)
    LinearLayout rootView;

    private Bitmap bitmap;
    private List<String> itemRefs;

    private MongoShow show;
    private String uuid;
    private LoadingDialogs dialog;

    @Override
    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s20_preview);
        ButterKnife.inject(this);
        dialog = new LoadingDialogs(this, R.style.dialog);
        dialog.setCanceledOnTouchOutside(false);
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
    public void back() {
        this.finish();
    }

    private void allowClick() {
        submitBtn.setClickable(true);
        back.setClickable(true);
        rootView.setClickable(true);
        rootView.setFocusable(true);
        dialog.dismiss();
    }

    private void forbidClick() {
        submitBtn.setClickable(false);
        back.setClickable(false);
        rootView.setClickable(false);
        rootView.setFocusable(false);
        dialog.show();
    }

    private void saveMatch() {
        forbidClick();
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
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S20MatchPreviewActivity.this, MetadataParser.getError(response));
                    allowClick();
                    return;
                }
                if (!QSModel.INSTANCE.isFinished(MongoPeople.MATCH_FINISHED)) {
                    QSModel.INSTANCE.setUserStatus(MongoPeople.MATCH_FINISHED);
                }
                try {
                    uuid = response.getJSONObject("data").getString("uuid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(null != bitmap)
                    if(!bitmap.isRecycled())
                        uploadImage();
            }
        }, new QSResponseErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                allowClick();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }


    private void uploadImage() {

        QSMultipartRequest multipartRequest = new QSMultipartRequest(Request.Method.POST,
                QSAppWebAPI.getUpdateMatchCoverApi(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(S20MatchPreviewActivity.class.getSimpleName(), "uploadImage_response:" + response);
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S20MatchPreviewActivity.this, MetadataParser.getError(response));
                    allowClick();
                    return;
                }
                show = ShowParser.parsePeopleAndItemString(response);
                allowClick();
                Class _class = S03SHowActivity.class;
                Intent intent = new Intent();
                if(QSModel.INSTANCE.isGuest()){
                    _class = S01MatchShowsActivity.class;
                }
                intent.setClass(S20MatchPreviewActivity.this, _class);
                intent.putExtra(S01MatchShowsActivity.INTENT_CURRENT_TYPE, 1);
                intent.putExtra(S03SHowActivity.INPUT_SHOW_ENTITY_ID, show._id);
                intent.putExtra(S03SHowActivity.CLASS_NAME, S20MatchPreviewActivity.class.getSimpleName());
                S20MatchPreviewActivity.this.startActivity(intent);
                S20MatchPreviewActivity.this.finish();
            }
        }, new QSResponseErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                allowClick();
            }
        });
        QSMultipartEntity multipartEntity = multipartRequest.getMultiPartEntity();
        multipartEntity.addBinaryPart("cover", BitMapUtil.bmpToByteArray(bitmap, false, Bitmap.CompressFormat.JPEG));
        multipartEntity.addStringPart("uuid", uuid);
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(150000, 0, 1f));
        RequestQueueManager.INSTANCE.getQueue().add(multipartRequest);
    }

    @Override
    protected void onDestroy() {
        if(null != bitmap) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == bitmap){
            finish();
        }
        MobclickAgent.onPageStart("S20MatcherPreviewActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("S20MatcherPreviewActivity");
        MobclickAgent.onPause(this);
    }
}
