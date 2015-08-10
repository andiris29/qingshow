package com.focosee.qingshow.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.focosee.qingshow.util.AppUtil;
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
                    new AlertDialog.Builder(BaseActivity.this)
                            .setTitle("连接失败")
                            .setMessage("未连接网络或者信号不好")
                            .setPositiveButton("重新连接", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    reconn();
                                }
                            }).show();
                }
            }
        }
    };

    BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            final ConfirmDialog dialog = new ConfirmDialog();
            final Bundle bundle = intent.getExtras();
            dialog.setTitle(bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE));
            dialog.setConfirm(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, QSPushActivity.class);
                    i.putExtras(bundle);
                    context.startActivity(i);
                }
            }).setCancel(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            }).show(getSupportFragmentManager());
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
