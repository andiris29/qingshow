package com.focosee.qingshow.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.widget.MImageView_OriginSize;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedList;

/**
 * Created by Administrator on 2015/3/13.
 */
public class S10ItemDetailActivity extends BaseActivity {

    public static final String INPUT_ITEM_ENTITY = "INPUT_ITEM_ENTITY";

    private ViewPager viewPager;
    private TextView discount;
    private TextView description;
    private TextView price;
    private TextView sourcePrice;
    private ImageView watch;
    private LinearLayout group;

    private MongoItem itemEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s10_item_detail);
        itemEntity = (MongoItem) getIntent().getExtras().getSerializable(INPUT_ITEM_ENTITY);
        init();
    }

    private void init() {
        viewPager = (ViewPager) findViewById(R.id.s10_item_viewpager);
        discount = (TextView) findViewById(R.id.s10_item_description);
        price = (TextView) findViewById(R.id.s10_item_price);
        sourcePrice = (TextView) findViewById(R.id.s10_item_source_price);
        watch = (ImageView) findViewById(R.id.s10_watch);
        group = (LinearLayout) findViewById(R.id.s10_item_viewGroup);

        viewPager.setAdapter(new ItemImgViewPagerAdapter(itemEntity.images,this));

        findViewById(R.id.s10_bay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("tag",itemEntity._id + "");
                Intent intent = new Intent(S10ItemDetailActivity.this, S11NewTradeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(S11NewTradeActivity.INPUT_ITEM_ENTITY,itemEntity);
                intent.putExtras(bundle);
                S10ItemDetailActivity.this.startActivity(intent);
            }
        });

        findViewById(R.id.s10_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    @Override
    public void reconn() {

    }

    private class ItemImgViewPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

        private LinkedList<MongoItem.Image> images;
        private ImageView[] _mImgViewS;
        private int imgSize;
        private Context context;
        /**
         * 装点点的ImageView数组
         */
        private ImageView[] tips;

        public ItemImgViewPagerAdapter(LinkedList<MongoItem.Image> images, Context context) {
            this.images = images;
            this.imgSize = images.size() == 0 ? 1 : images.size();
            this.context = context;
            initImageViewList();
            initTips();
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(_mImgViewS[position % this.imgSize]);
        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView imageView = _mImgViewS[position % this.imgSize];
            MongoItem.Image imgInfo = (MongoItem.Image) imageView.getTag();

            ImageLoader.getInstance().displayImage(imgInfo.url, imageView, AppUtil.getShowDisplayOptions());
            container.addView(imageView, 0);

            return imageView;
        }

        private void initTips() {
            tips = new ImageView[this.imgSize];
            group.removeAllViews();
            for (int i = 0; i < tips.length; i++) {
                ImageView imageView_tips = new ImageView(context);
                imageView_tips.setLayoutParams(new LinearLayout.LayoutParams(10, 10));
                tips[i] = imageView_tips;
                if (i == 0) {
                    tips[i].setBackgroundResource(R.drawable.image_indicator_focus);
                } else {
                    tips[i].setBackgroundResource(R.drawable.image_indicator);
                }

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                layoutParams.leftMargin = 5;
                layoutParams.rightMargin = 5;
                group.addView(imageView_tips, layoutParams);
            }
        }

        private void initImageViewList() {
            _mImgViewS = new ImageView[this.imgSize];
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);

            int index = 0;

            for (MongoItem.Image imgInfo : images) {

                MImageView_OriginSize imageView = new MImageView_OriginSize(context);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setTag(imgInfo);
                _mImgViewS[index] = imageView;
                index++;
            }


        }

        /**
         * 设置选中的tip的背景
         *
         * @param selectItems
         */
        private void setImageBackground(int selectItems) {


            for (int i = 0; i < tips.length; i++) {
                if (i == (selectItems % this.imgSize)) {
                    tips[i].setBackgroundResource(R.drawable.image_indicator_focus);
                } else {
                    tips[i].setBackgroundResource(R.drawable.image_indicator);
                }
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setImageBackground(position);
            //this.viewHolder.detailButton.setTag(position % this.imgSize);
            description.setText(((MongoItem.Image) _mImgViewS[position % this.imgSize].getTag()).description);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
