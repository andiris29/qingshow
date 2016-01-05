package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S10ItemDetailActivity;
import com.focosee.qingshow.constants.config.UserConfig;
import com.focosee.qingshow.model.vo.mongo.MongoBonus;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.adapter.AbsAdapter;
import com.focosee.qingshow.util.adapter.AbsViewHolder;
import com.focosee.qingshow.util.bonus.BonusHelper;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2015/9/1.
 */
public class U16BonusListAdapter extends AbsAdapter<MongoBonus> {

    /**
     * viewType的顺序的layoutId的顺序一致
     *
     * @param datas
     * @param context
     * @param layoutId
     */
    public U16BonusListAdapter(@NonNull List<MongoBonus> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        final MongoBonus bonuses = datas.get(position);
        if (null == bonuses) return;
        holder.setText(R.id.item_u16_description, bonuses.description);
        holder.setText(R.id.item_u16_time, TimeUtil.formatDateTime(bonuses.create
                , new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")));
        holder.setText(R.id.item_u16_money, StringUtil.FormatPrice(bonuses.amount));

        if (bonuses.icon != null){
           // holder.setImgeByController(R.id.item_u16_portrait, ImgUtil.getImgSrc(bonuses.icon, ImgUtil.Meduim), 1f);
            holder.setImgeByUrl(R.id.item_u16_portrait , bonuses.icon);
        }else {
            holder.setImgeByUrl(R.id.item_u16_portrait, UserConfig.USER_PORTRAIT_100);
        }

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, S10ItemDetailActivity.class);
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == datas ? 0 : datas.size();
    }
}
