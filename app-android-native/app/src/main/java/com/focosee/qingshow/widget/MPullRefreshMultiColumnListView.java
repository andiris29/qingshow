package com.focosee.qingshow.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;

import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AbsListView;

public class MPullRefreshMultiColumnListView extends PullToRefreshBase<MultiColumnListView> implements PLA_AbsListView.OnScrollListener {

    /**ListView*/
    private MultiColumnListView mListView;
    /**用于滑到底部自动加载的Footer*/
    private LoadingLayout mLoadMoreFooterLayout;
    /**滚动的监听器*/
    private PLA_AbsListView.OnScrollListener mScrollListener;

    /**
     * 构造方法
     *
     * @param context context
     */
    public MPullRefreshMultiColumnListView(Context context) {
        this(context, null);
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs attrs
     */
    public MPullRefreshMultiColumnListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs attrs
     * @param defStyle defStyle
     */
    public MPullRefreshMultiColumnListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setPullLoadEnabled(false);
    }

    @Override
    protected MultiColumnListView createRefreshableView(Context context, AttributeSet attrs) {
        MultiColumnListView listView = new MultiColumnListView(context);
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
        if (!hasMoreData) {
            if (null != mLoadMoreFooterLayout) {
                mLoadMoreFooterLayout.setState(ILoadingLayout.State.NO_MORE_DATA);
            }

            LoadingLayout footerLoadingLayout = getFooterLoadingLayout();
            if (null != footerLoadingLayout) {
                footerLoadingLayout.setState(ILoadingLayout.State.NO_MORE_DATA);
            }
        }
    }

    /**
     * 设置滑动的监听器
     *
     * @param l 监听器
     */
    public void setOnScrollListener(PLA_AbsListView.OnScrollListener l) {
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

        if (null != mLoadMoreFooterLayout) {
            mLoadMoreFooterLayout.setState(ILoadingLayout.State.REFRESHING);
        }
    }

    @Override
    public void onPullUpRefreshComplete() {
        super.onPullUpRefreshComplete();

        if (null != mLoadMoreFooterLayout) {
            mLoadMoreFooterLayout.setState(ILoadingLayout.State.RESET);
        }
    }

    @Override
    public void setScrollLoadEnabled(boolean scrollLoadEnabled) {
        super.setScrollLoadEnabled(scrollLoadEnabled);

        if (scrollLoadEnabled) {
            // 设置Footer
            if (null == mLoadMoreFooterLayout) {
                mLoadMoreFooterLayout = new FooterLoadingLayout(getContext());
            }

            if (null == mLoadMoreFooterLayout.getParent()) {
                mListView.addFooterView(mLoadMoreFooterLayout, null, false);
            }
            mLoadMoreFooterLayout.show(true);
        } else {
            if (null != mLoadMoreFooterLayout) {
                mLoadMoreFooterLayout.show(false);
            }
        }
    }

    @Override
    public LoadingLayout getFooterLoadingLayout() {
        if (isScrollLoadEnabled()) {
            return mLoadMoreFooterLayout;
        }

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
        if ((null != mLoadMoreFooterLayout) && (mLoadMoreFooterLayout.getState() == ILoadingLayout.State.NO_MORE_DATA)) {
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



//    @Override
//    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        if (isScrollLoadEnabled() && hasMoreData()) {
//            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
//                    || scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
//                if (isReadyForPullUp()) {
//                    startLoading();
//                }
//            }
//        }
//
//        if (null != mScrollListener) {
//            mScrollListener.onScrollStateChanged(view, scrollState);
//        }
//    }
//
//    @Override
//    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//        if (null != mScrollListener) {
//            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
//        }
//    }

    @Override
    public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {
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
    public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (null != mScrollListener) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }
}
