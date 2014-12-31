package com.focosee.qingshow.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.entity.ShowListEntity;
import com.focosee.qingshow.widget.MImageView_OriginSize;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class HomeViewHolder extends AbsViewHolder {


    // Public interface
    public void setData(ShowListEntity entity, ImageLoader imageLoader) {
//        holder.showIV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)_myHeight));
//        showIV.setLayoutParams(new LinearLayout.LayoutParams(entity.getCoverWidth(), entity.getCoverHeight()));

        showIV.setOriginWidth(entity.getCoverWidth());
        showIV.setOriginHeight(entity.getCoverHeight());

        imageLoader.displayImage(entity.getShowCover(), showIV, coverOptions, animateFirstListener);
        imageLoader.displayImage(entity.getModelPhoto(), modelIV, animateFirstListener);
        modelNameTV.setText(entity.getModelName());
        modelHeightTV.setText(entity.getModelHeight());
        modelWeightTV.setText(entity.getModelWeight());
        loveTV.setText(entity.getShowNumLike());
    }

    MImageView_OriginSize showIV;
    ImageView modelIV;
    TextView modelNameTV;
    TextView modelHeightTV;
    TextView modelWeightTV;
    TextView loveTV;


    // Helper property
    private AnimateFirstDisplayListener animateFirstListener = new AnimateFirstDisplayListener();
    private DisplayImageOptions coverOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_launcher) //设置图片在下载期间显示的图片
            .showImageForEmptyUri(R.drawable.ic_launcher)//设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.ic_launcher)  //设置图片加载/解码过程中错误时候显示的图片
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
            .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
            .build();//构建完成


    // Helper class Animation
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                Log.i("test", "load complete not null");
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

public class HomeWaterfallAdapter extends AbsWaterfallAdapter {

    public HomeWaterfallAdapter(Context context, int resourceId, ImageLoader mImageFetcher) {
        super(context, resourceId, mImageFetcher);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HomeViewHolder holder;
        ShowListEntity showInfo = (ShowListEntity) _data.get(position);

        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
            convertView = layoutInflator.inflate(_resourceId, null);
            holder = new HomeViewHolder();
            holder.showIV  = (MImageView_OriginSize) convertView.findViewById(R.id.item_show_image);
            holder.modelIV = (ImageView) convertView.findViewById(R.id.item_show_model_image);
            holder.modelNameTV = (TextView) convertView.findViewById(R.id.item_show_model_name);
            holder.modelHeightTV = (TextView) convertView.findViewById(R.id.item_show_model_height);
            holder.modelWeightTV = (TextView) convertView.findViewById(R.id.item_show_model_weight);
            holder.loveTV = (TextView) convertView.findViewById(R.id.item_show_love);
            convertView.setTag(holder);
        }

        holder = (HomeViewHolder) convertView.getTag();

        holder.setData(showInfo, _mImageFetcher);
        return convertView;
    }

    @Override
    public int getCount() {
        return (null == _data) ? 0 : _data.size();
    }

    public void addItemLast(LinkedList<ShowListEntity> datas) {
        _data.addAll(datas);
    }

    public void addItemTop(LinkedList<ShowListEntity> datas) {
        _data.addAll(datas);
    }

    public ShowListEntity getItemDataAtIndex(int index) {
        if (index >= _data.size()) return null;
        return (ShowListEntity)_data.get(index);
    }

}
