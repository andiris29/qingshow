package com.focosee.qingshow.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;

import com.focosee.qingshow.R;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.adapter.AbsAdapter;
import com.focosee.qingshow.util.adapter.AbsViewHolder;
import com.focosee.qingshow.util.sku.SkuUtil;
import com.focosee.qingshow.widget.Flow.FlowRadioButton;
import com.focosee.qingshow.widget.Flow.FlowRadioGroup;

import java.util.List;

/**
 * Created by Administrator on 2015/7/31.
 */
public class SkuPropsAdpater extends AbsAdapter<String> {

    private float radioBtnWdith = 35;
    private float radioBtnHeight = 28;
    private int checkIndex[];
    private OnCheckedChangeListener onCheckedChangeListener;


    public interface OnCheckedChangeListener {
        void onChanged(String key, int index);
    }

    public SkuPropsAdpater(@NonNull List datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
        checkIndex = new int[datas.size()];
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, final int position) {
        String data = datas.get(position);
        List<String> values = SkuUtil.getValues(data);

        final String key = SkuUtil.getPropName(data);
        FlowRadioGroup group = holder.getView(R.id.propGroup);
        holder.setText(R.id.propText, key);

        ViewGroup.MarginLayoutParams itemParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT);
        itemParams.setMargins(10, 10, 10, 10);
        for (int i = 0; i < values.size(); i++) {
            FlowRadioButton propItem = initPropItem(values.get(i));
            group.addView(propItem, itemParams);
            if (i == checkIndex[position]) {
                propItem.setChecked(true);
                if (onCheckedChangeListener != null)
                    onCheckedChangeListener.onChanged(key, i);
            }
        }

        group.setOnCheckedChangeListener(new FlowRadioGroup.OnCheckedChangeListener() {
            @Override
            public void checkedChanged(int index) {
                checkIndex[position] = index;
                if (onCheckedChangeListener != null)
                    onCheckedChangeListener.onChanged(key, index);
            }
        });


    }

    private FlowRadioButton initPropItem(String text) {
        FlowRadioButton propItem = new FlowRadioButton(context);
        propItem.setMinWidth((int) AppUtil.transformToDip(radioBtnWdith, context));
        propItem.setMinHeight((int) AppUtil.transformToDip(radioBtnHeight, context));
        propItem.setBackgroundResource(R.drawable.gay_btn_ring);
        propItem.setTextColor(context.getResources().getColor(R.color.gary));
        propItem.setGravity(Gravity.CENTER);
        propItem.setTextSize(13);
        if (!TextUtils.isEmpty(text)) {
            propItem.setText(text);
        }
        return propItem;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }
}
