package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.constants.config.QSPushAPI;
import com.focosee.qingshow.util.PushUtil;


/**
 * Created by Administrator on 2015/7/22.
 */
public class QSPushActivity extends BaseActivity {

    public static final String SHOW_DIALOG = "SHOW_DIALOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void reconn() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        jump();
    }

    private void jump() {
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

        if (command.equals(QSPushAPI.QUEST_SHARING_PROGRESS) || command.equals(QSPushAPI.QUEST_SHARING_OBJECTIVE_COMPLETE)) {
            intent = new Intent(QSPushActivity.this, PushWebActivity.class);
        }


        if (command.equals(QSPushAPI.TRADE_INITIALIZED) || command.equals(QSPushAPI.TRADE_SHIPPED)) {
            intent = new Intent(QSPushActivity.this, U09TradeListActivity.class);
            if(command.equals(QSPushAPI.TRADE_SHIPPED))
                intent.putExtra(U09TradeListActivity.FROM_WHERE, QSPushActivity.class.getSimpleName());
        }

        if (command.equals(QSPushAPI.ITEM_PRICE_CHANGED)) {
            intent = new Intent(QSPushActivity.this, S01MatchShowsActivity.class);
            intent.putExtra(S01MatchShowsActivity.S1_INPUT_SHOWABLE, true);
            intent.putExtras(bundle);

        }

        if (intent != null)
            startActivity(intent);
        else startActivity(new Intent(QSPushActivity.this, S01MatchShowsActivity.class));

        this.finish();
    }


}
