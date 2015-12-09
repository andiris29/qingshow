package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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
import com.focosee.qingshow.activity.S10ItemDetailActivity;
import com.focosee.qingshow.activity.U01UserActivity;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.ImgUtil;
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
        relativeSizeSpan = new RelativeSizeSpan(1.5f);
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
        final MongoTrade trade = datas.get(position);
        if (null == trade) return;
        if (null != trade.itemSnapshot.delist) {
            holder.getView(R.id.item_t01_delist).setVisibility(View.VISIBLE);
        }
        SpannableString spanStrDis = new SpannableString(disPreText + StringUtil.calculationException(
                trade.totalFee.doubleValue() / trade.quantity, trade.itemSnapshot.promoPrice.doubleValue()));
        spanStrDis.setSpan(relativeSizeSpan, disPreText.length(), spanStrDis.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStrDis.setSpan(styleSpan, 0, spanStrDis.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.setText(R.id.item_t01_discount, spanStrDis);

        SpannableString spanStrPrice = new SpannableString(pricePreText + StringUtil.FormatPrice(trade.totalFee.doubleValue() / trade.quantity));
        spanStrPrice.setSpan(relativeSizeSpan, pricePreText.length(), spanStrPrice.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStrPrice.setSpan(styleSpan, 0, spanStrPrice.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.setText(R.id.item_t01_price, spanStrPrice);

        holder.setText(R.id.item_t01_time, TimeUtil.parseDateString(trade.create));
        if (null != trade.peopleSnapshot) {
            if(!TextUtils.isEmpty(trade.peopleSnapshot.portrait)) {
                holder.setImgeByController(R.id.item_t01_portrait, ImgUtil.getImgSrc(trade.peopleSnapshot.portrait, ImgUtil.PORTRAIT_LARGE), ValueUtil.pre_img_AspectRatio);
            }else{
                holder.setImgeByRes(R.id.item_t01_portrait, R.drawable.root_cell_placehold_head);
            }
            holder.setText(R.id.item_t01_username, trade.peopleSnapshot.nickname);
        }

        if (null != trade.itemSnapshot) {
            String str = "原价：";
            String priceStr = str + StringUtil.FormatPrice(trade.itemSnapshot.price);
            SpannableString spannableString = new SpannableString(priceStr);
            spannableString.setSpan(new StrikethroughSpan(), 0, priceStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.setText(R.id.item_tradelist_sourcePrice, spannableString);

            holder.setText(R.id.item_tradelist_description, trade.itemSnapshot.name);
            holder.setImgeByUrl(R.id.item_tradelist_image, trade.itemSnapshot.thumbnail);
            holder.setText(R.id.item_tradelist_actualPrice, StringUtil.FormatPrice(trade.itemSnapshot.promoPrice));
        }
        String properties = StringUtil.formatSKUProperties(trade.selectedSkuProperties);
        if (!TextUtils.isEmpty(properties)) {
            properTextView.setVisibility(View.VISIBLE);
            properTextView.setText("规格：" + properties);
        }
        holder.setText(R.id.item_tradelist_quantity, String.valueOf(trade.quantity));

        holder.getView(R.id.item_t01_click_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, S10ItemDetailActivity.class);
                intent.putExtra(S10ItemDetailActivity.BONUSES_ITEMID, trade.itemSnapshot._id);
                context.startActivity(intent);
            }
        });

        holder.getView(R.id.item_t01_portrait).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, U01UserActivity.class);
                intent.putExtra("user", trade.peopleSnapshot);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return null == datas ? 0 : datas.size();
    }
}
