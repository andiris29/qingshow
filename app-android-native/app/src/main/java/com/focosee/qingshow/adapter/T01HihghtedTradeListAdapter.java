package com.focosee.qingshow.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;

import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;
import com.focosee.qingshow.widget.QSTextView;

import java.util.List;

/**
 * Created by Administrator on 2015/9/2.
 */
public class T01HihghtedTradeListAdapter extends AbsAdapter<MongoTrade> {

    private CharSequence disPreText;
    private CharSequence pricePreText;
    /**
     * viewType的顺序的layoutId的顺序一致
     *
     * @param datas
     * @param context
     * @param layoutId
     */
    public T01HihghtedTradeListAdapter(@NonNull List<MongoTrade> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
        disPreText = context.getText(R.string.t01_successed_discount);
        pricePreText = context.getText(R.string.t01_successed_price);

    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        if(null == datas)return;
        MongoTrade trade = datas.get(position);
        if(null == trade)return;
        RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(2);
        QSTextView discount = holder.getView(R.id.item_t01_discount);
        SpannableString spanStrDis = new SpannableString(disPreText + StringUtil.calculationException(
                datas.get(position).expectedPrice, datas.get(position).actualPrice));
        spanStrDis.setSpan(relativeSizeSpan, disPreText.length(), spanStrDis.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        discount.setText(spanStrDis);

        SpannableString spanStrPrice = new SpannableString(pricePreText + String.valueOf(datas.get(position).expectedPrice));
        spanStrDis.setSpan(relativeSizeSpan, pricePreText.length(), spanStrPrice.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (null != trade.itemSnapshot) {
            String str = "原价：";
            int start = str.length() + 1;
            String priceStr = str + StringUtil.FormatPrice(trade.itemSnapshot.price);
            SpannableString spannableString = new SpannableString(priceStr);
            spannableString.setSpan(new StrikethroughSpan(), start, priceStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.setText(R.id.item_tradelist_sourcePrice, spannableString);

            holder.setText(R.id.item_tradelist_description, trade.itemSnapshot.name);
            holder.setText(R.id.item_tradelist_exception, StringUtil.calculationException(trade.expectedPrice, trade.itemSnapshot.promoPrice));
            holder.setImgeByUrl(R.id.item_tradelist_image, trade.itemSnapshot.thumbnail);
            holder.setText(R.id.item_tradelist_actualPrice, StringUtil.FormatPrice(String.valueOf(trade.itemSnapshot.promoPrice)));
        }

    }

    @Override
    public int getItemCount() {
        return 20;
    }
}
