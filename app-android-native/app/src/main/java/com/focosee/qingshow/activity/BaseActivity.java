package com.focosee.qingshow.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import com.focosee.qingshow.util.AppUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2015/2/5.
 */
public abstract class BaseActivity extends Activity {

    public static String NOTNET = "not_net";

    BroadcastReceiver netReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(NOTNET.equals(intent.getAction())) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(netReceiver, new IntentFilter(NOTNET));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(netReceiver);
        super.onDestroy();
    }

    public abstract void reconn();

}
