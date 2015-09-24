package com.focosee.qingshow.util.push;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.focosee.qingshow.activity.PushWebActivity;
import com.focosee.qingshow.activity.S01MatchShowsActivity;
import com.focosee.qingshow.activity.S04CommentActivity;
import com.focosee.qingshow.activity.U01UserActivity;
import com.focosee.qingshow.activity.U09TradeListActivity;
import com.focosee.qingshow.activity.U15BonusActivity;
import com.focosee.qingshow.constants.config.QSPushAPI;
import com.focosee.qingshow.util.ValueUtil;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/9/2.
 */
public class PushHepler {

    public static Intent _jumpTo(Context context,Bundle bundle) {
        String command = PushUtil.getCommand(bundle);
        Intent intent = null;
        if (command.equals(QSPushAPI.NEW_SHOW_COMMENTS)) {
            String id = PushUtil.getExtra(bundle, "id");
            intent = new Intent(context, S04CommentActivity.class);
            intent.putExtra(S04CommentActivity.INPUT_SHOW_ID, id);
        }

        if (command.equals(QSPushAPI.NEW_RECOMMANDATIONS)) {
            intent = new Intent(context, U01UserActivity.class);
            intent.putExtra(U01UserActivity.NEW_RECOMMANDATIONS, true);
        }

        if (command.equals(QSPushAPI.QUEST_SHARING_PROGRESS) || command.equals(QSPushAPI.QUEST_SHARING_OBJECTIVE_COMPLETE)) {
            intent = new Intent(context, PushWebActivity.class);
        }


        if (command.equals(QSPushAPI.TRADE_INITIALIZED) || command.equals(QSPushAPI.TRADE_SHIPPED)) {
            intent = new Intent(context, U09TradeListActivity.class);
            if(command.equals(QSPushAPI.TRADE_SHIPPED))
                intent.putExtra(U09TradeListActivity.FROM_WHERE, U09TradeListActivity.PUSH_NOTIFICATION);
        }

        if (command.equals(QSPushAPI.ITEM_EXPECTABLE_PRICEUPDATED)) {
            intent = new Intent(context, S01MatchShowsActivity.class);
            String _id = PushUtil.getExtra(bundle, "_id");
            intent.putExtra(S01MatchShowsActivity.S1_INPUT_TRADEID_NOTIFICATION,_id);
            intent.putExtra(S01MatchShowsActivity.S1_INPUT_SHOWABLE, true);
        }

        if(command.equals(QSPushAPI.NEW_BONUSES) || command.equals(QSPushAPI.BONUS_WITHDRAW_COMPLETE)){
            intent = new Intent(context, U15BonusActivity.class);
            EventBus.getDefault().post(ValueUtil.BONUES_COMING);
        }

        if (intent != null)
            return intent;
        else return new Intent(context, S01MatchShowsActivity.class);
    }
}
