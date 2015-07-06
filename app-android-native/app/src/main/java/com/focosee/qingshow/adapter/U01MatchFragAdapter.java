package com.focosee.qingshow.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2015/7/3.
 */
public class U01MatchFragAdapter extends U01BaseAdapter<MongoShow>{


    public U01MatchFragAdapter(@NonNull List<MongoShow> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(0 == position) return;
        MongoShow show = getItemData(position);

        holder.setImgeByUrl(R.id.item_u01_match_img, show.cover, 0.5f);
        ((TextView)holder.getView(R.id.item_u01_match_likeNum)).setText(String.valueOf(show.numLike));

    }

    @Override
    public int getItemCount() {
        return null == datas ? 1 : datas.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return 0 == position ? 0 : position - 1;
    }

    @Override
    public MongoShow getItemData(int position) {
        return 0 == position ? null : datas.get(position - 1);
    }
}
