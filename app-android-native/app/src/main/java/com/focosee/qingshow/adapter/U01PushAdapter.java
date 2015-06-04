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


    private Map<Integer, Integer> map;//k : postion v : groupNum

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
        int result;
        if (position == 0) {
            result = 1;
        } else if (position == 1) {
            result = 2;
        } else if (position == 2) {
            result = 0;
        } else {

            if (lastR1 == 2) {
                result = 0;
            } else {
                int num = position - 1 - map.get(position);
                MongoShow item = datas.get(num);
                MongoShow lastItem = datas.get(num - 1);
                if (lastItem.recommend.date.equals(item.recommend.date)) {
                    result = 0;
                } else {
                    result = 2;
                }
            }
        }
        lastR1 = result;

        return result;
    }

    public int getTp(int position) {
        int result;
        if (position == 0) {
            result = 1;
        } else if (position == 1) {
            result = 2;
        } else if (position == 2) {
            result = 0;
        } else {
            if (lastR2 == 2) {
                result = 0;
            } else {
                int num = position - 1 - map.get(position);
                MongoShow item = datas.get(num);
                MongoShow lastItem = datas.get(num - 1);
                if (lastItem.recommend.date.equals(item.recommend.date)) {
                    result = 0;
                } else {
                    result = 2;
                }
            }
        }
        lastR2 = result;

        return result;
    }


    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        if (datas.size() == 0) {
            return;
        }
        switch (getTp(position)) {
            case 0:
                bindShowHolder(holder, position);
                break;
            case 1:
                bindUserHolder(holder);
                break;
            case 2:
//                bindDateHolder(holder, position);
                break;
        }
    }

    private void bindUserHolder(AbsViewHolder holder) {
        MongoPeople user = QSModel.INSTANCE.getUser();
        holder.setText(R.id.user_name, user.nickname)
                .setText(R.id.user_hw, user.height + "," + user.weight);
        if (user.portrait != null) {
            holder.setImgeByUrl(R.id.user_head, user.portrait);
        }
    }

    private void bindDateHolder(AbsViewHolder holder, int position) {
        MongoShow item = datas.get(position - map.get(position) - 1);
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
        plusNum = 0;

        map = new HashMap<>();
        map.put(0, 0);
        map.put(1, 0);

        for (int i = 0; i < datas.size(); i++) {
            if (i == 0) {
                plusNum++;
                map.put(2, 1);
                continue;
            }
            if (!datas.get(i).recommend.date.equals(datas.get(i - 1).recommend.date)) {
                map.put(i + 1 + plusNum, plusNum);
                plusNum++;
            }
            map.put(i + 1 + plusNum, plusNum);

        }
    }

    @Override
    public int getItemCount() {
        return datas.size() + plusNum + 1;
    }
}
