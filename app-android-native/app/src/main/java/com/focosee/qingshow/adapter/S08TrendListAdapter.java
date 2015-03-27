package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S04CommentActivity;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.response.error.ErrorCode;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPreview;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.indicator.PageIndicator;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class S08TrendListAdapter extends BaseAdapter {


    private final String TAG = "com.focosee.qingshow.adapter.S08TrendListAdapter";

    private Point itemSize;

    private int itemHeight;

    private Context context;
    private List<MongoPreview> data;
    private ImageLoader imageLoader;

    public S08TrendListAdapter(Context context, LinkedList<MongoPreview> trendEntities, Point screenSize, ImageLoader imageLoader) {
        this.context = context;
        this.data = trendEntities;
        this.imageLoader = imageLoader;
        this.itemSize = screenSize;
    }

    public List<MongoPreview> getData(){
        return data;
    }
    @Override
    public int getCount() {

        return (null != this.data) ? this.data.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return (null != this.data) ? this.data.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void resetData(LinkedList<MongoPreview> datas) {
        this.data = datas;
    }

    public void addItemLast(LinkedList<MongoPreview> datas) {
        this.data.addAll(datas);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        RelativeLayout.LayoutParams params_rLayout;

        final HolderView holderView;

        if (null == convertView) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.item_s08_trend_list, null);

            holderView = new HolderView();
            holderView.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.s08_relative_layout);

            holderView.viewPager = (ViewPager) convertView.findViewById(R.id.s08_viewPager);
            holderView.pageIndicator = (PageIndicator) convertView.findViewById(R.id.s08_viewGroup);
            holderView.descriptionTextView = (TextView) convertView.findViewById(R.id.S08_item_description);

            holderView.shareImageButton = (ImageButton) convertView.findViewById(R.id.S08_item_share_btn);
            holderView.likeImageButton = (ImageButton) convertView.findViewById(R.id.S08_item_like_btn);
            holderView.likeTextView = (TextView) convertView.findViewById(R.id.S08_item_like_text_view);
            holderView.messageImageButton = (ImageButton) convertView.findViewById(R.id.S08_item_comment_btn);
            holderView.messageTextView = (TextView) convertView.findViewById(R.id.S08_item_comment_text_view);

            if(null != data){
                if(null != data.get(position).imageMetadata){
                    this.itemHeight = this.itemSize.x * data.get(position).imageMetadata.height / data.get(position).imageMetadata.width;
                }
            }

            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        int item_width = holderView.viewPager.getLayoutParams().width;

        //如何图片高度小于最小高度，则设为最小高度
        params_rLayout = new RelativeLayout.LayoutParams(item_width
                , this.itemHeight);
        convertView.setLayoutParams(new AbsListView.LayoutParams(item_width,
                this.itemHeight));

        holderView.pageIndicator.setCount(data.get(position).images.size());
        holderView.pageIndicator.setIndex(1);
        //设置Adapter
        MViewPagerAdapter mViewPagerAdapter = new MViewPagerAdapter(position, holderView);
        holderView.viewPager.setAdapter(mViewPagerAdapter);
        //设置监听，主要是设置点点的背景
        holderView.viewPager.setOnPageChangeListener(mViewPagerAdapter);
        //设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
        holderView.viewPager.setOffscreenPageLimit(1);
        holderView.viewPager.setCurrentItem(data.get(position).images.size() * 3);
        //分享
        holderView.shareImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //评论
        holderView.messageTextView.setText(String.valueOf(data.get(position).getNumComments()));
        holderView.messageImageButton.setTag(position);
        holderView.messageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mPosition = Integer.valueOf(((ImageButton) v).getTag().toString());
                if (null != data.get(position).get_id()) {
                    Intent intent = new Intent(context, S04CommentActivity.class);
                    intent.putExtra(S04CommentActivity.INPUT_SHOW_ID, data.get(position).get_id());
                    intent.putExtra("position:", position);
                    context.startActivity(intent);
                }
            }
        });
        //点赞
        holderView.likeTextView.setText(String.valueOf(data.get(position).numLike));
        Log.i("tag",data.get(position).getIsLikeByCurrentUser() + "");
        if (!data.get(position).getIsLikeByCurrentUser()) {
            holderView.likeImageButton.setBackgroundResource(R.drawable.s03_like_btn);
        } else {
            holderView.likeImageButton.setBackgroundResource(R.drawable.s03_like_btn_hover);
        }
        holderView.likeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!QSModel.INSTANCE.loggedin()) {
                    Toast.makeText(context, R.string.need_login, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (null != data && null != data.get(position).get_id()) {
                    holderView.likeImageButton.setClickable(false);
                    followOrNot(data.get(position).getIsLikeByCurrentUser(), position, holderView);
                }
            }
        });

        return convertView;
    }

    private void followOrNot(final boolean isLiked, final int position, final HolderView holderView) {

        String api;
        if (isLiked)
            api = QSAppWebAPI.getPreviewTrendUnLikeApi();
        else {
            api = QSAppWebAPI.getPreviewTrendLikeApi();
        }
        Log.i("tag",api);


        Map<String, String> likeData = new HashMap<String, String>();
        likeData.put("_id", data.get(position).get_id());
        JSONObject jsonObject = new JSONObject(likeData);
        QSJsonObjectRequest mJsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, api
                , jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String showMsg = "";
                if (!MetadataParser.hasError(response)) {
                    if (isLiked) {
                        holderView.likeImageButton.setBackgroundResource(R.drawable.s03_like_btn);
                        holderView.likeTextView.setText(
                                String.valueOf(Integer.parseInt(holderView.likeTextView.getText().toString()) + 1));
                        showMsg = "取消点赞";
                    } else {
                        holderView.likeImageButton.setBackgroundResource(R.drawable.s03_like_btn_hover);
                        holderView.likeTextView.setText(
                                String.valueOf(Integer.parseInt(holderView.likeTextView.getText().toString()) - 1));
                        showMsg = "点赞成功";
                    }
                    data.get(position).setIsLikeByCurrentUser(!isLiked);
                    showMessage(context, showMsg);

                } else {
                    handleResponseError(response);
                    showMessage(context, showMsg + "失败");

                }
                holderView.likeImageButton.setClickable(true);
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(mJsonObjectRequest);
    }

    private void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        Log.i(context.getPackageName(), message);
    }

    private void handleResponseError(JSONObject response) {
        try {
            int errorCode = MetadataParser.getError(response);
            //String errorMessage = showDetailEntity.likedByCurrentUser() ? "取消点赞失败" : "点赞失败";
            String errorMessage = "";
            switch (errorCode) {
                case 1012:
                    errorMessage = "请先登录！";
                    break;
                case 1000:
                    errorMessage = "服务器错误，请稍微重试！";
                    break;
                default:
                    errorMessage = String.valueOf(errorCode) + response.toString();
                    break;
            }
            showMessage(context, errorMessage);
        } catch (Exception e) {
            showMessage(context, e.toString() + response.toString());
        }
    }

    class HolderView {
        public RelativeLayout relativeLayout;
        public MPullRefreshListView listView;

        public ViewPager viewPager;
        public PageIndicator pageIndicator;
        public TextView descriptionTextView;

        public ImageButton shareImageButton;
        public ImageButton likeImageButton;
        public TextView likeTextView;
        public ImageButton messageImageButton;
        public TextView messageTextView;
    }

    public class MViewPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

        private final String TAG = "MViewPagerAdapter";

        private int imgSize;

        private int mPosition;

        private ImageView[] _mImgViewS;

        private List<MongoPreview.Image> Images;
        private HolderView holderView;
        /**
         * 装点点的ImageView数组
         */
        public MViewPagerAdapter(int position, HolderView holderView) {
            this.holderView = holderView;
            this.mPosition = position;
            this.Images = data.get(mPosition).images;
            this.imgSize = Images.size();
            Log.d(TAG, "图片数：" + this.imgSize);
            this.imgSize = (this.imgSize <= 0 ? 1 : this.imgSize);
            _mImgViewS = new ImageView[this.imgSize];
            initMImageViews(this.imgSize);


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
            container.removeView(_mImgViewS[position % _mImgViewS.length]);
        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView imageView = _mImgViewS[position % this._mImgViewS.length];
            MongoPreview.Image imgInfo = (MongoPreview.Image) imageView.getTag();

            imageLoader.displayImage(ImgUtil.imgTo2x(imgInfo.url), imageView, AppUtil.getShowDisplayOptions());
            container.addView(imageView, 0);

            return imageView;
        }

        private void initMImageViews(int size) {

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT
                    , itemHeight);
            for (int i = 0; i < size; i++) {
                ImageView imageView = new ImageView(context);
                MongoPreview.Image imgInfo = this.Images.get(i);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setTag(imgInfo);
                _mImgViewS[i] = imageView;

            }

        }


        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {

            MongoPreview.Image Image = this.Images.get(arg0 % this.imgSize);
            holderView.pageIndicator.setIndex(arg0 % this.imgSize + 1);
            holderView.descriptionTextView.setText(Image.description);
        }


    }


}
