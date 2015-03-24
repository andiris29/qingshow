package com.focosee.qingshow.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.focosee.qingshow.R;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by Chenhr on 2015/3/20.
 */
public class DialogCityPicker{

    private CityPicker cityPicker;
    private AlertDialog.Builder dialog;
    private Context context;

    public DialogCityPicker(Context context) {
        this.context = context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.dialog_citypicker, null);
        dialog = new AlertDialog.Builder(context);
        dialog.setTitle(context.getResources().getString(R.string.choose_address_please)).setIcon(
                android.R.drawable.ic_dialog_info).setView(view)
                .setPositiveButton(context.getResources().getString(R.string.confirm), null)
                .setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        cityPicker = (CityPicker) view.findViewById(R.id.dialog_citypicker);
    }

    public String getValue(){
        return cityPicker.getCity_string();
    }

    public void setOnClickListener(DialogInterface.OnClickListener onClickListener){
        dialog.setPositiveButton(context.getResources().getString(R.string.confirm), onClickListener);
    }

    public void dismiss(){
//        dialog.
    }

    public void show(){
        dialog.show();
    }
}
