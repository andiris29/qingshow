package com.focosee.qingshow.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import com.focosee.qingshow.util.RectUtil;
import com.focosee.qingshow.util.ToastUtil;
import com.focosee.qingshow.widget.LoadingDialogs;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2015/7/9.
 */
public class S22MatchPreviewActivity extends BaseActivity {

    private final long TIME_SUBMIT = 5000;
    private final int TIME_OUT = 0x1;
    @InjectView(R.id.image)
    ImageView image;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.submitBtn)
    Button submitBtn;
    @InjectView(R.id.rootView)
    LinearLayout rootView;

    private Bitmap bitmap;
    private List<String> innerItemRefs;
    private ArrayList<RectF> innerItemRects;

    private MongoShow show;
    private String uuid;
    private LoadingDialogs dialog;
    private Timer timer;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == TIME_OUT) {
                sublit();
                return true;
            }
            return false;
        }
    });

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
        innerItemRefs = getIntent().getStringArrayListExtra(S20MatcherActivity.S20_ITEMREFS);
        innerItemRects = getIntent().getParcelableArrayListExtra(S20MatcherActivity.S20_ITEMRECTS);
        initImage();
        timerSubmit();
    }

    private void timerSubmit() {
        timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (submitBtn.isEnabled())
                    handler.sendEmptyMessage(TIME_OUT);
            }
        };
        timer.schedule(timerTask, TIME_SUBMIT);
    }

    private void initImage() {
        bitmap = S20Bitmap.INSTANCE.getBitmap();
        if (bitmap != null) {
            image.setImageBitmap(bitmap);
        }
    }

    @OnClick(R.id.submitBtn)
    public void sublit() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }

        if (QSModel.INSTANCE.loggedin()) {
            saveMatch();
        }
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
            ArrayList<float[]> list = new ArrayList<>();
            for (RectF rect : innerItemRects) {
                list.add(RectUtil.rectSerializer(rect));
            }
            JSONArray itemRects = new JSONArray(QSGsonFactory.create().toJson(list));
            JSONArray itemRefs = new JSONArray(QSGsonFactory.create().toJson(innerItemRefs));

            map.put("itemRects", itemRects);
            map.put("itemRefs", itemRefs);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getMatchSaveApi(), new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S22MatchPreviewActivity.this, MetadataParser.getError(response));
                    allowClick();
                    return;
                }
                if (!QSModel.INSTANCE.isFinished(MongoPeople.MATCH_FINISHED)) {
                    QSModel.INSTANCE.setUserStatus(MongoPeople.MATCH_FINISHED);
                }

                if (null != bitmap)
                    if (!bitmap.isRecycled())
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
                Log.d(S22MatchPreviewActivity.class.getSimpleName(), "uploadImage_response:" + response);
                if (MetadataParser.hasError(response)) {
                    try {
                        if (response.getJSONObject("metadata").has("limitMessage")) {
                            ToastUtil.showShortToast(S22MatchPreviewActivity.this, response.getJSONObject("metadata").get("limitMessage").toString());
                        } else {
                            ErrorHandler.handle(S22MatchPreviewActivity.this, MetadataParser.getError(response));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    allowClick();
                    return;
                }
                show = ShowParser.parse(response);
                allowClick();
                Intent intent = new Intent(S22MatchPreviewActivity.this, S24ShowsDateActivity.class);
                GregorianCalendar calendar = new GregorianCalendar();
                intent.putExtra(S24ShowsDateActivity.MATCH_NEW_TO,
                        new GregorianCalendar(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.HOUR_OF_DAY) + 1, 0));
                intent.putExtra(S24ShowsDateActivity.MATCH_NEW_FROM, calendar);
                S22MatchPreviewActivity.this.startActivity(intent);
                S22MatchPreviewActivity.this.finish();
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
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(150000, 0, 1f));
        RequestQueueManager.INSTANCE.getQueue().add(multipartRequest);
    }

    @Override
    protected void onDestroy() {
        if (null != bitmap) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == bitmap) {
            finish();
        }
        MobclickAgent.onPageStart("S22MatcherPreviewActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("S22MatcherPreviewActivity");
        MobclickAgent.onPause(this);
    }
}
