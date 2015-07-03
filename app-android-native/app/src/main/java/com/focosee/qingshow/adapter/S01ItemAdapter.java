package com.focosee.qingshow.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
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
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {

        MongoShow show = getItemData(position);

        holder.setImgeByUrl(R.id.item_s01_preground, show.coverForeground, 0.5f);
        holder.setImgeByUrl(R.id.item_s01_img, show.cover, 1f);
        MongoPeople user = new MongoPeople();
        if(null != show.__context){
            if(null != show.__context.createdBy){
                user = show.__context.createdBy;
            }
        }
        if(null != user.portrait || "".equals(user.portrait))
            holder.setImgeByUrl(R.id.item_s01_head_img, user.portrait, 1f);

        ((TextView)holder.getView(R.id.item_s01_nikename)).setText(user.nickname);
        ((TextView)holder.getView(R.id.item_s01_likeNum)).setText(String.valueOf(show.numLike));


    }
}
