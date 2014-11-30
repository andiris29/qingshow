package com.focosee.qingshow.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.entity.ShowEntity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class ClassifyViewHolder extends AbsViewHolder {
    ImageView showIV;
    ImageView modelIV;
    TextView modelNameTV;
    TextView modelJobTV;
    TextView modelHeightTV;
    TextView modelWeightTV;
    TextView loveTV;
    TextView modelStatusTV;
}

public class ClassifyWaterfallAdapter extends AbsWaterfallAdapter {

    private DisplayImageOptions coverOptions;
    private AnimateFirstDisplayListener animateFirstListener = new AnimateFirstDisplayListener();

    public ClassifyWaterfallAdapter(Context context, int resourceId, ImageLoader mImageFetcher) {
        super(context, resourceId, mImageFetcher);

        coverOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_launcher) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.ic_launcher)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.ic_launcher)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
                .build();//构建完成
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ClassifyViewHolder holder;
        ShowEntity showInfo = (ShowEntity) _data.get(position);

        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
            convertView = layoutInflator.inflate(_resourceId, null);
            holder = new ClassifyViewHolder();
            holder.showIV = (ImageView) convertView.findViewById(R.id.item_show_image);
            holder.modelIV = (ImageView) convertView.findViewById(R.id.item_show_model_image);
            holder.modelNameTV = (TextView) convertView.findViewById(R.id.item_show_model_name);
            holder.modelJobTV = (TextView) convertView.findViewById(R.id.item_show_model_job);
            holder.modelHeightTV = (TextView) convertView.findViewById(R.id.item_show_model_height);
            holder.modelWeightTV = (TextView) convertView.findViewById(R.id.item_show_model_weight);
            holder.loveTV = (TextView) convertView.findViewById(R.id.item_show_love);
            holder.modelStatusTV = (TextView) convertView.findViewById(R.id.item_show_model_status);
            convertView.setTag(holder);
        }
        holder = (ClassifyViewHolder) convertView.getTag();

//        holder.showIV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)_myHeight));

        holder.showIV.setLayoutParams(new LinearLayout.LayoutParams(showInfo.getCoverWidth(), showInfo.getCoverHeight()));

        _mImageFetcher.displayImage(showInfo.getShowCover(), holder.showIV, coverOptions, animateFirstListener);
        _mImageFetcher.displayImage(showInfo.getModelImgSrc(), holder.modelIV, animateFirstListener);
        holder.modelNameTV.setText(showInfo.getModelName());
        holder.modelJobTV.setText(showInfo.getModelJob());
        holder.modelHeightTV.setText(showInfo.getModelHeight());
        holder.modelWeightTV.setText(showInfo.getModelWeight());
        holder.loveTV.setText(showInfo.getShowNumLike());
        holder.modelStatusTV.setText(showInfo.getModelStatus());

        return convertView;
    }


    public void addItemLast(LinkedList<ShowEntity> datas) {
        _data.addAll(datas);
    }

    public void addItemTop(LinkedList<ShowEntity> datas) {
        _data.clear();
        _data.addAll(datas);
    }

    public ShowEntity getItemDataAtIndex(int index) {
        if (index >= _data.size()) return null;
        return (ShowEntity)_data.get(index);
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
