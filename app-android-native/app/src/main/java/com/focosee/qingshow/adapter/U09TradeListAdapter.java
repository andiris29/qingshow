package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S17PayActivity;
import com.focosee.qingshow.activity.U09TradeListActivity;
import com.focosee.qingshow.activity.U12ReturnActivity;
import com.focosee.qingshow.constants.code.StatusCode;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.ShareUtil;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.util.adapter.AbsAdapter;
import com.focosee.qingshow.util.adapter.AbsViewHolder;
import com.focosee.qingshow.widget.ConfirmDialog;
import com.focosee.qingshow.widget.QSButton;
import com.focosee.qingshow.widget.QSTextView;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/3/16.
 */
public class U09TradeListAdapter extends AbsAdapter<MongoTrade> implements View.OnClickListener {

    private final int CANCEL = 0;
    private SpannableString spannableString;

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
        if (position == StatusCode.APPLYING) {
            holder.getView(R.id.U09_head_layout).setVisibility(View.INVISIBLE);
            return;
        }
        if (null == getItemData(position)) return;
        final MongoTrade trade = getItemData(position);
        if (null == trade) return;
        String dateStr = "付款日期：" + TimeUtil.parseDateString(trade.update);
        if (trade.status == StatusCode.APPLYING || trade.status == StatusCode.APPLY_SUCCESSED) {
            dateStr = "申请日期：" + TimeUtil.parseDateString(trade.update);
        }
        holder.setText(R.id.item_tradelist_payTime, dateStr);
        final QSButton btn1 = holder.getView(R.id.item_tradelist_btn1);
        QSButton btn2 = holder.getView(R.id.item_tradelist_btn2);
        QSTextView statusTV = holder.getView(R.id.item_tradelist_status);
        QSTextView properTextView = holder.getView(R.id.item_tradelist_skuProperties);
        properTextView.setVisibility(View.GONE);
        btn1.setVisibility(View.GONE);
        btn2.setVisibility(View.GONE);
        statusTV.setVisibility(View.GONE);
        holder.getView(R.id.item_tradelist_btn1_topImg).setVisibility(View.GONE);

        if (null != trade.itemSnapshot) {
            String str = "原价：";
            int start = str.length() + 1;
            String priceStr = str + StringUtil.FormatPrice(trade.itemSnapshot.price);
            spannableString = new SpannableString(priceStr);
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
        holder.setText(R.id.item_tradelist_expectedPrice, StringUtil.FormatPrice(String.valueOf(trade.expectedPrice)));

        //0-折扣申请中
        if (trade.status == StatusCode.APPLYING) {
            btn1.setVisibility(View.VISIBLE);
            btn1.setText("取消申请");
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickCancelTrade(trade, 18, CANCEL, position, "确定要取消申请？");
                }
            });
            return;
        }
        //1-等待付款
        if (trade.status == StatusCode.APPLY_SUCCESSED) {
            btn1.setVisibility(View.VISIBLE);
            if (null != trade.__context) {
                if (!trade.__context.sharedByCurrentUser && trade.shareToPay) {
                    holder.getView(R.id.item_tradelist_btn1_topImg).setVisibility(View.VISIBLE);
                    btn1.setText("分享后付款");
                    btn1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (QSApplication.instance().getWxApi().isWXAppInstalled()) {
                                EventBus.getDefault().post(trade);
                                ShareUtil.shareTradeToWX(trade._id, trade.peopleSnapshot._id, ValueUtil.SHARE_TRADE, context, true);
                            } else
                                Toast.makeText(context, "请先安装微信，然后才能分享", Toast.LENGTH_SHORT).show();
                        }

                    });
                } else {
                    btn1.setText("立即付款");
                    btn1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, S17PayActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(S17PayActivity.INPUT_ITEM_ENTITY, trade);
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        }
                    });
                }
            }
            btn2.setVisibility(View.VISIBLE);
            btn2.setText("取消订单");
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickCancelTrade(trade, StatusCode.TRADE_CANCEL, CANCEL, position, "确定要取消订单？");
                }
            });
            return;
        }
        //3-已发货
        if (trade.status == StatusCode.SENDED) {
            btn1.setVisibility(View.VISIBLE);
            btn2.setVisibility(View.VISIBLE);
            btn1.setText("申请退货");
            btn2.setText("物流信息");
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String msg = "暂无物流信息";
                    if (null != trade.logistic) {
                        msg = "物流公司：" + trade.logistic.company + "\n物流单号：" + (trade.logistic.trackingID == null ? "" : trade.logistic.trackingID);
                    }
                    final ConfirmDialog dialog = new ConfirmDialog();
                    dialog.setTitle(msg);
                    dialog.setConfirm(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show(((U09TradeListActivity) context).getSupportFragmentManager());
                }
            });
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, U12ReturnActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(U12ReturnActivity.TRADE_ENTITY, trade);
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
        final ConfirmDialog dialog = new ConfirmDialog();
        dialog.setTitle(msg);
        dialog.setConfirm(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusTo(trade, status, type, position);
                dialog.dismiss();
            }
        });
        dialog.show(((U09TradeListActivity) context).getSupportFragmentManager());
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

    public void statusTo(MongoTrade trade, final int status, final int type, final int position) {

        JSONObject jsonObject = getStatusJSONObjcet(trade, status);
        QSJsonObjectRequest jor = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getTradeStatustoApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(context, MetadataParser.getError(response));
                    return;
                }
                datas.remove(position - 1);
                notifyDataSetChanged();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jor);
    }


    private JSONObject getStatusJSONObjcet(MongoTrade trade, int status) {

        Map params = new HashMap();
        Map orders = new HashMap();
        Map taobaoInfo = new HashMap();
        Map logistic = new HashMap();
        Map returnLogistic = new HashMap();
        params.put("_id", trade._id);
        params.put("status", status);
        params.put("comment", (trade.statusLogs.get(trade.statusLogs.size() - 1)).comment);

        switch (status) {
            case 1:
                taobaoInfo.put("tradeID", trade.taobaoInfo.tradeID);
                taobaoInfo.put("userNick", trade.taobaoInfo.userNick);
                params.put("orders", orders);
                break;
            case 3:
                logistic.put("company", trade.logistic.company);
                logistic.put("trackingID", trade.logistic.trackingID);
                params.put("logistic", logistic);
                break;
            case 7:
                logistic.put("company", trade.returnlogistic.company);
                logistic.put("trackingID", trade.returnlogistic.trackingID);
                params.put("returnLogistic", returnLogistic);
                break;
        }

        return new JSONObject(params);

    }

    @Override
    public void onClick(View v) {

    }
}
