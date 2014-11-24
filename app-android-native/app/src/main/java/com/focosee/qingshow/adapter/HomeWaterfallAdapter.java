package com.focosee.qingshow.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.entity.ShowEntity;
import com.huewu.pla.lib.internal.PLA_AbsListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class HomeViewHolder extends AbsViewHolder {
    ImageView showIV;
    ImageView modelIV;
    TextView modelNameTV;
    TextView modelJobTV;
    TextView modelHeightTV;
    TextView modelWeightTV;
    TextView loveTV;
    TextView modelStatusTV;
}

public class HomeWaterfallAdapter extends AbsWaterfallAdapter {

    private DisplayImageOptions coverOptions;
    private AnimateFirstDisplayListener animateFirstListener = new AnimateFirstDisplayListener();

    public HomeWaterfallAdapter(Context context, int resourceId, ImageLoader mImageFetcher) {
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

        if (position == 0) {
            LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
            convertView = layoutInflator.inflate(R.layout.item_refresh_independent, null);
            convertView.setLayoutParams(new PLA_AbsListView.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 100));
            convertView.setTag(position);
            return convertView;
        }
        position--;

        HomeViewHolder holder;
        ShowEntity showInfo = (ShowEntity) _data.get(position);

        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
            convertView = layoutInflator.inflate(_resourceId, null);
            holder = new HomeViewHolder();
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
        holder = (HomeViewHolder) convertView.getTag();

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

    @Override
    public int getCount() {
//        return (super.getCount() == 0) ? super.getCount() : super.getCount()+ 1; // one more to show refresh status;
        return (_data.size() == 0) ? 0 : _data.size() + 1;
//        return _data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 0;
        else return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void addItemLast(LinkedList<ShowEntity> datas) {
        _data.addAll(datas);
    }

    public void addItemTop(LinkedList<ShowEntity> datas) {
        _data.clear();
        _data.addAll(datas);
    }


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