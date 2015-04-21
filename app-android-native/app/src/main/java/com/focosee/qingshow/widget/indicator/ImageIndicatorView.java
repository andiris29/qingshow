package com.focosee.qingshow.widget.indicator;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.focosee.qingshow.R;
import com.focosee.qingshow.widget.CtrlScrollViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用户指引,宣传画控件(类似于Gallery效果)
 *
 * @author savant-pan
 */
public class ImageIndicatorView extends RelativeLayout {
    /**
     * ViewPager控件
     */
    protected CtrlScrollViewPager viewPager;

    /**
     * 指示器容器
     */
    protected PageIndicator indicateLayout;
    /**
     * 页面列表
     */
    private List<View> viewList = new ArrayList<View>();


    /**
     * 滑动位置通知回调监听对象
     */
    private OnItemChangeListener onItemChangeListener;

    /**
     * 单个界面点击回调监听对象
     */
    private OnItemClickListener onItemClickListener;
    /**
     * 总页面条数
     */
    private int totelCount = 0;
    /**
     * 当前页索引
     */
    private int currentIndex = 0;

    /**
     * 圆形列表+箭头提示器
     */
    public static final int INDICATE_ARROW_ROUND_STYLE = 0;

    /**
     * 操作导引提示器
     */
    public static final int INDICATE_USERGUIDE_STYLE = 1;

    /**
     * INDICATOR样式
     */
    private int indicatorStyle = INDICATE_ARROW_ROUND_STYLE;

    /**
     * 最近一次划动时间
     */
    private long refreshTime = 0l;

    /**
     * 广告位置监听接口
     */
    public interface OnItemChangeListener {
        void onPosition(int position, int totalCount);
    }

    /**
     * 条目点击事件监听接口
     */
    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }

    public ImageIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public ImageIndicatorView(Context context) {
        super(context);
        this.init(context);
    }

    /**
     * @param context
     */
    public void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.image_indicator_layout, this);
        this.viewPager = (CtrlScrollViewPager) findViewById(R.id.view_pager);
        this.indicateLayout = (PageIndicator) findViewById(R.id.indicater_layout);

        this.viewPager.setOnPageChangeListener(new PageChangeListener());

    }

    public LinearLayout getIndicateLayout() {
        return indicateLayout;
    }

    /**
     * 取ViewPager实例
     *
     * @return
     */
    public CtrlScrollViewPager getViewPager() {
        return viewPager;
    }

    /**
     * 取当前位置Index值
     */
    public int getCurrentIndex() {
        return this.currentIndex;
    }

    /**
     * 取总VIEW数目
     */
    public int getTotalCount() {
        return this.totelCount;
    }

    /**
     * 取最近一次刷新时间
     */
    public long getRefreshTime() {
        return this.refreshTime;
    }

    /**
     * 添加单个View
     *
     * @param view
     */
    public void addViewItem(View view) {
        final int position = viewList.size();
        view.setOnClickListener(new ItemClickListener(position));
        this.viewList.add(view);
    }

    /**
     * 添加单个View
     *
     * @param view
     */
    public void addViewItemAtIndex(View view, int index) {
        final int position = viewList.size();
        view.setOnClickListener(new ItemClickListener(index));
        for (int i = 0; i < this.viewList.size(); i++) {
            if (i >= index) {
                this.viewList.get(i).setOnClickListener(new ItemClickListener(i + 1));
            }
        }
        this.viewList.add(index, view);
    }

    public void removeViewItemAtIndex(int index) {
        for (int i = 0; i < this.viewList.size(); i++) {
            if (i > index) {
                this.viewList.get(i).setOnClickListener(new ItemClickListener(i - 1));
            }
        }
        this.viewList.remove(index);
    }

    /**
     * 条目点击事件监听类
     */
    private class ItemClickListener implements OnClickListener {
        private int position = 0;

        public ItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.OnItemClick(view, position);
            }
        }
    }

    /**
     * 设置显示图片Drawable数组
     *
     * @param resArray Drawable数组
     */
    public void setupLayoutByDrawable(final Integer resArray[]) {
        if (resArray == null)
            throw new NullPointerException();

        this.setupLayoutByDrawable(Arrays.asList(resArray));
    }

    /**
     * 设置显示图片Drawable列表
     *
     * @param resList Drawable列表
     */
    public void setupLayoutByDrawable(final List<Integer> resList) {
        if (resList == null)
            throw new NullPointerException();

        final int len = resList.size();
        if (len > 0) {
            for (int index = 0; index < len; index++) {
                final View pageItem = new ImageView(getContext());
                ((ImageView) pageItem).setAdjustViewBounds(true);
                pageItem.setBackgroundResource(resList.get(index));
                addViewItem(pageItem);
            }
        }
    }

    /**
     * 设置当前显示项
     *
     * @param index postion
     */
    public void setCurrentItem(int index) {
        this.currentIndex = index;
    }

    /**
     * 设置指示器样式，默认为INDICATOR_ARROW_ROUND_STYLE
     *
     * @param style INDICATOR_USERGUIDE_STYLE或INDICATOR_ARROW_ROUND_STYLE
     */
    public void setIndicateStyle(int style) {
        this.indicatorStyle = style;
    }

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
     * 设置条目点击监听对象
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 显示
     */
    public void show() {
        this.totelCount = viewList.size();
        this.indicateLayout.setCount(totelCount);
        this.indicateLayout.setIndex(1);
        this.viewPager.setAdapter(new MyPagerAdapter(this.viewList));
        this.viewPager.setCurrentItem(0, false);
        this.viewPager.setOffscreenPageLimit(5);
    }


    /**
     * 页面变更监听
     */
    protected class PageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageSelected(int index) {
            currentIndex = index;
            indicateLayout.setIndex(currentIndex + 1);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * 刷新提示器
     */

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    public class MyPagerAdapter extends PagerAdapter {
        private List<View> pageViews = new ArrayList<View>();
        private int itemSize = 1;

        public MyPagerAdapter(List<View> pageViews) {

            this.pageViews = pageViews;
            if (null != pageViews && 0 != pageViews.size()) {
                this.itemSize = pageViews.size();
            }
        }

        @Override
        public int getCount() {
            return itemSize;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(pageViews.get(position));
            return pageViews.get(position);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

    }

}
