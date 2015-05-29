package com.focosee.qingshow.activity.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S11NewTradeActivity;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoOrder;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.sku.Prop;
import com.focosee.qingshow.util.sku.SkuColor;
import com.focosee.qingshow.util.sku.SkuUtil;
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

    private FrameLayout sizeLayout;
    private LinearLayout cate4;
    private RelativeLayout cate023;
    private EditText shoe;
    private EditText numOne;
    private EditText numTow;
    private ImageView sizeImg;

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
            EventBus.getDefault().post(new S11DetailsEvent(order, true, getNums(itemEntity.category)));
            return true;
        } else {
            ((TextView) rootView.findViewById(R.id.s11_details_price)).setText("");
            ((TextView) rootView.findViewById(R.id.s11_details_maxprice)).setText("");
            EventBus.getDefault().post(new S11DetailsEvent(null, false, getNums(itemEntity.category)));
            return false;
        }
    }

    public MongoOrder getOrder() {
        return order;
    }

    private void initView() {
        sizeLayout = (FrameLayout) rootView.findViewById(R.id.s11_details_size);
        cate4 = (LinearLayout) rootView.findViewById(R.id.cate_4);
        cate023 = (RelativeLayout) rootView.findViewById(R.id.cate_023);
        shoe = (EditText) rootView.findViewById(R.id.shoe);
        numOne = (EditText) rootView.findViewById(R.id.num_one);
        numTow = (EditText) rootView.findViewById(R.id.num_tow);
        sizeImg = (ImageView) rootView.findViewById(R.id.size_img);

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
        sizeLayout.setVisibility(View.VISIBLE);
        switch (itemEntity.category) {
            case 0:
            case 2:
            case 3:
                cate023.setVisibility(View.VISIBLE);
                cate4.setVisibility(View.GONE);
                numOne.setHint("胸围：70cm");
                numTow.setHint("肩宽：30cm");
                sizeImg.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.category_shangyi));
                break;
            case 1:
                cate023.setVisibility(View.VISIBLE);
                cate4.setVisibility(View.GONE);
                numOne.setHint("腰围：70cm");
                numTow.setHint("臀围：30cm");
                sizeImg.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.category_pants));
                break;
            case 4:
                cate023.setVisibility(View.GONE);
                cate4.setVisibility(View.VISIBLE);
                break;
            case 5:
            case 6:
                sizeLayout.setVisibility(View.GONE);
                break;
        }

        EditText.OnEditorActionListener listener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onSecletChanged();
                    return true;
                }
                return false;
            }
        };
        numOne.setOnEditorActionListener(listener);
        numTow.setOnEditorActionListener(listener);
        shoe.setOnEditorActionListener(listener);
    }

    private MongoPeople.MeasureInfo getNums(int category) {
        MongoPeople.MeasureInfo measureInfo = new MongoPeople().new MeasureInfo();
        switch (category) {
            case 0:
            case 2:
            case 3:
                measureInfo.shoulder = Integer.parseInt(numOne.getText().toString());
                measureInfo.bust = Integer.parseInt(numTow.getText().toString());
                break;
            case 1:
                measureInfo.waist = Integer.parseInt(numOne.getText().toString());
                measureInfo.hips = Integer.parseInt(numTow.getText().toString());
                break;
            case 4:
                shoe.getText().toString();
                break;
        }
        return measureInfo;
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
            int imgWidth = (int) AppUtil.transformToDip(35, getActivity());
            int imgHeight = (int) AppUtil.transformToDip(35, getActivity());
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
                        colorItem.setBackgroundDrawable(new RoundBitmapDrawable(loadedImage, AppUtil.transformToDip(5, getActivity()), AppUtil.transformToDip(5, getActivity())));
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
