package com.focosee.qingshow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.entity.BrandEntity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

class P03BrandHolderView {
    public ImageView brandImageView;
    public ImageView previewImageView;
}

public class P03BrandListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<BrandEntity> data;
    private ImageLoader imageLoader;

    public P03BrandListAdapter(Context context, ArrayList<BrandEntity> data, ImageLoader imageLoader) {
        this.context = context;
        this.data = data;
        this.imageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        P03BrandHolderView holderView;
        if (null != convertView) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            convertView = layoutInflater.inflate(R.layout.item_brandlist, null);
            holderView = new P03BrandHolderView();
            holderView.brandImageView = (ImageView) convertView.findViewById(R.id.item_brand_list_brand);
            holderView.previewImageView = (ImageView) convertView.findViewById(R.id.item_brand_list_preview);
            convertView.setTag(holderView);
        }
        holderView = (P03BrandHolderView) convertView.getTag();
        this.imageLoader.displayImage(this.data.get(position).brandImage, holderView.brandImageView);
        this.imageLoader.displayImage(this.data.get(position).previewImage, holderView.previewImageView);
        return convertView;
    }
}
