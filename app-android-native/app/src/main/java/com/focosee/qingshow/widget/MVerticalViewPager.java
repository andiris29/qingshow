/*
 * Copyright 2014 shhp

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

package com.focosee.qingshow.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class MVerticalViewPager extends ScrollView {
    
    private final String TAG = "MVerticalViewPager";
    private final float SCROLL_PERCENTAGE = 0.3f;
    private final int MIN_FLING_VELOCITY = 1000;

    private OnPageChangeListener mOnPageChangeListener;
    private LinearLayout mRootContainer;
    private View[] mViews;
    private int mSinglePageHeight;
    private float mLastY;
    private boolean mMoved;
    private int mCurrentPage;
    private VelocityTracker mVelocityTracker;
    
    public MVerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MVerticalViewPager(Context context) {
        super(context);
        init();
    }
    
    public MVerticalViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    /**
     * Set views to be presented. Generally, a view represents a page which will occupy the whole screen.
     * @param views Page views to be presented
     */
    public void setViews(View[] views) {
        mViews = views;
        for (View view : mViews) {
            mRootContainer.addView(view);
        }
    }
    
    /**
     * {@link com.focosee.qingshow.widget.MVerticalViewPager.OnPageChangeListener#onPageSelected(int)} will be called whenever a different page is selected.
     * @param pageChangeListener
     */
    public void setOnPageChangeListener(OnPageChangeListener pageChangeListener) {
        this.mOnPageChangeListener = pageChangeListener;
    }

    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mSinglePageHeight = this.getMeasuredHeight();
        final LayoutParams lp = (LayoutParams) mRootContainer.getLayoutParams();
        int rootWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                0, lp.width);
        int rootHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mSinglePageHeight*mViews.length, MeasureSpec.EXACTLY);

        mRootContainer.measure(rootWidthMeasureSpec, rootHeightMeasureSpec);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mSinglePageHeight, MeasureSpec.EXACTLY);
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mRootContainer.getMeasuredWidth(), MeasureSpec.EXACTLY);
        for (View view : mViews) {
            view.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }
    
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        
        float currentY = event.getRawY();
        switch (event.getAction() & MotionEventCompat.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            mLastY = currentY;
            mMoved = false;
            break;
        case MotionEvent.ACTION_MOVE:
            mMoved = true;
            break;
        case MotionEvent.ACTION_UP:
            if (mMoved) {
                mVelocityTracker.computeCurrentVelocity(1000);
                int velocity = (int) mVelocityTracker.getYVelocity();
                float moveDistance = currentY - mLastY;
                int nextPage = mCurrentPage;
                if (moveDistance < 0 && Math.abs(moveDistance) >= SCROLL_PERCENTAGE * mSinglePageHeight
                        || velocity < 0 && Math.abs(velocity) > MIN_FLING_VELOCITY) {
                    ++nextPage;
                } else if (moveDistance > 0 && Math.abs(moveDistance) >= SCROLL_PERCENTAGE * mSinglePageHeight
                        || velocity > 0 && Math.abs(velocity) > MIN_FLING_VELOCITY) {
                    --nextPage;
                }
                setCurrentItem(nextPage);
            }
            break;
        }
        return true;
    }
    
    /**
     * Scroll to the specified page.
     * @param pageIndex
     */
    public void setCurrentItem(int pageIndex) {
        if (pageIndex >= 0 && pageIndex < mViews.length) {
            if (mCurrentPage != pageIndex && this.mOnPageChangeListener != null) {
                this.mOnPageChangeListener.onPageSelected(pageIndex);
            }
            mCurrentPage = pageIndex;
            this.smoothScrollTo(0, mCurrentPage*mSinglePageHeight);
        }
    }
    
    private void init() {
        mRootContainer = new LinearLayout(this.getContext());
        mRootContainer.setOrientation(LinearLayout.VERTICAL);
        this.addView(mRootContainer);
        this.setVerticalScrollBarEnabled(false);
        mViews = new View[0];
    }
    
    public static interface OnPageChangeListener {

        public void onPageSelected(int position);
    }

}
