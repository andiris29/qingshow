package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S01MatchShowsActivity;
import com.focosee.qingshow.activity.S03SHowActivity;
import com.focosee.qingshow.activity.U01UserActivity;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.util.adapter.AbsAdapter;
import com.focosee.qingshow.util.adapter.AbsViewHolder;
import com.huewu.pla.lib.internal.PLA_AbsListView;

import org.w3c.dom.Text;

import java.util.LinkedList;

/**
 * Created by Administrator on 2015/7/1.
 */
public class S01ItemAdapter extends AbsAdapter<MongoShow> {

    private RecyclerView.OnScrollListener onScrollListener;

    public S01ItemAdapter(@NonNull LinkedList<MongoShow> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, final int position) {

        if(null == getItemData(position))return;

        final MongoShow show = getItemData(position);

        holder.getView(R.id.item_s01_matchlist_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, S03SHowActivity.class);
                intent.putExtra(S03SHowActivity.INPUT_SHOW_ENTITY_ID, show._id);
                intent.putExtra(S03SHowActivity.CLASS_NAME, "S01MatchShowsActivity");
                intent.putExtra(S03SHowActivity.POSITION, position);
                context.startActivity(intent);
            }
        });
        holder.getView(R.id.item_s01_bottom_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == show.ownerRef) return;
                Intent intent = new Intent(context, U01UserActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", show.ownerRef);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        if(!TextUtils.isEmpty(show.coverForeground))
            holder.setImgeByController(R.id.item_s01_preground, ImgUtil.getImgSrc(show.coverForeground, ImgUtil.LARGE), ValueUtil.pre_img_AspectRatio);
        if(!TextUtils.isEmpty(show.cover))
            holder.setImgeByController(R.id.item_s01_img, ImgUtil.getImgSrc(show.cover, ImgUtil.LARGE), ValueUtil.match_img_AspectRatio);
        holder.setText(R.id.item_s01_likeNum, String.valueOf(show.numLike));
        holder.setText(R.id.item_s01_time, TimeUtil.formatDateTime_CN_Pre(show.create));
        if(null == show.ownerRef)return;

        MongoPeople user = show.ownerRef;

        if(!TextUtils.isEmpty(user.portrait)) {
            holder.setImgeByController(R.id.item_s01_head_img, ImgUtil.getImgSrc(user.portrait, ImgUtil.PORTRAIT_LARGE), 1f);
        }
        if ("0".equals(user.rank)) {
            holder.setVisibility(R.id.iv_rank_gold,View.VISIBLE);
        }else {
            holder.setVisibility(R.id.iv_rank_gold,View.GONE);
      }

        holder.setText(R.id.item_s01_nikename, user.nickname);

    }

}
