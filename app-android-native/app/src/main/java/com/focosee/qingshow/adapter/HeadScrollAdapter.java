package com.focosee.qingshow.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

import com.focosee.qingshow.util.DensityUtil;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AbsListView;

/**
 * Created by Administrator on 2015/3/5.
 */
public class HeadScrollAdapter  implements AbsListView.OnScrollListener, View.OnTouchListener, PLA_AbsListView.OnScrollListener{
    private Context context;
    private RelativeLayout headRelativeLayout;
    private int firstVisibleItem;
    private int padding;
    public int headHeight;
    private boolean isHeadMove = false;

    public HeadScrollAdapter(RelativeLayout headRelativeLayout, Context context) {
        this.headRelativeLayout = headRelativeLayout;
        this.context = context;
        headHeight = headRelativeLayout.getLayoutParams().height;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
        padding = headRelativeLayout.getLayoutParams().height+(int)headRelativeLayout.getY();
        padding = padding > headHeight ? headHeight : padding;
        padding = padding < 0 ? 0 : padding;
        if(isHeadMove) {
            if (null != view.getChildAt(0)) {
                if (padding > 0 && padding <= headHeight) {

                    System.out.println("viewChildTop:" + view.getChildAt(0).getTop());
                    System.out.println("viewChildY:" + view.getChildAt(0).getY());
//                    System.out.println("headTop:" + headRelativeLayout.getTop());
//                    System.out.println("headY:" + headRelativeLayout.getY());

//                    view.getChildAt(0).setY(view.getY());
                    view.setPadding(0, padding, 0, 0);

                    headRelativeLayout.setY((view.getChildAt(0).getY() - headRelativeLayout.getLayoutParams().height) > 0 ? 0 : view.getChildAt(0).getY() - headRelativeLayout.getLayoutParams().height);
//                viewPager.setY(view.getChildAt(firstVisibleItem).getY());


//                System.out.println("滚动到第一个");
//                System.out.println("firstViewY:" + view.getChildAt(firstVisibleItem).getY());
                }else {
                    isHeadMove = false;
                    if (view.getPaddingTop() != 0 && padding == 0)
                        view.setPadding(0, padding, 0, 0);
                }
            }
        }
        System.out.println("padding：" + padding);
    }

    private VelocityTracker mVelocityTracker;

    private float speedTouch;
    private float offSetTouch;
    private float maxVelocity = 8;
    private float direction = 500;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //final float scale = getResources().getDisplayMetrics().density;
//        System.out.println("相对于屏幕左上角的Y:" + (230 * scale + 0.5f));5
        if(null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
            isHeadMove = true;
        }

        mVelocityTracker.addMovement(event);

        final VelocityTracker velocityTracker = mVelocityTracker;
        // 1000 provides pixels per second
        velocityTracker.computeCurrentVelocity(1, maxVelocity); //设置maxVelocity值为0.1时，速率大于0.01时，显示的速率都是0.01,速率小于0.01时，显示正常
        speedTouch = velocityTracker.getYVelocity();

        velocityTracker.computeCurrentVelocity(1000); //设置units的值为1000，意思为一秒时间内运动了多少个像素
        offSetTouch = velocityTracker.getYVelocity();
        if(offSetTouch > 0 && offSetTouch > direction) {//向下滑动

            if(speedTouch == maxVelocity) {//快速滑动
//                if(padding < headHeight){
                headRelativeLayout.setY(0);
                padding = headHeight;
                v.setPadding(0, padding, 0, 0);
                isHeadMove = false;
            } else {//触摸滑动
                if(this.firstVisibleItem == 0) {
                    headRelativeLayout.setY(0);
                    padding = headHeight;
                    v.setPadding(0, padding, 0, 0);
                    isHeadMove = false;
                }
            }

        } else if (offSetTouch < -direction) {//向上滑动
            isHeadMove = true;
//            if(speedTouch == (float)-0.8) {//快速滑动
//
//            }
        }
        return false;
    }

    @Override
    public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {

    }

    int[] location = new int[2];

    @Override
    public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
        padding = headRelativeLayout.getLayoutParams().height+(int)headRelativeLayout.getY();
        padding = padding > headHeight ? headHeight : padding;
        padding = padding < 0 ? 0 : padding;
        System.out.println("padding计算:" + padding);
        System.out.println("padding得到:" + view.getPaddingTop());
        if(isHeadMove) {
            if (null != view.getChildAt(0)) {
                if (padding > 0 && padding <= headHeight) {
                    System.out.println("U01Activity_padding: " + view.getPaddingTop());

                    System.out.println("viewChildTop:" + view.getChildAt(0).getTop());
                    System.out.println("viewTop:" + view.getTop());
//                    System.out.println("headTop:" + headRelativeLayout.getTop());
                    view.getChildAt(0).setBackgroundColor(Color.BLACK);
//                    view.getChildAt(0).setY(view.getY());
                    view.setPadding(0, padding, 0, 0);

                    view.getChildAt(0).getLocationOnScreen(location);
                    headRelativeLayout.setY(((float) view.getChildAt(0).getTop() - headHeight) > 0 ? 0 : (float) view.getChildAt(0).getTop() - headHeight);
                    System.out.println("headY:" + headRelativeLayout.getY());
//                viewPager.setY(view.getChildAt(firstVisibleItem).getY());


//                System.out.println("滚动到第一个");
//                System.out.println("firstViewY:" + view.getChildAt(firstVisibleItem).getY());
                }else {
                    isHeadMove = false;

                    if (view.getPaddingTop() != 0 && padding == 0)
                        view.setPadding(0, padding, 0, 0);
                }
            }
        }
    }
}
