package com.focosee.qingshow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.focosee.qingshow.R;
import com.focosee.qingshow.entity.ModelShowEntity;
import com.focosee.qingshow.widget.MImageView_OriginSize;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class P02ModelItemListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ModelShowEntity> itemList;

    public P02ModelItemListAdapter(Context concreteContext, ArrayList<ModelShowEntity> concreteItemList) {
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
            convertView = layoutInflater.inflate(R.layout.item_p02_show_list, null);

            viewHolder = new ItemViewHolder();
            viewHolder.image = (MImageView_OriginSize) convertView.findViewById(R.id.item_p02_item_image);
            viewHolder.detailButton = (ImageButton) convertView.findViewById(R.id.item_p02_item_detail_button);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder) convertView.getTag();
        }

        viewHolder.image.setOriginWidth(itemList.get(position).getCoverWidth());
        viewHolder.image.setOriginHeight(itemList.get(position).getCoverHeight());
        ImageLoader.getInstance().displayImage(itemList.get(position).getCover(), viewHolder.image);
        viewHolder.detailButton.setTag(position);
        viewHolder.detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.valueOf(((ImageButton) v).getTag().toString()).intValue();
                Toast.makeText(context, "click item " + String.valueOf(position) + " button", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    public void resetData(ArrayList<ModelShowEntity> newData) {
        this.itemList = newData;
    }

    public void addData(ArrayList<ModelShowEntity> moreData) {
        this.itemList.addAll(this.itemList.size(), moreData);
    }

    class ItemViewHolder {
        public MImageView_OriginSize image;
        public ImageButton detailButton;
    }

}
