package com.focosee.qingshow.adapter;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S04CommentActivity;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.response.error.ErrorCode;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoPreview;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;



public class S08TrendListAdapter extends BaseAdapter {


    private final String TAG = "com.focosee.qingshow.adapter.S08TrendListAdapter";
    //每一行的最小高度
    private int minHeight;


    private Context context;
    private List<MongoPreview> data;
    private ImageLoader imageLoader;

    public S08TrendListAdapter(Context context, LinkedList<MongoPreview> trendEntities, int screenHeight, ImageLoader imageLoader) {
        this.context = context;
        this.data = trendEntities;
        this.imageLoader = imageLoader;
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

    public void resetData(LinkedList<MongoPreview> datas) { this.data = datas; }

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
            holderView.viewGroup = (LinearLayout) convertView.findViewById(R.id.s08_viewGroup);
            holderView.nameTextView = (TextView) convertView.findViewById(R.id.S08_item_name);
            holderView.descriptionTextView = (TextView) convertView.findViewById(R.id.S08_item_description);
            holderView.priceTextView = (TextView) convertView.findViewById(R.id.S08_item_price);

            holderView.shareImageButton = (ImageButton) convertView.findViewById(R.id.S08_item_share_btn);
            holderView.likeImageButton = (ImageButton) convertView.findViewById(R.id.S08_item_like_btn);
            holderView.likeTextView = (TextView) convertView.findViewById(R.id.S08_item_like_text_view);
            holderView.messageImageButton = (ImageButton) convertView.findViewById(R.id.S08_item_comment_btn);
            holderView.messageTextView = (TextView) convertView.findViewById(R.id.S08_item_comment_text_view);

            holderView.likeImageButton.setTag(1);

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
        MViewPagerAdapter mViewPagerAdapter = new MViewPagerAdapter(position, holderView);
        holderView.viewPager.setAdapter(mViewPagerAdapter);
        //设置监听，主要是设置点点的背景
        holderView.viewPager.setOnPageChangeListener(mViewPagerAdapter);
        //设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
        holderView.viewPager.setOffscreenPageLimit(1);
        holderView.viewPager.setCurrentItem(data.get(position).images.size() * 100);
        //分享
        holderView.shareImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ShareSDK.initSDK(context);
//                OnekeyShare oks = new OnekeyShare();
//                //关闭sso授权
//                oks.disableSSOWhenAuthorize();
//
//// 分享时Notification的图标和文字
//                oks.setNotification(R.drawable.app_icon, context.getString(R.string.app_name));
//                // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//                oks.setTitle(context.getString(R.string.share));
//                // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//                oks.setTitleUrl("http://sharesdk.cn");
//                // text是分享文本，所有平台都需要这个字段
//                oks.setText("欢迎大家过来使用~");
//                // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
////                oks.setImagePath(getResources().getResourceName(R.drawable.app_icon));//确保SDcard下面存在此张图片
//                // url仅在微信（包括好友和朋友圈）中使用
//                oks.setUrl("http://sharesdk.cn");
//                // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//                oks.setComment("快来试用吧~");
//                // site是分享此内容的网站名称，仅在QQ空间使用
//                oks.setSite(context.getString(R.string.app_name));
//                // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//                oks.setSiteUrl("http://sharesdk.cn");
//// 启动分享GUI
//                oks.show(context);

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
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Plese NPC!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //点赞
        holderView.likeTextView.setText(String.valueOf(data.get(position).numLike));
        if(data.get(position).getIsLikeByCurrentUser()) {
            holderView.likeImageButton.setBackgroundResource(R.drawable.s03_like_btn);
        }else{
            holderView.likeImageButton.setBackgroundResource(R.drawable.s03_like_btn_hover);
        }
        holderView.likeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != data && null != data.get(position).get_id()) {
                    followOrNot(data.get(position).getIsLikeByCurrentUser(), position, holderView);
                }
            }
        });

        return convertView;
    }

    private void followOrNot(final boolean isLiked, final int position,final HolderView holderView){

        String api;
        if(isLiked)
            api = QSAppWebAPI.getPreviewTrendUnLikeApi();
        else {
            api = QSAppWebAPI.getPreviewTrendLikeApi();
        }

        Map<String, String> likeData = new HashMap<String, String>();
        likeData.put("_id", data.get(position).get_id());
        JSONObject jsonObject = new JSONObject(likeData);
        QSJsonObjectRequest mJsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, api
                , jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    String showMsg = "";
                    if (!MetadataParser.hasError(response)) {
                        if(isLiked){
                            holderView.likeImageButton.setBackgroundResource(R.drawable.s03_like_btn_hover);
                            holderView.likeTextView.setText(
                                    String.valueOf(Integer.parseInt(holderView.likeTextView.getText().toString()) + 1));
                            showMsg = "点赞";
                        }else{
                            holderView.likeImageButton.setBackgroundResource(R.drawable.s03_like_btn);
                            holderView.likeTextView.setText(
                                    String.valueOf(Integer.parseInt(holderView.likeTextView.getText().toString()) - 1));
                            showMsg = "取消点赞";
                        }
                        data.get(position).setIsLikeByCurrentUser(!isLiked);
                        showMessage(context, showMsg + "成功");

                    } else {
                        handleResponseError(response);
                        showMessage(context, showMsg+ "失败");

                    }
                } catch (Exception e) {
                    //showMessage(context, e.toString());
                    ErrorHandler.handle(context, ErrorCode.NoNetWork);
                }
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

        private List<MongoPreview.Image> Images;
        private HolderView holderView;
        private LinearLayout _mViewGroup;
        /**
         * 装点点的ImageView数组
         */
        private ImageView[] tips;

        public MViewPagerAdapter(int position, HolderView holderView) {
            this.holderView = holderView;
            this._mViewGroup = holderView.viewGroup;
            this.mPosition = position;
            this.Images = data.get(mPosition).images;
            this.imgSize = Images.size();
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
            MongoPreview.Image imgInfo = (MongoPreview.Image)imageView.getTag();

            imageLoader.displayImage(ImgUtil.imgTo2x(imgInfo.url), imageView, AppUtil.getShowDisplayOptions());
            container.addView(imageView, 0);

            return imageView;
        }

        private void initMImageViews(int size){


            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);
            for (int i = 0;i<size;i++){
                ImageView imageView = new ImageView(context);
                MongoPreview.Image imgInfo = this.Images.get(i);
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

            MongoPreview.Image Image = this.Images.get(arg0 % this.imgSize);
            // TODO Remove nameTextView & priceTextView, then support newline in descriptionTextView
            holderView.nameTextView.setText("");
            holderView.descriptionTextView.setText(Image.description);
            holderView.priceTextView.setText("");
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
