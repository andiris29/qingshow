package com.focosee.qingshow.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.KeyEvent;
import android.view.View;

import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.CategoriesCommand;
import com.focosee.qingshow.command.SystemCommand;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.util.push.PushHepler;
import com.focosee.qingshow.widget.ConfirmDialog;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2015/2/5.
 */
public abstract class BaseActivity extends FragmentActivity {

    public static String NOTNET = "not_net";
    public static String PUSHNOTIFY = "PUSHNOTIFY";
    public static String SHOW_GUIDE = "SHOW_GUIDE";
    private boolean isTop = true;
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
                    if (null != i) {
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

    BroadcastReceiver showGuideReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showLoginGuide();
        }
    };

    private void showLoginGuide() {
        if (this instanceof U19LoginGuideActivity
                || this instanceof U17ResetPasswordStep1Activity
                || this instanceof U18ResetPasswordStep2Activity
                || this instanceof U07RegisterActivity
                || this instanceof U06LoginActivity) return;

        startActivity(new Intent(BaseActivity.this, U19LoginGuideActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (QSApplication.instance().getPreferences().getBoolean(ValueUtil.UPDATE_APP_FORCE, false)) {
            showUpdateDialog();
        }
        registerReceiver(netReceiver, new IntentFilter(NOTNET));
        registerReceiver(pushReceiver, new IntentFilter(PUSHNOTIFY));
        registerReceiver(showGuideReceiver, new IntentFilter(SHOW_GUIDE));
        getWindow().setBackgroundDrawable(null);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        unregisterReceiver(showGuideReceiver);
        super.onDestroy();
    }

    public abstract void reconn();

    public void showUpdateDialog() {
        if (dialog == null) {
            dialog = new ConfirmDialog(BaseActivity.this);
            SpannableString spanStrPrice = new SpannableString("请更新最新版本\n\n更多意想不到在等着你哦");
            RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(1.5f);
            spanStrPrice.setSpan(relativeSizeSpan, 0, "请更新最新版本".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
            dialog.setTitle(spanStrPrice);
            return;
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
