package com.focosee.qingshow.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2016/1/8.
 */
public class DialogUtils {
    /**
     * 创建一个单选对话框
     *
     * @param context
     * @param
     *            提示消息
     * @param dialogClickListener
     *            点击监听
     * @return
     */
    public static android.app.Dialog showRadioDialog(Context context,String title ,String url ,  final DialogClickListener dialogClickListener) {
        return ShowDialog(context, title, url ,dialogClickListener);
    }
    /**
     * 消息提醒
     *
     * @param context
     * @param title
     *            标题
     * @param dialogClickListener
     * type == 1 显示一个button  2 显示两个button
     * @return
     */
    public static android.app.Dialog ShowDialog(Context context, String title, String url , final DialogClickListener dialogClickListener) {
        final android.app.Dialog dialog = new android.app.Dialog(context, R.style.DialogStyle);
        dialog.setCancelable(false);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_share_question, null);
        dialog.setContentView(view);
      //  Button btnOk = (Button) view.findViewById(R.id.btn_dialog_remind_ok);
        ImageView btnCancle = (ImageView) view.findViewById(R.id.iv_question_cancle);
        ImageView iv = (ImageView) view.findViewById(R.id.iv_share_question_image);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        iv.setLayoutParams(params);
        ImageLoader.getInstance().displayImage(url, iv);
        //iv.setImageURI(Uri.parse(url));
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_question_title);
        tvTitle.setText(title);
//        btnOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                dialog.dismiss();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        dialogClickListener.confirm();
//                    }
//                }, 200);
//            }
//        });
        btnCancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Window mWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {// 横屏
            // lp.width = getScreenHeight(context) / 10 * 8;
            lp.width = getScreenWidth(context) -10;
        } else {
           // lp.width = getScreenWidth(context) / 10 * 8;
            lp.width = getScreenWidth(context) -10;
        }
        mWindow.setAttributes(lp);
        dialog.show();

        return dialog;
    }

    /** 获取屏幕分辨率宽 */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /** 获取屏幕分辨率高 */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }
    public interface DialogClickListener {
        public abstract void confirm();

        public abstract void cancel();
    }
}
