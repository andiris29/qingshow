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
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;
import java.util.LinkedList;

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

        final MongoShow show = getItemData(position);

        holder.getView(R.id.item_s01_matchlist_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S03Model.INSTANCE.setShow(show);
                context.startActivity(new Intent(context, S03SHowActivity.class));
            }
        });
        holder.getView(R.id.item_s01_bottom_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                U01Model.INSTANCE.setUser(show.__context.createdBy);
                context.startActivity(new Intent(context, U01UserActivity.class));
            }
        });
        holder.setImgeByUrl(R.id.item_s01_preground, show.coverForeground, ValueUtil.pre_img_AspectRatio);
        holder.setImgeByUrl(R.id.item_s01_img, show.cover, ValueUtil.match_img_AspectRatio);
        MongoPeople user = new MongoPeople();
        if(null != show.__context){
            if(null != show.__context.createdBy){
                user = show.__context.createdBy;
            }
        }

        if(null != user.portrait || "".equals(user.portrait))
            holder.setImgeByUrl(R.id.item_s01_head_img, user.portrait, 1f);

        holder.setText(R.id.item_s01_time, null == TimeUtil.formatDateTime_CN_Pre(show.create) ? "刚刚" :TimeUtil.formatDateTime_CN_Pre(show.create) + "前");
        holder.setText(R.id.item_s01_nikename, user.nickname);
        holder.setText(R.id.item_s01_likeNum, String.valueOf(show.numLike));

    }

}
