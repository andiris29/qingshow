package com.focosee.qingshow.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.push.PushHepler;
import com.focosee.qingshow.widget.ConfirmDialog;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2015/2/5.
 */
public abstract class BaseActivity extends FragmentActivity {

    public static String NOTNET = "not_net";
    public static String PUSHNOTIFY = "PUSHNOTIFY";

    BroadcastReceiver netReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NOTNET.equals(intent.getAction())) {
                if (!AppUtil.checkNetWork(BaseActivity.this)) {
                    final ConfirmDialog dialog = new ConfirmDialog(BaseActivity.this);
                    dialog.setTitle("未连接网络或者信号不好");
                    dialog.setConfirm("重新连接", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            reconn();
                            dialog.dismiss();
                        }
                    }).show();
                    dialog.hideCancel();
                }
            }
        }
    };

    BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            final ConfirmDialog dialog = new ConfirmDialog(context);
            final Bundle bundle = intent.getExtras();
            dialog.setTitle(bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE));
            dialog.setConfirm(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = PushHepler._jumpTo(context,bundle);
                    context.startActivity(i);
                    dialog.dismiss();
                }
            }).setCancel(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            }).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(netReceiver, new IntentFilter(NOTNET));
        registerReceiver(pushReceiver, new IntentFilter(PUSHNOTIFY));
        getWindow().setBackgroundDrawable(null);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(netReceiver);
        unregisterReceiver(pushReceiver);
        super.onDestroy();
    }

    public abstract void reconn();

}
