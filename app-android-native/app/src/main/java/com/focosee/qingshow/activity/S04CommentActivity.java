package com.focosee.qingshow.activity;

import android.content.Context;
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
import com.focosee.qingshow.model.GoToWhereAfterLoginModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoComment;
import com.focosee.qingshow.util.ToastUtil;
import com.focosee.qingshow.widget.ActionSheet;
import com.focosee.qingshow.widget.ConfirmDialog;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import de.greenrobot.event.EventBus;

public class S04CommentActivity extends BaseActivity implements ActionSheet.ActionSheetListener, BGARefreshLayout.BGARefreshLayoutDelegate {

    public static final String INPUT_SHOW_ID = "S04CommentActivity show id";
    public static final String COMMENT_NUM_CHANGE = "comment_num_changed";
    @InjectView(R.id.left_btn)
    ImageView leftBtn;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.S04_user_image)
    SimpleDraweeView S04UserImage;
    @InjectView(R.id.S04_input)
    EditText s04Input;
    @InjectView(R.id.S04_send_button)
    Button s04SendButton;
    @InjectView(R.id.s04_recyclerview)
    RecyclerView recyclerView;
    @InjectView(R.id.s04_refresh)
    BGARefreshLayout mRefreshLayout;

    private S04CommentListAdapter adapter;

    private int currentPage = 1;
    private int numbersPerPage = 10;
    private String id;
    private String replyUserId = null;

    private int clickCommentIndex = -1;

    private int position;

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
        Intent intent = getIntent();

        id = intent.getStringExtra(INPUT_SHOW_ID);

        position = intent.getIntExtra("s08_position", 0);

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
        initRefreshLayout();
        mRefreshLayout.beginRefreshing();
    }

    private void initRefreshLayout() {
        mRefreshLayout.setDelegate(this);
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!TextUtils.isEmpty(intent.getStringExtra(INPUT_SHOW_ID))) {
            id = intent.getStringExtra(INPUT_SHOW_ID);
        }
    }

    @Override
    public void reconn() {
        doRefreshTask();
    }

    private void doLoadMoreTask() {
        QSJsonObjectRequest jsonArrayRequest = new QSJsonObjectRequest(QSAppWebAPI.getShowCommentsListApi(id, currentPage + 1, numbersPerPage), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(S04CommentActivity.class.getSimpleName(), "load_response:" + response);
                if (MetadataParser.hasError(response)) {
                    mRefreshLayout.endLoadingMore();
                    return;
                }
                currentPage++;
                adapter.addData(S04CommentActivity.getCommentsFromJsonObject(response));
                adapter.notifyDataSetChanged();
                mRefreshLayout.endLoadingMore();
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonArrayRequest);
    }

    private void doRefreshTask() {

        QSJsonObjectRequest jsonArrayRequest = new QSJsonObjectRequest(QSAppWebAPI.getShowCommentsListApi(id, 0, numbersPerPage), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(S04CommentActivity.class.getSimpleName(), "refresh_response:" + response);
                mRefreshLayout.endRefreshing();
                if (MetadataParser.hasError(response)) {
                    if(MetadataParser.getError(response) == ErrorCode.PagingNotExist){
                        adapter.clearData();
                        adapter.notifyDataSetChanged();
                        return;
                    }
                }
                currentPage = 1;
                adapter.addDataAtTop(S04CommentActivity.getCommentsFromJsonObject(response));
                adapter.notifyDataSetChanged();
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonArrayRequest);
    }

    private void postComment() {

        if (!QSModel.INSTANCE.loggedin()) {
            GoToWhereAfterLoginModel.INSTANCE.set_class(null);
            startActivity(new Intent(S04CommentActivity.this, U19LoginGuideActivity.class));
            return;
        }

        String comment = s04Input.getText().toString().trim();
        if (comment.length() <= 0) {
            ToastUtil.showShortToast(getApplicationContext(), "评论不能为空");
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("_id", id);
        map.put("_atId", replyUserId);
        map.put("comment", comment);
        JSONObject jsonObject = new JSONObject(map);
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getCommentPostApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S04CommentActivity.this, MetadataParser.getError(response));
                    return;
                }
                EventBus.getDefault().post(new S04PostCommentEvent(S04PostCommentEvent.addComment));
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
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getCommentDeleteApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(S04CommentActivity.this, MetadataParser.getError(response));
                    return;
                }
                EventBus.getDefault().post(new S04PostCommentEvent(S04PostCommentEvent.delComment));
                adapter.remove(clickCommentIndex);
                adapter.notifyDataSetChanged();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private static ArrayList<MongoComment> getCommentsFromJsonObject(JSONObject response) {
        String jsonString = "";
        String arrayName = "showComments";
        Log.d(S04CommentActivity.class.getSimpleName(), "response:" + response);
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
                if (null == adapter.getItemData(clickCommentIndex).getAuthorRef()) return;
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
        final ConfirmDialog dialog = new ConfirmDialog(S04CommentActivity.this);
        dialog.setTitle("确定要删除？");
        dialog.setConfirm("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteComment();
                dialog.dismiss();
            }
        });
        dialog.setCancel("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("S04CommentList");
        MobclickAgent.onResume(this);
        if (QSModel.INSTANCE.loggedin()) {
            if (null != QSModel.INSTANCE.getUser()) {
                S04UserImage.setImageURI(Uri.parse(QSModel.INSTANCE.getUser().portrait));
                S04UserImage.setAspectRatio(1f);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("S04CommentList");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        doRefreshTask();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        doLoadMoreTask();
        return true;
    }
}
