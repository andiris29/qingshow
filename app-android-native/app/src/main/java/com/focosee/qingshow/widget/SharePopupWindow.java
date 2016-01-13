package com.focosee.qingshow.widget;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.focosee.qingshow.R;

/**
 * Created by Administrator on 2015/2/7.
 */
public class SharePopupWindow extends PopupWindow {

    private View mainview;
    private ViewGroup viewGroup;

    private View showView;
    public SharePopupWindow(Activity context,View.OnClickListener itemclick){
        super(context);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        mainview=inflater.inflate(R.layout.share, null);


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.skyblue_logo_sinaweibo,options);
        int outHeight = options.outHeight;
        Log.i("tag",outHeight + "");
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        this.setBackgroundDrawable(dw);
        this.setContentView(mainview);
        this.setFocusable(true);

        mainview.findViewById(R.id.share_wechat).setOnClickListener(itemclick);
        mainview.findViewById(R.id.share_wx_timeline).setOnClickListener(itemclick);

    }


    public void setupDismiss(ViewGroup viewGroup){
        this.viewGroup = viewGroup;
    }

    public void setShowView(View showView) {
        this.showView = showView;
    }


    private void showOneView(View view){
        view.setVisibility(View.VISIBLE);
    }

    private void showAllView(ViewGroup viewGroup){
        for (int i = 0;i < viewGroup.getChildCount();i++){
            viewGroup.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void dismiss() {
        if(showView != null) showOneView(showView);
        if(viewGroup != null)   showAllView(viewGroup);
        super.dismiss();
    }
}
