package com.focosee.qingshow.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2015/4/30.
 */
public class S17TopAdapter extends AbsAdapter<MongoShow> {
    public S17TopAdapter(List datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
                
    }
}
