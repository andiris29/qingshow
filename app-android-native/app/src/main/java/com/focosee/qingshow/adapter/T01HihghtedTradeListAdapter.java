package com.focosee.qingshow.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2015/9/2.
 */
public class T01HihghtedTradeListAdapter extends AbsAdapter<MongoTrade> {
    /**
     * viewType的顺序的layoutId的顺序一致
     *
     * @param datas
     * @param context
     * @param layoutId
     */
    public T01HihghtedTradeListAdapter(@NonNull List<MongoTrade> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }
}
