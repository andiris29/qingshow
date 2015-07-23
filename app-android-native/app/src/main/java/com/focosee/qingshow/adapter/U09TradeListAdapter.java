package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.U09TradeListActivity;
import com.focosee.qingshow.activity.U12ReturnActivity;
import com.focosee.qingshow.command.PayCommand;
import com.focosee.qingshow.constants.code.StatusCode;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;
import com.focosee.qingshow.util.sku.Prop;
import com.focosee.qingshow.util.sku.SkuUtil;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by Administrator on 2015/3/16.
 */
public class U09TradeListAdapter extends AbsAdapter<MongoTrade> implements View.OnClickListener{

    private OnViewHolderListener onViewHolderListener;

    public LinkedList<MongoTrade> datas = null;

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
        if(position == 0)return;
        if(null == getItemData(position))return;
        final MongoTrade trade = getItemData(position);
        if(null == trade)return;

        View tradingLayout = holder.getView(R.id.item_trade_time_layout);
        Button applyReceive = holder.getView(R.id.item_tradelist_applyreceive);
        Button applyChange = holder.getView(R.id.item_tradelist_applychange);
        Button applyReturn = holder.getView(R.id.item_tradelist_applyreturn);
        Button payBtn = holder.getView(R.id.item_tradelist_payBtn);

        holder.setText(R.id.item_tradeno_text, null == trade.orders.get(0).selectedItemSkuId ? "" : trade.orders.get(0).selectedItemSkuId);
        holder.setText(R.id.item_tradelist_createTime, TimeUtil.formatDateTime(trade.create));

        if(!(trade.status > 18 || trade.status < 0)){//设置交易状态
            holder.setText(R.id.item_tradelist_status, StatusCode.statusArrays[trade.status]);
        }

        LinkedList<MongoItem.TaoBaoInfo.SKU> skus = trade.orders.get(0).itemSnapshot.taobaoInfo.skus;


        holder.setText(R.id.item_tradelist_color, SkuUtil.getPropValue(skus, SkuUtil.KEY.COLOR.id));
        holder.setText(R.id.item_tradelist_measurement, SkuUtil.getPropValue(skus, SkuUtil.KEY.SIZE_1.id, SkuUtil.KEY.SIZE_2.id, SkuUtil.KEY.SIZE_3.id));
        holder.setText(R.id.item_tradelist_quantity, String.valueOf(trade.orders.get(0).quantity));
        holder.setText(R.id.item_tradelist_price, "￥" + String.valueOf(trade.orders.get(0).price));
        holder.setImgeByUrl(R.id.item_tradelist_image, trade.orders.get(0).itemSnapshot.thumbnail);
        holder.setText(R.id.item_tradelist_description, trade.orders.get(0).itemSnapshot.taobaoInfo.top_title);
        //等待买家付款
        if(trade.status == 0){
            payBtn.setVisibility(View.VISIBLE);
            payBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            return;
        }
        //卖家已发货
        if(trade.status == 3 || trade.status == 14){
            tradingLayout.setVisibility(View.VISIBLE);
            applyReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, U12ReturnActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(U12ReturnActivity.TRADE_ENTITY, datas.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
            applyReceive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final MaterialDialog dialog = new MaterialDialog(context);
                    LayoutInflater layoutInflater = LayoutInflater.from(context);
                    View view = layoutInflater.inflate(R.layout.tradelist_dialog, null);
                    dialog.setContentView(view);
                    ((TextView) view.findViewById(R.id.tradelist_dialog_receiveTime)).setText("确认收货");
                    view.findViewById(R.id.tradedialog_cancel_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    view.findViewById(R.id.tradedialog_comfirm_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            statusTo(trade, 1);
                            if (context instanceof U09TradeListActivity) {
                                ((U09TradeListActivity) context).doRefresh();
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
            applyChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    statusTo(trade, 0);
                    final MaterialDialog dialog = new MaterialDialog(context);
                    LayoutInflater layoutInflater = LayoutInflater.from(context);
                    View view = layoutInflater.inflate(R.layout.tradelist_dialog, null);
                    dialog.setContentView(view);
                    ((TextView) view.findViewById(R.id.tradelist_dialog_receiveTime)).setText("确认收货");
                    view.findViewById(R.id.tradedialog_cancel_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    view.findViewById(R.id.tradedialog_comfirm_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            statusTo(trade, 1);
                            if(context instanceof U09TradeListActivity){
                                ((U09TradeListActivity)context).doRefresh();
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
            return;
        }

        holder.getView(R.id.item_trade_finishTime_layout).setVisibility(View.VISIBLE);
        holder.setText(R.id.item_tradelist_createTime, TimeUtil.formatDateTime(trade.create.getTimeInMillis()));
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

    public void setOnViewHolderListener(OnViewHolderListener onViewHolderListener){
        this.onViewHolderListener = onViewHolderListener;
    }

    public void statusTo(MongoTrade trade, final int type){

        JSONObject jsonObject = getStatusJSONObjcet(trade);

        QSJsonObjectRequest jor = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getTradeStatustoApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(context, MetadataParser.getError(response));
                    return;
                }

                responseToStatusToSuccessed(type);
                //改变订单状态成功
//                Toast.makeText(context, "确认收货成功！", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jor);
    }


    private JSONObject getStatusJSONObjcet(MongoTrade trade){

        Map params = new HashMap();
        Map taobaoInfo = new HashMap();
        Map logistic = new HashMap();
        Map returnLogistic = new HashMap();
        params.put("_id", trade._id);
        params.put("status", trade.status);
        params.put("comment", (trade.statusLogs.get(0)).comment);

        switch (trade.status){
            case 2:
//                params.put("taobaoInfo.userNick", trade.orders.get(0).itemSnapshot.taobaoInfo.nick);
                taobaoInfo.put("tradeID", trade.taobaoInfo.tradeID);
                taobaoInfo.put("userNick", trade.taobaoInfo.userNick);
                params.put("taobaoInfo", taobaoInfo);
                break;
            case 3:
            case 14:
                logistic.put("company", trade.logistic.company);
                logistic.put("trackingID", trade.logistic.trackingID);
                params.put("logistic", logistic);
                break;
            case 7:
            case 11:
                logistic.put("company", trade.returnlogistic.company);
                logistic.put("trackingID", trade.returnlogistic.trackingID);
                params.put("returnLogistic", returnLogistic);
                break;
        }

        return new JSONObject(params);

    }

    private void responseToStatusToSuccessed(int type){
        switch (type){
            case 0://申请换货
                final MaterialDialog dialog = new MaterialDialog(context);
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View view = layoutInflater.inflate(R.layout.tradelist_dialog, null);
                dialog.setContentView(view);
                ((TextView) view.findViewById(R.id.tradelist_dialog_receiveTime)).setText("您的换货申请已经受理我们的客服会尽快与您联系");
                view.findViewById(R.id.tradedialog_cancel_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                view.findViewById(R.id.tradedialog_comfirm_btn).setVisibility(View.GONE);
                dialog.show();

        }
    }

    public void resetDatas(LinkedList<MongoTrade> datas){
        this.datas = datas;
    }

    public void addDatas(LinkedList<MongoTrade> datas){
        this.datas.addAll(datas);
    }

    @Override
    public void onClick(View v) {

    }

    public interface OnViewHolderListener {
        void onRequestedLastItem();
    }

}
