package com.focosee.qingshow.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.request.MJsonObjectRequest;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S04CommentActivity;
import com.focosee.qingshow.entity.TrendEntity;
import org.json.JSONObject;
import java.util.LinkedList;

public class S08TrendListAdapter extends BaseAdapter {


    private final String _TAG = "com.focosee.qingshow.adapter.S08TrendListAdapter";
    //每一行的最小高度
    private int minHeight;


    private Context context;
    private List<TrendEntity> data;

    private HolderView holderView;

    public S08TrendListAdapter(Context context, LinkedList<TrendEntity> trendEntities, int screenHeight) {
        this.context = context;
        this.data = trendEntities;
        this.minHeight = screenHeight * 8 / 9;
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

    public void resetData(LinkedList<TrendEntity> datas) { this.data = datas; }

    public void addItemLast(LinkedList<TrendEntity> datas) {
        this.data.addAll(datas);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        RelativeLayout.LayoutParams params_rLayout;

        if (null == convertView) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.item_s08_trend_list, null);

            holderView = new HolderView();
            holderView.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.s08_relative_layout);

            holderView.viewPager = (ViewPager) convertView.findViewById(R.id.s08_viewPager);
            holderView.viewGroup = (LinearLayout) convertView.findViewById(R.id.s08_viewGroup);
            holderView.nameTextView = (TextView) convertView.findViewById(R.id.S08_item_name);
            holderView.descriptionTextView = (TextView) convertView.findViewById(R.id.S08_item_description);
            holderView.priceTextView = (TextView) convertView.findViewById(R.id.S08_item_price);

            holderView.shareImageButton = (ImageButton) convertView.findViewById(R.id.S08_item_share_btn);
            holderView.likeImageButton = (ImageButton) convertView.findViewById(R.id.S08_item_like_btn);
            holderView.likeTextView = (TextView) convertView.findViewById(R.id.S08_item_like_text_view);
            holderView.messageImageButton = (ImageButton) convertView.findViewById(R.id.S08_item_comment_btn);
            holderView.messageTextView = (TextView) convertView.findViewById(R.id.S08_item_comment_text_view);

            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        int item_height = this.minHeight;
        int item_width = holderView.viewPager.getLayoutParams().width;

        //如何图片高度小于最小高度，则设为最小高度
        params_rLayout = new RelativeLayout.LayoutParams(item_width
                , item_height);
        convertView.setLayoutParams(new AbsListView.LayoutParams(item_width,
                item_height));

        //设置Adapter
        MViewPagerAdapter mViewPagerAdapter = new MViewPagerAdapter(position, holderView.viewGroup);
        holderView.viewPager.setAdapter(mViewPagerAdapter);
        //设置监听，主要是设置点点的背景
        holderView.viewPager.setOnPageChangeListener(mViewPagerAdapter);
        //设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
        holderView.viewPager.setCurrentItem(data.get(position).images.size());
        //分享
        holderView.shareImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                //intent.setType("image*//*");
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                intent.putExtra(Intent.EXTRA_TEXT, "测试内容!!!");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(Intent.createChooser(intent, context.getPackageName()));
            }
        });
        //评论
        holderView.messageTextView.setText(data.get(position).getNumComments() + "");
        holderView.messageImageButton.setTag(position);
        holderView.messageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mPosition = Integer.valueOf(((ImageButton) v).getTag().toString());
                if (null != data.get(position).images.get(mPosition) && null != data.get(position).get_id()) {
                    Intent intent = new Intent(context, S04CommentActivity.class);
                    intent.putExtra(S04CommentActivity.INPUT_SHOW_ID, data.get(position).get_id());
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Plese NPC!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //点赞
        holderView.likeTextView.setText(data.get(position).numLike + "");
        holderView.likeImageButton.setTag(position);
        holderView.likeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mPosition = Integer.valueOf(((ImageButton) v).getTag().toString());
                if (null != data && null != data.get(position).get_id()) {

                    Map<String, String> likeData = new HashMap<String, String>();
                    likeData.put("_id", data.get(position).get_id());
                    JSONObject jsonObject = new JSONObject(likeData);

                    MJsonObjectRequest mJsonObjectRequest = new MJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getPreviewTrendLikeApi()
                            , jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.get("metadata").toString().equals("{}")) {
                                    holderView.likeTextView.setText(
                                            Integer.parseInt(holderView.likeTextView.getText().toString()) + 1 + "");
                                    holderView.likeImageButton.setClickable(false);
                                    holderView.likeImageButton.setBackgroundResource(R.drawable.s03_like_btn_hover);
                                    showMessage(context, "点赞成功");
                                    //showDetailEntity.setLikedByCurrentUser(!showDetailEntity.likedByCurrentUser());
                                } else {
                                    handleResponseError(response);
                                    showMessage(context, "点赞失败" + response.toString() + response.get("metadata").toString().length());

                                }
                            } catch (Exception e) {
                                showMessage(context, e.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showMessage(context, error.toString());
                        }
                    });

                    QSApplication.get().QSRequestQueue().add(mJsonObjectRequest);

                }
            }
        });

        return convertView;
    }

    private void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        Log.i(context.getPackageName(), message);
    }

    private void handleResponseError(JSONObject response) {
        try {
            int errorCode = response.getJSONObject("metadata").getInt("error");
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
        public LinearLayout viewGroup;
        public TextView nameTextView;
        public TextView descriptionTextView;
        public TextView priceTextView;

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

        private List<TrendEntity.ImageInfo> imageInfos;

        private LinearLayout _mViewGroup;
        /**
         * 装点点的ImageView数组
         */
        private ImageView[] tips;

        public MViewPagerAdapter(int position, LinearLayout mViewGroup) {
            this._mViewGroup = mViewGroup;
            this.mPosition = position;
            this.imageInfos = data.get(mPosition).images;
            this.imgSize = imageInfos.size();
            Log.d(TAG, "图片数：" + this.imgSize);
            this.imgSize = (this.imgSize <= 0 ? 1 : this.imgSize);
            _mImgViewS = new ImageView[this.imgSize];

            initTips();
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
            TrendEntity.ImageInfo imgInfo = (TrendEntity.ImageInfo)imageView.getTag();

            ImageLoader.getInstance().displayImage(imgInfo.url, imageView, AppUtil.getShowDisplayOptions());
            container.addView(imageView, 0);

            return imageView;
        }

        private void initMImageViews(int size){


            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);
            for (int i = 0;i<size;i++){
                ImageView imageView = new ImageView(context);
                TrendEntity.ImageInfo imgInfo = this.imageInfos.get(i);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setTag(imgInfo);
                _mImgViewS[i] = imageView;

            }

        }



        @Override
        public void onPageScrollStateChanged(int arg0) {}

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {}

        @Override
        public void onPageSelected(int arg0) {

            setImageBackground(arg0);

            TrendEntity.ImageInfo imageInfo = this.imageInfos.get(arg0 % this.imgSize);
            holderView.nameTextView.setText(imageInfo.brandDescription);
            holderView.descriptionTextView.setText(imageInfo.description);
            holderView.priceTextView.setText(imageInfo.priceDescription);
        }

        private void initTips(){
            tips = new ImageView[this.imgSize];
            _mViewGroup.removeAllViews();
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
                _mViewGroup.addView(imageView_tips, layoutParams);
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


    }


}
