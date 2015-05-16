package com.focosee.qingshow.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoShow;

import java.util.ArrayList;
import java.util.LinkedList;

public class S08TrendListAdapter extends BaseAdapter {

    public Context context;
    private LinkedList<MongoShow> datas;

    public S08TrendListAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return null == datas ? 0 : datas.size();
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

        MongoShow show = datas.get(position);

        if(null == convertView){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_s08_trend_list, null);
            viewHolder = new S08ItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        viewHolder = (S08ItemViewHolder)convertView.getTag();
        String uri = "";
        if(show.getCover().equals("") || null == show.getCover()){
            uri = show.getHorizontalCover();
            viewHolder.imageView.setAspectRatio(show.getHorizontalCoverWidth() / show.getHorizontalCoverHeight());
        }else{
            uri = show.getCover();
            viewHolder.imageView.setAspectRatio(show.getCoverWidth() / show.getCoverHeight());
        }

        viewHolder.imageView.setImageURI(Uri.parse(uri));
        //TODO

        return convertView;
    }

    public void resetData(LinkedList<MongoShow> datas){
        this.datas = datas;
    }

    public void addItemLast(LinkedList<MongoShow> datas){
        this.datas.addAll(datas);
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
