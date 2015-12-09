package com.focosee.qingshow.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2015/11/19.
 */
public class S24Adapter extends AbsAdapter<MongoShow>{
    public S24Adapter(@NonNull List<MongoShow> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {

    }
}
