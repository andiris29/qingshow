package com.focosee.qingshow.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S04CommentListAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.error.ErrorCode;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoComment;
import com.focosee.qingshow.widget.ActionSheet;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.focosee.qingshow.widget.RecyclerPullToRefreshView;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class S04CommentActivity extends BaseActivity implements ActionSheet.ActionSheetListener {

    public static final String INPUT_SHOW_ID = "S04CommentActivity show id";
    public static final String COMMENT_NUM_CHANGE = "comment_num_changed";
    public static boolean isOpened = false;
    @InjectView(R.id.left_btn)
    ImageView leftBtn;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.right_btn)
    ImageView rightBtn;
    @InjectView(R.id.S04_recyclerViewPull)
    RecyclerPullToRefreshView recyclerPullToRefreshView;
    @InjectView(R.id.S04_user_image)
    SimpleDraweeView S04UserImage;
    @InjectView(R.id.S04_input)
    EditText s04Input;
    @InjectView(R.id.S04_send_button)
    Button s04SendButton;

    private S04CommentListAdapter adapter;

    private int currentPage = 1;
    private int numbersPerPage = 10;
    private String id;
    private String replyUserId = null;

    private int clickCommentIndex = -1;

    private int position;

    private int API_TYPE = 0;//0是show, 1是preview
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s04_comment);
        ButterKnife.inject(this);

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title.setText("评论");
        rightBtn.setVisibility(View.INVISIBLE);
        if (!QSModel.INSTANCE.loggedin()) {
            startActivity(new Intent(S04CommentActivity.this, U07RegisterActivity.class));
            finish();
            return;
        }
        Intent intent = getIntent();

        if (!TextUtils.isEmpty(intent.getStringExtra(INPUT_SHOW_ID))) {
            id = intent.getStringExtra(INPUT_SHOW_ID);
            API_TYPE = 0;
        }
        position = intent.getIntExtra("s08_position", 0);
        
        recyclerView = recyclerPullToRefreshView.getRefreshableView();

        s04Input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (i3 > 0) {
                    s04SendButton.setBackgroundColor(Color.RED);
                    s04SendButton.setTextColor(Color.WHITE);
                } else if (i3 == 0) {
                    s04SendButton.setBackgroundResource(R.drawable.s04_send_background);
                    s04SendButton.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (QSModel.INSTANCE.loggedin()) {
            if(null != QSModel.INSTANCE.getUser()) {
                if (null != QSModel.INSTANCE.getUser().portrait && !"".equals(QSModel.INSTANCE.getUser().portrait)) {
                    S04UserImage.setImageURI(Uri.parse(QSModel.INSTANCE.getUser().portrait));
                    S04UserImage.setAspectRatio(1f);
                }
            }
        }

        s04SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(S04CommentActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new S04CommentListAdapter(new LinkedList<MongoComment>(), S04CommentActivity.this, R.layout.comment_item_list);
        recyclerView.setAdapter(adapter);

        recyclerPullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                doRefreshTask();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                doLoadMoreTask();
            }
        });

        recyclerPullToRefreshView.doPullRefreshing(true, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isOpened = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!TextUtils.isEmpty(intent.getStringExtra(INPUT_SHOW_ID))) {
            id = intent.getStringExtra(INPUT_SHOW_ID);
            API_TYPE = 0;
        }
    }

    @Override
    public void reconn() {
        doRefreshTask();
    }

    private void doLoadMoreTask() {
        String api;
        if (API_TYPE == 0)
            api = QSAppWebAPI.getShowCommentsListApi(id, currentPage + 1, numbersPerPage);
        else
            api = QSAppWebAPI.getPreviewQuerycommentsApi(id, currentPage + 1, numbersPerPage);
        QSJsonObjectRequest jsonArrayRequest = new QSJsonObjectRequest(api, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    if(MetadataParser.getError(response) == ErrorCode.PagingNotExist)
                        recyclerPullToRefreshView.setHasMoreData(false);
                    else {
                        ErrorHandler.handle(S04CommentActivity.this, MetadataParser.getError(response));
                        recyclerPullToRefreshView.onPullUpRefreshComplete();
                    }
                    return;
                }
                currentPage++;
                adapter.addData(S04CommentActivity.getCommentsFromJsonObject(response, API_TYPE));
                adapter.notifyDataSetChanged();
                recyclerPullToRefreshView.onPullUpRefreshComplete();
                setLastUpdateTime();
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonArrayRequest);
    }

    private void doRefreshTask() {
        String api;
        if (API_TYPE == 0)
            api = QSAppWebAPI.getShowCommentsListApi(id, 0, numbersPerPage);
        else
            api = QSAppWebAPI.getPreviewQuerycommentsApi(id, 0, numbersPerPage);
        QSJsonObjectRequest jsonArrayRequest = new QSJsonObjectRequest(api, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(MetadataParser.hasError(response)){
                    recyclerPullToRefreshView.onPullDownRefreshComplete();
                    ErrorHandler.handle(S04CommentActivity.this, MetadataParser.getError(response));
                    return;
                }
                currentPage = 1;
                adapter.addDataAtTop(S04CommentActivity.getCommentsFromJsonObject(response, API_TYPE));
                adapter.notifyDataSetChanged();
                recyclerPullToRefreshView.onPullDownRefreshComplete();
                setLastUpdateTime();
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonArrayRequest);
    }

    private void postComment() {

        if (!QSModel.INSTANCE.loggedin()) {
            Toast.makeText(S04CommentActivity.this, R.string.need_login, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(S04CommentActivity.this, U07RegisterActivity.class));
            return;
        }

        String comment = s04Input.getText().toString().trim();
        if (comment.length() <= 0) {
            Toast.makeText(this, "评论不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("_id", id);
        map.put("_atId", replyUserId);
        map.put("comment", comment);
        JSONObject jsonObject = new JSONObject(map);
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getCommentPostApi(API_TYPE), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (!MetadataParser.hasError(response)) {
                    sendBroadcast(new Intent(COMMENT_NUM_CHANGE).putExtra("value", 1).putExtra("position", position));

                }
                doRefreshTask();
                s04Input.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(s04Input.getWindowToken(), 0); //强制隐藏键盘
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void deleteComment() {
        Map<String, String> map = new HashMap<>();
        map.put("_id", adapter.getItemData(clickCommentIndex).getId());
        JSONObject jsonObject = new JSONObject(map);
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getCommentDeleteApi(API_TYPE), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                doRefreshTask();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void setLastUpdateTime() {
        String text = formatDateTime(System.currentTimeMillis());
        recyclerPullToRefreshView.setLastUpdatedLabel(text);
    }

    private String formatDateTime(long time) {
        if (0 == time) {
            return "";
        }

        return new SimpleDateFormat("MM-dd HH:mm").format(new Date(time));
    }

    private static ArrayList<MongoComment> getCommentsFromJsonObject(JSONObject response, int API_TYPE) {
        String jsonString = "";
        String arrayName = "showComments";
        if (API_TYPE == 1) arrayName = "previewComments";
        try {
            jsonString = response.getJSONObject("data").getJSONArray(arrayName).toString();
        } catch (JSONException e) {
            Log.i("json", e.toString());
        }
        return QSGsonFactory.create().fromJson(jsonString, new TypeToken<ArrayList<MongoComment>>() {
        }.getType());
    }

    public void showActionSheet(int commentIndex) {
        String userId = QSModel.INSTANCE.getUserId();
        String commentUserId = adapter.getItemData(commentIndex).getUserId();

        clickCommentIndex = commentIndex;

        if (null != userId && userId.equals(commentUserId)) {
            ActionSheet.createBuilder(this, getSupportFragmentManager())
                    .setCancelButtonTitle("取消")
                    .setOtherButtonTitles("查看个人主页", "删除")
                    .setCancelableOnTouchOutside(true).setListener(this).show();
        } else {
            ActionSheet.createBuilder(this, getSupportFragmentManager())
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
                if(null == adapter.getItemData(clickCommentIndex).getAuthorRef())return;
                Intent intent = new Intent(S04CommentActivity.this, U01UserActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("user", adapter.getItemData(clickCommentIndex).getAuthorRef());
                intent.putExtras(bundle1);
                startActivity(intent);
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
