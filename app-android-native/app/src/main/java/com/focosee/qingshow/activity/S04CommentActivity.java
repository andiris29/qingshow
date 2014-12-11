package com.focosee.qingshow.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S04CommentListAdapter;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.CommentEntity;
import com.focosee.qingshow.widget.MNavigationView;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class S04CommentActivity extends Activity {

    public static final String INPUT_SHOW_ID = "S04CommentActivity show id";

    private ImageView userImage;
    private EditText inputText;
    private Button sendButton;
    private MPullRefreshListView pullRefreshListView;
    private ListView listView;

    private S04CommentListAdapter adapter;

    private int currentPage = 0;
    private int numbersPerPage = 10;
    private String showId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s04_comment);

        showId = getIntent().getStringExtra(INPUT_SHOW_ID);

        ((MNavigationView)findViewById(R.id.S04_navigation_bar)).getBtn_left().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S04CommentActivity.this.finish();
            }
        });
        ((MNavigationView)findViewById(R.id.S04_navigation_bar)).getBtn_right().setVisibility(View.INVISIBLE);

        userImage = (ImageView)findViewById(R.id.S04_user_image);
        inputText = (EditText) findViewById(R.id.S04_input);
        sendButton = (Button) findViewById(R.id.S04_send_button);
        pullRefreshListView = (MPullRefreshListView) findViewById(R.id.S04_container_list);

        pullRefreshListView.setPullRefreshEnabled(true);
        pullRefreshListView.setScrollLoadEnabled(true);
        listView = pullRefreshListView.getRefreshableView();
        adapter = new S04CommentListAdapter(this, null, ImageLoader.getInstance());
        listView.setAdapter(adapter);

        pullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                doRefreshTask();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                doLoadMoreTask();
            }
        });

        pullRefreshListView.doPullRefreshing(true, 0);
    }

    class OnCommentClickListener implements ListView.OnClickListener {

        @Override
        public void onClick(View v) {

        }
    }

    private void doLoadMoreTask() {
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(QSAppWebAPI.getShowCommentsListApi(showId, currentPage+1, numbersPerPage), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                currentPage++;
                adapter.addDataInTail(S04CommentActivity.getCommentsFromJsonObject(response));
                adapter.notifyDataSetChanged();
                pullRefreshListView.onPullUpRefreshComplete();
                pullRefreshListView.setHasMoreData(true);
                setLastUpdateTime();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pullRefreshListView.onPullUpRefreshComplete();
                Toast.makeText(S04CommentActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                Log.i("test", error.toString());
            }
        });
        QSApplication.QSRequestQueue().add(jsonArrayRequest);
    }

    private void doRefreshTask() {
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(QSAppWebAPI.getShowCommentsListApi(showId, 0, numbersPerPage), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                currentPage = 0;
                adapter.resetData(S04CommentActivity.getCommentsFromJsonObject(response));
                adapter.notifyDataSetChanged();
                pullRefreshListView.onPullDownRefreshComplete();
                pullRefreshListView.setHasMoreData(true);
                setLastUpdateTime();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pullRefreshListView.onPullDownRefreshComplete();
                Toast.makeText(S04CommentActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                Log.i("test", error.toString());
            }
        });
        QSApplication.QSRequestQueue().add(jsonArrayRequest);
    }

    private void setLastUpdateTime() {
        String text = formatDateTime(System.currentTimeMillis());
        pullRefreshListView.setLastUpdatedLabel(text);
    }

    private String formatDateTime(long time) {
        if (0 == time) {
            return "";
        }

        return new SimpleDateFormat("MM-dd HH:mm").format(new Date(time));
    }

    private static ArrayList<CommentEntity> getCommentsFromJsonObject(JSONObject response) {
        String jsonString = "";
        try {
            jsonString = response.getJSONObject("data").getJSONArray("showComments").toString();
        } catch (JSONException e) {
            Log.i("json", e.toString());
        }
        return new Gson().fromJson(jsonString, new TypeToken<ArrayList<CommentEntity>>(){}.getType());
    }
}
