package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S18ShowByDateActivity;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.FontsUtil;
import com.focosee.qingshow.util.ImgUtil;
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
        PointF proint = new PointF(0f,0f);
        List<MongoShow> item = getItemData(position);
        SimpleDraweeView img02 = holder.getView(R.id.show02);
        SimpleDraweeView img01 = holder.getView(R.id.show01);

        final MongoShow show = item.get(0);

        GregorianCalendar calendar = show.recommend.date;
        Log.i("tag", TimeUtil.parseDateString(calendar));
        holder.setText(R.id.year, String.valueOf(calendar.get(GregorianCalendar.YEAR)))
                .setText(R.id.day, String.valueOf(calendar.get(calendar.DAY_OF_MONTH)) + " " + TimeUtil.formatManthInfo(calendar.get(calendar.MONTH)))
                .setText(R.id.week, TimeUtil.formatWeekInfo(calendar.get(calendar.DAY_OF_WEEK)));

//        img01.getHierarchy().setActualImageFocusPoint(proint);

        img01.setImageURI(Uri.parse(ImgUtil.getImgSrc(show.cover,-1)));

        if (null != item.get(1)) {
//            img02.getHierarchy().setActualImageFocusPoint(proint);

            img02.setImageURI(Uri.parse(ImgUtil.getImgSrc(item.get(1).cover,-1)));
            img02.setVisibility(View.VISIBLE);
        }
        holder.setText(R.id.like,String.valueOf(show.numLike));

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,S18ShowByDateActivity.class);
                intent.putExtra("dateforS18",TimeUtil.parseDateString(show.recommend.date));
                context.startActivity(intent);
            }
        });
    }
}
