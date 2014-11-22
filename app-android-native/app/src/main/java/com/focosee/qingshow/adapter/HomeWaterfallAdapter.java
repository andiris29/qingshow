package com.focosee.qingshow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.entity.AbsEntity;
import com.focosee.qingshow.entity.ShowEntity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedList;

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

    public HomeWaterfallAdapter(Context context, int resourceId, ImageLoader mImageFetcher) {
        super(context, resourceId, mImageFetcher);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

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

        holder.showIV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Integer.valueOf(showInfo.getCoverHeight())));

        _mImageFetcher.displayImage(showInfo.getShowCover(), holder.showIV);
        _mImageFetcher.displayImage(showInfo.getModelImgSrc(), holder.modelIV);
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
        for (AbsEntity info : datas) {
            _data.addFirst(info);
        }
    }
}
