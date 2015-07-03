package com.focosee.qingshow.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2015/7/2.
 */
public class U01BaseAdapter<T> extends AbsAdapter<T>{

    public U01BaseAdapter(@NonNull List<T> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    @Override
    public int getItemViewType(int position) {
        return 0 == position ? 0 : 1;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        if(0 == position){

        }
    }
}
