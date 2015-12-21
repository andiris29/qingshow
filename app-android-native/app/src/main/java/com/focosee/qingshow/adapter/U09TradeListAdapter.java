package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S10ItemDetailActivity;
import com.focosee.qingshow.activity.U12ReturnActivity;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.TradeStatusToCommand;
import com.focosee.qingshow.constants.code.StatusCode;
import com.focosee.qingshow.constants.config.QSPushAPI;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.adapter.AbsAdapter;
import com.focosee.qingshow.util.adapter.AbsViewHolder;
import com.focosee.qingshow.util.user.UnreadHelper;
import com.focosee.qingshow.widget.ConfirmDialog;
import com.focosee.qingshow.widget.QSButton;
import com.focosee.qingshow.widget.QSTextView;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/3/16.
 */
public class U09TradeListAdapter extends AbsAdapter<MongoTrade> {

    private final int CANCEL = 0;
    private SpannableString spannableString;
    public List<MongoItem> items;

    /**
     * viewType的顺序的layoutId的顺序一致
     *
     * @param datas
     * @param context
     * @param layoutId
     */
    public U09TradeListAdapter(@NonNull List<MongoTrade> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, final int position) {
        if (position == 0) {
            holder.getView(R.id.U09_head_layout).setVisibility(View.INVISIBLE);
            return;
        }
        final QSButton btn1 = holder.getView(R.id.item_tradelist_btn1);
        QSButton btn2 = holder.getView(R.id.item_tradelist_btn2);
        QSTextView statusTV = holder.getView(R.id.item_tradelist_status);
        QSTextView properTextView = holder.getView(R.id.item_tradelist_skuProperties);
        QSTextView properTab = holder.getView(R.id.item_tradelist_skuProperties_tab);
//        final View newDiscountCircleTip = holder.getView(R.id.circle_tip);
        properTextView.setVisibility(View.GONE);
        properTab.setVisibility(View.GONE);
        btn1.setVisibility(View.GONE);
        btn2.setVisibility(View.GONE);
        statusTV.setVisibility(View.GONE);
//        newDiscountCircleTip.setVisibility(View.GONE);

        if (null == getItemData(position)) return;
        final MongoTrade trade = getItemData(position);
        if (null == trade) return;
        String dateStr = "付款日期：" + TimeUtil.parseDateString(trade.update);
        if (trade.status == StatusCode.APPLYING || trade.status == StatusCode.APPLY_SUCCESSED) {
            dateStr = "申请日期：" + TimeUtil.parseDateString(trade.update);
        }
        holder.setText(R.id.item_tradelist_payTime, dateStr);


        if (null != trade.itemSnapshot) {
            String priceStr = "";
            if (trade.itemSnapshot.promoPrice != null){
                float total;
                total = trade.itemSnapshot.promoPrice.floatValue();
                if(trade.itemSnapshot.expectable != null && trade.itemSnapshot.expectable.reduction != null){
                    total -= trade.itemSnapshot.expectable.reduction.floatValue();
                }
                priceStr = StringUtil.FormatPrice(total);
            }



            holder.setText(R.id.item_tradelist_actualPrice, priceStr);

            holder.setText(R.id.item_tradelist_description, trade.itemSnapshot.name);
            holder.setImgeByUrl(R.id.item_tradelist_image, trade.itemSnapshot.thumbnail);
            holder.getView(R.id.item_tradelist_image).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, S10ItemDetailActivity.class);
                    intent.putExtra(S10ItemDetailActivity.INPUT_ITEM_ENTITY, datas.get(position - 1).itemSnapshot);
                    context.startActivity(intent);
                }
            });

            holder.getView(R.id.item_tradelist_check).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, S10ItemDetailActivity.class);
                    intent.putExtra(S10ItemDetailActivity.INPUT_ITEM_ENTITY, datas.get(position - 1).itemSnapshot);
                    context.startActivity(intent);
                }
            });
        }

        String properties = StringUtil.formatSKUProperties(trade.selectedSkuProperties);
        if (!TextUtils.isEmpty(properties)) {
            properTextView.setVisibility(View.VISIBLE);
            properTab.setVisibility(View.VISIBLE);
            properTextView.setText(properties);
        }
        holder.setText(R.id.item_tradelist_quantity, String.valueOf(trade.quantity));

        //0-折扣申请中
        if (trade.status == StatusCode.APPLYING) {
            btn1.setVisibility(View.VISIBLE);
            btn1.setText("取消申请");
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickCancelTrade(trade, StatusCode.TRADE_CANCEL, CANCEL, position, "确定要取消申请？");
                }
            });
            return;
        }
        //1-等待付款
        if (trade.status == StatusCode.APPLY_SUCCESSED) {
            btn1.setVisibility(View.VISIBLE);
            btn1.setText("取消订单");
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickCancelTrade(trade, StatusCode.TRADE_CANCEL, CANCEL, position, "确定要取消订单？");
                }
            });

            //new discount
            if (null == trade.itemRef) return;
            if (null == trade.itemRef.expectable) return;
            if (trade.itemRef.expectable.expired) {
                if(UnreadHelper.hasMyNotificationId(trade._id)){
                    UnreadHelper.userReadNotificationId(trade._id);
                }
                return;
            }

            //push guide
            if (UnreadHelper.hasMyNotificationId(trade._id)) {
                String command = UnreadHelper.getCommand(trade._id);
                Log.d(U09TradeListAdapter.class.getSimpleName(), "command:" + command);
            } else {
                btn2.setVisibility(View.VISIBLE);
                btn2.setText("立即付款");
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
            return;
        }

        //3-已发货
        if (trade.status == StatusCode.SENDED) {
            btn1.setVisibility(View.VISIBLE);
            btn2.setVisibility(View.VISIBLE);
            btn1.setText("申请退货");
            btn2.setText("物流信息");
            //push guide
            if (UnreadHelper.hasMyNotificationId(trade._id))
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //push guide
                        UnreadHelper.userReadNotificationId(trade._id);

                        String msg = "暂无物流信息";
                        if (null != trade.logistic) {
                            msg = "物流公司：" + trade.logistic.company + "\n物流单号：" + (trade.logistic.trackingId == null ? "" : trade.logistic.trackingId);
                        }
                        final ConfirmDialog dialog = new ConfirmDialog(context);
                        dialog.setTitle(msg);
                        dialog.setConfirm(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        dialog.hideCancel();
                }
            });
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, U12ReturnActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(U12ReturnActivity.TRADE_ENTITY, trade);
                    bundle.putInt("position", position);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
            return;
        }
        statusTV.setVisibility(View.VISIBLE);
        statusTV.setText(StatusCode.getStatusText(trade.status));
    }


    private void onClickCancelTrade(final MongoTrade trade, final int status, final int type, final int position, String msg) {
        UnreadHelper.userReadNotificationId(trade._id);
        final ConfirmDialog dialog = new ConfirmDialog(context);
        dialog.setTitle(msg);
        dialog.setConfirm(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                TradeStatusToCommand.statusTo(trade, status, new Callback() {
                    @Override
                    public void onComplete() {
                        datas.remove(position - 1);
                        notifyDataSetChanged();
                    }
                });
            }
        });
        dialog.show();
    }

    @Override
    public MongoTrade getItemData(int position) {
        return 0 == position ? null : datas.get(position - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return 0 == position ? 0 : 1;
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return null == datas ? 1 : datas.size() + 1;
    }
}
