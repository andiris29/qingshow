package com.focosee.qingshow.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.view.View;
import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.util.adapter.AbsAdapter;
import com.focosee.qingshow.util.adapter.AbsViewHolder;
import com.focosee.qingshow.widget.QSTextView;
import java.util.List;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Administrator on 2015/9/2.
 */
public class T01HihghtedTradeListAdapter extends AbsAdapter<MongoTrade> {

    private CharSequence disPreText;
    private CharSequence pricePreText;
    private RelativeSizeSpan relativeSizeSpan;
    private StyleSpan styleSpan;

    public T01HihghtedTradeListAdapter(@NonNull List<MongoTrade> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
        disPreText = context.getText(R.string.t01_successed_discount);
        pricePreText = context.getText(R.string.t01_successed_price);
        relativeSizeSpan = new RelativeSizeSpan(1.2f);
        styleSpan = new StyleSpan(Typeface.BOLD);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        QSTextView properTextView = holder.getView(R.id.item_tradelist_skuProperties);
        properTextView.setVisibility(View.GONE);
        if (null == datas || datas.size() == 0) return;
        MongoTrade trade = datas.get(position);
        if (null == trade) return;
        if (!TextUtils.isEmpty(trade.itemSnapshot.delist)) {
            holder.getView(R.id.item_t01_delist).setVisibility(View.VISIBLE);
        }
        SpannableString spanStrDis = new SpannableString(disPreText + StringUtil.calculationException(
                datas.get(position).expectedPrice, datas.get(position).actualPrice));
        spanStrDis.setSpan(relativeSizeSpan, disPreText.length(), spanStrDis.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStrDis.setSpan(styleSpan, disPreText.length(), spanStrDis.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.setText(R.id.item_t01_discount, spanStrDis);

        SpannableString spanStrPrice = new SpannableString(pricePreText + StringUtil.FormatPrice(String.valueOf(datas.get(position).expectedPrice)));
        spanStrPrice.setSpan(relativeSizeSpan, pricePreText.length(), spanStrPrice.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStrPrice.setSpan(styleSpan, pricePreText.length(), spanStrPrice.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.setText(R.id.item_t01_price, spanStrPrice);

        holder.setText(R.id.item_t01_time, TimeUtil.parseDateString(trade.create));
        if (null != trade.peopleSnapshot) {
            holder.setImgeByController(R.id.item_t01_portrait, trade.peopleSnapshot.portrait, ValueUtil.pre_img_AspectRatio);
            holder.setText(R.id.item_t01_username, trade.peopleSnapshot.nickname);
        }

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
        String properties = StringUtil.formatSKUProperties(trade.selectedSkuProperties);
        if (!TextUtils.isEmpty(properties)) {
            properTextView.setVisibility(View.VISIBLE);
            properTextView.setText("规格：" + properties);
        }
        holder.setText(R.id.item_tradelist_quantity, String.valueOf(trade.quantity));
    }

    @Override
    public int getItemCount() {
        return null == datas ? 0 : datas.size();
    }
}
