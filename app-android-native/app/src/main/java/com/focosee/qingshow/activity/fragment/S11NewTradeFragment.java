package com.focosee.qingshow.activity.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
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
import com.focosee.qingshow.activity.U07RegisterActivity;
import com.focosee.qingshow.activity.U11EditAddressActivity;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.GoToWhereAfterLoginModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.util.sku.SkuHelper;
import com.focosee.qingshow.util.sku.SkuUtil;
import com.focosee.qingshow.widget.QSTextView;
import com.focosee.qingshow.widget.flow.FlowRadioButton;
import com.focosee.qingshow.widget.flow.FlowRadioGroup;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

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

    private ArrayMap<String, List<String>> props;
    private Map<String, List<String>> selectProps;
    private Map<String, List<FlowRadioButton>> selectRadioButton;
    private List<String> keys_order;

    private int num = 1;
    private int numOffline = 1;

    private int discountNum;
    private int discountOffline;
    private int discountOnline;
    private double basePrice;
    private int checkIndex[];
    private Map<String, String> skuTable = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.activity_s11_trade, container, false);
        ButterKnife.inject(this, rootView);

        itemEntity = (MongoItem) getActivity().getIntent().getExtras().getSerializable(S10ItemDetailActivity.OUTPUT_ITEM_ENTITY);
        trade = new MongoTrade();
        selectProps = new HashMap<>();
        selectRadioButton = new HashMap<>();


        Log.d(S11NewTradeFragment.class.getSimpleName(), "itemEntity:" + itemEntity);
        Log.d(S11NewTradeFragment.class.getSimpleName(), "skuTable:" + itemEntity.skuTable);

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        basePrice = itemEntity.promoPrice.doubleValue();
        if (itemEntity.minExpectedPrice == null) {
            discountOffline = 5;
        } else
            discountOffline = Math.min(5, ((Double) (itemEntity.minExpectedPrice.doubleValue() / basePrice)).intValue());
        discountNum = discountOnline = 9;
        if (discountNum == 10)
            discountNum = discountOnline = 9;

        initDes();
        if (null != itemEntity.skuTable && !Collections.emptyList().equals(itemEntity.skuTable)
                && itemEntity.skuProperties != null && itemEntity.skuProperties.size() != 0) {
            initProps();
            initSkuTable();
        } else {
            changeBtnClickable(false);
        }

        checkDiscount();
        checkNum();

        return rootView;
    }

    //skuTable(没有库存的商品)
    private void initSkuTable() {
        for (String key : itemEntity.skuTable.keySet()) {
            if (SkuHelper.obtainSkuStock(itemEntity.skuTable, key) < 1) {
                skuTable.put(key, itemEntity.skuTable.get(key));
            }
        }

        Log.d(S11NewTradeFragment.class.getSimpleName(), "skuTable:" + new JSONObject(skuTable).toString());
        Log.d(S11NewTradeFragment.class.getSimpleName(), "item.skuTable:" + new JSONObject(itemEntity.skuTable).toString());
    }

    private boolean inited = false;

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onChanged(String key, int index) {
            List<String> values = new ArrayList<>();
            values.add(props.get(key).get(index));
            List<FlowRadioButton> btnList = new ArrayList<>();
            btnList.add(btnMap.get(key).getChildViews().get(index));

            if (btnMap.get(key).getChildViews().get(index).isChecked()) {
                selectProps.put(key, values);
                selectRadioButton.put(key, btnList);
            } else {
                selectProps.remove(key);
                selectRadioButton.remove(key);
            }
            Log.d(S11NewTradeFragment.class.getSimpleName(), "selectProps:" + new JSONObject(selectProps).toString());
            Log.d(S11NewTradeFragment.class.getSimpleName(), "selectRadioButton:" + new JSONObject(selectRadioButton).toString());
            if (null == itemEntity.skuTable) {
                changeBtnClickable(false);
                for (String myKey : keys_order) {
                    for (FlowRadioButton btn : btnMap.get(myKey).getChildViews()) {
                        btn.setChecked(false);
                        btn.setEnable(false);
                    }
                }
                return;
            }

            if (SkuHelper.obtainSkuStock(itemEntity.skuTable, SkuUtil.formetPropsAsTableKey(selectProps, keys_order)) < 1) {
                changeBtnClickable(false);
            } else {
                changeBtnClickable(true);
            }
            if (!inited) {
                checkNotExistItem(key, 0);
                inited = true;
            } else {
                if (btnMap.get(key).getChildViews().get(index).isEnable())
                    checkNotExistItem(key, index);
            }
        }
    };

    private void initProps() {
        keys_order = SkuUtil.getKeyOrder(itemEntity.skuProperties);
        props = SkuUtil.filter(itemEntity.skuProperties, keys_order);
        checkIndex = new int[props.size()];
        int i = 0;
        for (String key : props.keySet()) {
            bindItem(key, props.get(key), i, onCheckedChangeListener);
            i++;
        }
    }

    //选择属性时，同步更新其他属性的状态。
    private void checkNotExistItem(String prop, int index) {
        Log.d(S11NewTradeFragment.class.getSimpleName(), "index:" + index);
        Map<String, List<String>> tempMap = new HashMap<>(selectProps);
        for (String p : keys_order) {
            if (p.equals(prop)) {
                continue;
            }
            if (null == btnMap.get(p)) continue;
            for (FlowRadioButton btn : btnMap.get(p).getChildViews()) {
                btn.setEnable(true);
            }

            for (String value : props.get(p)) {
                if (null == btnMap.get(p)) continue;
                List<String> bList = new ArrayList<>();
                boolean isAble;
                bList.add(value);
                tempMap.put(p, bList);
                Log.d(S11NewTradeFragment.class.getSimpleName(), "tempMap:" + SkuUtil.formetPropsAsTableKey(tempMap, keys_order));
                if(tempMap.keySet().size() == props.size()) {
                    isAble = SkuHelper.obtainSkuStock(itemEntity.skuTable, SkuUtil.formetPropsAsTableKey(tempMap, keys_order)) >= 1;
                    if (isAble) continue;

                    for (FlowRadioButton btn : btnMap.get(p).getChildViews()) {
                        if (btn.getText().equals(value)) {
                            btn.setEnable(false);
                        }
                    }
                }
            }
        }
        //设置选中的btn样式
        for (String p : keys_order) {
            if (Collections.emptyList() == selectRadioButton) return;
            if (!selectRadioButton.containsKey(p)) continue;
            for (FlowRadioButton btn : selectRadioButton.get(p)) {
                btn.setChecked(true);
            }
        }

    }

    private void changeBtnClickable(boolean clickable) {
        submit.setClickable(clickable);

        if (clickable == false) {
            submit.setBackgroundDrawable(getResources().getDrawable(R.drawable.gary_btn));
        } else {
            submit.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_submit_match));
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
        total.setText(StringUtil.FormatPrice(basePrice / 10f * discountNum));
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
        if (!QSModel.INSTANCE.loggedin() || QSModel.INSTANCE.isGuest()) {
            GoToWhereAfterLoginModel.INSTANCE.set_class(null);
            startActivity(new Intent(getActivity(), U07RegisterActivity.class));
            return;
        }

        UserCommand.refresh(new Callback() {
            @Override
            public void onComplete() {
                if (TextUtils.isEmpty(QSModel.INSTANCE.getUser().mobile)) {
                    startActivity(new Intent(getActivity(), U11EditAddressActivity.class));
                    return;
                }

                submit.setClickable(false);
                trade.selectedSkuProperties = SkuUtil.propParser(selectProps, keys_order);
                trade.expectedPrice = new BigDecimal(basePrice * discountNum * 0.1).setScale(2, RoundingMode.HALF_UP).floatValue();
                trade.itemSnapshot = itemEntity;
                trade.quantity = num;
                submitToNet(trade);
            }
        });
    }

    private void submitToNet(MongoTrade trade) {
        Map params = new HashMap();
        params.put("expectedPrice", trade.expectedPrice);
        try {
            params.put("selectedSkuProperties", new JSONArray(QSGsonFactory.create().toJson(trade.selectedSkuProperties)));
            params.put("itemSnapshot", new JSONObject(QSGsonFactory.create().toJson(trade.itemSnapshot)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("quantity", trade.quantity);
        Log.d(S11NewTradeFragment.class.getSimpleName(), "promoterRef:" + getActivity().getIntent().getStringExtra(S10ItemDetailActivity.PROMOTRER));
        params.put("promoterRef", getActivity().getIntent().getStringExtra(S10ItemDetailActivity.PROMOTRER));
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getTradeCreateApi(), new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(getActivity(), MetadataParser.getError(response));
                    submit.setClickable(true);
                    return;
                }
                Toast.makeText(getActivity(), R.string.toast_activity_discount_successed, Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(ValueUtil.SUBMIT_TRADE_SUCCESSED);
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
        propItem.setPadding(5, 25, 5, 25);
        propItem.setTextSize(13);
        if (!TextUtils.isEmpty(text)) {
            propItem.setText(text);
        }
        return propItem;
    }

    private Map<String, FlowRadioGroup> btnMap = new HashMap<>();

    private void bindItem(final String key, final List<String> values, final int position, final OnCheckedChangeListener onCheckedChangeListener) {

        LinearLayout prop = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.item_sku_prop, null);
        FlowRadioGroup group = (FlowRadioGroup) prop.findViewById(R.id.propGroup);
        ((TextView) prop.findViewById(R.id.propText)).setText(key.split("_")[0]);

        ViewGroup.MarginLayoutParams itemParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT);
        itemParams.setMargins(10, 10, 10, 10);

        for (int i = 0; i < values.size(); i++) {
            FlowRadioButton propItem = initPropItem(values.get(i));
            group.addChildView(propItem, itemParams);
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
        btnMap.put(key, group);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("S11NewTradeFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("S11NewTradeFragment");
    }

}
