package com.focosee.qingshow.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

/**
 * Created by Administrator on 2015/4/24.
 */
public class RecyclerPullToRefreshView extends PullToRefreshBase<RecyclerView>{

    private RecyclerView recyclerView;

    private FooterLoadingLayout mLoadMoreFooterLayout;
    private AbsListView.OnScrollListener mScrollListener;

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

        final RecyclerView.Adapter adapter = recyclerView.getAdapter();

        if (null == adapter) {
            return true;
        }

        LinearLayoutManager layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        final int lastItemPosition = adapter.getItemCount() - 1;
        final int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

        /**
         * This check should really just be: lastVisiblePosition == lastItemPosition, but ListView
         * internally uses a FooterView which messes the positions up. For me we'll just subtract
         * one to account for it and rely on the inner condition which checks getBottom().
         */
        if (lastVisiblePosition >= lastItemPosition - 1) {
            final int childIndex = lastVisiblePosition - layoutManager.findFirstVisibleItemPosition();
            final int childCount = recyclerView.getChildCount();
            final int index = Math.min(childIndex, childCount - 1);
            final View lastVisibleChild = recyclerView.getChildAt(index);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= recyclerView.getBottom();
            }
        }
        return false;
    }

    /**
     * 设置是否有更多数据的标志
     *
     * @param hasMoreData true表示还有更多的数据，false表示没有更多数据了
     */
    public void setHasMoreData(boolean hasMoreData) {
        if (!hasMoreData) {
            getFooterLoadingLayout().setState(ILoadingLayout.State.NO_MORE_DATA);
            getFooterLoadingLayout().show(true);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    onPullUpRefreshComplete();
                }
            }, 500);
        }else{
            if(recyclerView == getFooterLoadingLayout().getParent()){
                getFooterLoadingLayout().show(false);
            }
        }
    }

    private boolean isLastItemVisible() {
        return false;
    }

//    /**
//     * 璁剧疆鏄惁鏈夋洿澶氭暟鎹殑鏍囧織
//     *
//     * @param hasMoreData true琛ㄧず杩樻湁鏇村鐨勬暟鎹紝false琛ㄧず娌℃湁鏇村鏁版嵁浜�
//     */
//    public void setHasMoreData(boolean hasMoreData) {
//        Log.i("tag", "getParent" + mListView.getFooterViewsCount());
//
//        if (!hasMoreData) {
//            if (0 == mListView.getFooterViewsCount()) {
//                mLoadMoreFooterLayout.setState(ILoadingLayout.State.NO_MORE_DATA);
//                mLoadMoreFooterLayout.show(true);
//            }
//            LoadingLayout footerLoadingLayout = getFooterLoadingLayout();
//            if (null != footerLoadingLayout) {
//                footerLoadingLayout.setState(ILoadingLayout.State.NO_MORE_DATA);
//            }
//        }else{
//            if(mListView == mLoadMoreFooterLayout.getParent()){
//                mLoadMoreFooterLayout.show(false);
//            }
//        }
//    }

}
