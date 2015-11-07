package com.focosee.qingshow.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.focosee.qingshow.R;

/**
 * Created by Administrator on 2015/3/27.
 */
public class ConfirmDialog extends Dialog {

    private QSTextView dialogTitle;
    private QSButton dialogCancel;
    private QSButton dialogConfirm;
    private LinearLayout layout;
    private View centerLine;
    private Context context;

    private boolean isBackFinish = false;

    private CharSequence titleStr;
    private String confirmStr = "确定";
    private String cancelStr = "取消";

    private View.OnClickListener confirmOnClickListener;
    public View.OnClickListener cancelOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    public ConfirmDialog(Context context) {
        super(context, R.style.comfirmDialog);
        init(context);
    }

    public ConfirmDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    protected ConfirmDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context){
        this.context = context;
    }

    public void setIsBackFinish(boolean isBackFinish) {
        this.isBackFinish = isBackFinish;
    }

    @Override
    public void show() {
        super.show();
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_dialog);

        dialogTitle = (QSTextView) findViewById(R.id.dialog_title);
        dialogConfirm = (QSButton) findViewById(R.id.dialog_confirm);
        dialogCancel = (QSButton) findViewById(R.id.dialog_cancel);
        layout = (LinearLayout) findViewById(R.id.dialog_layout);
        centerLine = findViewById(R.id.dialog_center_line);

        ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
        layoutParams.width = getWindow().getWindowManager().getDefaultDisplay().getWidth() / 2;
        layout.setLayoutParams(layoutParams);

        dialogTitle.setText(titleStr);
        dialogConfirm.setText(confirmStr);
        dialogConfirm.setOnClickListener(confirmOnClickListener);
        dialogCancel.setText(cancelStr);
        dialogCancel.setOnClickListener(cancelOnClickListener);

    }

    @Override
    public void setTitle(CharSequence title) {
        this.titleStr = title;
    }

    public void setTitle(SpannableString title) {
        if (null != this.dialogTitle) this.dialogTitle.setText(title);
    }

    public ConfirmDialog setConfirm(View.OnClickListener onClickListener) {
        this.confirmOnClickListener = onClickListener;
        if (null != this.dialogConfirm) {
            this.dialogConfirm.setText(confirmStr);
            this.dialogConfirm.setOnClickListener(onClickListener);
        }
        return this;
    }

    public ConfirmDialog setConfirm(String confirm, View.OnClickListener onClickListener) {
        this.confirmStr = confirm;
        this.confirmOnClickListener = onClickListener;
        if (null != this.dialogConfirm) {
            this.dialogConfirm.setText(confirm);
            this.dialogConfirm.setOnClickListener(onClickListener);
        }
        return this;
    }

    public ConfirmDialog setCancel(View.OnClickListener onClickListener) {
        this.cancelOnClickListener = onClickListener;
        if (null != this.dialogCancel) {
            this.dialogCancel.setText(cancelStr);
            this.dialogConfirm.setOnClickListener(onClickListener);
        }
        return this;
    }

    public ConfirmDialog setCancel(String cancel, View.OnClickListener onClickListener) {
        this.cancelStr = cancel;
        this.cancelOnClickListener = onClickListener;
        if (null != this.dialogCancel) {
            this.dialogCancel.setText(cancel);
            this.dialogConfirm.setOnClickListener(onClickListener);
        }
        return this;
    }

    public void hideCancel() {
        if(null != this.dialogCancel) {
            this.dialogCancel.setVisibility(View.GONE);
            this.centerLine.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (isBackFinish && keyCode == KeyEvent.KEYCODE_BACK) {
            ((Activity) context).finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
