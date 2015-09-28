package com.focosee.qingshow.util.user;

import android.text.TextUtils;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSPushAPI;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.receiver.PushGuideEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/9/28.
 */
public class UnreadHelper {

    /**
     * 菜单显示红点
     *
     * @return
     */
    public static boolean hasUnread() {
        if (!QSModel.INSTANCE.loggedin() || QSModel.INSTANCE.getUser() == null) return false;
        MongoPeople user = QSModel.INSTANCE.getUser();
        if (Collections.emptyList().equals(user.unreadNotifications)) return false;

        for (MongoPeople.UnreadNotification unreadNotification : user.unreadNotifications){
            if(unreadNotification.extra.command.equals(QSPushAPI.NEW_SHOW_COMMENTS)) continue;
            return true;
        }
        return false;
    }

    public static void userReadNotificationCommand(String command, String _id) {

        Map<String, String> params = new HashMap<>();
        if (TextUtils.isEmpty(_id)) {
            params.put("command", command);
        }else{
            params.put("command", getCommand(_id));
            params.put("_id", _id);
        }

        UserCommand.readNotification(params, QSApplication.instance(), new Callback() {
            @Override
            public void onComplete() {
                if (!UnreadHelper.hasUnread()) {
                    EventBus.getDefault().post(new PushGuideEvent(false, ""));
                }
            }
        });
    }

    public static void userReadNotificationCommand(String command) {
        userReadNotificationCommand(command, null);
    }

    public static void userReadNotificationId(String _id) {//我只知道_id,command 要再extra中取
        userReadNotificationCommand(null, _id);
    }

    public static String getCommand(String _id) {
        MongoPeople user = QSModel.INSTANCE.getUser();
        if (null == user) return null;

        for (MongoPeople.UnreadNotification unreadNotification : user.unreadNotifications) {
            if (unreadNotification.extra._id.equals(_id)) return unreadNotification.extra.command;
        }

        return null;
    }


    /**
     * 判断某个功能是否有未读的推送
     * @param command
     * @return
     */
    private static boolean hasMyNotification(String command, String _id){

        if(!hasUnread()) return false;

        for (MongoPeople.UnreadNotification unreadNotification : QSModel.INSTANCE.getUser().unreadNotifications){
            if(TextUtils.isEmpty(_id))
                if(unreadNotification.extra.command.equals(command)) return true;
            if(TextUtils.isEmpty(command))
                if(unreadNotification.extra._id.equals(_id)) return true;
        }
        return false;
    }

    public static boolean hasMyNotificationCommand(String command){
        return hasMyNotification(command, null);
    }

    public static boolean hasMyNotificationId(String _id){
        return hasMyNotification(null, _id);
    }
}
