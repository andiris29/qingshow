package com.focosee.qingshow.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.adapter.AbsViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2015/7/3.
 */
public class U01FansFragAdapter extends U01BaseAdapter<MongoPeople>{


    public U01FansFragAdapter(@NonNull List<MongoPeople> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    /**
     * 0:R.layout.item_u01_push
     * 1:R.layout.item_u01_fan_and_followers
     * @param position
     * @return
     */

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(0 == position) return;
        MongoPeople people = getItemData(position);
        holder.getView(R.id.item_u01_fans_follow).setVisibility(View.GONE);
        holder.setImgeByUrl(R.id.item_u01_fans_image, people.portrait);
        holder.setText(R.id.item_u01_fans_name, people.nickname);
//        holder.setText(R.id.item_u01_fans_cloth_number, people.)
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
    public MongoPeople getItemData(int position) {
        return 0 == position ? null : datas.get(position - 1);
    }
}
