package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.TimeUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S03SHowActivity;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.Bean;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.FontsUtil;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/28.
 */
public class U01PushAdapter extends AbsAdapter<MongoShow> {

    private int plusNum = 0;
    private int lastR1 = 0;
    private int lastR2 = 0;

    private int missNum = 0;


    private Map<Integer, Integer> map;//k : postion v : groupNum

    public U01PushAdapter(List<MongoShow> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    /**
     * 0: R.layout.item_u01_push
     * 1: R.layout.item_u01_date
     * 2: R.layout.item_s17
     */

    @Override
    public int getItemViewType(int position) {

        if (position == 0) return 0;

        if(position == 1) return 1;

        if(isDataItem(position)){
            return 1;
        }else{
            return 2;
        }
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        if (datas.size() == 0) {
            return;
        }

        if (position == 0)
            return;

        if(position == 1) {
            bindDateHolder(holder, position - missNum);
            return;
        }

        if(isDataItem(position)){
            bindDateHolder(holder, position - missNum);
            missNum++;
        }else{
            bindShowHolder(holder, position - missNum);
        }

    }

    private boolean isDataItem(int position){
        if(null == datas || 0 == datas.size()) return false;
        MongoShow item = datas.get(position - missNum);
        MongoShow lastItem = datas.get(position - missNum - 1);
        if (lastItem.recommend.date.equals(item.recommend.date)) {
            return false;
        } else {
            return true;
        }
    }

    private void bindDateHolder(AbsViewHolder holder, int position) {
        MongoShow item = datas.get(position);
        GregorianCalendar calendar = item.recommend.date;
        FontsUtil.changeFont(context, (TextView) holder.getView(R.id.day), "fonts/HelveticaInserat-Roman-SemiBold.ttf");
        holder.setText(R.id.year, String.valueOf(calendar.get(calendar.YEAR)))
                .setText(R.id.manth, TimeUtil.formatManthInfo(calendar.get(calendar.MONTH)))
                .setText(R.id.day, String.valueOf(calendar.get(calendar.DAY_OF_MONTH)))
                .setText(R.id.week, TimeUtil.formatWeekInfo(calendar.get(calendar.DAY_OF_WEEK)))
            .setText(R.id.des,item.recommend.description);
    }

    private void bindShowHolder(AbsViewHolder holder, int position) {
        MongoShow item = datas.get(position - map.get(position) - 1);
//        holder.setImgeByUrl(R.id.cover, item.cover).setText(R.id.description, item.description);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, S03SHowActivity.class);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        if(null == datas || datas.size() == 0) return 1;
        return datas.size() + 1 + TimeUtil.day_between(datas.get(0).recommend.date, datas.get(datas.size() - 1).recommend.date);
    }

}
