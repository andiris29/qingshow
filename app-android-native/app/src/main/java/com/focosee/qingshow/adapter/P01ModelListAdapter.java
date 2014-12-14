package com.focosee.qingshow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.entity.BrandEntity;
import com.focosee.qingshow.entity.ModelEntity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

class P01ModelHolderView {
    public ImageView modelImageView;
    public TextView nameTextView;
    public TextView heightTextView;
    public TextView weightTextView;
    public TextView jobTextView;
    public TextView clothNumberTextView;
    public TextView likeNumberTextView;
}

public class P01ModelListAdapter extends BaseAdapter {

    private ArrayList<ModelEntity> data;
    private ImageLoader imageLoader;
    private Context context;

    public P01ModelListAdapter(Context context, ArrayList<ModelEntity> data, ImageLoader imageLoader) {
        this.context = context;
        this.data = data;
        this.imageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @Override
    public Object getItem(int position) {
        return this.data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        P01ModelHolderView holderView;
        if (null == convertView) {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.item_modellist, null);
            holderView = new P01ModelHolderView();
            holderView.modelImageView = (ImageView) convertView.findViewById(R.id.item_model_image);
            holderView.nameTextView = (TextView) convertView.findViewById(R.id.item_model_name);
            holderView.heightTextView = (TextView) convertView.findViewById(R.id.item_model_height);
            holderView.weightTextView = (TextView) convertView.findViewById(R.id.item_model_weight);
            holderView.clothNumberTextView = (TextView) convertView.findViewById(R.id.item_model_cloth_number);
            holderView.likeNumberTextView = (TextView) convertView.findViewById(R.id.item_model_like_number);

            convertView.setTag(holderView);
        }
        holderView = (P01ModelHolderView)convertView.getTag();

        this.imageLoader.displayImage(this.data.get(position).getPortrait(),holderView.modelImageView);
        holderView.nameTextView.setText(this.data.get(position).getName());
        holderView.heightTextView.setText(this.data.get(position).getHeight());
        holderView.weightTextView.setText(this.data.get(position).getWeight());
        holderView.clothNumberTextView.setText(String.valueOf(this.data.get(position).getNumberShows()));
        holderView.likeNumberTextView.setText(String.valueOf(this.data.get(position).getNumberFollowers()));
        return convertView;
    }

    public void resetData(ArrayList<ModelEntity> newData) {
        this.data = newData;
    }

    public void addData(ArrayList<ModelEntity> moreData) {
        this.data.addAll(this.data.size(), moreData);
    }
}
