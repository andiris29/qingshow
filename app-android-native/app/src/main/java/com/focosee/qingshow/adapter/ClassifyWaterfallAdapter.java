package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.P02ModelActivity;
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


class ClassifyViewHolder extends AbsViewHolder {
    MImageView_OriginSize showIV;
    ImageView modelIV;
    TextView modelNameTV;
    TextView modelHeightWeightTV;
    TextView loveTV;
    ImageView shadowIV;
    ImageView loveIV;
}

public class ClassifyWaterfallAdapter extends AbsWaterfallAdapter {

    private Context context;

    private DisplayImageOptions coverOptions;
    private AnimateFirstDisplayListener animateFirstListener = new AnimateFirstDisplayListener();

    public ClassifyWaterfallAdapter(Context context, int resourceId, ImageLoader mImageFetcher) {
        super(context, resourceId, mImageFetcher);
        this.context = context;
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
        final ShowListEntity showInfo = (ShowListEntity) _data.get(position);

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

        _mImageFetcher.displayImage(showInfo.getShowCover(), holder.showIV, coverOptions, animateFirstListener);
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


    public void addItemLast(LinkedList<ShowListEntity> datas) {
        _data.addAll(datas);
    }

    public void addItemTop(LinkedList<ShowListEntity> datas) {
        _data.clear();
        _data.addAll(datas);
    }

    public ShowListEntity getItemDataAtIndex(int index) {
        if (index >= _data.size()) return null;
        return (ShowListEntity)_data.get(index);
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
