package com.focosee.qingshow.activity.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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

import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S17PayActivity;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.CategoryParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoCategories;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoOrder;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.sku.Prop;
import com.focosee.qingshow.util.sku.SkuColor;
import com.focosee.qingshow.util.sku.SkuUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by Administrator on 2015/3/11.
 */
public class S17DetailsFragment extends Fragment {

    @InjectView(R.id.s11_details_name)
    TextView s11DetailsName;
    @InjectView(R.id.s11_details_price)
    TextView s11DetailsPrice;
    @InjectView(R.id.s11_details_maxprice)
    TextView s11DetailsMaxprice;
    @InjectView(R.id.props)
    RecyclerView recyclerView;
    @InjectView(R.id.S11_num)
    TextView S11Num;
    private View rootView;


    private MongoItem itemEntity;
    private MongoOrder order;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_s17_details, container, false);
        ButterKnife.inject(this, rootView);
        itemEntity = (MongoItem) getActivity().getIntent().getExtras().getSerializable(S17PayActivity.INPUT_ITEM_ENTITY);

        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public MongoOrder getOrder() {
        return order;
    }
}
