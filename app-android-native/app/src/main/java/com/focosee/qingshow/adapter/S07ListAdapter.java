package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S10ItemDetailActivity;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;

import java.util.List;

public class S07ListAdapter extends AbsAdapter<MongoItem> {

    private final String TAG = "S07ListAdapter";

    private Context context;

    /**
     * viewType的顺序的layoutId的顺序一致
     *
     * @param datas
     * @param context
     * @param layoutId
     */
    public S07ListAdapter(@NonNull List<MongoItem> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {

        final MongoItem item = getItemData(position);

        holder.setImgeByUrl(R.id.item_s07_category, item.categoryRef.icon);
        holder.setText(R.id.item_s07_name, item.name);
        holder.getView(R.id.item_s07_detail_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump(item);
            }
        });
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump(item);
            }
        });
    }

    public void jump(MongoItem item){
        if (null == item) {
            Toast.makeText(context, R.string.item_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }
        if (null == item.images || 0 == item.images.size()) {
            Toast.makeText(context, R.string.image_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(context, S10ItemDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(S10ItemDetailActivity.INPUT_ITEM_ENTITY, item);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
