package com.focosee.qingshow.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;

public class MHorizontalScrollView extends HorizontalScrollView {
    private int subChildCount = 0;
    private ViewGroup firstChild = null;
    private int downX = 0;
    private int currentPage = 0;
    private ArrayList<Integer> pointList = new ArrayList<Integer>();

    private OnItemChangeListener onItemChangeListener;

    /**
     * 添加位置监听回调
     *
     * @param //onGuideListener
     */
    public void setOnItemChangeListener(OnItemChangeListener onItemChangeListener) {
        if (onItemChangeListener == null) {
            throw new NullPointerException();
        }
        this.onItemChangeListener = onItemChangeListener;
    }

    /**
     * 广告位置监听接口
     */
    public interface OnItemChangeListener {
        void onPosition(int position, int totalCount);
    }

    public MHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    public MHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MHorizontalScrollView(Context context) {
        super(context);
        init();
    }
    private void init() {
        setHorizontalScrollBarEnabled(false);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        receiveChildInfo();
    }
    public void receiveChildInfo() {

        firstChild = (ViewGroup) getChildAt(0);
        if(firstChild != null){
            subChildCount = firstChild.getChildCount();
            for(int i = 0;i < subChildCount;i++){
                if(((View)firstChild.getChildAt(i)).getWidth() > 0){
                    pointList.add(((View)firstChild.getChildAt(i)).getLeft());
                }
            }
        }

    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:{

            }break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:{
                if( Math.abs((ev.getX() - downX)) > getWidth() / 4){
                    if(ev.getX() - downX > 0){
                        smoothScrollToPrePage();
                    }else{
                        smoothScrollToNextPage();
                    }
                }else{
                    smoothScrollToCurrent();
                }
                if (null != this.onItemChangeListener)
                    this.onItemChangeListener.onPosition(this.currentPage, this.subChildCount);
                return true;
            }
        }
        return super.onTouchEvent(ev);
    }

    private void smoothScrollToCurrent() {
        smoothScrollTo(pointList.get(currentPage), 0);
    }

    private void smoothScrollToNextPage() {
        if(currentPage < subChildCount - 1){
            currentPage++;
            smoothScrollTo(pointList.get(currentPage), 0);
        }
    }

    private void smoothScrollToPrePage() {
        if(currentPage > 0){
            currentPage--;
            smoothScrollTo(pointList.get(currentPage), 0);
        }
    }
    /**
     * 下一页
     */
    public void nextPage(){
        smoothScrollToNextPage();
    }
    /**
     * 上一页
     */
    public void prePage(){
        smoothScrollToPrePage();
    }
    /**
     * 跳转到指定的页面
     * @param page
     * @return
     */
    public boolean gotoPage(int page){
        if(page > 0 && page < subChildCount - 1){
            smoothScrollTo(pointList.get(page), 0);
            currentPage = page;
            return true;
        }
        return false;
    }
}