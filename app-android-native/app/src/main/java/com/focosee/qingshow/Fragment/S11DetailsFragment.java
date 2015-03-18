package com.focosee.qingshow.Fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S11NewTradeActivity;
import com.focosee.qingshow.adapter.S11ItemImgAdapter;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.util.sku.Prop;
import com.focosee.qingshow.util.sku.SkuColor;
import com.focosee.qingshow.util.sku.SkuUtil;
import com.focosee.qingshow.widget.FlowRadioGroup;
import com.focosee.qingshow.widget.ImageRadio;
import com.focosee.qingshow.widget.ImageRadioGroup;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
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
    private ImageRadioGroup itemGroup;
    private ImageView reference;
    private TextView name;

    private MongoItem itemEntity;
    private LinkedList<MongoItem.TaoBaoInfo.SKU> skus;

    private int num = 1;
    private String price = "";
    private String skuId = "";

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
                    skuColor.setUrl(skusProp.get(props).properties_thumbnail);
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
            skuId = skusProp.get(myPropList).sku_id;
            price = skusProp.get(myPropList).promo_price;
            EventBus.getDefault().post(new S11DetailsEvent(num,price,skuId,true));
            Log.i("tag",skuId);
            return true;
        } else {
            EventBus.getDefault().post(new S11DetailsEvent(num,price,skuId,false));
            return false;
        }
    }


    private void initView() {

        name = (TextView) rootView.findViewById(R.id.s11_details_name);
        addButton = (Button) rootView.findViewById(R.id.S11_add_num);
        cutButton = (Button) rootView.findViewById(R.id.S11_cut_num);
        numView = (TextView) rootView.findViewById(R.id.S11_num);
        sizeGroup = (FlowRadioGroup) rootView.findViewById(R.id.s11_size_group);
        itemGroup = (ImageRadioGroup) rootView.findViewById(R.id.s11_item_group);
        reference = (ImageView) rootView.findViewById(R.id.s11_reference);

        addButton.setOnClickListener(this);
        cutButton.setOnClickListener(this);
        numView.setText(num + "");

        if (!TextUtils.isEmpty(itemEntity.name)) {
            name.setText(itemEntity.name);
        }

        if (!TextUtils.isEmpty(itemEntity.taobaoInfo.getMaxPrice())) {
            ((TextView) rootView.findViewById(R.id.s11_details_maxprice)).setText(itemEntity.taobaoInfo.getMaxPrice());
        }

        rootView.findViewById(R.id.s11_show_reference).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reference.getVisibility() == View.VISIBLE) {
                    reference.setVisibility(View.GONE);
                } else {
                    reference.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    private void initSize() {
        if (null == skus) {
            return;
        }

        sizeGroup.setRealPadding(20);
        int i = 0;

        final ArrayList<Prop> sizeList = new ArrayList<Prop>();

        for (Prop size : sizes) {
            RadioGroup.LayoutParams itemParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);
            RadioButton sizeItem = new RadioButton(getActivity());
            sizeItem.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            sizeItem.setBackgroundResource(R.drawable.trade_size_bg);
            sizeItem.setTextColor(getResources().getColor(R.color.black));
            sizeItem.setGravity(Gravity.CENTER);
            sizeItem.setPadding(30, 30, 30, 30);

            if (!TextUtils.isEmpty(size.getName())) {
                sizeItem.setText(size.getName());
                sizeGroup.addView(sizeItem, itemParams);
                sizeList.add(size);
                i++;
            }

            if (i == 1) {
                sizeGroup.check(sizeItem.getId());
                sizeItem.setTextColor(getResources().getColor(R.color.white));
                myPropList.add(size);
            }
        }

        sizeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            int selectNum = 0;
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    ((RadioButton) group.getChildAt(i)).setTextColor(getResources().getColor(R.color.darker_gray));
                    if(group.getChildAt(i).getId() ==  group.findViewById(checkedId).getId()){
                        int index = myPropList.indexOf(sizeList.get(selectNum));
                        myPropList.remove(index);
                        myPropList.add(index,sizeList.get(i));
                        onSecletChanged();
                        selectNum = i;
                    }
                }
                RadioButton radioButton = ((RadioButton) group.findViewById(checkedId));
                radioButton.setTextColor(getResources().getColor(R.color.white));
            }
        });
    }

    private void initItem() {

        if (null == skus) {
            return;
        }

        itemGroup.setRealPadding(20);
        int i = 0;

        final ArrayList<Prop> colorList = new ArrayList<Prop>();

        for (SkuColor color : colors) {
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(200, 200);
            final ImageRadio colorItem = new ImageRadio(getActivity());
            colorItem.setLayoutParams(itemParams);

            if (!TextUtils.isEmpty(color.getUrl())) {
                ImageLoader.getInstance().loadImage(color.getUrl(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        super.onLoadingStarted(imageUri, view);
                        colorItem.setBackgroundResource(R.drawable.root_cell_placehold_image1);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        colorItem.setBackgroundDrawable(new BitmapDrawable(null, loadedImage));
                    }
                });
                itemGroup.addView(colorItem, itemParams);
                colorList.add(color.prop);
                i++;
            }

            if(i == 1){
                colorItem.setImageResource(R.drawable.s11_item_chek);
                myPropList.add(color.prop);
            }

            itemGroup.setOnCheckedChangeListener(new ImageRadioGroup.OnCheckedChangeListener() {
                int selectNum = 0;
                @Override
                public void checkedChanged(View view,int i) {
                    int index = myPropList.indexOf(colorList.get(selectNum));
                    myPropList.remove(index);
                    myPropList.add(index,colorList.get(i));
                    onSecletChanged();
                    selectNum = i;
                }
            });
        }

        onSecletChanged();
    }


    @Override
    public void onClick(View v) {
        if (v == addButton) {
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
