package com.focosee.qingshow.activity.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S10ItemDetailActivity;
import com.focosee.qingshow.activity.U09TradeListActivity;
import com.focosee.qingshow.adapter.SkuPropsAdpater;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.sku.SkuUtil;
import com.focosee.qingshow.widget.QSTextView;
import com.focosee.qingshow.widget.flow.FlowRadioButton;
import com.focosee.qingshow.widget.flow.FlowRadioGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by Administrator on 2015/3/11.
 */
public class S11NewTradeFragment extends Fragment {

    @InjectView(R.id.itemName)
    TextView itemName;
    @InjectView(R.id.desImg)
    SimpleDraweeView desImg;
    @InjectView(R.id.s11_details_price)
    QSTextView price;
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
    @InjectView(R.id.submitBtn)
    Button submit;
    @InjectView(R.id.props)
    LinearLayout propsLayout;

    private MongoItem itemEntity;
    private MongoTrade trade;

    private View rootView;

    private Map<String, List<String>> props;
    private Map<String, List<String>> selectProps;

    private int num = 1;
    private int numOffline = 1;

    private int discountNum;
    private int discountOffline;
    private int discountOnline;
    double basePrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.activity_s11_trade, container, false);
        ButterKnife.inject(this, rootView);

        itemEntity = (MongoItem) getActivity().getIntent().getExtras().getSerializable(S10ItemDetailActivity.INPUT_ITEM_ENTITY);
        trade = new MongoTrade();
        selectProps = new HashMap<>();

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        basePrice = Double.parseDouble(itemEntity.promoPrice);
        if (itemEntity.minExpectedPrice == null) {
            discountOffline = 5;
        } else
            discountOffline = Math.min(5, ((Double) (Double.parseDouble(itemEntity.minExpectedPrice) / basePrice)).intValue());
        discountNum = discountOnline = 9;
        if (discountNum == 10)
            discountNum = discountOnline = 9;


        initProps();
        initDes();

        checkDiscount();
        checkNum();

        return rootView;
    }

    private void initProps() {
        props = SkuUtil.filter(itemEntity.skuProperties);
        Log.i("tag", QSGsonFactory.create().toJson(itemEntity.skuProperties).toString());
        for (int i = 0; i < itemEntity.skuProperties.size(); i++) {
            bindItem(itemEntity.skuProperties, i, new OnCheckedChangeListener() {
                @Override
                public void onChanged(String key, int index) {
                    List<String> values = new ArrayList<>();
                    values.add(props.get(key).get(index));
                    selectProps.put(key, values);
                }
            });
        }
    }


    private void initDes() {
        desImg.setImageURI(Uri.parse(itemEntity.thumbnail));
        itemName.setText(itemEntity.name);
        price.setText(StringUtil.FormatPrice(itemEntity.promoPrice));
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

        cutNum.setClickable(true);
        cutNum.setImageDrawable(getResources().getDrawable(R.drawable.cut));
        plusNum.setClickable(true);
        plusNum.setImageDrawable(getResources().getDrawable(R.drawable.plus));
        if (num <= numOffline) {
            cutNum.setClickable(false);
            cutNum.setImageDrawable(getResources().getDrawable(R.drawable.cut_hover));
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
        total.setText(StringUtil.FormatPrice(String.valueOf(basePrice / 10f * discountNum)));
        if (discountNum >= discountOnline) {
            plusDiscount.setClickable(false);
            plusDiscount.setImageDrawable(getResources().getDrawable(R.drawable.plus_hover));
        } else {
            plusDiscount.setClickable(true);
            plusDiscount.setImageDrawable(getResources().getDrawable(R.drawable.plus));
        }

        if (discountNum <= discountOffline) {
            cutDiscount.setClickable(false);
            cutDiscount.setImageDrawable(getResources().getDrawable(R.drawable.cut_hover));
        } else {
            cutDiscount.setClickable(true);
            cutDiscount.setImageDrawable(getResources().getDrawable(R.drawable.cut));
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
        submit.setClickable(false);
        trade.selectedSkuProperties = SkuUtil.propParser(selectProps);

        trade.expectedPrice = basePrice * discountNum / 10;
        trade.itemSnapshot = itemEntity;
        trade.quantity = num;
        submitToNet(trade);
    }

    private void submitToNet(MongoTrade trade) {
        Map<String, Object> params = new HashMap<>();
        params.put("expectedPrice", trade.expectedPrice);
        try {
            params.put("selectedSkuProperties", new JSONArray(QSGsonFactory.create().toJson(trade.selectedSkuProperties)));
            params.put("itemSnapshot", new JSONObject(QSGsonFactory.create().toJson(trade.itemSnapshot)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("quantity", trade.quantity);
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getTradeCreateApi(), new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(getActivity(), MetadataParser.getError(response));
                    submit.setClickable(true);
                    return;
                }
                Toast.makeText(getActivity(), R.string.toast_activity_discount_successed, Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                submit.setClickable(true);
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    //------------------------------------------------------------------------------------

    private float radioBtnWdith = 35;
    private float radioBtnHeight = 28;

    public interface OnCheckedChangeListener {
        void onChanged(String key, int index);
    }


    private FlowRadioButton initPropItem(String text) {
        FlowRadioButton propItem = new FlowRadioButton(getActivity());
        propItem.setMinWidth((int) AppUtil.transformToDip(radioBtnWdith, getActivity()));
        propItem.setMinHeight((int) AppUtil.transformToDip(radioBtnHeight, getActivity()));
        propItem.setBackgroundResource(R.drawable.gay_btn_ring);
        propItem.setTextColor(getActivity().getResources().getColor(R.color.gary));
        propItem.setGravity(Gravity.CENTER);
        propItem.setPadding(2, 2, 2, 2);
        propItem.setTextSize(13);
        if (!TextUtils.isEmpty(text)) {
            propItem.setText(text);
        }
        return propItem;
    }

    private void bindItem(List<String> datas, final int position, final OnCheckedChangeListener onCheckedChangeListener) {
        final int checkIndex[] = new int[datas.size()];
        LinearLayout prop = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.item_sku_prop, null);
        String data = datas.get(position);
        List<String> values = SkuUtil.getValues(data);

        final String key = SkuUtil.getPropName(data);
        FlowRadioGroup group = (FlowRadioGroup) prop.findViewById(R.id.propGroup);
        ((TextView) prop.findViewById(R.id.propText)).setText(key.split("_")[0]);

        ViewGroup.MarginLayoutParams itemParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT);
        itemParams.setMargins(10, 10, 10, 10);
        for (int i = 0; i < values.size(); i++) {
            FlowRadioButton propItem = initPropItem(values.get(i));
            group.addView(propItem, itemParams);
            if (i == checkIndex[position]) {
                propItem.setChecked(true);
                if (onCheckedChangeListener != null)
                    onCheckedChangeListener.onChanged(key, i);
            }
        }

        group.setOnCheckedChangeListener(new FlowRadioGroup.OnCheckedChangeListener() {
            @Override
            public void checkedChanged(int index) {
                checkIndex[position] = index;
                if (onCheckedChangeListener != null)
                    onCheckedChangeListener.onChanged(key, index);
            }
        });
        propsLayout.addView(prop);
    }
}
