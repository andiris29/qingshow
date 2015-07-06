package com.focosee.qingshow.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;

import com.focosee.qingshow.QSApplication;

/**
 * Created by Administrator on 2015/4/24.
 */
public class RecyclerPullToRefreshView extends PullToRefreshBase<RecyclerView>{

    private RecyclerView recyclerView;

    public RecyclerPullToRefreshView(Context context) {
        this(context, null, 0);
    }

    public RecyclerPullToRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerPullToRefreshView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        recyclerView = new RecyclerView(context);
        return recyclerView;
    }

    @Override
    public boolean isReadyForPullDown() {

        final RecyclerView.Adapter adapter = recyclerView.getAdapter();

        if (null == adapter) {
            return true;
        }

        int mostTop = (recyclerView.getChildCount() > 0) ? recyclerView.getChildAt(0).getTop() : 0;
        if (mostTop >= 0) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isReadyForPullUp() {
        return true;
    }

    /**
     * 判断最后一个child是否完全显示出来
     *
     * @return true完全显示出来，否则false
     */
    private boolean isLastItemVisible() {
        return false;
    }

}
