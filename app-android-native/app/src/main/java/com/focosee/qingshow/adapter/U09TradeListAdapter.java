package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.U12ReturnActivity;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by Administrator on 2015/3/16.
 */
public class U09TradeListAdapter extends RecyclerView.Adapter<U09TradeListAdapter.ViewHolder>{

    private Context context;
    private OnViewHolderListener onViewHolderListener;

    public String[] datas = null;
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

        if(i % 2 ==0){
            viewHolder.tradingLayout.setVisibility(View.GONE);
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
                    ((TextView)view.findViewById(R.id.tradelist_dialog_receiveTime)).setText("头图范德萨辅导费萨芬四大皆空啦");
                    view.findViewById(R.id.tradedialog_cancel_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    view.findViewById(R.id.tradedialog_comfirm_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "确认收货成功！", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });

                    dialog.show();


                }
            });
        } else {
            viewHolder.tradingLayout.setVisibility(View.VISIBLE);
            viewHolder.finishLayout.setVisibility(View.GONE);
        }

        //加载更多
        if (i == getItemCount() - 1) onViewHolderListener.onRequestedLastItem();

    }
    //获取数据的数量
    @Override
    public int getItemCount() {
        return 10;
    }

    public void setOnViewHolderListener(OnViewHolderListener onViewHolderListener){
        this.onViewHolderListener = onViewHolderListener;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public class ViewHolder extends RecyclerView.ViewHolder {
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
        public ImageView applyReturn;
        public ImageView applyReceive;
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
            applyReturn = (ImageView) view.findViewById(R.id.item_tradelist_applyreturn);
            applyReceive = (ImageView) view.findViewById(R.id.item_tradelist_applyreceive);
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
