package com.focosee.qingshow.adapter;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.widget.MImageView_OriginSize;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/3/2.
 */
public class HomeViewHolder extends AbsViewHolder {


    // Public interface
    public void setData(MongoShow entity, ImageLoader imageLoader) {

        showIV.setOriginWidth(entity.getCoverWidth());
        showIV.setOriginHeight(entity.getCoverHeight());

        imageLoader.displayImage(ImgUtil.imgTo2x(entity.getShowCover()), showIV, AppUtil.getShowDisplayOptions(), animateFirstListener);
        imageLoader.displayImage(entity.getModelPhoto(), modelIV, AppUtil.getSimapleDisplayOptions(), animateFirstListener);
        modelNameTV.setText(entity.getModelName());
        modelHeightWeightTV.setText(entity.getModelHeightAndHeightWithFormat());
        loveTV.setText(entity.getShowNumLike());
        //TODO 换图片
        if (entity.getModelRef().getModelIsFollowedByCurrentUser()) {
            loveIV.setBackgroundResource(R.drawable.model_cell_icon02_noticeno);
        }
    }

    MImageView_OriginSize showIV;
    ImageView modelIV;
    TextView modelNameTV;
    TextView modelHeightWeightTV;
    TextView loveTV;
    ImageView loveIV;
    public ImageView shadowView;


    //    // Helper property
    private AnimateFirstDisplayListener animateFirstListener = new AnimateFirstDisplayListener();

    // Helper class Animation
    private class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            if(view.getId() == R.id.item_show_model_image)
                view.setVisibility(View.GONE);
            modelHeightWeightTV.setVisibility(View.GONE);
            modelNameTV.setVisibility(View.GONE);
        }

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
            if(view.getId() == R.id.item_show_model_image)
                view.setVisibility(View.VISIBLE);
            modelHeightWeightTV.setVisibility(View.VISIBLE);
            modelNameTV.setVisibility(View.VISIBLE);
        }
    }
}
