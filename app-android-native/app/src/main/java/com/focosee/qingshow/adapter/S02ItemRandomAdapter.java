package com.focosee.qingshow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/2/12.
 */
public class S02ItemRandomAdapter extends AbsWaterfallAdapter<MongoItem> {

    private Context context;

    public S02ItemRandomAdapter(Context context, int resourceId, ImageLoader mImageFetcher) {
        super(context, resourceId, mImageFetcher);
        this.context = context;
    }

    @Override
    public void refreshDate(JSONObject response) {

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        S02ItemViewHolder viewHolder;

        if(null == convertView){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_randomlist, null);
            viewHolder = new S02ItemViewHolder();
            viewHolder.itemImage = (SimpleDraweeView) convertView.findViewById(R.id.item_randomlist_image);
            viewHolder.likeBtn = (ImageView) convertView.findViewById(R.id.like_image);
            viewHolder.likeNum = (TextView) convertView.findViewById(R.id.item_randomlist_likeNum);
            viewHolder.describe = (TextView) convertView.findViewById(R.id.item_randomlist_describe);
            viewHolder.price = (TextView) convertView.findViewById(R.id.item_randomlist_price);
            convertView.setTag(viewHolder);
        }

        viewHolder = (S02ItemViewHolder) convertView.getTag();

        return convertView;
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    public static class S02ItemViewHolder {
        public SimpleDraweeView itemImage;
        public ImageView likeBtn;
        public TextView likeNum;
        public TextView describe;
        public TextView price;
    }

}
