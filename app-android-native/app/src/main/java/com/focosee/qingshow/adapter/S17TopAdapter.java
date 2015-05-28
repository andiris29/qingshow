package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S18ShowByDateActivity;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.FontsUtil;
import com.focosee.qingshow.util.ShowUtil;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/30.
 */
public class S17TopAdapter extends AbsAdapter<List<MongoShow>> {

    public S17TopAdapter(List datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        List<MongoShow> item = getItemData(position);
        SimpleDraweeView img02 = holder.getView(R.id.show02);

        MongoShow show = item.get(0);

        GregorianCalendar calendar = show.recommend.date;
        FontsUtil.changeFont(context, (TextView) holder.getView(R.id.day), "fonts/HelveticaInserat-Roman-SemiBold.ttf");
        holder.setText(R.id.year, String.valueOf(calendar.YEAR))
                .setText(R.id.day, String.valueOf(calendar.DAY_OF_MONTH) + " " + TimeUtil.formatManthInfo(calendar.MONTH))
                .setText(R.id.week, TimeUtil.formatWeekInfo(calendar.DAY_OF_WEEK));

        holder.setImgeByUrl(R.id.show01, show.cover);
        if (null != item.get(1)) {
            img02.setImageURI(Uri.parse(item.get(1).cover));
            img02.setVisibility(View.VISIBLE);
        }
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,S18ShowByDateActivity.class);
                context.startActivity(intent);
            }
        });
    }
}
