package com.focosee.qingshow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.focosee.qingshow.R;
import com.focosee.qingshow.entity.BrandItemEntity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class P04BrandItemListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<BrandItemEntity> itemList;

    public P04BrandItemListAdapter(Context concreteContext, ArrayList<BrandItemEntity> concreteItemList) {
        context = concreteContext;
        itemList = concreteItemList;
    }

    @Override
    public int getCount() {
        return (null != itemList) ? itemList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return (null != itemList) ? itemList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.item_p04_item_list, null);

            viewHolder = new ItemViewHolder();
            viewHolder.image = (ImageView)convertView.findViewById(R.id.item_p04_item_image);
            viewHolder.discount = (TextView) convertView.findViewById(R.id.item_p04_item_discount);
            viewHolder.detailButton = (ImageButton) convertView.findViewById(R.id.item_p04_item_detail_button);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder)convertView.getTag();
        }

        ImageLoader.getInstance().displayImage(itemList.get(position).getImage(), viewHolder.image);
        viewHolder.discount.setText(itemList.get(position).getDiscount());
        viewHolder.detailButton.setTag(position);
        viewHolder.detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.getInteger(((ImageButton)v).getTag().toString()).intValue();
                Toast.makeText(context, "click item " + String.valueOf(position) + " button", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }


    public void resetData(ArrayList<BrandItemEntity> newData) {
        this.itemList = newData;
    }

    public void addData(ArrayList<BrandItemEntity> moreData) {
        this.itemList.addAll(this.itemList.size(), moreData);
    }

    class ItemViewHolder {
        public ImageView image;
        public TextView discount;
        public ImageButton detailButton;
    }
}
