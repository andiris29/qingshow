package com.focosee.qingshow.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.focosee.qingshow.R;

/**
 * Created by 华榕 on 2015/5/23.
 */

class ViewHolder extends RecyclerView.ViewHolder{

    public ViewHolder(View itemView) {
        super(itemView);
    }
}

public class U14CollectionAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context context;

    public U14CollectionAdapter(Context context){
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_u14_collection_list, null);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }






}
