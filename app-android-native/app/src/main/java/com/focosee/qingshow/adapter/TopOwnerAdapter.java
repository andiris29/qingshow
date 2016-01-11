package com.focosee.qingshow.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2015/11/30.
 */
public class TopOwnerAdapter extends AbsAdapter<MongoPeople> {
    public TopOwnerAdapter(@NonNull List<MongoPeople> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        holder.setImgeByUrl(R.id.portrait, ImgUtil.getImgSrc(datas.get(position).portrait, "50"));
        if ("0".equals(datas.get(position).rank)) {
            holder.setVisibility(R.id.iv_rank_gold, View.VISIBLE);
        }else {
            holder.setVisibility(R.id.iv_rank_gold, View.GONE);
        }
    }
}
