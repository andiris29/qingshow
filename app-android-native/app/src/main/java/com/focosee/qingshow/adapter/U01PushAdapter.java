package com.focosee.qingshow.adapter;

import android.content.Context;

import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.Bean;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2015/4/28.
 */
public class U01PushAdapter extends AbsAdapter<Bean> {

    public U01PushAdapter(List<Bean> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return 1;
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        Bean bean = getItemData(position);
        if(position == 0){

        }else {
            holder.setImgeByUrl(R.id.cover,bean.url).setText(R.id.description,bean.text);
        }
    }
}
