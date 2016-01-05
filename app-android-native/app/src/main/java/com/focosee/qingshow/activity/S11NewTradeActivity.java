package com.focosee.qingshow.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S11CanvasPagerAdapter;
import com.focosee.qingshow.adapter.TopOwnerAdapter;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.QSRxApi;
import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.QSSubscriber;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.TradeParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.GoToWhereAfterLoginModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.model.vo.remix.RemixByItem;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.RectUtil;
import com.focosee.qingshow.util.ShareUtil;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.ToastUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.util.sku.SkuHelper;
import com.focosee.qingshow.util.sku.SkuUtil;
import com.focosee.qingshow.widget.QSCanvasView;
import com.focosee.qingshow.widget.QSImageView;
import com.focosee.qingshow.widget.QSTextView;
import com.focosee.qingshow.widget.flow.FlowRadioButton;
import com.focosee.qingshow.widget.flow.FlowRadioGroup;
import com.focosee.qingshow.wxapi.ShareTradeEvent;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class S11NewTradeActivity extends BaseActivity {
    @InjectView(R.id.itemName)
    TextView itemName;
    @InjectView(R.id.desImg)
    SimpleDraweeView desImg;
    @InjectView(R.id.s11_details_price)
    QSTextView price;
    @InjectView(R.id.num)
    QSTextView numText;
    @InjectView(R.id.cut_num)
    ImageView cutNum;
    @InjectView(R.id.plus_num)
    ImageView plusNum;
    @InjectView(R.id.submitBtn)
    QSTextView submit;
    @InjectView(R.id.props)
    LinearLayout propsLayout;
    @InjectView(R.id.s11_canvas_pager)
    ViewPager canvasPager;
    @InjectView(R.id.s11_follow)
    TextView follow;
    @InjectView(R.id.buyers)
    RecyclerView buyers;
    @InjectView(R.id.text)
    TextView text;
    @InjectView(R.id.share)
    QSTextView share;
    @InjectView(R.id.s11_go_det)
    TextView goDet;
    @InjectView(R.id.iv_s11_go_det_logo)
    SimpleDraweeView ivLogo;
    @InjectView(R.id.s11_hint)
    TextView hint;

    private MongoItem itemEntity;
    private MongoTrade trade;

    private ArrayMap<String, List<String>> props;
    private Map<String, List<String>> selectProps;
    private Map<String, List<FlowRadioButton>> selectRadioButton;
    private List<String> keys_order;

    private List<FrameLayout> canvasList;
    private S11CanvasPagerAdapter adapter;

    private int num = 1;
    private int numOffline = 1;
    private double basePrice;
    private int checkIndex[];
    private MongoTrade t;
    private Map<String, String> skuTable = new HashMap<>();
    private boolean isCheck = true;


    public static final String OUTPUT_ITEM_ENTITY = "OUTPUT_ITEM_ENTITY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s11_trade);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);

        itemEntity = (MongoItem) this.getIntent().getExtras().getSerializable(OUTPUT_ITEM_ENTITY);
        trade = new MongoTrade();
        selectProps = new HashMap<String, List<String>>();
        selectRadioButton = new HashMap<String, List<FlowRadioButton>>();
        if (itemEntity.promoPrice != null) {
            basePrice = itemEntity.promoPrice.doubleValue();
        }
        initDes(itemEntity);
        if (null != itemEntity.skuTable && !Collections.emptyList().equals(itemEntity.skuTable)
                && itemEntity.skuProperties != null && itemEntity.skuProperties.size() != 0) {
            initProps(itemEntity);
            initSkuTable(itemEntity);
        } else {
            changeBtnClickable(false);
        }
        canvasList = new ArrayList<FrameLayout>();
        adapter = new S11CanvasPagerAdapter(canvasList);
        canvasPager.setAdapter(adapter);

        checkNum();

        if (itemEntity != null){
            addCanvas(itemEntity._id);
        }
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemEntity != null) {
                    addCanvas(itemEntity._id);
                }
            }
        });

        initBuyers(itemEntity._id);
        if( itemEntity != null && itemEntity.expectable != null) {
            float price = itemEntity.promoPrice.floatValue() - itemEntity.expectable.reduction.floatValue();
            if(price < 0) {
                price = 0 ;
            }
            submit.setText(StringUtil.FormatPrice(price)+ " 购买");
            share.setText("活动立减" + itemEntity.expectable.reduction.floatValue() + "元");
        }else {
            share.setVisibility(View.GONE);
            submit.setText( " 立即购买");
        }


        findViewById(R.id.backImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S11NewTradeActivity.this.finish();
            }
        });

        goDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S11NewTradeActivity.this, S10ItemDetailActivity.class);
                intent.putExtra(S10ItemDetailActivity.INPUT_ITEM_ENTITY, itemEntity);
                S11NewTradeActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    //skuTable(没有库存的商品)
    private void initSkuTable(MongoItem itemEntity) {
        if (itemEntity.skuTable == null){
            propsLayout.removeAllViews();
            return;
        }

        for (String key : itemEntity.skuTable.keySet()) {
            if (SkuHelper.obtainSkuStock(itemEntity.skuTable, key) < 1) {
                skuTable.put(key, itemEntity.skuTable.get(key));
            }
        }

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

    private void initProps(MongoItem itemEntity) {
        propsLayout.removeAllViews();
        keys_order = SkuUtil.getKeyOrder(itemEntity.skuProperties);
        props = SkuUtil.filter(itemEntity.skuProperties, keys_order);
        checkIndex = new int[props.size()];
        int i = 0;
        for (String key : keys_order) {
            bindItem(key, props.get(key), i, onCheckedChangeListener);
            i++;
        }
    }

    //选择属性时，同步更新其他属性的状态。
    private void checkNotExistItem(String prop, int index) {
        Map<String, List<String>> tempMap = new HashMap<String, List<String>>(selectProps);
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
                if (tempMap.keySet().size() == props.size()) {
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
        isCheck = clickable;
      //  submit.setClickable(clickable);
    }


    private void initDes(MongoItem itemEntity) {

        if( itemEntity != null){
            desImg.setImageURI(Uri.parse(itemEntity.thumbnail));
            itemName.setText(itemEntity.name);
            if( itemEntity.expectable != null){
                hint.setText(itemEntity.expectable.message);
            }
            //ivLogo
       //     Log.e("test_i" ,"itemEntity.thumbnail  --> "+ itemEntity.thumbnail);
            ivLogo.setImageURI(Uri.parse(itemEntity.sourceInfo.icon));

            price.setText(StringUtil.FormatPrice(itemEntity.promoPrice));
        }
    }

    public void onEventMainThread(ShareTradeEvent event){
        if (event.shareByCreateUser && t != null){
            Intent intent = new Intent(this, U14PayActivity.class);
            intent.putExtra(U14PayActivity.INPUT_ITEM_ENTITY, t);
            startActivity(intent);
        }
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

    @OnClick(R.id.submitBtn)
    public void submit() {
        if (isCheck) {
            if (!QSModel.INSTANCE.loggedin() || QSModel.INSTANCE.isGuest()) {
                GoToWhereAfterLoginModel.INSTANCE.set_class(null);
                Log.e("submitBtn --> ","QSModel.INSTANCE.loggedin() --> "+QSModel.INSTANCE.loggedin() +"  QSModel.INSTANCE.isGuest()---> "+QSModel.INSTANCE.isGuest());
                return;
            }

            UserCommand.refresh(new Callback() {
                @Override
                public void onComplete() {
                    if (TextUtils.isEmpty(QSModel.INSTANCE.getUser().mobile)) {
                        return;
                    }
                    //submit.setClickable(false);
                    if (selectProps.size() > 0){
                        trade.selectedSkuProperties = SkuUtil.propParser(selectProps, keys_order);
                        trade.itemSnapshot = itemEntity;
                        trade.quantity = num;
                        submitToNet(trade);
                    }else {
                        ToastUtil.showShortToast(S11NewTradeActivity.this,"选择商品属性哦~");
                    }
                }
            });
        }else {
            ToastUtil.showShortToast(this ,"系统有误，请稍后！");
        }

    }

    /***
     * 提交订单
     * @param trade
     */
    private void submitToNet(MongoTrade trade) {
        Map params = new HashMap();
        params.put("expectedPrice", trade.expectedPrice);
        if (trade.selectedSkuProperties == null || trade.selectedSkuProperties.size() < 0){
            ToastUtil.showShortToast(S11NewTradeActivity.this,"expectedPrice 为空");
            return;
        }
        try {
            params.put("selectedSkuProperties", new JSONArray(QSGsonFactory.create().toJson(trade.selectedSkuProperties)));
            params.put("itemRef", trade.itemSnapshot._id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("quantity", trade.quantity);
        params.put("promoterRef", this.getIntent().getStringExtra(S10ItemDetailActivity.PROMOTRER));
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getTradeCreateApi(), new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S11NewTradeActivity.this, MetadataParser.getError(response));
                    submit.setClickable(true);
                    return;
                }
                EventBus.getDefault().post(ValueUtil.SUBMIT_TRADE_SUCCESSED);
                t = TradeParser.parse(response);
                ShareUtil.shareTradeToWX(t._id, ValueUtil.SHARE_TRADE, S11NewTradeActivity.this, true);
                EventBus.getDefault().post(new ShareTradeEvent(true));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                submit.setClickable(true);
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    //buyers
    //------------------------------------------------------------------------------------
    private void initBuyers(String itemRef) {
        QSRxApi.queryBuyers(itemRef)
                .subscribe(new QSSubscriber<List<MongoPeople>>() {
                    @Override
                    public void onNetError(int message) {
                        ErrorHandler.handle(S11NewTradeActivity.this, message);
                        buyers.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(List<MongoPeople> mongoPeoples) {
                        if (mongoPeoples != null && mongoPeoples.size() > 0) {
                            buyers.setLayoutManager(new LinearLayoutManager(S11NewTradeActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            buyers.setAdapter(new TopOwnerAdapter(mongoPeoples, S11NewTradeActivity.this, R.layout.item_portrait));
                            buyers.setVisibility(View.VISIBLE);
                            text.setVisibility(View.VISIBLE);
                            text.setText(mongoPeoples.size() + "位用户已通过分享获得优惠");
                        }
                    }
                });
    }


    //canvas
    //------------------------------------------------------------------------------------

    Handler skuPropHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            MongoItem item = (MongoItem)msg.obj;
            initDes(item);
            initProps(item);
            initSkuTable(item);
            super.handleMessage(msg);
        }
    };

    private void addCanvas(String itemRef) {
        final QSCanvasView canvas = new QSCanvasView(this);
        canvas.setOnCheckedChangeListener(new QSCanvasView.OnCheckedChangeListener() {
            @Override
            public void checkedChanged(QSImageView view) {
                Message message = skuPropHandler.obtainMessage();
                message.obj = view.getTag();
                skuPropHandler.sendMessage(message);
            }
        });
        QSRxApi.remixByItem(itemRef)
                .subscribe(new QSSubscriber<RemixByItem>() {
                    @Override
                    public void onNetError(int message) {
                        ErrorHandler.handle(S11NewTradeActivity.this, message);
                    }

                    @Override
                    public void onNext(RemixByItem remixByItem) {
                        final Point canvasPoint = new Point();
                        canvasPoint.x = canvasPager.getWidth();
                        canvasPoint.y = canvasPager.getHeight();
                        RectF rect = remixByItem.master.rect.getRect(canvasPoint);
                        addItemToCanvas(canvas, itemEntity, rect);

                        for (RemixByItem.Slave slave : remixByItem.slaves) {
                            addItemToCanvas(canvas, slave.itemRef, slave.rect.getRect(canvasPoint));
                        }
                        canvasList.add(canvas);
                        adapter.notifyDataSetChanged();
                        canvasPager.setCurrentItem(canvasList.size(), true);
                    }
                });

    }

    private void addItemToCanvas(QSCanvasView canvas, MongoItem item, final RectF rectF) {
        final QSImageView imageView = new QSImageView(this);
        imageView.setTag(item);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(layoutParams);
        imageView.setRemoveEnable(false);
        canvas.attach(imageView);
        ImageLoader.getInstance().displayImage(item.thumbnail, imageView.getImageView(), AppUtil.getShowDisplayOptions(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                PointF point = RectUtil.getImageViewDrawablePoint(imageView.getImageView());
                RectUtil.locateView(rectF, imageView, point.x, point.y);
                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0, 1.0f);
                animator.setDuration(500);
                animator.start();
            }
        });
    }


    //sku prop
    //------------------------------------------------------------------------------------

    private float radioBtnWdith = 35;
    private float radioBtnHeight = 28;

    public interface OnCheckedChangeListener {
        void onChanged(String key, int index);
    }


    private FlowRadioButton initPropItem(String text) {
        FlowRadioButton propItem = new FlowRadioButton(this);
        propItem.setMinWidth((int) AppUtil.transformToDip(radioBtnWdith, this));
        propItem.setMinHeight((int) AppUtil.transformToDip(radioBtnHeight, this));
        propItem.setBackgroundResource(R.drawable.gay_btn_ring);
        propItem.setTextColor(this.getResources().getColor(R.color.gary));
        propItem.setGravity(Gravity.CENTER);
        propItem.setPadding(25, 10, 25, 10);
        propItem.setTextSize(13);
        if (!TextUtils.isEmpty(text)) {
            propItem.setText(text);
        }
        return propItem;
    }

    private Map<String, FlowRadioGroup> btnMap = new HashMap<>();

    private void bindItem(final String key, final List<String> values, final int position, final OnCheckedChangeListener onCheckedChangeListener) {

        LinearLayout prop = (LinearLayout) this.getLayoutInflater().inflate(R.layout.item_sku_prop, null);
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
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void reconn() {

    }

}
