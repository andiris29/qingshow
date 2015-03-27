package com.focosee.qingshow.widget.indicator;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.focosee.qingshow.R;

/**
 * Created by Administrator on 2015/3/20.
 */
public class PageIndicator extends LinearLayout {

    private TextView index;
    private TextView count;
    private TextView division;

    public PageIndicator(Context context) {
        this(context, null);
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

        setBackgroundResource(R.drawable.page_indicator);
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);

        index = new TextView(getContext());
        count = new TextView(getContext());
        division = new TextView(getContext());

        index.setTextColor(Color.WHITE);
        count.setTextColor(Color.WHITE);
        division.setTextColor(Color.WHITE);

        division.setText("/");
        division.setPadding(10,0,10,0);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);

        addView(index,layoutParams);
        addView(division,layoutParams);
        addView(count,layoutParams);
    }

    public void setIndex(int i) {
        index.setText(String.valueOf(i));
    }

    public void setCount(int i) {
        count.setText(String.valueOf(i));
    }
}
