package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.P04BrandActivity;
import com.focosee.qingshow.entity.mongo.MongoItem;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.widget.MImageView_OriginSize;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/2/12.
 */
public class S02ItemRandomAdapter extends AbsWaterfallAdapter {


    private String updateTimeString = "15:00更新";
    private String updateDateString = "2015/01/01";
    private String updateWeekString = "星期四 THURS";

    private Context context;

    private AnimateFirstDisplayListener animateFirstListener = new AnimateFirstDisplayListener();


    public S02ItemRandomAdapter(Context context, int resourceId, ImageLoader mImageFetcher) {
        super(context, resourceId, mImageFetcher);
        this.context = context;
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
        ItemViewHolder holder;
        final MongoItem showInfo = (MongoItem) _data.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
            convertView = layoutInflator.inflate(_resourceId, null);
            holder = new ItemViewHolder();
            holder.showIV = (MImageView_OriginSize) convertView.findViewById(R.id.item_p02_random_image);
            holder.priceTV = (TextView) convertView.findViewById(R.id.item_s02_price);
            holder.sorcePriceTV = (TextView) convertView.findViewById(R.id.item_s02_source_price);
            convertView.setTag(holder);
        }
        holder = (ItemViewHolder) convertView.getTag();
        holder.showIV.setOriginWidth(showInfo.imageMetadata.width);
        holder.showIV.setOriginHeight(showInfo.imageMetadata.height);

        _mImageFetcher.displayImage(ImgUtil.imgTo2x(showInfo.imageMetadata.url), holder.showIV, AppUtil.getShowDisplayOptions(), animateFirstListener);

        holder.priceTV.setText(showInfo.getPrice());
        if(null != showInfo.brandDiscountInfo) {
            holder.sorcePriceTV.setText(showInfo.getSourcePrice());
            holder.sorcePriceTV.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.showIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, P04BrandActivity.class);
                intent.putExtra(P04BrandActivity.INPUT_ITEM, showInfo);
                context.startActivity(intent);
            }
        });

        return convertView;
    }


    public void addItemLast(LinkedList<MongoItem> datas) {
        _data.addAll(datas);
    }

    public void addItemTop(LinkedList<MongoItem> datas) {
        _data.clear();
        _data.addAll(datas);
    }

    @Override
    public int getCount() {
        return (null == _data) ? 0 : _data.size()+1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? 0 : 1;
    }

    public MongoItem getItemDataAtIndex(int index) {
        if (index >= _data.size()) return null;
        return (MongoItem)_data.get(index);
    }

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



    public void resetUpdateString(JSONObject response) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd_HH:mm");
        Calendar calendar = TimeUtil.getStringToCal(MetadataParser.getRefreshTime(response));
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

    class ItemViewHolder extends AbsViewHolder{
        TextView priceTV;
        TextView sorcePriceTV;
        MImageView_OriginSize showIV;

    }

}
