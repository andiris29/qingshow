package com.focosee.qingshow.util.push;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.focosee.qingshow.activity.BaseActivity;
import com.focosee.qingshow.activity.PushWebActivity;
import com.focosee.qingshow.activity.S01MatchShowsActivity;
import com.focosee.qingshow.activity.S04CommentActivity;
import com.focosee.qingshow.activity.S20MatcherActivity;
import com.focosee.qingshow.activity.U01UserActivity;
import com.focosee.qingshow.activity.U09TradeListActivity;
import com.focosee.qingshow.activity.U15BonusActivity;
import com.focosee.qingshow.activity.U20NewBonus;
import com.focosee.qingshow.activity.U21NewParticipantBonus;
import com.focosee.qingshow.constants.config.QSPushAPI;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.ValueUtil;
import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/9/2.
 */
public class PushHepler {

    public static Intent _jumpTo(Context context,Bundle bundle, String action) {
        String command = PushUtil.getCommand(bundle);
        Log.d(PushHepler.class.getSimpleName(), "command:" + command);
        Intent intent = null;
        if(QSModel.INSTANCE.getUserStatus() < MongoPeople.MATCH_FINISHED) {
            intent = new Intent(context, S20MatcherActivity.class);
        }
        if (command.equals(QSPushAPI.NEW_SHOW_COMMENTS)) {
            String id = PushUtil.getExtra(bundle, "_id");
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

        if (command.equals(QSPushAPI.TRADE_SHIPPED)
                || command.equals(QSPushAPI.TRADE_REFUND_COMPLETE)) {
            intent = new Intent(context, U09TradeListActivity.class);
            if(command.equals(QSPushAPI.TRADE_SHIPPED) || command.equals(QSPushAPI.TRADE_REFUND_COMPLETE))
                intent.putExtra(U09TradeListActivity.FROM_WHERE, U09TradeListActivity.PUSH_NOTIFICATION);
        }

        if (command.equals(QSPushAPI.ITEM_EXPECTABLE_PRICEUPDATED) || command.equals(QSPushAPI.TRADE_INITIALIZED)) {
            String _id = PushUtil.getExtra(bundle, "_id");
            if(action.equals(JPushInterface.ACTION_NOTIFICATION_OPENED)) {
                intent = new Intent(context, S01MatchShowsActivity.class);
                intent.putExtra(S01MatchShowsActivity.S1_INPUT_TRADEID_NOTIFICATION, _id);
                intent.putExtra(S01MatchShowsActivity.S1_INPUT_SHOWABLE, true);
            }

            if(action.equals(JPushInterface.ACTION_NOTIFICATION_RECEIVED)) {
                ((BaseActivity) context).getIntent().putExtra(S01MatchShowsActivity.S1_INPUT_TRADEID_NOTIFICATION, _id);
                ((BaseActivity) context).getIntent().putExtra(S01MatchShowsActivity.S1_INPUT_SHOWABLE, true);
                return null;
            }
        }

        if(command.equals(QSPushAPI.NEW_BONUSES)){
            intent = new Intent(context, U20NewBonus.class);
            EventBus.getDefault().post(ValueUtil.BONUES_COMING);
        }

        if(command.equals(QSPushAPI.NEW_PARTICIPANT_BONUS)){
            intent = new Intent(context, U21NewParticipantBonus.class);
            EventBus.getDefault().post(ValueUtil.BONUES_COMING);
        }

        if(command.equals(QSPushAPI.BONUS_WITHDRAW_COMPLETE)){
            intent = new Intent(context, U15BonusActivity.class);
            EventBus.getDefault().post(ValueUtil.BONUES_COMING);
        }

        if (intent != null)
            return intent;
        else return new Intent(context, S01MatchShowsActivity.class);
    }
}
