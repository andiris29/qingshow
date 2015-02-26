package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.P02ModelActivity;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.widget.MImageView_OriginSize;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

class HomeViewHolder extends AbsViewHolder {


    // Public interface
    public void setData(MongoShow entity, ImageLoader imageLoader) {

        showIV.setOriginWidth(entity.getCoverWidth());
        showIV.setOriginHeight(entity.getCoverHeight());

        imageLoader.displayImage(ImgUtil.imgTo2x(entity.getShowCover()), showIV, AppUtil.getShowDisplayOptions(),animateFirstListener);
        imageLoader.displayImage(entity.getModelPhoto(), modelIV, AppUtil.getPortraitDisplayOptions(),animateFirstListener);
        modelNameTV.setText(entity.getModelName());
        modelHeightWeightTV.setText(entity.getModelHeightAndHeightWithFormat());
        loveTV.setText(entity.getShowNumLike());
        //TODO 换图片
        if(entity.getModelRef().getModelIsFollowedByCurrentUser()){
            loveIV.setBackgroundResource(R.drawable.model_cell_icon02_noticeno);
        }
    }

    MImageView_OriginSize showIV;
    ImageView modelIV;
    TextView modelNameTV;
    TextView modelHeightWeightTV;
//    TextView modelHeightTV;
//    TextView modelWeightTV;
    TextView loveTV;
    ImageView loveIV;
    public ImageView shadowView;


//    // Helper property
    private AnimateFirstDisplayListener animateFirstListener = new AnimateFirstDisplayListener();

    // Helper class Animation
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

public class HomeWaterfallAdapter extends AbsWaterfallAdapter<MongoShow> {

    private String updateTimeString = "15:00更新";
    private String updateDateString = "2015/01/01";
    private String updateWeekString = "星期四 THURS";

    public HomeWaterfallAdapter(Context context, int resourceId, ImageLoader mImageFetcher) {
        super(context, resourceId, mImageFetcher);
    }

    @Override
    public void refreshDate(JSONObject response) {
        resetUpdateString(response);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position == 0) {
            UpdateCellHolderView updateCellHolderView;

            if (null == convertView) {
                LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
                convertView = layoutInflator.inflate(R.layout.item_refresh_independent, null);
                updateCellHolderView = new UpdateCellHolderView();
                updateCellHolderView.updateTimeTV = (TextView) convertView.findViewById(R.id.item_refresh_independent_update_time);
                updateCellHolderView.updateDateTV = (TextView) convertView.findViewById(R.id.item_refresh_independent_update_date);
                updateCellHolderView.updateWeekTV = (TextView) convertView.findViewById(R.id.item_refresh_independent_update_week);

                convertView.setTag(updateCellHolderView);
            }

            updateCellHolderView = (UpdateCellHolderView) convertView.getTag();

            updateCellHolderView.updateTimeTV.setText(updateTimeString);
            updateCellHolderView.updateDateTV.setText(updateDateString);
            updateCellHolderView.updateWeekTV.setText(updateWeekString);

            return convertView;
        }

        position--;
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
        }else{
            holder = (HomeViewHolder) convertView.getTag();
        }
        _mImageFetcher.cancelDisplayTask(holder.modelIV);
        _mImageFetcher.cancelDisplayTask(holder.showIV);
        if(showInfo.getShowIsFollowedByCurrentUser())
            holder.loveIV.setBackgroundResource(R.drawable.root_cell_icon_notice_hover);
        else
            holder.loveIV.setBackgroundResource(R.drawable.root_cell_icon_notice);

        holder.setData(showInfo, _mImageFetcher);
        final int final_position = position;
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

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? 0 : 1;
    }

    @Override
    public int getCount() {
        return (null == _data) ? 0 : _data.size()+1;
    }

    public void resetUpdateString(JSONObject response) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd_HH:mm");
        Calendar calendar = TimeUtil.getStringToCal(MetadataParser.getRefreshTime(response));
        Date date = calendar.getTime();
        String originDateString = simpleDateFormat.format(date);
        updateTimeString = originDateString.split("_")[1] + " 更新";
        updateDateString = originDateString.split("_")[0];
        updateWeekString = TimeUtil.formatWeekInfo(calendar.get(Calendar.DAY_OF_WEEK));
    }


    class UpdateCellHolderView {
        public TextView updateTimeTV;
        public TextView updateDateTV;
        public TextView updateWeekTV;
    }


}
