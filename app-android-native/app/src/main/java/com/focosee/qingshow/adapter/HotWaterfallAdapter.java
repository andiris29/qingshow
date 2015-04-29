package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S02ShowClassify;
import com.focosee.qingshow.activity.S03SHowActivity;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.widget.MImageView_OriginSize;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.json.JSONObject;

public class HotWaterfallAdapter extends AbsWaterfallAdapter<MongoShow> {

    public HotWaterfallAdapter(Context context, int resourceId, ImageLoader mImageFetcher) {
        super(context, resourceId, mImageFetcher);
    }

    @Override
    public void refreshDate(JSONObject response) {
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        HomeViewHolder holder;
        MongoShow showInfo = _data.get(position);

        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
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
        if(showInfo.getShowIsFollowedByCurrentUser())
            holder.loveIV.setImageResource(R.drawable.root_cell_icon_notice_hover);
        else
            holder.loveIV.setImageResource(R.drawable.root_cell_icon_notice);
        holder.setData(showInfo, _mImageFetcher);
        final int final_position = position;
        holder.shadowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(_context, P02ModelActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable(P02ModelActivity.INPUT_MODEL, ( _data.get(final_position)).getModelRef());
//                intent.putExtras(bundle);
//                _context.startActivity(intent);
            }
        });

        holder.showIV.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                S03SHowActivity.ACTION_MESSAGE = S02ShowClassify.ACTION_MESSAGE;
                Intent intent = new Intent(_context, S03SHowActivity.class);
                intent.putExtra("position", position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(S03SHowActivity.INPUT_SHOW_ENTITY_ID, ( _data.get(final_position)).get_id());
                intent.putExtras(bundle);
                _context.startActivity(intent);
            }
        });

        return convertView;
    }

}
