package com.focosee.qingshow.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.focosee.qingshow.R;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;
import com.focosee.qingshow.util.sku.SkuUtil;
import com.focosee.qingshow.widget.flow.FlowRadioButton;
import com.focosee.qingshow.widget.flow.FlowRadioGroup;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/31.
 */
public class SkuPropsAdpater extends AbsAdapter<String> {

    private float radioBtnWdith = 40;

    public SkuPropsAdpater(@NonNull List datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        String data = datas.get(position);
        List<String> values = SkuUtil.getValues(data);

        FlowRadioGroup group = holder.getView(R.id.propGroup);
        holder.setText(R.id.propText, SkuUtil.getPropName(data));

        ViewGroup.MarginLayoutParams itemParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT);
        itemParams.setMargins(10, 10, 10, 10);
        for (int i = 0; i < values.size(); i++) {
            FlowRadioButton propItem = initPropItem(values.get(i));
            group.addView(propItem,itemParams);
            if (i == 1) {
                propItem.setChecked(true);
//                onSecletChanged();
            }
        }

        group.setOnCheckedChangeListener(new FlowRadioGroup.OnCheckedChangeListener() {
            @Override
            public void checkedChanged(int index) {

            }
        });


    }

    private FlowRadioButton initPropItem(String text) {
        FlowRadioButton propItem = new FlowRadioButton(context);
        propItem.setMinWidth((int) AppUtil.transformToDip(radioBtnWdith, context));
        propItem.setBackgroundResource(R.drawable.s11_size_item_bg);
        propItem.setTextColor(context.getResources().getColor(R.color.black));
        propItem.setGravity(Gravity.CENTER);
        propItem.setTextSize(13);
        if (!TextUtils.isEmpty(text)) {
            propItem.setText(text);
        }
        return propItem;
    }

}
