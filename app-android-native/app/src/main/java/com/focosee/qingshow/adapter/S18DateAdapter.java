package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S03SHowActivity;
import com.focosee.qingshow.activity.S18ShowByDateActivity;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.FontsUtil;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.adapter.AbsAdapter;
import com.focosee.qingshow.util.adapter.AbsViewHolder;

import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Administrator on 2015/4/30.
 */
public class S18DateAdapter extends AbsAdapter<MongoShow> {

    public S18DateAdapter(List datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        if (datas.size() == 0) {
            return;
        }
        final MongoShow item;

        if (position == 0) {
            item = getItemData(position);
            GregorianCalendar calendar = item.recommend.date;
            FontsUtil.changeFont(context, (TextView) holder.getView(R.id.day), "fonts/HelveticaInserat-Roman-SemiBold.ttf");
            holder.setText(R.id.year, String.valueOf(calendar.get(calendar.YEAR)))
                    .setText(R.id.manth, TimeUtil.formatManthInfo(calendar.get(calendar.MONTH)))
                    .setText(R.id.day, String.valueOf(calendar.get(calendar.DAY_OF_MONTH)))
                    .setText(R.id.week, TimeUtil.formatWeekInfo(calendar.get(calendar.DAY_OF_WEEK)))
                    .setText(R.id.des, "Top List");
        } else {
            final int fPosition = position--;
            item = getItemData(position--);
            holder.setImgeByUrl(R.id.cover, item.cover)
                    .setText(R.id.numlike, String.valueOf(item.numLike));

            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, S03SHowActivity.class);
                    intent.putExtra(S03SHowActivity.INPUT_SHOW_ENTITY_ID, item._id);
                    intent.putExtra("position", fPosition);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }
}
