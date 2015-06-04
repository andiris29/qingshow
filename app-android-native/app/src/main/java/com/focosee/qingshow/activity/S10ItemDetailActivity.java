package com.focosee.qingshow.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.widget.MImageView_OriginSize;
import com.focosee.qingshow.widget.indicator.PageIndicator;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedList;

/**
 * Created by Administrator on 2015/3/13.
 */
public class S10ItemDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String INPUT_ITEM_ENTITY = "INPUT_ITEM_ENTITY";


    private ViewPager viewPager;
    private TextView description;
    private TextView price;
    private PageIndicator pageIndicator;

    private RelativeLayout videoLayout;
    private RelativeLayout info;
    private LinearLayout infoButton;
    private VideoView videoView;

    private MongoItem itemEntity;

    private boolean isFirstPlay = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s10_item_detail);
        itemEntity = (MongoItem) getIntent().getExtras().getSerializable(INPUT_ITEM_ENTITY);
        Log.i("tag",itemEntity._id);
        init();
    }

    private void init() {
        videoLayout = (RelativeLayout) findViewById(R.id.s10_video);
        infoButton = (LinearLayout) findViewById(R.id.s10_info_button);
        info = (RelativeLayout) findViewById(R.id.s10_info);
        viewPager = (ViewPager) findViewById(R.id.s10_item_viewpager);
        description = (TextView) findViewById(R.id.s10_item_description);
        price = (TextView) findViewById(R.id.s10_item_price);
        pageIndicator = (PageIndicator) findViewById(R.id.s10_page_indicator);
        videoView = (VideoView) findViewById(R.id.s10_video_view);

        ItemImgViewPagerAdapter viewPagerAdapter = new ItemImgViewPagerAdapter(itemEntity.images, this);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(itemEntity.images.size() * 3);
        pageIndicator.setCount(itemEntity.images.size());

        description.setText(itemEntity.name);
        price.setText(itemEntity.getPrice());
        configVideo();
    }


    @Override
    public void reconn() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.s10_bay:
                if (! QSModel.INSTANCE.loggedin()) {
                    Toast.makeText(S10ItemDetailActivity.this, R.string.need_login, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(S10ItemDetailActivity.this, U07RegisterActivity.class));
                    return;
                }
                Intent intent = new Intent(S10ItemDetailActivity.this, S11NewTradeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(S11NewTradeActivity.INPUT_ITEM_ENTITY, itemEntity);
                intent.putExtras(bundle);
                Log.i("tag",itemEntity._id);
                S10ItemDetailActivity.this.startActivity(intent);
                break;
            case R.id.s10_back_btn:
                finish();
                break;
//            case R.id.s10_watch:
//                startVideo();
//                break;
            case R.id.s10_video_play:
                if(videoView.isPlaying()){
                    videoView.pause();
                    v.setBackgroundResource(R.drawable.s03_play_btn);
                }else {
                    videoView.start();
                    v.setBackgroundResource(R.drawable.s03_pause_btn);
                }
                break;
            case R.id.s10_video_back_btn:
                videoView.pause();
                videoLayout.setVisibility(View.GONE);
                info.setVisibility(View.VISIBLE);
                infoButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void configVideo(){
        if (TextUtils.isEmpty(itemEntity.video)) {
            return;
        }
        videoView.setVideoPath(itemEntity.video);
        videoView.requestFocus();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isFirstPlay){
                    videoView.seekTo(1);
                    isFirstPlay = false;
                }
            }
        }).start();
    }

    private void startVideo(){
        if (TextUtils.isEmpty(itemEntity.video)) {
            return;
        }
        configVideo();
        info.setVisibility(View.GONE);
        infoButton.setVisibility(View.GONE);
        videoLayout.setVisibility(View.VISIBLE);

    }

    private class ItemImgViewPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

        private LinkedList<MongoItem.Image> images;
        private ImageView[] _mImgViewS;
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

            ImageView imageView = _mImgViewS[position % this.imgSize];
            MongoItem.Image imgInfo = (MongoItem.Image) imageView.getTag();

            ImageLoader.getInstance().displayImage(imgInfo.url, imageView, AppUtil.getShowDisplayOptions());
            container.addView(imageView, 0);

            return imageView;
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
                imageView.setPadding(0, -16, 0, 0);
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
