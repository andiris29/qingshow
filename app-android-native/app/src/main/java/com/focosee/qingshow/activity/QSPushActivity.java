package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.focosee.qingshow.constants.config.QSPushAPI;
import com.focosee.qingshow.util.PushUtil;
import com.lurencun.android.system.PhoneUtil;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2015/7/22.
 */
public class QSPushActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        String command = PushUtil.getCommand(bundle);

        Intent intent = null;
        if (command.equals(QSPushAPI.NEW_SHOW_COMMENTS)) {
            String id = PushUtil.getExtra(bundle, "id");
            intent = new Intent(QSPushActivity.this, S04CommentActivity.class);
            intent.putExtra(S04CommentActivity.INPUT_SHOW_ID, id);
        }

        if (command.equals(QSPushAPI.NEW_RECOMMANDATIONS)) {
            intent = new Intent(QSPushActivity.this, U01UserActivity.class);
            intent.putExtra(U01UserActivity.NEW_RECOMMANDATIONS, true);
        }

        if (command.equals(QSPushAPI.QUEST_SHARING_PROGRESS) || command.equals(QSPushAPI.QUEST_SHARING_OBJECTIVE_COMPLETE)){

        }

        if (intent != null)
            startActivity(intent);
        else startActivity(new Intent(QSPushActivity.this, S01MatchShowsActivity.class));

        this.finish();
    }


}
