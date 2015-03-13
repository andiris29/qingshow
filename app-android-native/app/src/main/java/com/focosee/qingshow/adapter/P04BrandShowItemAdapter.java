package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.P02ModelActivity;
import com.focosee.qingshow.activity.S02ShowClassify;
import com.focosee.qingshow.activity.S03SHowActivity;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.widget.MImageView_OriginSize;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/3/9.
 */
public class P04BrandShowItemAdapter extends AbsWaterfallAdapter<MongoShow>{
    private Context context;
    public P04BrandShowItemAdapter(Context context, int resourceId, ImageLoader mImageFetcher) {
        super(context, resourceId, mImageFetcher);
        this.context = context;
    }

    @Override
    public void refreshDate(JSONObject response) {
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position < 2) ? 0 : 1;
    }

    @Override
    public int getCount() {
        return (null == _data) ? 0 : _data.size() + 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position < 2){
            RelativeLayout headRelativeLayout;
            if(null == convertView){
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                convertView = layoutInflater.inflate(R.layout.activity_personal, null);
                headRelativeLayout = (RelativeLayout)convertView.findViewById(R.id.U01_head_relative);
                headRelativeLayout.removeAllViews();
                convertView.setTag(headRelativeLayout);
            }
            return convertView;
        }

        position = position -2;
        final int final_position = position;

        HomeViewHolder holder;
        MongoShow showInfo = _data.get(position);

        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(context);
            convertView = layoutInflator.inflate(_resourceId, null);
            holder = new HomeViewHolder();
            holder.showIV  = (MImageView_OriginSize) convertView.findViewById(R.id.item_show_image);
            holder.modelIV = (ImageView) convertView.findViewById(R.id.item_show_model_image);
            holder.modelNameTV = (TextView) convertView.findViewById(R.id.item_show_model_name);
            holder.modelHeightWeightTV = (TextView) convertView.findViewById(R.id.item_show_model_height_weight);
            holder.loveTV = (TextView) convertView.findViewById(R.id.item_show_love);
            holder.loveIV = (ImageView) convertView.findViewById(R.id.item_show_love_img);
            holder.shadowView = (ImageView) convertView.findViewById(R.id.item_show_shadow);
            convertView.setTag(holder);
        }

        holder = (HomeViewHolder) convertView.getTag();
        if(showInfo.getShowIsFollowedByCurrentUser()) {
            holder.loveIV.setBackgroundResource(R.drawable.root_cell_icon_notice_hover);
        }
        else {
            holder.loveIV.setBackgroundResource(R.drawable.root_cell_icon_notice);
        }
        holder.setData(showInfo, _mImageFetcher);
        holder.shadowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, P02ModelActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(P02ModelActivity.INPUT_MODEL, ( _data.get(final_position)).getModelRef());
                intent.putExtras(bundle);
                _context.startActivity(intent);
            }
        });

        holder.showIV.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                S03SHowActivity.ACTION_MESSAGE = S02ShowClassify.ACTION_MESSAGE;
                Intent intent = new Intent(_context, S03SHowActivity.class);
                intent.putExtra("position", final_position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(S03SHowActivity.INPUT_SHOW_ENTITY_ID, ( _data.get(final_position)).get_id());
                intent.putExtras(bundle);
                _context.startActivity(intent);
            }
        });

        return convertView;
    }
}
