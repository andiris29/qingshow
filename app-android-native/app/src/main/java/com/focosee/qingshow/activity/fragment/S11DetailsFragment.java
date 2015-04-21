package com.focosee.qingshow.activity.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S11NewTradeActivity;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoOrder;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.sku.Prop;
import com.focosee.qingshow.util.sku.SkuColor;
import com.focosee.qingshow.util.sku.SkuUtil;
import com.focosee.qingshow.widget.drawable.RoundBitmapDrawable;
import com.focosee.qingshow.widget.flow.FlowRadioButton;
import com.focosee.qingshow.widget.flow.FlowRadioGroup;
import com.focosee.qingshow.widget.flow.FlowRadioImgeView;
import com.focosee.qingshow.widget.flow.IRadioViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import de.greenrobot.event.EventBus;


/**
 * Created by Administrator on 2015/3/11.
 */
public class S11DetailsFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private Button addButton;
    private Button cutButton;
    private TextView numView;
    private FlowRadioGroup sizeGroup;
    private FlowRadioGroup itemGroup;
    private ImageView reference;
    private TextView showReference;
    private TextView name;

    private MongoItem itemEntity;
    private LinkedList<MongoItem.TaoBaoInfo.SKU> skus;
    private MongoOrder order;

    private int num = 1;

    private HashMap<ArrayList<Prop>, MongoItem.TaoBaoInfo.SKU> skusProp;
    private ArrayList<Prop> myPropList;


    private HashSet<Prop> sizes = new HashSet<Prop>();
    private HashSet<SkuColor> colors = new HashSet<SkuColor>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_s11_details, container, false);
        itemEntity = (MongoItem) getActivity().getIntent().getExtras().getSerializable(S11NewTradeActivity.INPUT_ITEM_ENTITY);
        skus = itemEntity.taobaoInfo.skus;
        myPropList = new ArrayList<Prop>();

        initView();
        filter();
        initSize();
        initItem();

        onSecletChanged();

        return rootView;
    }


    private void filter() {
        if (null == skus) {
            return;
        }
        skusProp = SkuUtil.filter(skus);


        for (ArrayList<Prop> props : skusProp.keySet()) {
            for (Prop prop : props) {

                if (prop.getPropId().equals(SkuUtil.KEY.COLOR.id)) {
                    SkuColor skuColor = new SkuColor(prop);
                    skuColor.setUrl(skusProp.get(props).properties_thumbnail != null ?
                            skusProp.get(props).properties_thumbnail : "");
                    colors.add(skuColor);
                }

                for (SkuUtil.KEY sizeKey : SkuUtil.KEY.values()) {
                    if (sizeKey.name.equals("size")) {
                        if (prop.getPropId().equals(sizeKey.id)) {
                            sizes.add(prop);
                        }
                    }
                }
            }
        }
    }

    private boolean onSecletChanged() {

        if (skusProp.containsKey(myPropList)) {

            MongoItem.TaoBaoInfo.SKU sku = skusProp.get(myPropList);

            order = new MongoOrder();
            order.quantity = num;
            order.itemSnapshot = itemEntity;
//            order.price = Double.parseDouble(sku.promo_price);
            order.price = 0.01;
            order.selectedItemSkuId = sku.sku_id;

            ((TextView) rootView.findViewById(R.id.s11_details_price)).setText(StringUtil.FormatPrice(sku.promo_price));
            ((TextView) rootView.findViewById(R.id.s11_details_maxprice)).setText("原价:" + StringUtil.FormatPrice(sku.price));
            EventBus.getDefault().post(new S11DetailsEvent(order, true));
            return true;
        } else {
            ((TextView) rootView.findViewById(R.id.s11_details_price)).setText("");
            ((TextView) rootView.findViewById(R.id.s11_details_maxprice)).setText("");
            EventBus.getDefault().post(new S11DetailsEvent(null, false));
            return false;
        }
    }

    public MongoOrder getOrder() {
        return order;
    }

    private void initView() {

        name = (TextView) rootView.findViewById(R.id.s11_details_name);
        addButton = (Button) rootView.findViewById(R.id.S11_add_num);
        cutButton = (Button) rootView.findViewById(R.id.S11_cut_num);
        numView = (TextView) rootView.findViewById(R.id.S11_num);
        sizeGroup = (FlowRadioGroup) rootView.findViewById(R.id.s11_size_group);
        itemGroup = (FlowRadioGroup) rootView.findViewById(R.id.s11_item_group);
        reference = (ImageView) rootView.findViewById(R.id.s11_reference);
        showReference = (TextView) rootView.findViewById(R.id.s11_show_reference);
        ((TextView) rootView.findViewById(R.id.s11_details_maxprice)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        addButton.setOnClickListener(this);
        cutButton.setOnClickListener(this);
        numView.setText(num + "");

        if (!TextUtils.isEmpty(itemEntity.name)) {
            name.setText(itemEntity.name);
        }

        if (!TextUtils.isEmpty(itemEntity.taobaoInfo.getMaxPrice())) {
            ((TextView) rootView.findViewById(R.id.s11_details_maxprice)).setText(itemEntity.taobaoInfo.getMaxPrice());
        }

        if (TextUtils.isEmpty(itemEntity.sizeExplanation)) {
            showReference.setVisibility(View.GONE);
        } else {
            ImageLoader.getInstance().displayImage(itemEntity.sizeExplanation, reference);
        }

        showReference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reference.getVisibility() == View.VISIBLE) {
                    showReference.setTextColor(Color.BLACK);
                    reference.setVisibility(View.GONE);
                } else {
                    showReference.setTextColor(getResources().getColor(R.color.hint_text_color));
                    reference.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    private void initSize() {
        if (null == skus) {
            return;
        }
        if (sizes.isEmpty()) {
            rootView.findViewById(R.id.s11_details_size_line).setVisibility(View.GONE);
            rootView.findViewById(R.id.s11_details_size).setVisibility(View.GONE);
            return;
        }
        int i = 0;

        final ArrayList<Prop> sizeList = new ArrayList<Prop>();
        ViewGroup.MarginLayoutParams itemParams = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT);
        itemParams.setMargins(10, 10, 10, 10);
        for (Prop size : sizes) {

            FlowRadioButton sizeItem = new FlowRadioButton(getActivity());
            sizeItem.setBackgroundResource(R.drawable.s11_size_item_bg);
            sizeItem.setTextColor(getResources().getColor(R.color.black));
            sizeItem.setGravity(Gravity.CENTER);
            sizeItem.setTextSize(13);

            if (!TextUtils.isEmpty(size.getName())) {
                sizeItem.setText(size.getName());
            } else {
                sizeItem.setText(size.getPropValue() != null ?
                        size.getPropValue() : "");
            }
            sizeGroup.addView(sizeItem, itemParams);
            sizeList.add(size);
            i++;

            if (i == 1) {
                sizeItem.setChecked(true);
                myPropList.add(size);
                onSecletChanged();
            }
        }

        if (0 == sizeGroup.getChildCount()) {
            rootView.findViewById(R.id.s11_details_size_line).setVisibility(View.GONE);
            rootView.findViewById(R.id.s11_details_size).setVisibility(View.GONE);
            return;
        }

        sizeGroup.setOnCheckedChangeListener(new FlowRadioGroup.OnCheckedChangeListener() {
            int selectNum = 0;

            @Override
            public void checkedChanged(int i) {
                int index = myPropList.indexOf(sizeList.get(selectNum));
                myPropList.remove(index);
                myPropList.add(index, sizeList.get(i));
                onSecletChanged();
                selectNum = i;
            }
        });
    }

    private void initItem() {

        if (null == skus) {
            return;
        }
        if (colors.isEmpty()) {
            rootView.findViewById(R.id.s11_details_color_line).setVisibility(View.GONE);
            rootView.findViewById(R.id.s11_details_color).setVisibility(View.GONE);
            return;
        }

        int i = 0;

        final ArrayList<Prop> colorList = new ArrayList<Prop>();

        for (SkuColor color : colors) {
            int imgWidth = (int) AppUtil.transformToDip(35,getActivity());
            int imgHeight = (int) AppUtil.transformToDip(35,getActivity());
            ViewGroup.MarginLayoutParams itemParams = new ViewGroup.MarginLayoutParams(imgWidth, imgHeight);
            itemParams.setMargins(10, 10, 10, 10);
            final FlowRadioImgeView colorItem = new FlowRadioImgeView(getActivity());

            if (!TextUtils.isEmpty(color.getUrl())) {
                ImageLoader.getInstance().loadImage(color.getUrl(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        super.onLoadingStarted(imageUri, view);
                        colorItem.setBackgroundResource(0);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        colorItem.setBackgroundDrawable(new RoundBitmapDrawable(loadedImage, AppUtil.transformToDip(5,getActivity()), AppUtil.transformToDip(5,getActivity())));
                    }
                });

                itemGroup.addView(colorItem, itemParams);
                colorList.add(color.prop);
                i++;
            } else {
                ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                        ViewGroup.MarginLayoutParams.WRAP_CONTENT);
                params.setMargins(10, 10, 10, 10);
                FlowRadioButton item = new FlowRadioButton(getActivity());
                item.setBackgroundResource(R.drawable.s11_size_item_bg);
                item.setTextColor(getResources().getColor(R.color.black));
                item.setTextSize(13);
                item.setGravity(Gravity.CENTER);
                if (!TextUtils.isEmpty(color.prop.getName())) {
                    item.setText(color.prop.getName());
                } else {
                    item.setText(color.prop.getPropValue() != null ?
                            color.prop.getPropValue() : "");
                }
                itemGroup.addView(item, params);
                colorList.add(color.prop);
                i++;
            }

            if (i == 1) {
                ((IRadioViewHelper) itemGroup.getChildAt(0)).setChecked(true);
                myPropList.add(color.prop);
                onSecletChanged();
            }
        }

        itemGroup.setOnCheckedChangeListener(new FlowRadioGroup.OnCheckedChangeListener() {
            int selectNum = 0;

            @Override
            public void checkedChanged(int i) {
                int index = myPropList.indexOf(colorList.get(selectNum));
                myPropList.remove(index);
                myPropList.add(index, colorList.get(i));
                onSecletChanged();
                selectNum = i;
            }
        });

        if (0 == itemGroup.getChildCount()) {
            rootView.findViewById(R.id.s11_details_color_line).setVisibility(View.GONE);
            rootView.findViewById(R.id.s11_details_color).setVisibility(View.GONE);
            return;
        }
    }


    @Override
    public void onClick(View v) {
        if (v == addButton) {
            if (num == 9) return;
            num++;
            numView.setText(num + "");
        }
        if (v == cutButton) {
            if (1 == num) {
                return;
            }
            num--;
            numView.setText(num + "");
        }

        onSecletChanged();
    }

}
