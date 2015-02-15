package com.focosee.qingshow.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S04CommentListAdapter;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.code.PeopleTypeInU01PersonalActivity;
import com.focosee.qingshow.code.RolesCode;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.mongo.MongoComment;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.request.QSJsonObjectRequest;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.widget.ActionSheet;
import com.focosee.qingshow.widget.MCircularImageView;
import com.focosee.qingshow.widget.MNavigationView;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class    S04CommentActivity extends BaseActivity implements ActionSheet.ActionSheetListener {

    public static final String INPUT_SHOW_ID = "S04CommentActivity show id";
    public static boolean isOpened = false;

    private MCircularImageView userImage;
    private EditText inputText;
    private Button sendButton;
    private MPullRefreshListView pullRefreshListView;
    private ListView listView;

    private S04CommentListAdapter adapter;

    private int currentPage = 1;
    private int numbersPerPage = 10;
    private String showId;
    private String replyUserId = null;

    private Intent viewMainPageIntent= null;
    private int clickCommentIndex= -1;

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

        userImage = (MCircularImageView)findViewById(R.id.S04_user_image);
        inputText = (EditText) findViewById(R.id.S04_input);
        sendButton = (Button) findViewById(R.id.S04_send_button);
        pullRefreshListView = (MPullRefreshListView) findViewById(R.id.S04_container_list);

        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if(i3 > 0) {
                    sendButton.setBackgroundColor(Color.RED);
                    sendButton.setTextColor(Color.WHITE);
                }else if(i3 == 0){
                    sendButton.setBackgroundResource(R.drawable.s04_send_background);
                    sendButton.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if(AppUtil.getAppUserLoginStatus(this) && null != QSApplication.get().getPeople()){
            ImageLoader.getInstance().displayImage(QSApplication.get().getPeople().portrait, userImage);
        }
        pullRefreshListView.setPullRefreshEnabled(true);
        pullRefreshListView.setScrollLoadEnabled(true);
        listView = pullRefreshListView.getRefreshableView();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(S04CommentActivity.this, "test", Toast.LENGTH_SHORT).show();
                postComment();
            }
        });

        adapter = new S04CommentListAdapter(this, null, ImageLoader.getInstance());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setTheme(R.style.ActionSheetStyleIOS7);
                showActionSheet(position);
            }
        });

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isOpened = false;
    }

    private void doLoadMoreTask() {
        QSJsonObjectRequest jsonArrayRequest = new QSJsonObjectRequest(QSAppWebAPI.getShowCommentsListApi(showId, currentPage+1, numbersPerPage), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(MetadataParser.hasError(response)){
                    pullRefreshListView.onPullUpRefreshComplete();
                    pullRefreshListView.setHasMoreData(false);
                    Toast.makeText(S04CommentActivity.this, R.string.no_more_data, Toast.LENGTH_SHORT).show();
                    return;
                }
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
                Toast.makeText(S04CommentActivity.this, R.string.check_wifi, Toast.LENGTH_SHORT).show();
                Log.i("test", error.toString());
            }
        });
        QSApplication.get().QSRequestQueue().add(jsonArrayRequest);
    }

    private void doRefreshTask() {
        QSJsonObjectRequest jsonArrayRequest = new QSJsonObjectRequest(QSAppWebAPI.getShowCommentsListApi(showId, 0, numbersPerPage), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                currentPage = 1;
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
                Log.i("test", error.toString());
            }
        });
        QSApplication.get().QSRequestQueue().add(jsonArrayRequest);
    }

    private void postComment() {

        if (! AppUtil.getAppUserLoginStatus(S04CommentActivity.this)) {
            Toast.makeText(S04CommentActivity.this, R.string.need_login, Toast.LENGTH_SHORT).show();
            return;
        }

        String comment = inputText.getText().toString().trim();
        if (comment.length() <= 0 ) {
            Toast.makeText(this, "评论不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("_id", showId);
        map.put("_atId", replyUserId);
        map.put("comment", comment);
        JSONObject jsonObject = new JSONObject(map);
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getCommentPostApi(),jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                doRefreshTask();
                inputText.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputText.getWindowToken(), 0); //强制隐藏键盘
                //Toast.makeText(S04CommentActivity.this, "get" + response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(S04CommentActivity.this, "发布失败，" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void deleteComment() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("_id", adapter.getCommentAtIndex(clickCommentIndex).getId());
        JSONObject jsonObject = new JSONObject(map);
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getCommentDeleteApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                doRefreshTask();
                Toast.makeText(S04CommentActivity.this, "get" + response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(S04CommentActivity.this, "删除失败，" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
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

    private static ArrayList<MongoComment> getCommentsFromJsonObject(JSONObject response) {
        String jsonString = "";
        try {
            jsonString = response.getJSONObject("data").getJSONArray("showComments").toString();
        } catch (JSONException e) {
            Log.i("json", e.toString());
        }
        return new Gson().fromJson(jsonString, new TypeToken<ArrayList<MongoComment>>(){}.getType());
    }

    public void showActionSheet(int commentIndex) {
        String userId = AppUtil.getAppUserId(S04CommentActivity.this);
        String commentUserId = adapter.getCommentAtIndex(commentIndex).getUserId();

        clickCommentIndex = commentIndex;
        viewMainPageIntent = new Intent();
        viewMainPageIntent.putExtra(P02ModelActivity.INPUT_MODEL, adapter.getCommentAtIndex(commentIndex).getAuthorRef());
        viewMainPageIntent.putExtra(U01PersonalActivity.U01PERSONALACTIVITY_PEOPLE, adapter.getCommentAtIndex(commentIndex).getAuthorRef());

        if (null != userId && userId.equals(commentUserId)) {
            ActionSheet.createBuilder(this, getFragmentManager())
                    .setCancelButtonTitle("取消")
                    .setOtherButtonTitles("查看个人主页", "删除")
                    .setCancelableOnTouchOutside(true).setListener(this).show();
        }
        else {
            ActionSheet.createBuilder(this, getFragmentManager())
                    .setCancelButtonTitle("取消")
                    .setOtherButtonTitles("查看个人主页")
                    .setCancelableOnTouchOutside(true).setListener(this).show();
        }
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch (index) {
            case 0:
                if(null != adapter.getCommentAtIndex(clickCommentIndex).getAuthorRef()){
                    int[] roles = adapter.getCommentAtIndex(clickCommentIndex).getAuthorRef().getRoles();

                    for(int role : roles){
                        if(role == RolesCode.MODEL.getIndex()){
                            viewMainPageIntent.setClass(S04CommentActivity.this, P02ModelActivity.class);
                            startActivity(viewMainPageIntent);
                            return;
                        }
                    }
                }
                viewMainPageIntent.setClass(S04CommentActivity.this, U01PersonalActivity.class);
                U01PersonalActivity.peopleType = PeopleTypeInU01PersonalActivity.OTHERS.getIndx();
                startActivity(viewMainPageIntent);
                break;
            case 1:
                createDeleteDialog();
                break;
            default:
                break;
        }
    }

    private void createDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(S04CommentActivity.this);
        builder.setMessage("确定要删除？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteComment();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("S04CommentList"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("S04CommentList"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}
