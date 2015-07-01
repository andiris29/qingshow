package com.focosee.qingshow.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/1.
 */
public class S01ItemAdapter extends AbsAdapter<MongoShow> {


    public S01ItemAdapter(@NonNull LinkedList<MongoShow> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    @Override
    public int getItemCount() {
        return 10;
//        return datas == null ? 0 : datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {

//        MongoShow show = getItemData(position);

    }
}
