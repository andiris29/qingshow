package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.TimeUtils;
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
import java.util.List;

/**
 * Created by Administrator on 2015/4/28.
 */
public class U01PushAdapter extends AbsAdapter<MongoShow> {

    private int groupCount = 0;
    private int count = 0;

    public U01PushAdapter(List<MongoShow> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    /**
     * 0: R.layout.item_u01_push
     * 1: R.layout.item_u01_header
     * 2: R.layout.item_u01_date
     */

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                count = 0;
                return 1;
            case 1:
                count++;
                return 2;
            case 2:
                return 0;
            default:
                position = position - 1 - count;
                MongoShow item = datas.get(position);
                MongoShow lastItem = datas.get(position - 1);
                if (lastItem.recommend.date.equals(item.recommend.date)) {
                    return 0;
                } else {
                    return 2;
                }
        }
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 0:
                bindShowHolder(holder, position);
                break;
            case 1:
                bindUserHolder(holder);
            case 2:
                bindDateHolder(holder, position);
                break;
        }
    }

    private void bindUserHolder(AbsViewHolder holder) {
        MongoPeople user = QSModel.INSTANCE.getUser();
        holder.setText(R.id.user_name,user.nickname)
                .setText(R.id.user_hw, user.height + "," + user.weight)
                .setImgeByUrl(R.id.user_head, user.portrait);
    }

    private void bindDateHolder(AbsViewHolder holder, int position) {
        groupCount++;
        MongoShow item = getItemData(position - groupCount - 1);
        GregorianCalendar calendar = item.recommend.date;

        FontsUtil.changeFont(context, (TextView) holder.getView(R.id.day),"fonts/HelveticaInserat-Roman-SemiBold.ttf");
        holder.setText(R.id.year, String.valueOf(calendar.YEAR))
                .setText(R.id.manth, TimeUtil.formatManthInfo(calendar.MONTH))
                .setText(R.id.day, String.valueOf(calendar.DAY_OF_MONTH))
                .setText(R.id.week, TimeUtil.formatWeekInfo(calendar.DAY_OF_WEEK));
    }

    private void bindShowHolder(AbsViewHolder holder, int position) {
        position--;
        position = -groupCount;
        MongoShow item = getItemData(position);
        holder.setImgeByUrl(R.id.cover, item.cover).setText(R.id.description, item.description);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, S03SHowActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void addDataAtTop(List<MongoShow> datas) {
        super.addDataAtTop(datas);
        groupCount = 0;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }
}
