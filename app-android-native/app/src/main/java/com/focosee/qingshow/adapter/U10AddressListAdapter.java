package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.U10AddressListActivity;
import com.focosee.qingshow.activity.U11EditAddressActivity;
import com.focosee.qingshow.activity.fragment.S17ReceiptFragment;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;
import com.focosee.qingshow.widget.ConfirmDialog;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/3/16.
 */
public class U10AddressListAdapter extends AbsAdapter<MongoPeople.Receiver> {

    private int default_posion = Integer.MAX_VALUE;
    private MongoPeople people;

    public U10AddressListAdapter(@NonNull List<MongoPeople.Receiver> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
        people = QSModel.INSTANCE.getUser();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, final int i) {


//        final int position = (datas.size() - i - 1) < 0 ? 0 : datas.size() - i - 1;

        holder.setText(R.id.item_addresslist_name, null == datas.get(i).name ? "" : datas.get(i).name);
        holder.setText(R.id.item_addresslist_phone, null == datas.get(i).phone ? "" : datas.get(i).phone);
        holder.setText(R.id.item_addresslist_province, (null == datas.get(i).province ? "" : datas.get(i).province)
                + (null == datas.get(i).address ? "" : datas.get(i).address));
        holder.setText(R.id.item_addresslist_address, null == datas.get(i).address ? "" : datas.get(i).address);
        if (datas.get(i).isDefault) {
            holder.setImgeByRes(R.id.item_addresslist_choose_btn, R.drawable.s11_payment_hover);
            holder.setImgeByRes(R.id.item_addresslist_edit_img, R.drawable.item_addresslist_edit);
            holder.setImgeByRes(R.id.item_addresslist_del_img, R.drawable.item_addresslist_del);
            ((TextView)holder.getView(R.id.item_addresslist_edit_tv)).setTextColor(context.getResources().getColor(R.color.master_pink));
            ((TextView)holder.getView(R.id.item_addresslist_del_tv)).setTextColor(context.getResources().getColor(R.color.master_pink));
            default_posion = i;
        } else {
            holder.setImgeByRes(R.id.item_addresslist_choose_btn, R.drawable.s11_payment);
            holder.setImgeByRes(R.id.item_addresslist_edit_img, R.drawable.item_addresslist_edit_gray);
            holder.setImgeByRes(R.id.item_addresslist_del_img, R.drawable.item_addresslist_del_gray);
            ((TextView)holder.getView(R.id.item_addresslist_edit_tv)).setTextColor(context.getResources().getColor(R.color.darker_gray));
            ((TextView)holder.getView(R.id.item_addresslist_del_tv)).setTextColor(context.getResources().getColor(R.color.darker_gray));
        }
        if (context instanceof U10AddressListActivity) {
            if (S17ReceiptFragment.TO_U10.equals(((U10AddressListActivity) context).fromWhere)) {
                holder.getView(R.id.item_addresslist_content).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(datas.get(i));
                        ((U10AddressListActivity) context).finish();
                    }
                });

            }
        }


        holder.getView(R.id.item_addresslist_edit_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, U11EditAddressActivity.class);
                intent.putExtra("receiver", datas.get(i));
                context.startActivity(intent);
            }
        });

        holder.getView(R.id.item_addresslist_del_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ConfirmDialog dialog = new ConfirmDialog();

                dialog.setTitle("确认删除？");
                dialog.setConfirm("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delReceiver(datas.get(i).uuid);
                        dialog.dismiss();
                    }
                });

                dialog.show(((U10AddressListActivity) context).getSupportFragmentManager());
            }
        });


        holder.getView(R.id.item_addresslist_choose_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (i == default_posion) return;

                if (default_posion != Integer.MAX_VALUE) {
                    datas.get(default_posion).isDefault = false;
                }

                datas.get(i).isDefault = true;

                Gson gson = new Gson();
                gson.toJson(datas.get(i));

                JSONObject jsonObject = null;

                try {
                    jsonObject = new JSONObject(new Gson().toJson(datas.get(i)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Map params1 = new HashMap();
                params1.put("uuid", datas.get(i).uuid);
                params1.put("name", datas.get(i).name);
                params1.put("phone", datas.get(i).phone);
                params1.put("province", datas.get(i).province);
                params1.put("address", datas.get(i).address);
                params1.put("isDefault", datas.get(i).isDefault);

                QSJsonObjectRequest jor1 = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getUserSaveReceiverApi(), jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (MetadataParser.hasError(response)) {
                            ErrorHandler.handle(context, MetadataParser.getError(response));
                            return;
                        }
                        notifyDataSetChanged();
                    }
                });

                if(default_posion == Integer.MAX_VALUE){
                    default_posion = i;
                    notifyDataSetChanged();
                    return;
                }
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
                        if (MetadataParser.hasError(response)) {
                            ErrorHandler.handle(context, MetadataParser.getError(response));
                            return;
                        }
                        default_posion = i;
                        notifyDataSetChanged();
                    }
                });

                RequestQueueManager.INSTANCE.getQueue().add(jor1);

                RequestQueueManager.INSTANCE.getQueue().add(jor2);
            }
        });
    }

    public void delReceiver(String uuid) {

        Map<String, String> params = new HashMap<>();
        params.put("uuid", uuid);

        QSJsonObjectRequest jor = new QSJsonObjectRequest(QSAppWebAPI.getUserRemoveReceiverApi(), new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(context, MetadataParser.getError(response));
                    return;
                }
                QSModel.INSTANCE.setUser(UserParser.parseGet(response));
                people = QSModel.INSTANCE.getUser();
                datas = people.receivers;
                notifyDataSetChanged();
                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jor);
    }
}

