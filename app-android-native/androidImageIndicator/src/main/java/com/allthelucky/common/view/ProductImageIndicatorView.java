package com.allthelucky.common.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.library.common.view.R;

import java.util.ArrayList;
import java.util.List;

public class ProductImageIndicatorView  extends RelativeLayout {
    /**
     * ViewPager控件
     */
    protected ViewPager viewPager;
    /**
     * 指示器容器
     */
    protected LinearLayout indicateLayout;
    /**
     * 物品描述
     */
    private TextView descriptionTV;
    /**
     * 品牌按钮
     */
    private Button brandButton;
    /**
     * 购买按钮
     */
    private Button buyButton;
    /**
     * 关闭按钮
     */
    private Button closeButton;
    /**
     * 页面列表
     */
    private List<View> viewList = new ArrayList<View>();
    /**
     * 描述数据列表
     */
    private List<String> descriptionList = new ArrayList<String>();
    /**
     * 品牌列表
     */
    private List<String> brandList = new ArrayList<String>();

    protected Handler refreshHandler;

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

    public ProductImageIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public ProductImageIndicatorView(Context context) {
        super(context);
        this.init(context);
    }

    /**
     * @param context
     */
    public void init(final Context context) {
        LayoutInflater.from(context).inflate(R.layout.product_image_indicator_layout, this);
        this.viewPager = (ViewPager) findViewById(R.id.product_view_pager);
        this.indicateLayout = (LinearLayout) findViewById(R.id.product_indicater_layout);

        this.descriptionTV = (TextView) findViewById(R.id.product_description);
        this.brandButton = (Button) findViewById(R.id.product_brand);
        this.buyButton = (Button) findViewById(R.id.product_buy);
        this.closeButton = (Button) findViewById(R.id.product_icon_close);

        this.viewPager.setOnPageChangeListener(new PageChangeListener());

        this.refreshHandler = new ProductScrollIndicateHandler(ProductImageIndicatorView.this);
    }

    /**
     * 取ViewPager实例
     *
     * @return
     */
    public ViewPager getViewPager() {
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
     * 条目点击事件监听类
     */
    private class ItemClickListener implements View.OnClickListener {
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
     * 设置当前显示项
     *
     * @param index
     *            postion
     */
    public void setCurrentItem(int index) {
        this.currentIndex = index;
    }

    /**
     * 设置指示器样式，默认为INDICATOR_ARROW_ROUND_STYLE
     *
     * @param style
     *            INDICATOR_USERGUIDE_STYLE或INDICATOR_ARROW_ROUND_STYLE
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
        final LayoutParams params = (LayoutParams) indicateLayout.getLayoutParams();
        if (INDICATE_USERGUIDE_STYLE == this.indicatorStyle) {// 操作指引
            params.bottomMargin = 45;
        }
        this.indicateLayout.setLayoutParams(params);
        // 初始化指示器
        for (int index = 0; index < this.totelCount; index++) {
            final View indicater = new ImageView(getContext());
            this.indicateLayout.addView(indicater, index);
        }
        this.refreshHandler.sendEmptyMessage(currentIndex);
        // 为ViewPager配置数据
        this.viewPager.setAdapter(new MyPagerAdapter(this.viewList));
        this.viewPager.setCurrentItem(currentIndex, false);
    }

    /**
     * 页面变更监听
     */
    protected class PageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int index) {
            currentIndex = index;
            refreshHandler.sendEmptyMessage(index);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    }

    protected void setBrandList(List<String> mBrandList) {
        this.brandList = mBrandList;
    }

    protected void setDescriptionList(List<String> mDescriptionList) {
        this.descriptionList = mDescriptionList;
    }

    /**
     * 刷新提示器
     */
    protected void refreshIndicateView() {
        this.refreshTime = System.currentTimeMillis();

        for (int index = 0; index < totelCount; index++) {
            final ImageView imageView = (ImageView) this.indicateLayout.getChildAt(index);
            if (this.currentIndex == index) {
                imageView.setBackgroundResource(R.drawable.image_indicator_focus);
            } else {
                imageView.setBackgroundResource(R.drawable.image_indicator);
            }
        }

        descriptionTV.setText(descriptionList.get(this.currentIndex));
        brandButton.setText(brandList.get(this.currentIndex));

        if (this.onItemChangeListener != null) {// 页面改更了
            try {
                this.onItemChangeListener.onPosition(this.currentIndex, this.totelCount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取品牌按钮
     */
    public Button getBrandButton() {
        return this.brandButton;
    }

    /**
     * 获取购买按钮
     */
    public Button getBuyButton() {
        return this.buyButton;
    }

    /**
     * 获取关闭按钮
     */
    public Button getCloseButton() {
        return this.closeButton;
    }

    public class MyPagerAdapter extends PagerAdapter {
        private List<View> pageViews = new ArrayList<View>();

        public MyPagerAdapter(List<View> pageViews) {
            this.pageViews = pageViews;
        }

        @Override
        public int getCount() {
            return pageViews.size();
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
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(pageViews.get(arg1));
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(pageViews.get(arg1));
            return pageViews.get(arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

        @Override
        public void finishUpdate(View arg0) {

        }
    }

}

class ProductScrollIndicateHandler extends Handler {
    private ProductImageIndicatorView scrollIndicateView;

    public ProductScrollIndicateHandler(ProductImageIndicatorView scrollIndicateView) {
        this.scrollIndicateView = scrollIndicateView;

    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (this.scrollIndicateView != null) {
            scrollIndicateView.refreshIndicateView();
        }
    }
}
