package com.focosee.qingshow.activity.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S01MatchShowsActivity;
import com.focosee.qingshow.activity.S17PayActivity;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.TradeParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.PushUtil;
import com.focosee.qingshow.util.ShareUtil;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.sku.SkuUtil;
import com.focosee.qingshow.widget.QSTextView;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


/**
 * Created by Administrator on 2015/3/11.
 */
public class S11NewTradeNotifyFragment extends Fragment {

    @InjectView(R.id.img)
    SimpleDraweeView img;
    @InjectView(R.id.item_name)
    QSTextView itemName;
    @InjectView(R.id.price)
    QSTextView price;
    @InjectView(R.id.promoPrice)
    QSTextView promoPrice;
    @InjectView(R.id.selectProp)
    QSTextView selectProp;
    @InjectView(R.id.num)
    QSTextView num;
    @InjectView(R.id.expectedDiscount)
    QSTextView expectedDiscount;
    @InjectView(R.id.expectedPrice)
    QSTextView expectedPrice;
    @InjectView(R.id.nowDiscount)
    TextView nowDiscount;
    @InjectView(R.id.nowPrice)
    TextView nowPrice;
    @InjectView(R.id.sign)
    TextView sign;

    private MongoTrade trade;
    String _id;
    String actualPrice;

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.activity_s11_trade_notify, container, false);
        ButterKnife.inject(this, rootView);
        Bundle bundle = getActivity().getIntent().getExtras();
        _id = PushUtil.getExtra(bundle, "_tradeId");
        actualPrice = PushUtil.getExtra(bundle, "actualPrice");
        if (!TextUtils.isEmpty(_id))
            getDataFromNet(_id);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        return rootView;
    }

    private void initProps() {
        Map<String, List<String>> skus = SkuUtil.filter(trade.selectedSkuProperties);
        StringBuilder sb = new StringBuilder();
        for (String key : skus.keySet()) {
            sb.append(key).append(": ").append(skus.get(key).get(0)).append("   ");
        }
        selectProp.append(sb);
    }


    private void initDes() {
        img.setImageURI(Uri.parse(trade.itemSnapshot.thumbnail));
        itemName.setText(trade.itemSnapshot.name);

        promoPrice.append(StringUtil.FormatPrice(trade.itemSnapshot.promoPrice));
        price.append(StringUtil.FormatPrice(trade.itemSnapshot.price));
        num.append(trade.quantity + "");

        expectedPrice.append(StringUtil.FormatPrice(trade.expectedPrice + ""));
        expectedDiscount.append(StringUtil.formatDiscount(trade.expectedPrice + "", trade.itemSnapshot.promoPrice));

        nowPrice.append(StringUtil.FormatPrice(actualPrice + "").substring(1));
        nowDiscount.append(StringUtil.formatDiscount(actualPrice + "", trade.itemSnapshot.promoPrice));

        promoPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        nowPrice.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        nowDiscount.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        sign.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    @OnClick(R.id.close)
    public void close() {
        getFragmentManager().popBackStack();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.remove(this);
        ft.commit();
    }

    public void onEventMainThread(final MongoTrade trade) {
        if (trade != null) {
            Map<String, Object> prarms = new TreeMap<>();
            LinkedList<MongoTrade.StatusLog> statusLogs = trade.statusLogs;
            prarms.put("_id", trade._id);
            prarms.put("status", 1);
            prarms.put("comment", statusLogs.get(statusLogs.size() - 1));
            prarms.put("actualPrice", actualPrice);
            QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getTradeStatustoApi(), new JSONObject(prarms), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (MetadataParser.hasError(response)) {
                        ErrorHandler.handle(getActivity(), MetadataParser.getError(response));
                        return;
                    }
                    Intent intent = new Intent(getActivity(), S17PayActivity.class);
                    intent.putExtra(S17PayActivity.INPUT_ITEM_ENTITY, trade);
                    startActivity(intent);
                }
            });
            RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);

        }
    }


    @OnClick(R.id.submitBtn)
    public void submit() {
        ShareUtil.shareTradeToWX(_id, QSModel.INSTANCE.getUserId(), System.currentTimeMillis() + "", getActivity(), true);
        EventBus.getDefault().post(trade);
    }

    public void getDataFromNet(String _id) {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getTradeApi(_id), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(getActivity(), MetadataParser.getError(response));
                    return;
                }
                LinkedList<MongoTrade> trades = TradeParser.parseQuery(QSGsonFactory.cateGoryBuilder().create(), response);
                trade = trades.get(0);
                if (null != trade) {
                    initProps();
                    initDes();
                }
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
