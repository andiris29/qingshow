package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S03SHowActivity;
import com.focosee.qingshow.activity.U01UserActivity;
import com.focosee.qingshow.model.S03Model;
import com.focosee.qingshow.model.U01Model;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.util.adapter.AbsViewHolder;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2015/7/3.
 */
public class U01CollectionFragAdapter extends U01BaseAdapter<MongoShow>{


    public U01CollectionFragAdapter(@NonNull List<MongoShow> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    /**
     * 0:R.layout.item_u01_push
     * 1:R.layout.item_u01_collection_createby
     * 2:R.layout.item_u01_collection_qingshow
     * @param position
     * @return
     */

    @Override
    public int getItemViewType(int position) {
        if(null == datas || Collections.emptyMap() == datas || position == 0){
            return 0;
        }

        if(null == datas.get(position - 1).__context){
            return 2;
        }

        if(null == datas.get(position - 1).__context.createdBy){
            return 2;
        }
        return 1;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        MongoShow show = getItemData(position);
        switch (getItemViewType(position)){
            case 1 :
                bindCreateBy(holder, show);
                break;
            case 2 :
                holder.setImgeByUrl(R.id.item_u01_collection_qingshow_img, show.cover);
                break;
        }
    }

    private void bindCreateBy(AbsViewHolder holder, final MongoShow show){
        holder.setImgeByUrl(R.id.item_u01_collection_img, show.cover, 0.72f);
        holder.setImgeByUrl(R.id.item_u01_collection_preground, show.coverForeground, 0.56f);
        MongoPeople people = show.__context.createdBy;
        holder.getView(R.id.item_u01_collection_top_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S03Model.INSTANCE.setShow(show);
                context.startActivity(new Intent(context, S03SHowActivity.class));
            }
        });
        holder.getView(R.id.item_u01_collection_bottom_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                U01Model.INSTANCE.setUser(show.__context.createdBy);
                context.startActivity(new Intent(context, U01UserActivity.class));
            }
        });
        if(null != people.portrait)
            holder.setImgeByUrl(R.id.item_u01_collection_head_img, people.portrait);
        holder.setText(R.id.item_u01_collection_nikename, people.nickname);
        holder.setText(R.id.item_u01_collection_likeNum, String.valueOf(show.numLike));
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
