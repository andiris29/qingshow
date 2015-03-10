package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.P02ModelActivity;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.widget.MImageView_OriginSize;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/3/9.
 */
public class ClassifyWaterfallAdapter_HasHeadRelativeLayout extends AbsWaterfallAdapter<MongoShow>{

    private Context context;

    private AnimateFirstDisplayListener animateFirstListener = new AnimateFirstDisplayListener();

    public ClassifyWaterfallAdapter_HasHeadRelativeLayout(Context context, int resourceId, ImageLoader mImageFetcher) {
        super(context, resourceId, mImageFetcher);
        this.context = context;
    }

    @Override
    public void refreshDate(JSONObject response) {

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position < 2) ? 0 : 1;
    }

    @Override
    public int getCount() {
        return (null == _data) ? 0 : _data.size() + 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(position < 2){
            RelativeLayout headRelativeLayout;
            if(null == convertView){
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                convertView = layoutInflater.inflate(R.layout.activity_personal, null);
                headRelativeLayout = (RelativeLayout)convertView.findViewById(R.id.U01_head_relative);
                headRelativeLayout.removeAllViews();
                convertView.setTag(headRelativeLayout);
            }
            return convertView;
        }

        position = position -2;
        ClassifyViewHolder holder;
        final MongoShow showInfo = _data.get(position);

        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
            convertView = layoutInflator.inflate(_resourceId, null);
            holder = new ClassifyViewHolder();
            holder.showIV = (MImageView_OriginSize) convertView.findViewById(R.id.item_show_image);
            holder.modelIV = (ImageView) convertView.findViewById(R.id.item_show_model_image);
            holder.modelNameTV = (TextView) convertView.findViewById(R.id.item_show_model_name);
            holder.modelHeightWeightTV = (TextView) convertView.findViewById(R.id.item_show_model_height_weight);
//            holder.modelHeightTV = (TextView) convertView.findViewById(R.id.item_show_model_height);
//            holder.modelWeightTV = (TextView) convertView.findViewById(R.id.item_show_model_weight);
            holder.loveTV = (TextView) convertView.findViewById(R.id.item_show_love);
            holder.loveIV = (ImageView) convertView.findViewById(R.id.item_show_love_img);
            holder.shadowIV = (ImageView)  convertView.findViewById(R.id.item_show_shadow);
            convertView.setTag(holder);
        }
        holder = (ClassifyViewHolder) convertView.getTag();

//        holder.showIV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)_myHeight));

        //holder.showIV.setLayoutParams(new LinearLayout.LayoutParams(showInfo.getCoverWidth(), showInfo.getCoverHeight()));
        holder.showIV.setOriginWidth(showInfo.getCoverWidth());
        holder.showIV.setOriginHeight(showInfo.getCoverHeight());

        _mImageFetcher.displayImage(ImgUtil.imgTo2x(showInfo.getShowCover()), holder.showIV, AppUtil.getShowDisplayOptions(), animateFirstListener);
        _mImageFetcher.displayImage(showInfo.getModelPhoto(), holder.modelIV, animateFirstListener);
        holder.modelNameTV.setText(showInfo.getModelName());
        holder.modelHeightWeightTV.setText(showInfo.getModelHeightAndHeightWithFormat());
//        holder.modelHeightTV.setText(showInfo.getModelHeight());
//        holder.modelWeightTV.setText(showInfo.getModelWeight());
        holder.loveTV.setText(showInfo.getShowNumLike());
        if(showInfo.getShowIsFollowedByCurrentUser())
            holder.loveIV.setBackgroundResource(R.drawable.root_cell_icon_notice_hover);
        else
            holder.loveIV.setBackgroundResource(R.drawable.root_cell_icon_notice);
        holder.shadowIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, P02ModelActivity.class);
                intent.putExtra(P02ModelActivity.INPUT_MODEL, showInfo.getModelRef());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    // Animation
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
