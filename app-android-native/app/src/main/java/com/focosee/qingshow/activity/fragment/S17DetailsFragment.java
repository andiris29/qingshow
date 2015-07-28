package com.focosee.qingshow.activity.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class S17DetailsFragment extends Fragment implements View.OnClickListener {

    @InjectView(R.id.s11_details_name)
    TextView s11DetailsName;
    @InjectView(R.id.s11_details_price)
    TextView s11DetailsPrice;
    @InjectView(R.id.s11_details_maxprice)
    TextView s11DetailsMaxprice;
    @InjectView(R.id.s11_details_color)
    LinearLayout s11DetailsColor;
    @InjectView(R.id.s11_details_color_line)
    View s11DetailsColorLine;
    @InjectView(R.id.category_name)
    TextView categoryName;
    @InjectView(R.id.box)
    LinearLayout box;
    @InjectView(R.id.num_one_layout)
    LinearLayout numOneLayout;
    @InjectView(R.id.s11_details_size)
    FrameLayout s11DetailsSize;
    @InjectView(R.id.s11_details_size_line)
    View s11DetailsSizeLine;
    @InjectView(R.id.S11_cut_num)
    Button S11CutNum;
    @InjectView(R.id.S11_num)
    TextView S11Num;
    @InjectView(R.id.S11_add_num)
    Button S11AddNum;
    @InjectView(R.id.s11_details_color_text)
    TextView s11DetailsColorText;
    @InjectView(R.id.shoe)
    EditText shoe;
    @InjectView(R.id.cate_4)
    LinearLayout cate4;
    @InjectView(R.id.num_one)
    EditText numOne;
    @InjectView(R.id.num_tow)
    EditText numTow;
    @InjectView(R.id.size_img)
    ImageView sizeImg;
    @InjectView(R.id.cate_023)
    RelativeLayout cate023;
    private View rootView;
    private Button addButton;
    private Button cutButton;
    private TextView numView;
    private TextView name;

    private MongoItem itemEntity;
    private LinkedList<MongoItem.TaoBaoInfo.SKU> skus;
    private MongoItem.TaoBaoInfo.SKU sku;
    private MongoOrder order;

    private FrameLayout sizeLayout;

    private int num = 1;

    private ArrayList<Prop> props;
    private ArrayList<Prop> myPropList;
    private String skuId;

    private int measureComposition;


    private HashSet<SkuColor> colors = new HashSet<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_s17_details, container, false);
        ButterKnife.inject(this, rootView);
        itemEntity = (MongoItem) getActivity().getIntent().getExtras().getSerializable(S17PayActivity.INPUT_ITEM_ENTITY);
        skus = itemEntity.taobaoInfo.skus;
        System.out.println("repsonse_skus:" + skus);
        myPropList = new ArrayList<>();

        initView();
        filter();
        getDataFormMet(itemEntity.categoryRef.parentRef._id);

        onSecletChanged();

        return rootView;
    }

    private void getDataFormMet(final String id) {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getQueryCategories(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(getActivity(), MetadataParser.getError(response));
                }
                List<MongoCategories> categories = CategoryParser.parseQuery(response);
                for (MongoCategories category : categories) {
                    if (id.equals(category._id)) {
                        measureComposition = Integer.parseInt(category.measureComposition);
                    }
                }
                initSize();
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }


    private void filter() {
        if (null == skus) {
            return;
        }

        skuId = SkuUtil.getSkuId(itemEntity.source);

        if (skuId == null) {
            return;
        }

        for (MongoItem.TaoBaoInfo.SKU sku : skus) {
            if (sku.sku_id.equals(skuId)) {
                this.sku = sku;
            }
        }

        if (sku == null) {
            sku = skus.get(0);
        }

        props = SkuUtil.filter(sku);


        for (Prop prop : props) {

            if (prop.getPropId().equals(SkuUtil.KEY.COLOR.id)) {
                SkuColor skuColor = new SkuColor(prop);
                skuColor.setUrl(sku.properties_thumbnail != null ?
                        sku.properties_thumbnail : "");
                colors.add(skuColor);
            }
        }
    }

    private boolean onSecletChanged() {

        if (null != sku) {

            order = new MongoOrder();
            order.quantity = num;
            order.itemSnapshot = itemEntity;
            order.price = Double.parseDouble(sku.promo_price);
//            order.price = 0.01;
            order.selectedItemSkuId = sku.sku_id;

            ((TextView) rootView.findViewById(R.id.s11_details_price)).setText(StringUtil.FormatPrice(sku.promo_price));
            ((TextView) rootView.findViewById(R.id.s11_details_maxprice)).setText("原价:" + StringUtil.FormatPrice(sku.price));

            return true;
        } else {
            ((TextView) rootView.findViewById(R.id.s11_details_price)).setText("");
            ((TextView) rootView.findViewById(R.id.s11_details_maxprice)).setText("");
            return false;
        }
    }

    public MongoOrder getOrder() {
        return order;
    }

    private void initView() {
        sizeLayout = (FrameLayout) rootView.findViewById(R.id.s11_details_size);

        name = (TextView) rootView.findViewById(R.id.s11_details_name);
        addButton = (Button) rootView.findViewById(R.id.S11_add_num);
        cutButton = (Button) rootView.findViewById(R.id.S11_cut_num);
        numView = (TextView) rootView.findViewById(R.id.S11_num);
//        sizeGroup = (FlowRadioGroup) rootView.findViewById(R.id.s11_size_group);
//        reference = (ImageView) rootView.findViewById(R.id.s11_reference);
//        showReference = (TextView) rootView.findViewById(R.id.s11_show_reference);
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
    }

    private void initSize() {
        sizeLayout.setVisibility(View.VISIBLE);
        int measureComposition;
        if (itemEntity.categoryRef.measureComposition == null || itemEntity.categoryRef.measureComposition.isEmpty()) {
            measureComposition = this.measureComposition;
        } else {
            measureComposition = Integer.parseInt(itemEntity.categoryRef.measureComposition);
        }
        //TODO 显示颜色
        Log.i("tag", measureComposition + "");

        if (null != SkuUtil.getPropValue(skus, SkuUtil.KEY.COLOR.id))
            s11DetailsColorText.setText(SkuUtil.getPropValue(skus, SkuUtil.KEY.COLOR.id));
        switch (measureComposition) {
            case 0:
                cate023.setVisibility(View.GONE);
                break;
            case 2:
                sizeImg.setImageResource(R.drawable.category_pants);
                break;
            case 3:
                cate023.setVisibility(View.GONE);
                cate4.setVisibility(View.VISIBLE);
                break;
            case 1:
                sizeImg.setImageResource(R.drawable.category_shangyi);
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
            case 1:
                measureInfo.shoulder = Integer.parseInt(numOne.getText().toString());
                measureInfo.bust = Integer.parseInt(numTow.getText().toString());
                break;
            case 2:
                measureInfo.waist = Integer.parseInt(numOne.getText().toString());
                measureInfo.hips = Integer.parseInt(numTow.getText().toString());
                break;
            case 3:
                shoe.getText().toString();
                break;
        }
        return measureInfo;
    }


    @Override
    public void onClick(View v) {
        if (v == addButton) {
            if (num == 9) return;
            num++;
            numView.setText(String.valueOf(num));
        }
        if (v == cutButton) {
            if (1 == num) {
                return;
            }
            num--;
            numView.setText(String.valueOf(num));
        }

        onSecletChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
