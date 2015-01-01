package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S05ItemActivity;
import com.focosee.qingshow.entity.ShowDetailEntity;

import java.util.ArrayList;

public class S07ListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ShowDetailEntity.RefItem> data;

    public S07ListAdapter(Context context, ArrayList<ShowDetailEntity.RefItem> items) {
        this.context = context;
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
            convertView = layoutInflater.inflate(R.layout.item_s07_item_list, null);

            holderView = new HolderView();
            holderView.category = (TextView) convertView.findViewById(R.id.item_S07_category);
            holderView.name = (TextView) convertView.findViewById(R.id.item_S07_name);
            holderView.detailButton = (Button) convertView.findViewById(R.id.item_S07_detail_btn);

            convertView.setTag(holderView);
        }

        holderView = (HolderView) convertView.getTag();

        holderView.name.setText(data.get(position).getItemName());
        holderView.category.setText(data.get(position).getItemCategory());
        holderView.detailButton.setTag(position);
        holderView.detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, S05ItemActivity.class);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class HolderView {
        public TextView category;
        public TextView name;
        public Button detailButton;
    }
}
