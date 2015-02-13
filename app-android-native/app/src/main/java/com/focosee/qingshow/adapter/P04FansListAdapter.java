package com.focosee.qingshow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.entity.mongo.MongoPeople;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class P04FansListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MongoPeople> data;

    public P04FansListAdapter(Context context, ArrayList<MongoPeople> data) {
        this.context = context;
        this.data = data;
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
            convertView = layoutInflater.inflate(R.layout.item_p02_follow_people_list, null);
            holderView = new HolderView();

            holderView.imageView = (ImageView) convertView.findViewById(R.id.item_p02_follow_people_image);
            holderView.nameTextView = (TextView) convertView.findViewById(R.id.item_p02_follow_people_name);
            holderView.showNumberTextView = (TextView) convertView.findViewById(R.id.item_p02_follow_people_show);
            holderView.likedNumberTextView = (TextView) convertView.findViewById(R.id.item_p02_follow_people_like);

            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }

        ImageLoader.getInstance().displayImage(data.get(position).getPeoplePortrait(), holderView.imageView);
        holderView.nameTextView.setText(data.get(position).getPeopleName());
        holderView.showNumberTextView.setText(String.valueOf(data.get(position).get__context().numFollowBrands));
        holderView.likedNumberTextView.setText(String.valueOf(data.get(position).get__context().numFollowPeoples));

        return convertView;
    }

    public void resetData(ArrayList<MongoPeople> newData) {
        this.data = newData;
    }

    public void addData(ArrayList<MongoPeople> moreData) {
        this.data.addAll(this.data.size(), moreData);
    }

    class HolderView {
        public ImageView imageView;
        public TextView nameTextView;
        public TextView showNumberTextView;
        public TextView likedNumberTextView;
    }

}