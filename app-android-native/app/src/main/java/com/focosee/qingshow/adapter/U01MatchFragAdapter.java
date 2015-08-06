package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S03SHowActivity;
import com.focosee.qingshow.activity.U01UserActivity;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.ValueUtil;
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

        holder.getView(R.id.item_s01_matchlist_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, S03SHowActivity.class);
                intent.putExtra(S03SHowActivity.INPUT_SHOW_ENTITY_ID, show._id);
                intent.putExtra(S03SHowActivity.CLASS_NAME, U01UserActivity.class.getSimpleName());
                context.startActivity(intent);
            }
        });
        holder.getView(R.id.item_s01_bottom_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null == show.ownerRef)return;
                Intent intent = new Intent(context, U01UserActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", show.ownerRef);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        holder.setImgeByUrl(R.id.item_s01_preground, ImgUtil.getImgSrc(show.coverForeground, ImgUtil.LARGE), ValueUtil.pre_img_AspectRatio);
        holder.setImgeByUrl(R.id.item_s01_img, ImgUtil.getImgSrc(show.cover, ImgUtil.LARGE), ValueUtil.match_img_AspectRatio);
        MongoPeople user = new MongoPeople();
        if(null != show.ownerRef){
            user = show.ownerRef;
        }

        if(TextUtils.isEmpty(user.portrait))
            holder.setImgeByUrl(R.id.item_s01_head_img, ImgUtil.getImgSrc(user.portrait, ImgUtil.PORTRAIT_LARGE), 1f);

        holder.setText(R.id.item_s01_time, null == TimeUtil.formatDateTime_CN_Pre(show.create) ? "刚刚" : TimeUtil.formatDateTime_CN_Pre(show.create) + "前");
        holder.setText(R.id.item_s01_nikename, user.nickname);
        holder.setText(R.id.item_s01_likeNum, String.valueOf(show.numLike));

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
