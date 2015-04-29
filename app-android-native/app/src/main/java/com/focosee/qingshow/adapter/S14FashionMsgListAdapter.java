package com.focosee.qingshow.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.focosee.qingshow.R;

/**
 * Created by Administrator on 2015/4/23.
 */
public class S14FashionMsgListAdapter extends RecyclerView.Adapter<S14FashionMsgListAdapter.ItemViewHold>{


    private Context context;

    @Override
    public ItemViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHold(LayoutInflater.from(context).inflate(R.layout.activity_s14_fashion_msg, null));
    }

    @Override
    public void onBindViewHolder(ItemViewHold holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public static class ItemViewHold extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView describe;
        public TextView btn;

        public ItemViewHold(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_s14_imageView);
            describe = (TextView) itemView.findViewById(R.id.item_s14_describe);
            btn = (TextView) itemView.findViewById(R.id.item_s14_btn);
        }
    }





}
