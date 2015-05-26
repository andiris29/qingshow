package com.focosee.qingshow.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
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
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/3/2.
 */
public class HomeViewHolder extends AbsViewHolder {

    private ImageLoader imageLoader;
    private String modelUrl = "";

    // Public interface
    public void setData(MongoShow entity, ImageLoader imageLoader) {

        this.imageLoader = imageLoader;
//        showIV.setOriginWidth(entity.getCoverWidth());
//        showIV.setOriginHeight(entity.getCoverHeight());
//        modelUrl = entity.getModelPhoto();
//        goneView();
//        imageLoader.cancelDisplayTask(modelIV);
//        imageLoader.displayImage(ImgUtil.imgTo2x(entity.getShowCover()), showIV, AppUtil.getShowDisplayOptions(), animateFirstListener);
//        modelNameTV.setText(entity.getModelName());
//        modelHeightWeightTV.setText(entity.getModelHeightAndHeightWithFormat());
//        loveTV.setText(entity.getShowNumLike());
    }

    private void goneView(){
        modelIV.setVisibility(View.GONE);
        modelHeightWeightTV.setVisibility(View.GONE);
        modelNameTV.setVisibility(View.GONE);
        shadowView.setVisibility(View.GONE);
        loveIV.setVisibility(View.GONE);
        loveTV.setVisibility(View.GONE);
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
            modelHeightWeightTV.setVisibility(View.GONE);
            modelNameTV.setVisibility(View.GONE);
            shadowView.setVisibility(View.GONE);
            loveIV.setVisibility(View.GONE);
            loveTV.setVisibility(View.GONE);
            modelIV.setVisibility(View.GONE);
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 1000);
                    displayedImages.add(imageUri);
                }
            }
            if(((BitmapDrawable)showIV.getDrawable()).getBitmap().sameAs(loadedImage)){
                modelIV.setVisibility(View.VISIBLE);
            }
            imageLoader.displayImage(modelUrl, modelIV, AppUtil.getSimapleDisplayOptions(),new SimpleImageLoadingListener(){

                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    view.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    modelHeightWeightTV.setVisibility(View.VISIBLE);
                    modelNameTV.setVisibility(View.VISIBLE);
                    shadowView.setVisibility(View.VISIBLE);
                    loveIV.setVisibility(View.VISIBLE);
                    loveTV.setVisibility(View.VISIBLE);
                    view.setVisibility(View.VISIBLE);
                }
            });
        }

    }
}
