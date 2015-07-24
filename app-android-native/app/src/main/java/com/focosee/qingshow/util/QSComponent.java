package com.focosee.qingshow.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.focosee.qingshow.R;

/**
 * Created by Administrator on 2015/3/11.
 */
public class QSComponent {

    public static void showToast(Context context, String msg, int duration){
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, null);
        TextView textView = (TextView)layout.findViewById(R.id.toast_textview);
        textView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/black_fangzheng_simple.TTF"));
        textView.setText(msg);
        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }

    public static void showToast(Context context, int msg, int duration) {
        showToast(context, context.getResources().getString(msg), duration);
    }
}
