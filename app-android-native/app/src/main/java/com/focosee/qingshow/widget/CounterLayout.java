package com.focosee.qingshow.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.focosee.qingshow.R;

/**
 * Created by Administrator on 2015/3/12.
 */
public class CounterLayout extends LinearLayout implements View.OnClickListener {

    private Button addButton;
    private Button cutButton;
    private TextView numView;

    private int addButtonBgRes = 0;
    private int cutButtonBgRes = 0;
    private int numTextColorRes = 0;

    private Context context;

    private int num = 1;

    public CounterLayout(Context context) {
        this(context, null);
    }

    public CounterLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CounterLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initAttrbutes(attrs);
        initContent();
    }

    private void initAttrbutes(AttributeSet attrs) {
        if (null != attrs) {
            final int attrIds[] = new int[]{R.attr.add_button_bg, R.attr.cut_button_bg, R.attr.num_text_color,R.attr.num_text};
            TypedArray array = context.obtainStyledAttributes(attrs,
                    attrIds);
            addButtonBgRes = array.getResourceId(0, 0);
            cutButtonBgRes = array.getResourceId(1, 0);
            numTextColorRes = array.getColor(2, 0);
            num = array.getInt(3,0);
            array.recycle();
        }
    }

    private void initContent() {
        setOrientation(HORIZONTAL);

        addButton = new Button(context);
        cutButton = new Button(context);
        numView = new TextView(context);

        addButton.setBackgroundResource(addButtonBgRes);
        cutButton.setBackgroundResource(cutButtonBgRes);
        numView.setBackgroundResource(R.drawable.counter_text);

        LayoutParams addParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        addButton.setLayoutParams(addParams);
        addButton.setPadding(10,10,10,10);

        LayoutParams cutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        cutButton.setLayoutParams(cutParams);
        cutButton.setPadding(10,10,10,10);

        LayoutParams textParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textParams.gravity = Gravity.CENTER;
        textParams.setMargins(10,0,10,0);
        numView.setLayoutParams(textParams);
        numView.setPadding(10, 10, 10, 10);

        addButton.setOnClickListener(this);
        cutButton.setOnClickListener(this);

        numView.setTextSize(R.dimen.text_size);
        numView.setTextColor(numTextColorRes);

        numView.setText(num + "");

        addButton.setText("+");
        addButton.setTextSize(R.dimen.title_bar_height);
        cutButton.setText("-");
        cutButton.setTextSize(R.dimen.title_bar_height);

        addView(addButton);
        addView(numView);
        addView(cutButton);
    }

    @Override
    public void onClick(View v) {
        if (v == addButton) {
            num++;
            numView.setText(num + "");
        }
        if (v == cutButton) {
            num--;
            numView.setText(num + "");
        }
        addButton.setText("!!!");
        cutButton.setText("---");
        Toast.makeText(context.getApplicationContext(),"button",Toast.LENGTH_LONG).show();
    }
}
