package com.focosee.qingshow.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ListView;
import com.huewu.pla.lib.internal.PLA_AbsListView;

public class MPullRefreshListView extends PullToRefreshBase<ListView> implements AbsListView.OnScrollListener{

    /**ListView*/
    private ListView mListView;
    /**用于滑到底部自动加载的Footer*/
    private FooterLoadingLayout mLoadMoreFooterLayout;
    /**滚动的监听器*/
    private AbsListView.OnScrollListener mScrollListener;


    /**
     * 构造方法
     *
     * @param context context
     */
    public MPullRefreshListView(Context context) {
        this(context, null);
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs attrs
     */
    public MPullRefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs attrs
     * @param defStyle defStyle
     */
    public MPullRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mLoadMoreFooterLayout = new FooterLoadingLayout(context);
        mLoadMoreFooterLayout.setState(ILoadingLayout.State.RESET);
        mListView.addFooterView(mLoadMoreFooterLayout,null,false);
        mLoadMoreFooterLayout.show(false);
        setPullLoadEnabled(false);
    }

    @Override
    protected ListView createRefreshableView(Context context, AttributeSet attrs) {
        ListView listView = new ListView(context);
        mListView = listView;
        listView.setOnScrollListener(this);

        return listView;
    }

    /**
     * 设置是否有更多数据的标志
     *
     * @param hasMoreData true表示还有更多的数据，false表示没有更多数据了
     */
    public void setHasMoreData(boolean hasMoreData) {
        Log.i("tag","getParent" + mListView.getFooterViewsCount());

        if (!hasMoreData) {
            if (0 == mListView.getFooterViewsCount()) {
                mLoadMoreFooterLayout.setState(ILoadingLayout.State.NO_MORE_DATA);
                mLoadMoreFooterLayout.show(true);
            }
            LoadingLayout footerLoadingLayout = getFooterLoadingLayout();
            if (null != footerLoadingLayout) {
                footerLoadingLayout.setState(ILoadingLayout.State.NO_MORE_DATA);
            }
        }else{
            if(mListView == mLoadMoreFooterLayout.getParent()){
                mLoadMoreFooterLayout.show(false);
            }
        }
    }

    /**
     * 设置滑动的监听器
     *
     * @param l 监听器
     */
    public void setOnScrollListener(AbsListView.OnScrollListener l) {
        mScrollListener = l;
    }

    @Override
    protected boolean isReadyForPullUp() {
        return isLastItemVisible();
    }

    @Override
    protected boolean isReadyForPullDown() {
        return isFirstItemVisible();
    }

    @Override
    protected void startLoading() {
        super.startLoading();
    }

    @Override
    public void onPullUpRefreshComplete() {
        super.onPullUpRefreshComplete();
    }

    @Override
    public void setScrollLoadEnabled(boolean scrollLoadEnabled) {
        super.setScrollLoadEnabled(scrollLoadEnabled);
    }

    @Override
    public LoadingLayout getFooterLoadingLayout() {
        return super.getFooterLoadingLayout();
    }

    @Override
    protected LoadingLayout createHeaderLoadingLayout(Context context, AttributeSet attrs) {
        return new RotateLoadingLayout(context);
    }

    /**
     * 表示是否还有更多数据
     *
     * @return true表示还有更多数据
     */
    private boolean hasMoreData() {
        if ((null != getFooterLoadingLayout()) && (getFooterLoadingLayout().getState() == ILoadingLayout.State.NO_MORE_DATA)) {
            return false;
        }

        return true;
    }

    /**
     * 判断第一个child是否完全显示出来
     *
     * @return true完全显示出来，否则false
     */
    private boolean isFirstItemVisible() {
        final Adapter adapter = mListView.getAdapter();

        if (null == adapter || adapter.isEmpty()) {
            return true;
        }

        int mostTop = (mListView.getChildCount() > 0) ? mListView.getChildAt(0).getTop() : 0;
        if (mostTop >= 0) {
            return true;
        }

        return false;
    }

    /**
     * 判断最后一个child是否完全显示出来
     *
     * @return true完全显示出来，否则false
     */
    private boolean isLastItemVisible() {
        final Adapter adapter = mListView.getAdapter();

        if (null == adapter || adapter.isEmpty()) {
            return true;
        }

        final int lastItemPosition = adapter.getCount() - 1;
        final int lastVisiblePosition = mListView.getLastVisiblePosition();

        /**
         * This check should really just be: lastVisiblePosition == lastItemPosition, but ListView
         * internally uses a FooterView which messes the positions up. For me we'll just subtract
         * one to account for it and rely on the inner condition which checks getBottom().
         */
        if (lastVisiblePosition >= lastItemPosition - 1) {
            final int childIndex = lastVisiblePosition - mListView.getFirstVisiblePosition();
            final int childCount = mListView.getChildCount();
            final int index = Math.min(childIndex, childCount - 1);
            final View lastVisibleChild = mListView.getChildAt(index);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= mListView.getBottom();
            }
        }

        return false;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (isScrollLoadEnabled() && hasMoreData()) {
            if (scrollState == PLA_AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                    || scrollState == PLA_AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                if (isReadyForPullUp()) {
                    startLoading();
                }
            }
        }

        if (null != mScrollListener) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (null != mScrollListener) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }
}