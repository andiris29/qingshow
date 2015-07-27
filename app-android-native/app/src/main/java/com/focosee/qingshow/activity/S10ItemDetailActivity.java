package com.focosee.qingshow.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.widget.MViewPager_NoScroll;
import com.focosee.qingshow.widget.indicator.PageIndicator;

import java.util.LinkedList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/3/13.
 */
public class S10ItemDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String INPUT_ITEM_ENTITY = "INPUT_ITEM_ENTITY";
    @InjectView(R.id.s10_item_viewpager)
    MViewPager_NoScroll viewPager;
    @InjectView(R.id.s10_back_btn)
    ImageButton s10BackBtn;
    @InjectView(R.id.s10_page_indicator)
    PageIndicator pageIndicator;
    @InjectView(R.id.s10_item_description)
    TextView description;
    @InjectView(R.id.s10_item_price)
    TextView price;
    @InjectView(R.id.s10_bay)
    Button s10Bay;
    @InjectView(R.id.s10_item_img)
    SimpleDraweeView s10ItemImg;


    private MongoItem itemEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s10_item_detail);
        ButterKnife.inject(this);
        itemEntity = (MongoItem) getIntent().getExtras().getSerializable(INPUT_ITEM_ENTITY);
        Log.i("tag", itemEntity._id);
        init();
    }

    private void init() {

        if (itemEntity.images.size() == 1) {
            s10ItemImg.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
            pageIndicator.setIndex(1);
            pageIndicator.setCount(1);
            s10ItemImg.setImageURI(Uri.parse(itemEntity.images.get(0).url));
            s10ItemImg.setAspectRatio(0.56f);
        }else {
            ItemImgViewPagerAdapter viewPagerAdapter = new ItemImgViewPagerAdapter(itemEntity.images, this);
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.addOnPageChangeListener(viewPagerAdapter);
            viewPager.setOffscreenPageLimit(1);
            viewPager.setCurrentItem(itemEntity.images.size() * 3);
            pageIndicator.setCount(itemEntity.images.size());
        }
        description.setText(itemEntity.name);
//        price.setText(itemEntity.price);
    }


    @Override
    public void reconn() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.s10_bay:
                if (!QSModel.INSTANCE.loggedin()) {
                    Toast.makeText(S10ItemDetailActivity.this, R.string.need_login, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(S10ItemDetailActivity.this, U07RegisterActivity.class));
                    return;
                }
                Intent intent = new Intent(S10ItemDetailActivity.this, S11NewTradeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(S11NewTradeActivity.INPUT_ITEM_ENTITY, itemEntity);
                intent.putExtras(bundle);
                Log.i("tag", itemEntity._id);
                startActivity(intent);
                break;
            case R.id.s10_back_btn:
                finish();
                break;
        }
    }

    private class ItemImgViewPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

        private LinkedList<MongoItem.Image> images;
        private SimpleDraweeView[] _mImgViewS;
        private int imgSize;
        private Context context;

        public ItemImgViewPagerAdapter(LinkedList<MongoItem.Image> images, Context context) {
            this.images = images;
            this.imgSize = images.size() == 0 ? 1 : images.size();
            this.context = context;
            initImageViewList();
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

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            SimpleDraweeView imageView = _mImgViewS[position % this.imgSize];
            container.addView(imageView, 0);
            return imageView;
        }


        private void initImageViewList() {
            _mImgViewS = new SimpleDraweeView[this.imgSize];
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);

            int index = 0;


            for (MongoItem.Image imgInfo : images) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.root_cell_placehold_image1);
                BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
                GenericDraweeHierarchyBuilder builder =
                        new GenericDraweeHierarchyBuilder(getResources());
                GenericDraweeHierarchy hierarchy = builder
                        .setPlaceholderImage(drawable)
                        .build();

                SimpleDraweeView imageView = new SimpleDraweeView(context);
                imageView.setLayoutParams(params);
                imageView.setTag(imgInfo);
                imageView.setPadding(0, -16, 0, 0);
                imageView.setImageURI(Uri.parse(imgInfo.url));
                imageView.setAspectRatio(0.56f);
                imageView.setHierarchy(hierarchy);
                _mImgViewS[index] = imageView;
                index++;
            }


        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            pageIndicator.setIndex(position % this.imgSize + 1);
            description.setText(((MongoItem.Image) _mImgViewS[position % this.imgSize].getTag()).description);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
