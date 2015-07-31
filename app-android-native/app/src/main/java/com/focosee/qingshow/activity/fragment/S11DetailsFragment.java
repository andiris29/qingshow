package com.focosee.qingshow.activity.fragment;

import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S10ItemDetailActivity;
import com.focosee.qingshow.adapter.SkuPropsAdpater;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoOrder;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.sku.SkuUtil;
import com.focosee.qingshow.widget.QSTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by Administrator on 2015/3/11.
 */
public class S11DetailsFragment extends Fragment {

    @InjectView(R.id.itemName)
    TextView itemName;
    @InjectView(R.id.desImg)
    SimpleDraweeView desImg;
    @InjectView(R.id.s11_details_price)
    QSTextView price;
    @InjectView(R.id.s11_details_maxprice)
    QSTextView maxPrice;
    @InjectView(R.id.num)
    QSTextView numText;
    @InjectView(R.id.discount)
    TextView discountText;
    @InjectView(R.id.total)
    TextView total;
    @InjectView(R.id.cut_num)
    ImageView cutNum;
    @InjectView(R.id.plus_num)
    ImageView plusNum;
    @InjectView(R.id.cut_discount)
    ImageView cutDiscount;
    @InjectView(R.id.plus_discount)
    ImageView plusDiscount;
    @InjectView(R.id.props)
    RecyclerView recyclerView;

    private MongoItem itemEntity;
    private MongoOrder order;

    private SkuPropsAdpater adpater;
    private View rootView;

    private Map<String, List<String>> props;
    private Map<String, List<String>> selectProps;

    private int num = 1;
    private int numOffline = 1;
    private int numOnline = 9;

    private int discountNum;
    private int discountOffline = 1;
    private int discountOnline = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.activity_s11_trade, container, false);
        ButterKnife.inject(this, rootView);

        itemEntity = (MongoItem) getActivity().getIntent().getExtras().getSerializable(S10ItemDetailActivity.INPUT_ITEM_ENTITY);
        order = new MongoOrder();
        selectProps = new HashMap<>();

        Log.d("s11", itemEntity._id);

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        if (itemEntity.skuProperties == null) {
            return rootView;
        }

        discountOffline = Integer.parseInt(itemEntity.minExpectedPrice);
        discountNum = discountOnline = ((Double) (Double.parseDouble(itemEntity.promoPrice) / Double.parseDouble(itemEntity.price) * 10)).intValue();

        initProps();
        initDes();

        checkDiscount();
        checkNum();

        return rootView;
    }

    private void initProps() {
        props = SkuUtil.filter(itemEntity.skuProperties);

        adpater = new SkuPropsAdpater(itemEntity.skuProperties, getActivity(), R.layout.item_sku_prop);
        adpater.notifyDataSetChanged();
        adpater.setOnCheckedChangeListener(new SkuPropsAdpater.OnCheckedChangeListener() {
            @Override
            public void onChanged(String key, int index) {
                List<String> values = new ArrayList<>();
                values.add(props.get(key).get(index));
                selectProps.put(key, values);
            }
        });
        recyclerView.setAdapter(adpater);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }


    private void initDes() {
        desImg.setImageURI(Uri.parse(itemEntity.thumbnail));
        itemName.setText(itemEntity.name);
        price.setText(StringUtil.FormatPrice(itemEntity.promoPrice));
        maxPrice.setText(StringUtil.FormatPrice(itemEntity.price));
        maxPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @OnClick({R.id.cut_num, R.id.plus_num})
    public void clickNum(ImageView v) {
        switch (v.getId()) {
            case R.id.cut_num:
                num--;
                checkNum();
                break;
            case R.id.plus_num:
                num++;
                checkNum();
                break;
        }
    }

    private void checkNum() {
        numText.setText(String.valueOf(num));
        if (num <= numOffline) {
            cutNum.setClickable(false);
            cutNum.setImageDrawable(getResources().getDrawable(R.drawable.cut_hover));
        } else if (num >= numOnline) {
            plusNum.setClickable(false);
            plusNum.setImageDrawable(getResources().getDrawable(R.drawable.plus_hover));
        } else {
            cutNum.setClickable(true);
            cutNum.setImageDrawable(getResources().getDrawable(R.drawable.cut));
            plusNum.setClickable(true);
            plusNum.setImageDrawable(getResources().getDrawable(R.drawable.plus));
        }
    }

    @OnClick({R.id.cut_discount, R.id.plus_discount})
    public void clickDiscount(ImageView v) {
        switch (v.getId()) {
            case R.id.cut_discount:
                discountNum--;
                checkDiscount();
                break;
            case R.id.plus_discount:
                discountNum++;
                checkDiscount();
                break;
        }
    }

    private void checkDiscount() {
        discountText.setText(String.valueOf(discountNum) + getResources().getString(R.string.s11_discount));
        total.setText(String.valueOf(discountNum));
        if (discountNum <= discountOffline) {
            cutDiscount.setClickable(false);
            cutDiscount.setImageDrawable(getResources().getDrawable(R.drawable.cut_hover));
        } else if (discountNum >= discountOnline) {
            plusDiscount.setClickable(false);
            plusDiscount.setImageDrawable(getResources().getDrawable(R.drawable.plus_hover));
        } else {
            cutDiscount.setClickable(true);
            cutDiscount.setImageDrawable(getResources().getDrawable(R.drawable.cut));
            plusDiscount.setClickable(true);
            plusDiscount.setImageDrawable(getResources().getDrawable(R.drawable.plus));
        }
    }

    @OnClick({R.id.close, R.id.cancel})
    public void close() {
        getFragmentManager().popBackStack();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.remove(this);
        ft.commit();
    }

    @OnClick(R.id.submitBtn)
    public void submit() {
        order.selectedSkuProperties = SkuUtil.propParser(selectProps);
        order.actualPrice = Double.parseDouble(itemEntity.promoPrice);
        order.expectedPrice = Double.parseDouble(itemEntity.price) * discountNum / 10;
        order.itemSnapshot = itemEntity;
        order.quantity = num;

        List<MongoOrder> orders = new ArrayList<>();
        orders.add(order);
        submitToNet(orders);
    }

    private void submitToNet(List<MongoOrder> orders) {
        Map<String, Object> params = new HashMap<>();
        try {
            JSONArray array = new JSONArray(QSGsonFactory.create().toJson(orders));
            params.put("orders", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getTradeCreateApi(), new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

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
