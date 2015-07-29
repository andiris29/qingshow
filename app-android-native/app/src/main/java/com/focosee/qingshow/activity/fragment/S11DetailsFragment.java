package com.focosee.qingshow.activity.fragment;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S10ItemDetailActivity;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoOrder;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.sku.Prop;
import com.focosee.qingshow.util.sku.SkuColor;
import com.focosee.qingshow.util.sku.SkuUtil;
import com.focosee.qingshow.widget.QSTextView;
import com.focosee.qingshow.widget.drawable.RoundBitmapDrawable;
import com.focosee.qingshow.widget.flow.FlowRadioButton;
import com.focosee.qingshow.widget.flow.FlowRadioGroup;
import com.focosee.qingshow.widget.flow.FlowRadioImgeView;
import com.focosee.qingshow.widget.radio.IRadioViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by Administrator on 2015/3/11.
 */
public class S11DetailsFragment extends Fragment {

    @InjectView(R.id.itemName)
    TextView itemName;
    @InjectView(R.id.color_group)
    FlowRadioGroup colorGroup;
    @InjectView(R.id.size_group)
    FlowRadioGroup sizeGroup;
    @InjectView(R.id.desImg)
    SimpleDraweeView desImg;
    @InjectView(R.id.s11_details_price)
    QSTextView price;
    @InjectView(R.id.s11_details_maxprice)
    QSTextView maxPrice;

    private View rootView;

    private MongoItem itemEntity;
    private LinkedList<MongoItem.TaoBaoInfo.SKU> skus;
    private MongoOrder order;

    private HashMap<ArrayList<Prop>, MongoItem.TaoBaoInfo.SKU> skusProp;
    private ArrayList<Prop> myPropList;

    private HashSet<Prop> sizes = new HashSet<>();
    private HashSet<SkuColor> colors = new HashSet<>();

    private float radioBtnWdith = 40;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.activity_s11_trade, container, false);
        ButterKnife.inject(this, rootView);

        itemEntity = (MongoItem) getActivity().getIntent().getExtras().getSerializable(S10ItemDetailActivity.INPUT_ITEM_ENTITY);
        Log.d("s11", itemEntity._id);
        skus = itemEntity.taobaoInfo.skus;
        myPropList = new ArrayList<>();

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        filter();
        initSize();
        initItem();
        initDes();

        return rootView;
    }


    private void initDes() {
        desImg.setImageURI(Uri.parse(itemEntity.thumbnail));
        itemName.setText(itemEntity.name);
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

    private void initSize() {
        if (null == skus) {
            return;
        }
        if (sizes.isEmpty()) {
//            rootView.findViewById(R.id.s11_details_size_line).setVisibility(View.GONE);
//            rootView.findViewById(R.id.s11_details_size).setVisibility(View.GONE);
            return;
        }
        int i = 0;

        final ArrayList<Prop> sizeList = new ArrayList<>();
        ViewGroup.MarginLayoutParams itemParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT);
        itemParams.setMargins(10, 10, 10, 10);
        for (Prop size : sizes) {

            FlowRadioButton sizeItem = new FlowRadioButton(getActivity());
            sizeItem.setMinWidth((int) AppUtil.transformToDip(radioBtnWdith, getActivity()));
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
//            rootView.findViewById(R.id.s11_details_size_line).setVisibility(View.GONE);
//            rootView.findViewById(R.id.s11_details_size).setVisibility(View.GONE);
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
//            rootView.findViewById(R.id.s11_details_color_line).setVisibility(View.GONE);
//            rootView.findViewById(R.id.s11_details_color).setVisibility(View.GONE);
            return;
        }

        int i = 0;

        final ArrayList<Prop> colorList = new ArrayList<>();

        for (SkuColor color : colors) {
            int imgWidth = (int) AppUtil.transformToDip(35, getActivity());
            int imgHeight = (int) AppUtil.transformToDip(35, getActivity());
            ViewGroup.MarginLayoutParams itemParams = new ViewGroup.MarginLayoutParams(imgWidth, imgHeight);
            itemParams.setMargins(10, 10, 10, 10);
            final FlowRadioImgeView colorItem = new FlowRadioImgeView(getActivity());

            if (!TextUtils.isEmpty(color.getUrl())) {
                ImageLoader.getInstance().loadImage("http:" + color.getUrl(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        super.onLoadingStarted(imageUri, view);
                        colorItem.setBackgroundResource(0);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        colorItem.setBackgroundDrawable(new RoundBitmapDrawable(loadedImage, AppUtil.transformToDip(5, getActivity()), AppUtil.transformToDip(5, getActivity())));
                    }
                });

                colorGroup.addView(colorItem, itemParams);
                colorList.add(color.prop);
                i++;
            } else {
                ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.MarginLayoutParams.WRAP_CONTENT);
                params.setMargins(10, 10, 10, 10);
                FlowRadioButton item = new FlowRadioButton(getActivity());
                item.setBackgroundResource(R.drawable.s11_size_item_bg);
                item.setMinWidth((int) AppUtil.transformToDip(radioBtnWdith, getActivity()));
                item.setTextColor(getResources().getColor(R.color.black));
                item.setTextSize(13);
                item.setGravity(Gravity.CENTER);
                if (!TextUtils.isEmpty(color.prop.getName())) {
                    item.setText(color.prop.getName());
                } else {
                    item.setText(color.prop.getPropValue() != null ?
                            color.prop.getPropValue() : "");
                }
                colorGroup.addView(item, params);
                colorList.add(color.prop);
                i++;
            }

            if (i == 1) {
                ((IRadioViewHelper) colorGroup.getChildAt(0)).setChecked(true);
                myPropList.add(color.prop);
                onSecletChanged();
            }
        }

        colorGroup.setOnCheckedChangeListener(new FlowRadioGroup.OnCheckedChangeListener() {
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

        if (0 == colorGroup.getChildCount()) {
//            rootView.findViewById(R.id.s11_details_color_line).setVisibility(View.GONE);
//            rootView.findViewById(R.id.s11_details_color).setVisibility(View.GONE);
            return;
        }
    }

    private boolean onSecletChanged() {

        if (skusProp.containsKey(myPropList)) {

            MongoItem.TaoBaoInfo.SKU sku = skusProp.get(myPropList);

            order = new MongoOrder();

            order.itemSnapshot = itemEntity;
//            order.price = Double.parseDouble(sku.promo_price);
            order.price = 0.01;
            order.selectedItemSkuId = sku.sku_id;

            price.setText(StringUtil.FormatPrice(sku.promo_price));
            maxPrice.setText(getResources().getString(R.string.s11_price) + StringUtil.FormatPrice(sku.price));
            maxPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            return true;
        } else {
            price.setText("");
            maxPrice.setText("");
            return false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
