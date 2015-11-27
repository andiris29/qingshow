package com.focosee.qingshow.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.focosee.qingshow.R;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.aggregation.FeedingAggregation;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2015/11/27.
 */
public class S01MatchNewAdapter extends AbsAdapter<FeedingAggregation>{

    public S01MatchNewAdapter(@NonNull List<FeedingAggregation> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        FeedingAggregation data = datas.get(position);
        int[] imgId = new int[]{R.id.item_new_img1, R.id.item_new_img2, R.id.item_new_img3};
        List<MongoShow> topShows = data.topShows;
        for (int i = 0; i < topShows.size(); i++) {
            if (i > 3) break;
            holder.setImgeByUrl(imgId[i], topShows.get(i).cover).setVisibility(imgId[i], View.VISIBLE);
        }
        String start = data.key;
        String end = Integer.parseInt(start) + 1 + "";
        String timeTemplate = "S:00~E:00";
        holder.setText(R.id.time, timeTemplate.replace("S", start).replace("E", end));
        holder.setText(R.id.day, "");
        if (QSModel.INSTANCE.loggedin() && data.indexOfCurrentUser != -1){
            holder.setImgeByUrl(R.id.current_head, QSModel.INSTANCE.getUser().portrait);
        }else{
            holder.setVisibility(R.id.current_head, View.GONE);
            holder.setVisibility(R.id.rank, View.GONE);
        }
    }
}
