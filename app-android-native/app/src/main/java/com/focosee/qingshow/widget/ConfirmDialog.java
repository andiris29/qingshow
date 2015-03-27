package com.focosee.qingshow.widget;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import confirmdialog.chen.org.confirmdialog.R;


/**
 * Created by Administrator on 2015/3/27.
 */
public class ConfirmDialog extends Fragment implements View.OnClickListener{

    private static final int ALPHA_DURATION = 300;

    private TextView title;
    private TextView confirm;
    private TextView cancel;

    private String titleStr;
    private String confirmStr;
    private String cancelStr;

    private View.OnClickListener confirmOnClickListener;
    public View.OnClickListener cancelOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    private ViewGroup mGroup;
    private View mView;
    private FrameLayout frameLayout;

    private boolean isDismissed = false;

    public void show(FragmentManager fm){

        FragmentTransaction ft = fm.beginTransaction();
        ft.add(this, "dialog");
        ft.addToBackStack(null);
        ft.commit();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.confirm_dialog, container);

        title = (TextView) mView.findViewById(R.id.dialog_title);
        confirm = (TextView) mView.findViewById(R.id.dialog_confirm);
        cancel = (TextView) mView.findViewById(R.id.dialog_cancel);

        title.setText(titleStr);
        confirm.setText(confirmStr);
        confirm.setOnClickListener(confirmOnClickListener);
        cancel.setText(cancelStr);
        cancel.setOnClickListener(cancelOnClickListener);

        mGroup = (ViewGroup) getActivity().getWindow().getDecorView();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        frameLayout = new FrameLayout(getActivity());
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.setBackgroundColor(Color.argb(136, 0, 0, 0));
        frameLayout.setOnClickListener(this);

        LinearLayout mPanel = new LinearLayout(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(size.x/5, 0, size.x/5, 0);
        params.gravity = Gravity.CENTER;
        mPanel.setLayoutParams(params);
        mPanel.setOrientation(LinearLayout.VERTICAL);

        mPanel.addView(mView);
        frameLayout.addView(mPanel);
        mGroup.addView(frameLayout);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        mGroup.removeView(frameLayout);
        frameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGroup.removeView(frameLayout);
            }
        }, ALPHA_DURATION);
        super.onDestroyView();
    }

    public void setTitle(String title){
        this.titleStr = title;
        if(null != this.title) this.title.setText(title);
    }

    public void setConfirm(String confirm, View.OnClickListener onClickListener){
        this.confirmStr = confirm;
        this.confirmOnClickListener = onClickListener;
        if(null != this.confirm) {
            this.confirm.setText(confirm);
            this.confirm.setOnClickListener(onClickListener);
        }
    }

    public void setCancel(String cancel, View.OnClickListener onClickListener){
        this.cancelStr = cancel;
        if(null != this.cancel) {
            this.cancel.setText(cancel);
            this.confirm.setOnClickListener(onClickListener);
        }
    }

    public void dismiss(){
        if(isDismissed){
            return;
        }
        isDismissed = true;
        getFragmentManager().popBackStack();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.remove(this);
        ft.commit();
    }


    @Override
    public void onClick(View v) {

    }
}
