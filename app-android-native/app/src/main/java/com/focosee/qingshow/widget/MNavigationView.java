package com.focosee.qingshow.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.entity.People;


public class MNavigationView extends LinearLayout {

    private Button btn_left;
    private Button btn_right;
    private TextView tv_title;
    private String strBtnLeft;
    private String strBtnRight;
    private String strTitle;
    private int left_drawable;
    private int right_drawable;
    private int center_drawable;

    public MNavigationView(Context context) {
        super(context);
        initContent();
    }

    public MNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(attrs);
        initContent();
    }

    private void initAttributes(AttributeSet attributeSet) {

        if (null != attributeSet) {

            final int attrIds[] = new int[] { R.attr.btn_leftText,
                    R.attr.btn_rightText, R.attr.tv_title,
                    R.attr.left_drawable, R.attr.center_drawable, R.attr.right_drawable };

            Context context = getContext();

            TypedArray array = context.obtainStyledAttributes(attributeSet,
                    attrIds);

            CharSequence t1 = array.getText(0);
            CharSequence t2 = array.getText(1);
            CharSequence t3 = array.getText(2);
            left_drawable = array.getResourceId(3, 0);
            center_drawable = array.getResourceId(4, 0);
            right_drawable = array.getResourceId(5, 0);

            array.recycle();

            if (null != t1) {
                strBtnLeft = t1.toString();
            }
            if (null != t2) {
                strBtnRight = t2.toString();

            }
            if (null != t3) {
                strTitle = t3.toString();
            }
        }

    }

    private void initContent() {
        // Orientation
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        // Back color
        setBackgroundColor(Color.WHITE);

        Context context = getContext();

        // Left Image
        btn_left = new Button(context);
        btn_left.setVisibility(View.INVISIBLE);// 设置设置不可见
        if (left_drawable != 0) {
            btn_left.setBackgroundResource(left_drawable);
        } else {
            btn_left.setBackgroundColor(Color.TRANSPARENT);
        }
        btn_left.setTextColor(Color.BLACK);// 字体颜色

        if (null != strBtnLeft) {
            LayoutParams btnLeftParams = new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            btnLeftParams.setMargins(10, 0, 0, 0);
            btn_left.setLayoutParams(btnLeftParams);
            btn_left.setText(strBtnLeft);
        } else if (left_drawable != 0) {
            LayoutParams btnLeftParams = new LayoutParams(50, 50);
            btnLeftParams.setMargins(30, 0,0,0);
            btn_left.setLayoutParams(btnLeftParams);//new LayoutParams(50, 50));
        }

        // 添加这个按钮
        addView(btn_left);

        //
        tv_title = new TextView(context);

        LayoutParams centerParam = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
//        centerParam.weight = 1;
        tv_title.setLayoutParams(centerParam);
        tv_title.setTextColor(Color.BLACK);

        if (null != strTitle) {
            tv_title.setText(strTitle);
        }
        if (center_drawable != 0) {
//            tv_title.setBackgroundResource(center_drawable);
            tv_title.setBackgroundResource(R.drawable.nav_btn_image_logo);
            LayoutParams btnLeftParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            btnLeftParams.setMargins(30, 0,0,0);
            btn_left.setLayoutParams(btnLeftParams);//new LayoutParams(50, 50));
        }
        // 添加这个标题
        addView(tv_title);

        btn_right = new Button(context);
        btn_right.setVisibility(View.INVISIBLE);// 设置设置不可见

        if (right_drawable != 0) {
//            btn_right.setBackgroundResource(right_drawable);
              btn_right.setBackgroundResource(R.drawable.nav_account_btn_woman);
        } else {
            btn_right.setBackgroundColor(Color.TRANSPARENT);
        }
        if (null != strBtnRight) {

            LayoutParams btnRightParams = new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            btnRightParams.setMargins(0, 0, 10, 0);
            btn_right.setLayoutParams(btnRightParams);

            btn_right.setText(strBtnRight);
        } else if (right_drawable != 0) {
            btn_right.setLayoutParams(new LayoutParams(50, 50));
        }

        // 添加这个按钮
        addView(btn_right);

        tv_title.setGravity(Gravity.CENTER);
        if (left_drawable != 0 || null != strBtnLeft) {
//            btn_left.setTextColor(getResources().getColor(R.color.defaultBlue));// 字体颜色
            btn_left.setVisibility(View.VISIBLE);
        }
        if (right_drawable != 0 || null != strBtnRight) {
//            btn_right.setTextColor(getResources().getColor(R.color.defaultBlue));// 字体颜色
            btn_right.setVisibility(VISIBLE);
        }

    }

    public Button getBtn_left() {
        return btn_left;
    }

    public Button getBtn_right() {
        return btn_right;
    }

    public TextView getTv_title() {
        return tv_title;
    }

}