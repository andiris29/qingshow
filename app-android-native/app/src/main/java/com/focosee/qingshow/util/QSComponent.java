package com.focosee.qingshow.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.focosee.qingshow.R;

import org.w3c.dom.Text;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by Administrator on 2015/3/11.
 */
public class QSComponent {

    public static void showDialag(Context context, String msg){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.version_dialag, null);
        TextView textView = (TextView) linearLayout.findViewById(R.id.version);
        textView.setText(msg);
        final MaterialDialog materialDialog = new MaterialDialog(context);

        materialDialog.setContentView(linearLayout)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                }).show();
    }

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
}
