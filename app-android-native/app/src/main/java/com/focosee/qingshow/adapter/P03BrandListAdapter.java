package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.P04BrandActivity;
import com.focosee.qingshow.model.vo.mongo.MongoBrand;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.widget.MImageView_OriginSize;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

class P03BrandHolderView {
    public MImageView_OriginSize brandSlogan;
    public ImageView brandPortrait;
    public TextView brandName;
    public TextView brandDescription;
    public ImageView detailButton;
}

public class P03BrandListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MongoBrand> data;
    private ImageLoader imageLoader;

    public P03BrandListAdapter(Context context, ArrayList<MongoBrand> data, ImageLoader imageLoader) {
        this.context = context;
        this.data = data;
        this.imageLoader = imageLoader;
    }

    public ArrayList<MongoBrand> getData(){
        return data;
    }

    @Override
    public int getCount() {
        return (null != data) ? data.size() : 0;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        P03BrandHolderView holderView;
        if (null == convertView) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            convertView = layoutInflater.inflate(R.layout.item_brandlist, null);
            holderView = new P03BrandHolderView();
            holderView.brandName = (TextView) convertView.findViewById(R.id.item_brand_name);
            //holderView.brandDescription = (TextView) convertView.findViewById(R.id.item_brand_description);
            convertView.setTag(holderView);
        }
        holderView = (P03BrandHolderView) convertView.getTag();
        holderView.brandSlogan = (MImageView_OriginSize) convertView.findViewById(R.id.item_brand_slogan);
        holderView.brandPortrait = (ImageView) convertView.findViewById(R.id.item_brand_portrait);
        holderView.detailButton = (ImageView) convertView.findViewById(R.id.item_brand_detail);

        holderView.brandSlogan.setOriginWidth(this.data.get(position).getCoverWidth());
        holderView.brandSlogan.setOriginHeight(this.data.get(position).getCoverHeight());
        final ImageView brandPortrait = holderView.brandPortrait;
        this.imageLoader.loadImage(this.data.get(position).getBrandLogo(),new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                brandPortrait.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                brandPortrait.setImageBitmap(loadedImage);
                brandPortrait.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
        final ImageView brandSlogan = holderView.brandSlogan;
        this.imageLoader.loadImage(ImgUtil.imgTo2x(this.data.get(position).getBrandCover()),new ImageLoadingListener(){

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                brandSlogan.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                brandSlogan.setImageBitmap(loadedImage);
                brandSlogan.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
        holderView.brandName.setText(this.data.get(position).getBrandName());
        holderView.detailButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(context, P04BrandActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable(P04BrandActivity.INPUT_BRAND, data.get(position));
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        //holderView.brandDescription.setText(this.data.get(position).getBrandDescription());
        return convertView;
    }

    public void resetData(ArrayList<MongoBrand> newData) {
        this.data = newData;
    }

    public void addData(ArrayList<MongoBrand> moreData) {
        this.data.addAll(moreData);
    }

}
