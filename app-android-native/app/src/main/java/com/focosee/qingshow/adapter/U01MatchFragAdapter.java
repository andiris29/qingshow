package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S03SHowActivity;
import com.focosee.qingshow.model.S03Model;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.util.ValueUtil;
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
        final MongoShow show = getItemData(position);

        holder.setImgeByUrl(R.id.item_u01_match_img, show.cover, ValueUtil.match_img_AspectRatio);
        ((TextView)holder.getView(R.id.item_u01_match_likeNum)).setText(String.valueOf(show.numLike));
        holder.setImgeByUrl(R.id.item_u01_match_preImg, ImgUtil.getImgSrc(show.coverForeground, ImgUtil.Large), ValueUtil.pre_img_AspectRatio);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S03Model.INSTANCE.setShow(show);
                context.startActivity(new Intent(context, S03SHowActivity.class));
            }
        });

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
