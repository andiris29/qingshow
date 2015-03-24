package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.U09TradeListActivity;
import com.focosee.qingshow.activity.U12ReturnActivity;
import com.focosee.qingshow.constants.code.StatusCode;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.sku.Prop;
import com.focosee.qingshow.util.sku.SkuUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by Administrator on 2015/3/16.
 */
public class U09TradeListAdapter extends RecyclerView.Adapter<U09TradeListAdapter.ViewHolder>{

    private Context context;
    private OnViewHolderListener onViewHolderListener;

    public LinkedList<MongoTrade> datas = null;
    public U09TradeListAdapter(Context context) {
        this.context = context;
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_trade_list,viewGroup,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        if(null == datas.get(i))return;
        final MongoTrade trade = datas.get(i);

        final int position = i;

        viewHolder.tradeNo.setText(null == trade.orders.get(0).selectedItemSkuId ? "" : trade.orders.get(0).selectedItemSkuId);
        if(!(trade.status > 8 || trade.status < 0)){//设置交易状态
            viewHolder.tradeStatus.setText(StatusCode.statusArrays[trade.status]);
        }
        try {
            viewHolder.description.setText(trade.orders.get(0).itemSnapshot.taobaoInfo.top_title);
            LinkedList<MongoItem.TaoBaoInfo.SKU> skus = trade.orders.get(0).itemSnapshot.taobaoInfo.skus;
            MongoItem.TaoBaoInfo.SKU mSku = null;
            for (MongoItem.TaoBaoInfo.SKU sku : skus) {
                if(trade.orders.get(0).selectedItemSkuId.equals(sku.sku_id)){
                    mSku = sku;
                }
            }
            ArrayList<Prop> props = SkuUtil.filter(mSku);
            String color = "";
            String measurement = "";
            for (Prop prop : props) {
                if(SkuUtil.KEY.COLOR.id.equals(prop.getPropId())){
                    color = prop.getName();
                }
                if(SkuUtil.KEY.SIZE_1.id.equals(prop.getPropId()) || SkuUtil.KEY.SIZE_2.id.equals(prop.getPropId()) || SkuUtil.KEY.SIZE_3.id.equals(prop.getPropId())){
                    measurement = prop.getName();
                }
            }
            viewHolder.color.setText(color);
            viewHolder.measurement.setText(measurement);
            viewHolder.quantity.setText(String.valueOf(trade.orders.get(0).quantity));
            viewHolder.price.setText(String.valueOf(trade.orders.get(0).price));
            ImageLoader.getInstance().displayImage(trade.orders.get(0).itemSnapshot.imageMetadata.url, viewHolder.image, AppUtil.getPortraitDisplayOptions());
            viewHolder.description.setText(trade.orders.get(0).itemSnapshot.taobaoInfo.top_title);
        }catch (Exception e){
            e.printStackTrace();
        }
        //等待买家付款
        if(trade.status == 0){
            viewHolder.tradingLayout.setVisibility(View.VISIBLE);
            viewHolder.applyReceive.setText(context.getResources().getString(R.string.pay_activity_trade));
            viewHolder.applyReceive.setVisibility(View.VISIBLE);
            viewHolder.applyReceive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        //卖家已发货
        if(trade.status == 3){
//            viewHolder.tradingLayout.setVisibility(View.GONE);
            viewHolder.finishLayout.setVisibility(View.VISIBLE);
            viewHolder.applyReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, U12ReturnActivity.class);
                    context.startActivity(intent);
                }
            });
            viewHolder.applyReceive.setOnClickListener(new View.OnClickListener() {
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
                            statusTo(trade);
                            if(context instanceof U09TradeListActivity){
                                ((U09TradeListActivity)context).doRefresh();
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
        }

        //买家已签收
        if(trade.status == 4){
            viewHolder.tradingLayout.setVisibility(View.VISIBLE);
            viewHolder.applyReturn.setVisibility(View.VISIBLE);
            viewHolder.applyReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, U12ReturnActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(U12ReturnActivity.TRADE_ENTITY, datas.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });




        }
        //交易成功，交易自动关闭

//        viewHolder.finishLayout.setVisibility(View.GONE);
        viewHolder.creatTime.setText(trade.create.toString());
        if(trade.status == 5){
            viewHolder.tradingLayout.setVisibility(View.VISIBLE);
            viewHolder.finishLayout.setVisibility(View.GONE);
            viewHolder.creatTime.setText(trade.create.toString());
        }

        //test
//        viewHolder.applyReturn.setVisibility(View.VISIBLE);
//        viewHolder.applyReturn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, U12ReturnActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable(U12ReturnActivity.TRADE_ENTITY, trade);
//                intent.putExtras(bundle);
//                context.startActivity(intent);
//            }
//        });

    }
    //获取数据的数量
    @Override
    public int getItemCount() {
        return null == datas ? 0 : datas.size();
    }

    public void setOnViewHolderListener(OnViewHolderListener onViewHolderListener){
        this.onViewHolderListener = onViewHolderListener;
    }

    public void statusTo(MongoTrade trade){

        JSONObject jsonObject = getStatusJSONObjcet(trade);

        QSJsonObjectRequest jor = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getTradeStatustoApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(context, MetadataParser.getError(response));
                    return;
                }

                //改变订单状态成功
                Toast.makeText(context, "确认收货成功！", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jor);
    }


    private JSONObject getStatusJSONObjcet(MongoTrade trade){

        Map params = new HashMap();
        params.put("_id", trade._id);
        params.put("status", trade.status);
        if(trade.status == 1 || trade.status == 2 || trade.status == 3
                || trade.status == 6 || trade.status == 7) params.put("status", trade.status);
        switch (trade.status){
            case 2:
//                params.put("taobaoInfo.userNick", trade.orders.get(0).itemSnapshot.taobaoInfo.nick);
                params.put("taobaoInfo.tradeID", trade._id);
                break;
            case 3:
                params.put("logistic.company", trade.logistic.company);
                params.put("logistic.trackingID", trade.logistic.trackingID);
                break;
            case 7:
                params.put("return.logistic.company", trade.returnlogistic.company);
                params.put("return.logistic.trackingID", trade.returnlogistic.trackingID);
                break;
        }

        return new JSONObject(params);

    }

    public void resetDatas(LinkedList<MongoTrade> datas){
        this.datas = datas;
    }

    public void addDatas(LinkedList<MongoTrade> datas){
        this.datas.addAll(datas);
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tradeNo;
        public TextView tradeStatus;
        public ImageView image;
        public TextView description;
        public TextView measurement;
        public TextView quantity;
        public TextView color;
        public TextView price;
        public TextView creatTime;
        public TextView finishTime;
        public LinearLayout finishLayout;
        public RelativeLayout tradingLayout;
        public Button applyReturn;
        public Button applyReceive;
        public ViewHolder(View view){
            super(view);
            tradeNo = (TextView) view.findViewById(R.id.item_tradelist_num);
            tradeStatus = (TextView) view.findViewById(R.id.item_tradelist_status);
            image = (ImageView) view.findViewById(R.id.item_tradelist_image);
            description = (TextView) view.findViewById(R.id.item_tradelist_description);
            measurement = (TextView) view.findViewById(R.id.item_tradelist_measurement);
            quantity = (TextView) view.findViewById(R.id.item_tradelist_quantity);
            color = (TextView) view.findViewById(R.id.item_tradelist_color);
            price = (TextView) view.findViewById(R.id.item_tradelist_price);
            creatTime = (TextView) view.findViewById(R.id.item_tradelist_createTime);
            finishTime = (TextView) view.findViewById(R.id.item_tradelist_finishTime);
            finishLayout = (LinearLayout) view.findViewById(R.id.item_tradelist_finish);
            tradingLayout = (RelativeLayout) view.findViewById(R.id.item_tradelist_trading);
            applyReturn = (Button) view.findViewById(R.id.item_tradelist_applyreturn);
            applyReceive = (Button) view.findViewById(R.id.item_tradelist_applyreceive);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public SpacesItemDecoration getItemDecoration(int space){
        return new SpacesItemDecoration(space);
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            outRect.left = space;
//            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if(parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }

    public interface OnViewHolderListener {
        void onRequestedLastItem();
    }

}
