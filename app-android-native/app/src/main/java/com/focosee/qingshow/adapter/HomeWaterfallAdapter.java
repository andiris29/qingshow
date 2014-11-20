package com.focosee.qingshow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.focosee.qingshow.entity.ShowEntity;
import com.nostra13.universalimageloader.core.ImageLoader;

class HomeViewHolder extends AbsViewHolder {
    ImageView imageView;
    TextView contentView;
    TextView timeView;
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
//            holder.imageView = (ImageView) convertView.findViewById(R.id.news_pic);
//            holder.contentView = (TextView) convertView.findViewById(R.id.news_title);
            convertView.setTag(holder);
        }
        holder = (HomeViewHolder) convertView.getTag();

//        holder.imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) ShowEntity.getIht()));
//        holder.contentView.setText(ShowEntity.getMsg());

        return convertView;
    }
}
