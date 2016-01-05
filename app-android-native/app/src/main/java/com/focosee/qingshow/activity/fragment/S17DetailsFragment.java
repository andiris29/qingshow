package com.focosee.qingshow.activity.fragment;

import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.U14PayActivity;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.sku.SkuUtil;
import com.focosee.qingshow.widget.QSTextView;
import java.util.List;
import java.util.Map;
import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by Administrator on 2015/3/11.
 */
public class S17DetailsFragment extends Fragment {

    @InjectView(R.id.s11_details_name)
    QSTextView itemName;
    @InjectView(R.id.s11_details_price)
    QSTextView price;
    @InjectView(R.id.iv_s17_details_price)
    TextView ivPriceIcon;
    @InjectView(R.id.S11_num)
    QSTextView num;
    @InjectView(R.id.props)
    LinearLayout group;
    @InjectView(R.id.desImg)
    SimpleDraweeView desImg;

    private View rootView;

    private MongoTrade trade;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_s17_details, container, false);
        ButterKnife.inject(this, rootView);
        Log.d(S17DetailsFragment.class.getSimpleName(), "intent:" + getActivity().getIntent());
        trade = (MongoTrade) getActivity().getIntent().getSerializableExtra(U14PayActivity.INPUT_ITEM_ENTITY);
        Log.d(S17DetailsFragment.class.getSimpleName(), "trade:" + trade);
        initDes();
        initProp();

        return rootView;
    }

    private void initProp() {
        Map<String, List<String>> map = SkuUtil.filter(trade.selectedSkuProperties);
        for (String key : map.keySet()) {
            List<String> list = map.get(key);
            addProp(group,key,list.get(0));
        }
    }

    private void addProp(ViewGroup parent,String title ,String text){
        LinearLayout itemLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.item_s17_prop,null,false);
        QSTextView name = (QSTextView) itemLayout.findViewById(R.id.propName);
        QSTextView value = (QSTextView) itemLayout.findViewById(R.id.propValue);
        name.setText(title);
        value.setText(text);
        parent.addView(itemLayout);
    }

    private void initDes() {
        if(null != trade.itemSnapshot) {
            itemName.setText(trade.itemSnapshot.name);
            //减价的
            if(null != trade.itemSnapshot.expectable){
                price.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG); //中划线
                price.setText(StringUtil.FormatPrice(trade.itemSnapshot.promoPrice));
                price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
                ivPriceIcon.setVisibility(View.VISIBLE);
                ivPriceIcon.setText("减" + trade.itemSnapshot.expectable.reduction.intValue());
            } else {//不减价
                price.setText(StringUtil.FormatPrice(trade.itemSnapshot.promoPrice));
                price.getPaint().setFlags(0);  // 取消设置的的划线
                ivPriceIcon.setVisibility(View.GONE);
            }
            desImg.setImageURI(Uri.parse(trade.itemSnapshot.thumbnail));
        }
        num.setText(String.valueOf(trade.quantity));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
