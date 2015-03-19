package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.U11EditAddressActivity;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.QSStringRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Administrator on 2015/3/16.
 */
public class U10AddressListAdapter extends RecyclerView.Adapter<U10AddressListAdapter.ViewHolder>{

    private Context context;
    private int default_posion = Integer.MAX_VALUE;
    private MongoPeople people;
    public LinkedList<MongoPeople.Receiver> datas = null;
    public U10AddressListAdapter(Context context) {
        this.context = context;
        people = QSModel.INSTANCE.getUser();
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_addreslist,viewGroup,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {

        final int position = i;

        viewHolder.nameTV.setText(null == datas.get(i).name ? "" : datas.get(i).name);
        viewHolder.phoneTV.setText(null == datas.get(i).phone ? "" : datas.get(i).phone);
        viewHolder.addressTV.setText((null == datas.get(i).province ? "" : datas.get(i).province)
                + (null == datas.get(i).address ? "" : datas.get(i).address));
        if(datas.get(i).isDefault){
            viewHolder.chooseBtn.setImageResource(R.drawable.s11_payment_hover);
            default_posion = i;
        } else {
            viewHolder.chooseBtn.setImageResource(R.drawable.s11_payment);
        }

        viewHolder.editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, U11EditAddressActivity.class);
                intent.putExtra("receiver", datas.get(position));
                context.startActivity(intent);
            }
        });

        viewHolder.delLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delReceiver(datas.get(position).uuid);
            }
        });

        viewHolder.chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(position == default_posion)return;

                if(default_posion != Integer.MAX_VALUE){
                    datas.get(default_posion).isDefault = false;
                }


                datas.get(position).isDefault = true;

                Map params1 = new HashMap();
                params1.put("uuid", datas.get(position).uuid);
                params1.put("name", datas.get(position).name);
                params1.put("phone", datas.get(position).phone);
                params1.put("province", datas.get(position).province);
                params1.put("address", datas.get(position).address);
                params1.put("isDefault", datas.get(position).isDefault);

                QSJsonObjectRequest jor1 = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getUserSaveReceiverApi(), new JSONObject(params1), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(MetadataParser.hasError(response)){
                            ErrorHandler.handle(context,MetadataParser.getError(response));
                        }
                        notifyDataSetChanged();
                    }
                });

                Map params2 = new HashMap();
                params2.put("uuid", datas.get(default_posion).uuid);
                params2.put("name", datas.get(default_posion).name);
                params2.put("phone", datas.get(default_posion).phone);
                params2.put("province", datas.get(default_posion).province);
                params2.put("address", datas.get(default_posion).address);
                params2.put("isDefault", datas.get(default_posion).isDefault);

                QSJsonObjectRequest jor2 = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getUserSaveReceiverApi(), new JSONObject(params2), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(MetadataParser.hasError(response)){
                            ErrorHandler.handle(context,MetadataParser.getError(response));
                        }
                        default_posion = position;
                        notifyDataSetChanged();
                    }
                });

                RequestQueueManager.INSTANCE.getQueue().add(jor1);
                RequestQueueManager.INSTANCE.getQueue().add(jor2);
            }
        });

    }
    //获取数据的数量
    @Override
    public int getItemCount() {
        return null == datas ? 0 : datas.size();
    }

    public void resetData(LinkedList<MongoPeople.Receiver> datas){
        this.datas = datas;
    }


    public void delReceiver(String uuid){

        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid", uuid);

        QSJsonObjectRequest jor = new QSJsonObjectRequest(QSAppWebAPI.getUserRemoveReceiverApi(), new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(context, MetadataParser.getError(response));
                    return;
                }
                QSModel.INSTANCE.setUser(UserParser.parseGet(response));
                notifyDataSetChanged();
                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jor);
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView chooseBtn;
        public LinearLayout delLayout;
        public LinearLayout editLayout;
        public TextView nameTV;
        public TextView phoneTV;
        public TextView addressTV;
        public ViewHolder(View view){
            super(view);
            chooseBtn = (ImageView) view.findViewById(R.id.item_addresslist_choose_btn);
            delLayout = (LinearLayout) view.findViewById(R.id.item_addresslist_del_layout);
            editLayout = (LinearLayout) view.findViewById(R.id.item_addresslist_edit_layout);
            nameTV = (TextView) view.findViewById(R.id.item_addresslist_name);
            phoneTV = (TextView) view.findViewById(R.id.item_addresslist_phone);
            addressTV = (TextView) view.findViewById(R.id.item_addresslist_address);
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
}
