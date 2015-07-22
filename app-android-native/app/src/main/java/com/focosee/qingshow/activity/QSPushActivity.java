package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.focosee.qingshow.constants.config.QSPushAPI;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2015/7/22.
 */
public class QSPushActivity extends Activity {

    private static final String TAG = "JPush_QS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        String command = gotCommand(bundle);

        Intent intent = null;
        if (command.equals(QSPushAPI.NEW_SHOW_COMMENTS)) {
            String id = gotCommentId(bundle);
            intent = new Intent(QSPushActivity.this, S04CommentActivity.class);
            intent.putExtra(S04CommentActivity.INPUT_SHOW_ID,id);
        }

        if (command.equals(QSPushAPI.NEW_RECOMMANDATIONS)){
            intent = new Intent(QSPushActivity.this,U01UserActivity.class);
            intent.putExtra(U01UserActivity.NEW_RECOMMANDATIONS,true);
        }

        if (intent != null)
            startActivity(intent);

        this.finish();
    }

    private String gotCommand(Bundle bundle) {
        String command = "";
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                try {
                    JSONObject json = new JSONObject(bundle.getString(key));
                    command = json.get(QSPushAPI.EXTRA_KEY).toString();

                    Log.d(TAG, "push command" + command);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return command;
    }

    private String gotCommentId(Bundle bundle) {
        String id = "";
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                try {
                    JSONObject json = new JSONObject(bundle.getString(key));
                    id = json.get("id").toString();
                    Log.d(TAG, "push comment id" + id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return id;
    }

}
