package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.U01UserActivity;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.util.adapter.AbsViewHolder;
import java.util.List;

/**
 * Created by Administrator on 2015/7/3.
 */
public class U01FollowerFragAdapter extends U01BaseAdapter<MongoPeople>{


    public U01FollowerFragAdapter(@NonNull List<MongoPeople> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    /**
     * 0:R.layout.item_u01_push
     * 1:R.layout.item_u01_fan_and_followers
     * @param position
     * @return
     */

    @Override
    public void onBindViewHolder(final AbsViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(0 == position) return;
        final MongoPeople people = getItemData(position);
        if(null != people.portrait && !"".equals(people.portrait))
            holder.setImgeByUrl(R.id.item_u01_fans_image, ImgUtil.getImgSrc(people.portrait, ImgUtil.Large));
        holder.setText(R.id.item_u01_fans_name, people.nickname);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null == people)return;
                Intent intent = new Intent(context, U01UserActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", people);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
//        holder.setText(R.id.item_u01_fans_cloth_number, people.)
    }

    @Override
    public int getItemCount() {
        return null == datas ? 1 : datas.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return 0 == position ? 0 : position - 1;
    }

    @Override
    public MongoPeople getItemData(int position) {
        return 0 == position ? null : datas.get(position - 1);
    }
}
