package com.focosee.qingshow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;

public class S08TrendListAdapter extends BaseAdapter {

    public Context context;

    public S08TrendListAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return 20;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        S08ItemViewHolder viewHolder;

        if(null == convertView){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_s08_trend_list, null);
            viewHolder = new S08ItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        viewHolder = (S08ItemViewHolder)convertView.getTag();
        viewHolder.imageView.setAspectRatio(1.0f);
        viewHolder.imageView.setImageURI(null);
        //TODO

        return convertView;
    }

    public static class S08ItemViewHolder {
        public SimpleDraweeView imageView;
        public TextView describe;
        public TextView btn;

        public S08ItemViewHolder(View view){
            imageView = (SimpleDraweeView) view.findViewById(R.id.item_s08_imageView);
            describe = (TextView) view.findViewById(R.id.item_s08_describe);
            btn = (TextView) view.findViewById(R.id.item_s08_btn);
        }
    }
}
