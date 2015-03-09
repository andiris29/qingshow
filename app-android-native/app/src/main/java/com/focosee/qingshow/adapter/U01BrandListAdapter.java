package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.P04BrandActivity;
import com.focosee.qingshow.model.vo.mongo.MongoBrand;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.widget.MImageView_OriginSize;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/3/9.
 */
public class U01BrandListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MongoBrand> data;
    private ImageLoader imageLoader;

    public U01BrandListAdapter(Context context, ArrayList<MongoBrand> data, ImageLoader imageLoader) {
        this.context = context;
        this.data = data;
        this.imageLoader = imageLoader;
    }

    public ArrayList<MongoBrand> getData(){
        return data;
    }

    @Override
    public int getCount() {
        return (null != data) ? data.size() + 1 : 0;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position + 1);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(position == 0){
            RelativeLayout headRelativeLayout;
            if(null == convertView){
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                convertView = layoutInflater.inflate(R.layout.activity_personal, null);

                headRelativeLayout = (RelativeLayout)convertView.findViewById(R.id.U01_head_relative);
                convertView.setTag(headRelativeLayout);
            }
            headRelativeLayout = (RelativeLayout)convertView.getTag();
            return convertView;
        }

        position--;

        final int final_position = position;
        P03BrandHolderView holderView;
        if (null == convertView) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            convertView = layoutInflater.inflate(R.layout.item_brandlist, null);
            holderView = new P03BrandHolderView();
            holderView.brandName = (TextView) convertView.findViewById(R.id.item_brand_name);
            holderView.brandSlogan = (MImageView_OriginSize) convertView.findViewById(R.id.item_brand_slogan);
            holderView.brandPortrait = (ImageView) convertView.findViewById(R.id.item_brand_portrait);
            holderView.detailButton = (ImageView) convertView.findViewById(R.id.item_brand_detail);
            convertView.setTag(holderView);
        }else{
            holderView = (P03BrandHolderView) convertView.getTag();
        }
        this.imageLoader.cancelDisplayTask(holderView.brandPortrait);
        this.imageLoader.cancelDisplayTask(holderView.brandSlogan);

        holderView.brandSlogan.setOriginWidth(this.data.get(position).getCoverWidth());
        holderView.brandSlogan.setOriginHeight(this.data.get(position).getCoverHeight());

        this.imageLoader.displayImage(this.data.get(position).getBrandLogo(),holderView.brandPortrait, AppUtil.getSimapleDisplayOptions(),new P03ImageLoadingListener());
        this.imageLoader.displayImage(ImgUtil.imgTo2x(this.data.get(position).getBrandCover()),holderView.brandSlogan,AppUtil.getSimapleDisplayOptions(),new P03ImageLoadingListener());
        holderView.brandName.setText(this.data.get(position).getBrandName());
        holderView.detailButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(context, P04BrandActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(P04BrandActivity.INPUT_BRAND, data.get(final_position));
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    public void resetData(ArrayList<MongoBrand> newData) {
        this.data = newData;
    }

    public void addData(ArrayList<MongoBrand> moreData) {
        this.data.addAll(moreData);
    }

    class P03ImageLoadingListener implements ImageLoadingListener {


        @Override
        public void onLoadingStarted(String imageUri, View view) {
            view.setVisibility(View.INVISIBLE);

        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            ((ImageView)view).setImageBitmap(loadedImage);
            view.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {

        }
    }
}
