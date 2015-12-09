package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S24ShowsDateActivity;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.aggregation.FeedingAggregation;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Administrator on 2015/11/27.
 */
public class S01MatchNewAdapter extends AbsAdapter<FeedingAggregation>{

    private GregorianCalendar calendar;
    public S01MatchNewAdapter(@NonNull List<FeedingAggregation> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
        calendar = new GregorianCalendar();
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
        for (int id : imgId) {
            holder.setVisibility(id, View.INVISIBLE);
        }
        for (int i = 0; i < topShows.size(); i++) {
            if (i > 3) break;
            holder.setImgeByUrl(imgId[i], topShows.get(i).cover).setVisibility(imgId[i], View.VISIBLE);
        }
        final int key = Integer.parseInt(data.key);
        final int from;

        String timeTemplate = "S:00~E:00";

        if (key < 0){
            from = 24 + key;
            holder.setText(R.id.day,  (calendar.get(Calendar.DAY_OF_MONTH) - 1) + "." +  TimeUtil.formatManthInfo(calendar.get(Calendar.MONTH)));

        }else {
            from = key;
            holder.setText(R.id.day,  "TODAY | " + calendar.get(Calendar.DAY_OF_MONTH) + "." +  TimeUtil.formatManthInfo(calendar.get(Calendar.MONTH)));
        }
        final int to = from + 1;

        holder.setText(R.id.time, timeTemplate.replace("S", from + "").replace("E", to + ""));
        if (QSModel.INSTANCE.loggedin() && data.indexOfCurrentUser != -1){
            holder.setImgeByUrl(R.id.current_head, QSModel.INSTANCE.getUser().portrait);
        }else{
            holder.setVisibility(R.id.current_head, View.GONE);
            holder.setVisibility(R.id.rank, View.GONE);
        }
        // top owner
        holder.setText(R.id.num_owners, data.numOwners + "");
        RecyclerView portraits = holder.getView(R.id.top_owner);
        portraits.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        portraits.setAdapter(new TopOwnerAdapter(data.topOwners, context, R.layout.item_portrait));
        //jump
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, S24ShowsDateActivity.class);
                if (key > 0){
                    intent.putExtra("MATCH_NEW_FROM", new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),from,0));
                    intent.putExtra("MATCH_NEW_TO", new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),to,0));
                }else {
                    intent.putExtra("MATCH_NEW_FROM", new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) - 1,from,0));
                    intent.putExtra("MATCH_NEW_TO", new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) - 1,to,0));

                }

                context.startActivity(intent);
            }
        });
    }
}
