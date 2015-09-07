package com.focosee.qingshow.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.focosee.qingshow.QSApplication;
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
    public static String UPDATE_APP = "UPDATE_APP";

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

    BroadcastReceiver updateAppReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final ConfirmDialog dialog = new ConfirmDialog(BaseActivity.this);
            dialog.setTitle("请更新最新版本，更多意想不到在等着你哦");
            dialog.setConfirm(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Uri uri= Uri.parse("http://a.app.qq.com/o/simple.jsp?pkgname=com.focosee.qingshow");
//                    Uri uri= Uri.parse("http://dd.myapp.com/16891/C033480CED9F6141C9A55D94AD285F29.apk?fsname=com.focosee.qingshow_2.0.0_8.apk");
                    Intent intent=new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            dialog.hideCancel();
            dialog.show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(netReceiver, new IntentFilter(NOTNET));
        registerReceiver(pushReceiver, new IntentFilter(PUSHNOTIFY));
        registerReceiver(updateAppReceiver, new IntentFilter(UPDATE_APP));
        getWindow().setBackgroundDrawable(null);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(netReceiver);
        unregisterReceiver(pushReceiver);
        unregisterReceiver(updateAppReceiver);
        super.onDestroy();
    }

    public abstract void reconn();

}
