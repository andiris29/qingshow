package com.focosee.qingshow.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.activity.fragment.S11NewTradeNotifyFragment;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.CategoriesCommand;
import com.focosee.qingshow.command.SystemCommand;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.util.push.PushHepler;
import com.focosee.qingshow.widget.ConfirmDialog;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2015/2/5.
 */
public abstract class BaseActivity extends FragmentActivity {

    public static String NOTNET = "not_net";
    public static String PUSHNOTIFY = "PUSHNOTIFY";
    public static String UPDATE_APP = "UPDATE_APP";
    private boolean isTop = true;
    private final int TIME_LOGIN = 15000;
    private ConfirmDialog dialog;


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
                            SystemCommand.systemGet(null, new Callback() {
                                @Override
                                public void onComplete(JSONObject response) {
                                    reconn();
                                    CategoriesCommand.getCategories(new Callback());
                                }
                            });
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
            if (!isTop) return;
            final ConfirmDialog dialog = new ConfirmDialog(context);
            final Bundle bundle = intent.getExtras();
            dialog.setTitle(bundle.getString(JPushInterface.EXTRA_ALERT));
            dialog.setConfirm(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = PushHepler._jumpTo(context, bundle, JPushInterface.ACTION_NOTIFICATION_RECEIVED);
                    if(null != i) {
                        context.startActivity(i);
                    }
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

        }
    };

    private void showLoginGuide(){
        if(QSModel.INSTANCE.getUserStatus() == MongoPeople.MATCH_FINISHED
                || (QSModel.INSTANCE.getUserStatus() == MongoPeople.LOGIN_GUIDE_FINISHED && QSModel.INSTANCE.isGuest())) {
            Timer timer = new Timer(true);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(BaseActivity.this, U19LoginGuideActivity.class));
                }
            };
            timer.schedule(timerTask, TIME_LOGIN);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(QSApplication.instance().getPreferences().getBoolean(ValueUtil.UPDATE_APP_FORCE, false)){
            showUpdateDialog();
        }
        registerReceiver(netReceiver, new IntentFilter(NOTNET));
        registerReceiver(pushReceiver, new IntentFilter(PUSHNOTIFY));
        registerReceiver(updateAppReceiver, new IntentFilter(UPDATE_APP));
        getWindow().setBackgroundDrawable(null);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        showLoginGuide();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isTop = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isTop = false;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(netReceiver);
        unregisterReceiver(pushReceiver);
        unregisterReceiver(updateAppReceiver);
        super.onDestroy();
    }

    public abstract void reconn();

    public void showNewTradeNotify() {
        S11NewTradeNotifyFragment fragment = new S11NewTradeNotifyFragment();
        fragment.show(getSupportFragmentManager(), S01MatchShowsActivity.class.getSimpleName());
    }

    public void showUpdateDialog(){
        if(dialog == null) {
            dialog = new ConfirmDialog(BaseActivity.this);
            dialog.setTitle("请更新最新版本，更多意想不到在等着你哦");
            dialog.setConfirm(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("http://a.app.qq.com/o/simple.jsp?pkgname=com.focosee.qingshow");
//                    Uri uri= Uri.parse("http://dd.myapp.com/16891/C033480CED9F6141C9A55D94AD285F29.apk?fsname=com.focosee.qingshow_2.0.0_8.apk");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            dialog.setIsBackFinish(true);
            dialog.show();
            dialog.hideCancel();
            return;
        }
        if(!dialog.isShowing()){
            dialog.show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
