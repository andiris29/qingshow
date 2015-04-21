package com.focosee.qingshow.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S05ItemActivity;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.util.AppUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class S07ListAdapter extends BaseAdapter {

    private final String TAG = "S07ListAdapter";

    private int ITEMHEIGHT = 100;

    private Context context;
    private ArrayList<MongoItem> data;

    public S07ListAdapter(Context context, ArrayList<MongoItem> items, int itemHeiht) {
        this.context = context;
        this.data = items;
        ITEMHEIGHT = itemHeiht/5;
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
    @SuppressLint("WrongViewCast")
    public View getView(int position, View convertView, ViewGroup parent) {
        HolderView holderView;

        if (null == convertView) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            holderView = new HolderView();
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                    ITEMHEIGHT);
            if ("".equals(data.get(position).getPrice()) || null ==data.get(position).getPrice()) {
                convertView = layoutInflater.inflate(R.layout.item_s07_item_list, null);
                holderView.item_collection = (LinearLayout) convertView.findViewById(R.id.item_s07_collection);
                holderView.category = (TextView) convertView.findViewById(R.id.item_S07_category);
                holderView.brandLogo = (ImageView) convertView.findViewById(R.id.item_S07_brandlogo);
                holderView.name = (TextView) convertView.findViewById(R.id.item_S07_name);
                holderView.detailButton = (Button) convertView.findViewById(R.id.item_S07_detail_btn);
            } else {
                convertView = layoutInflater.inflate(R.layout.item_s07_item_with_price_list, null);
                holderView.originPriceTV = (TextView) convertView.findViewById(R.id.item_S07_with_price_origin_price);
                holderView.priceTV = (TextView) convertView.findViewById(R.id.item_S07_with_price_price);
                holderView.item_collection = (LinearLayout) convertView.findViewById(R.id.item_s07_with_price_collection);
                holderView.category = (TextView) convertView.findViewById(R.id.item_S07_with_price_category);
                holderView.brandLogo = (ImageView) convertView.findViewById(R.id.item_S07_with_price_brandlogo);
                holderView.name = (TextView) convertView.findViewById(R.id.item_S07_with_price_name);
                holderView.detailButton = (Button) convertView.findViewById(R.id.item_S07_with_price_detail_btn);
            }

            holderView.item_collection.setLayoutParams(params);

            convertView.setTag(holderView);
        }

        holderView = (HolderView) convertView.getTag();
        holderView.priceTV = (TextView) convertView.findViewById(R.id.item_S07_with_price_price);

        holderView.name.setText(data.get(position).getItemName());
        holderView.category.setText(data.get(position).getItemCategory());
        if(null != data.get(position).getBrandRef()) {
            ImageLoader.getInstance().displayImage(data.get(position).getBrandRef().getBrandLogo(), holderView.brandLogo, AppUtil.getPortraitDisplayOptions());
        }else{
            holderView.brandLogo.setImageResource(R.drawable.user_head_default);
        }

        holderView.detailButton.setTag(position);
        holderView.detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, S05ItemActivity.class);
                context.startActivity(intent);
            }
        });

        if (!("".equals(data.get(position).getPrice()) || null ==data.get(position).getPrice())) {
            if(null != data.get(position).brandDiscountInfo) {
                holderView.originPriceTV.setText(data.get(position).getSourcePrice());
                holderView.originPriceTV.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }
            if(data.get(position).getPrice() != null){
                holderView.priceTV.setText(data.get(position).getPrice());
            }
        }

        return convertView;
    }

    class HolderView {
        public LinearLayout item_collection;
        public TextView category;
        public ImageView brandLogo;
        public TextView name;
        public Button detailButton;

        public TextView originPriceTV;
        public TextView priceTV;
    }
}
