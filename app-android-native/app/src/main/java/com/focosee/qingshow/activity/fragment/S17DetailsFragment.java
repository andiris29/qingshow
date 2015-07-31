package com.focosee.qingshow.activity.fragment;

import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S17PayActivity;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoOrder;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.sku.SkuUtil;

import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by Administrator on 2015/3/11.
 */
public class S17DetailsFragment extends Fragment {

    @InjectView(R.id.s11_details_name)
    TextView itemName;
    @InjectView(R.id.s11_details_price)
    TextView price;
    @InjectView(R.id.s11_details_maxprice)
    TextView maxPrice;
    @InjectView(R.id.S11_num)
    TextView num;
    @InjectView(R.id.props)
    LinearLayout group;

    private View rootView;

    private MongoTrade trade;
    private MongoOrder order;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_s17_details, container, false);
        ButterKnife.inject(this, rootView);
        trade = (MongoTrade) getActivity().getIntent().getExtras().getSerializable(S17PayActivity.INPUT_ITEM_ENTITY);
        order = trade.orders.get(0);
        initDes();
        initProp();

        return rootView;
    }

    private void initProp() {
        Map<String, List<String>> map = SkuUtil.filter(order.selectedSkuProperties);
        for (String key : map.keySet()) {
            List<String> list = map.get(key);
            addProp(group,key,list.get(0));
        }
    }

    private void addProp(ViewGroup parent,String title ,String text){
        LinearLayout itemLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.item_s17_prop,null,false);
        TextView name = (TextView) itemLayout.findViewById(R.id.propName);
        TextView value = (TextView) itemLayout.findViewById(R.id.propValue);
        name.setText(title);
        value.setText(text);
        parent.addView(itemLayout);
    }

    private void initDes() {
        itemName.setText(order.itemSnapshot.name);
        price.setText(StringUtil.FormatPrice(order.itemSnapshot.promoPrice));
        maxPrice.setText(StringUtil.FormatPrice(order.itemSnapshot.price));
        maxPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        num.setText(order.quantity);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
