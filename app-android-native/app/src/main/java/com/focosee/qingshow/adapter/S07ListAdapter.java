package com.focosee.qingshow.adapter;

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
import android.widget.TextView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S05ItemActivity;
import com.focosee.qingshow.entity.ShowDetailEntity;
import com.focosee.qingshow.util.AppUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;

public class S07ListAdapter extends BaseAdapter {

    private final String TAG = "S07ListAdapter";

    private final int ITEMHEIGHT = 200;

    private Context context;
    private boolean withPrice;
    private ArrayList<ShowDetailEntity.RefItem> data;

    public S07ListAdapter(Context context, boolean withPrice, ArrayList<ShowDetailEntity.RefItem> items) {
        this.context = context;
        this.withPrice = withPrice;
        this.data = items;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        HolderView holderView;

        if (null == convertView) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            if (this.withPrice) {
                convertView = layoutInflater.inflate(R.layout.item_s07_item_with_price_list, null);
            } else {
                convertView = layoutInflater.inflate(R.layout.item_s07_item_list, null);
            }

            holderView = new HolderView();
            holderView.item_collection = (LinearLayout) convertView.findViewById(R.id.item_s07_collection);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                    ITEMHEIGHT);
            holderView.item_collection.setLayoutParams(params);
            holderView.category = (TextView) convertView.findViewById(R.id.item_S07_category);
            holderView.brandLogo = (ImageView) convertView.findViewById(R.id.item_S07_brandlogo);
            holderView.name = (TextView) convertView.findViewById(R.id.item_S07_name);
            holderView.detailButton = (Button) convertView.findViewById(R.id.item_S07_detail_btn);

            if (this.withPrice) {
                holderView.originPriceTV = (TextView) convertView.findViewById(R.id.item_S07_with_price_origin_price);
                holderView.priceTV = (TextView) convertView.findViewById(R.id.item_S07_with_price_price);
            }

            convertView.setTag(holderView);
        }

        holderView = (HolderView) convertView.getTag();

        holderView.name.setText(data.get(position).getItemName());
        holderView.category.setText(data.get(position).getItemCategory());
        ImageLoader.getInstance().displayImage(data.get(position).getBrandPortrait(), holderView.brandLogo, AppUtil.getPortraitDisplayOptions());

        holderView.detailButton.setTag(position);
        holderView.detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, S05ItemActivity.class);
                context.startActivity(intent);
            }
        });

        if (this.withPrice) {
            holderView.originPriceTV.setText(data.get(position).getOriginPrice());
            holderView.originPriceTV.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holderView.priceTV.setText(data.get(position).getPrice());
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
