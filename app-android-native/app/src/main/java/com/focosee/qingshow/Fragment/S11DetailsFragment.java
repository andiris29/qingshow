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
import com.focosee.qingshow.widget.FlowRadioGroup;
import com.focosee.qingshow.widget.ImageRadio;
import com.focosee.qingshow.widget.ImageRadioGroup;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.LinkedList;


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

//    private String size[] = new String[]{"S", "M", "L", "XL"};
//    private int item[] = new int[]{R.drawable.s11_item_01, R.drawable.s11_item_02};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_s11_details, container, false);
        itemEntity = (MongoItem) getActivity().getIntent().getExtras().getSerializable(S11NewTradeActivity.INPUT_ITEM_ENTITY);
        skus = itemEntity.taobaoInfo.skus;
        initView();
        initSize();
        initItem();

        return rootView;
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

        if(!TextUtils.isEmpty(itemEntity.name)){
            name.setText(itemEntity.name);
        }

        if(!TextUtils.isEmpty(itemEntity.taobaoInfo.getMaxPrice())){
            ((TextView)rootView.findViewById(R.id.s11_details_maxprice)).setText(itemEntity.taobaoInfo.getMaxPrice());
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
        if(skus == null){
            return;
        }
        sizeGroup.setRealPadding(20);
        for (MongoItem.TaoBaoInfo.SKU sku : skus) {
            RadioGroup.LayoutParams itemParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);
            RadioButton sizeItem = new RadioButton(getActivity());
            sizeItem.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            sizeItem.setBackgroundResource(R.drawable.trade_size_bg);
            sizeItem.setTextColor(getResources().getColor(R.color.black));
            sizeItem.setGravity(Gravity.CENTER);
            sizeItem.setPadding(30,30,30,30);
            if (!TextUtils.isEmpty(sku.properties_name)) {
                sizeItem.setText(sku.properties_name.split(";")[0]);
                sizeGroup.addView(sizeItem,itemParams);
            }
        }

        sizeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    ((RadioButton) group.getChildAt(i)).setTextColor(getResources().getColor(R.color.darker_gray));
                }
                ((RadioButton) group.findViewById(checkedId)).setTextColor(getResources().getColor(R.color.white));
            }
        });
    }

    private void initItem() {
        if(skus == null){
            return;
        }
        itemGroup.setRealPadding(20);
        for (MongoItem.TaoBaoInfo.SKU sku : skus) {
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(200,200);
            final ImageRadio colorItem = new ImageRadio(getActivity());
            colorItem.setLayoutParams(itemParams);
            if(!TextUtils.isEmpty(sku.properties_thumbnail)){
                ImageLoader.getInstance().loadImage(sku.properties_thumbnail,new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        colorItem.setBackgroundDrawable(new BitmapDrawable(null,loadedImage));
                    }
                });
                itemGroup.addView(colorItem,itemParams);
            }
        }
//        LinearLayoutManager  manager = new LinearLayoutManager(getActivity());
//        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        itemGroup.setLayoutManager(manager);
//        itemGroup.setAdapter(new S11ItemImgAdapter(itemEntity.taobaoInfo.skus));
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
    }
}
