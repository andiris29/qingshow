package com.focosee.qingshow.util;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/9/7.
 */
public class ToastUtil {

    private static Toast mToast;

    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
            mToast=null;
        }
    };

    public static void showShortToast(Context context, String message) {
        mHandler.removeCallbacks(r);
        if (mToast == null){
            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        }
        mHandler.postDelayed(r, 2000);
        mToast.show();
    }






}
