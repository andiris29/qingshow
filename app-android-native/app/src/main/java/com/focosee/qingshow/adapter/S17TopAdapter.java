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
        return position % 2;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {

        if (datas.size() > position){
            holder.setImgeByUrl(R.id.cover,getItemData(position).cover,1.33f)
                    .setText(R.id.like_num, getItemData(position).numLike + "");
        }
        ImageView top = holder.getView(R.id.top);
        ImageView like = holder.getView(R.id.like);
        RelativeLayout bg = holder.getView(R.id.bg);
        switch (position){
            case 0:
                top.setImageResource(R.drawable.top_one);
                like.setImageResource(R.drawable.top_one_like);
                bg.setBackgroundColor(getContext().getResources().getColor(R.color.top_one));
                break;
            case 1:
                top.setImageResource(R.drawable.top_tow);
                like.setImageResource(R.drawable.top_tow_like);
                bg.setBackgroundColor(getContext().getResources().getColor(R.color.top_tow));
                break;
            case 2:
                top.setImageResource(R.drawable.top_three);
                like.setImageResource(R.drawable.top_three_like);
                bg.setBackgroundColor(getContext().getResources().getColor(R.color.top_three));
                break;
            case 3:
                top.setImageResource(R.drawable.top_for);
                like.setImageResource(R.drawable.top_for_like);
                bg.setBackgroundColor(getContext().getResources().getColor(R.color.top_for));
                break;
            case 4:
                top.setImageResource(R.drawable.top_frive);
                like.setImageResource(R.drawable.top_frive_like);
                bg.setBackgroundColor(getContext().getResources().getColor(R.color.top_frive));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
