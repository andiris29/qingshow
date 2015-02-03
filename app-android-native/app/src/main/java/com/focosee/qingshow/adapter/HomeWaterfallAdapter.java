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
import com.focosee.qingshow.entity.ShowListEntity;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.widget.MImageView_OriginSize;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

class HomeViewHolder extends AbsViewHolder {


    // Public interface
    public void setData(ShowListEntity entity, ImageLoader imageLoader) {
//        holder.showIV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)_myHeight));
//        showIV.setLayoutParams(new LinearLayout.LayoutParams(entity.getCoverWidth(), entity.getCoverHeight()));

        showIV.setOriginWidth(entity.getCoverWidth());
        showIV.setOriginHeight(entity.getCoverHeight());

        imageLoader.displayImage(entity.getShowCover(), showIV, AppUtil.getShowDisplayOptions(), animateFirstListener);
        imageLoader.displayImage(entity.getModelPhoto(), modelIV, AppUtil.getPortraitDisplayOptions(), animateFirstListener);
        modelNameTV.setText(entity.getModelName());
        modelHeightWeightTV.setText(entity.getModelHeightAndHeightWithFormat());
//        modelHeightTV.setText(entity.getModelHeight());
//        modelWeightTV.setText(entity.getModelWeight());
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
                Log.i("test", "load complete not null");
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

public class HomeWaterfallAdapter extends AbsWaterfallAdapter {

    private String updateTimeString = "15:00更新";
    private String updateDateString = "2015/01/01";
    private String updateWeekString = "星期四 THURS";

    public HomeWaterfallAdapter(Context context, int resourceId, ImageLoader mImageFetcher) {
        super(context, resourceId, mImageFetcher);
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
        ShowListEntity showInfo = (ShowListEntity) _data.get(position);

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
                bundle.putSerializable(P02ModelActivity.INPUT_MODEL, ((ShowListEntity) _data.get(final_position)).getModelRef());
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

    public void addItemLast(LinkedList<ShowListEntity> datas) {
        _data.addAll(datas);
    }

    public void addItemTop(LinkedList<ShowListEntity> datas) {
        _data.clear();
        _data.addAll(datas);
    }

    public ShowListEntity getItemDataAtIndex(int index) {
        index--;
        if (index >= _data.size() || index < 0) return null;
        return (ShowListEntity)_data.get(index);
    }

    public void resetUpdateString(JSONObject response) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd_HH:mm");
        Calendar calendar = TimeUtil.getStringToCal(getRefreshTime(response));
        Date date = calendar.getTime();
        String originDateString = simpleDateFormat.format(date);

//        final Calendar calendar = Calendar.getInstance();
//        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        updateTimeString = originDateString.split("_")[1] + " 更新";
        updateDateString = originDateString.split("_")[0];
        updateWeekString = formatWeekInfo(calendar.get(Calendar.DAY_OF_WEEK));
    }

    public String formatWeekInfo(int dayOfWeek) {
        String result;
        switch (dayOfWeek) {
            case 1:
                result = "星期日 SUN";
                break;
            case 2:
                result = "星期一 MON";
                break;
            case 3:
                result = "星期二 TUE";
                break;
            case 4:
                result = "星期三 WED";
                break;
            case 5:
                result = "星期四 THURS";
                break;
            case 6:
                result = "星期五 FRI";
                break;
            default:
                result = "星期六 SAT";
                break;
        }
        return result;
    }

    class UpdateCellHolderView {
        public TextView updateTimeTV;
        public TextView updateDateTV;
        public TextView updateWeekTV;
    }

    private String getRefreshTime(JSONObject response){
        try {
            return ((JSONObject)response.get("metadata")).get("refreshTime").toString();
        } catch (JSONException e) {
            return "请检查下网络。。";
        }
    }

}
