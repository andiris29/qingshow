package com.focosee.qingshow.activity.fragment;

import android.graphics.Bitmap;
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
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S10ItemDetailActivity;
import com.focosee.qingshow.adapter.SkuPropsAdpater;
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
import java.util.List;
import java.util.Map;

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
    @InjectView(R.id.submitBtn)
    Button submitBtn;
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

    private int num = 1;
    private int numOffline = 1;
    private int numOnline = 9;

    private int discountNum = 10;
    private int discountOffline = 1;
    private int discountOnline = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.activity_s11_trade, container, false);
        ButterKnife.inject(this, rootView);

        itemEntity = (MongoItem) getActivity().getIntent().getExtras().getSerializable(S10ItemDetailActivity.INPUT_ITEM_ENTITY);
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

        initProps();
        initDes();

        checkDiscount();
        checkNum();

        return rootView;
    }

    private void initProps() {
        adpater = new SkuPropsAdpater(itemEntity.skuProperties, getActivity(), R.layout.item_sku_prop);
        adpater.notifyDataSetChanged();
        recyclerView.setAdapter(adpater);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
    }


    private void initDes() {
        desImg.setImageURI(Uri.parse(itemEntity.thumbnail));
        itemName.setText(itemEntity.name);
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
