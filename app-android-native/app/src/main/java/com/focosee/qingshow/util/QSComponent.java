package com.focosee.qingshow.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.focosee.qingshow.R;

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
}
