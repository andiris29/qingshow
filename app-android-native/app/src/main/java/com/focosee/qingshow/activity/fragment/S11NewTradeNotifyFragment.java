package com.focosee.qingshow.activity.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Response;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S01MatchShowsActivity;
import com.focosee.qingshow.activity.S17PayActivity;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.TradeShareCommand;
import com.focosee.qingshow.command.TradeStatusToCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.TradeParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.ShareUtil;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.ToastUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.util.user.UnreadHelper;
import com.focosee.qingshow.widget.QSTextView;
import com.focosee.qingshow.wxapi.ShareTradeEvent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.LinkedList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/3/11.
 */
public class S11NewTradeNotifyFragment extends Fragment {

    @InjectView(R.id.img)
    SimpleDraweeView img;
    @InjectView(R.id.item_name)
    QSTextView itemName;
    @InjectView(R.id.price)
    QSTextView price;
    @InjectView(R.id.promoPrice)
    QSTextView promoPrice;
    @InjectView(R.id.selectProp)
    QSTextView selectProp;
    @InjectView(R.id.num)
    QSTextView num;
    @InjectView(R.id.nowDiscount)
    TextView nowDiscount;
    @InjectView(R.id.nowPrice)
    TextView nowPrice;
    @InjectView(R.id.hint)
    TextView hint;
    @InjectView(R.id.submitBtn)
    ImageButton submitBtn;
    @InjectView(R.id.selectPropValue)
    QSTextView selectPropValue;

    private MongoTrade trade;
    String _id;
    String actualPrice;

    private View rootView;
    private boolean mDismissed = true;
    private ViewGroup mGroup;

    public void show(FragmentManager manager, String tag) {
        if (!mDismissed) {
            return;
        }
        mDismissed = false;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.activity_s11_trade_notify, container, false);
        ButterKnife.inject(this, rootView);
        EventBus.getDefault().register(this);
        _id = getActivity().getIntent().getStringExtra(S01MatchShowsActivity.S1_INPUT_TRADEID_NOTIFICATION);
        getDataFromNet(_id);
        UnreadHelper.userReadNotificationId(_id);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mGroup = (ViewGroup) getActivity().getWindow().getDecorView();
        mGroup.addView(rootView);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initProps() {
        selectPropValue.setText(StringUtil.formatSKUProperties(trade.selectedSkuProperties));
    }


    private void initDes() {

        actualPrice = String.valueOf(trade.itemRef.expectable.price);

        img.setImageURI(Uri.parse(trade.itemSnapshot.thumbnail));
        itemName.setText(trade.itemSnapshot.name);

        promoPrice.setText(StringUtil.FormatPrice(trade.itemSnapshot.promoPrice));
        num.setText(String.valueOf(trade.quantity));

        String priceStr = "原价：" + StringUtil.FormatPrice(trade.itemSnapshot.price);
        SpannableString spannableString = new SpannableString(priceStr);
        spannableString.setSpan(new StrikethroughSpan(), 0, priceStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.price.setText(spannableString);

        spannableString = new SpannableString(StringUtil.FormatPrice(trade.itemRef.expectable.price));
        spannableString.setSpan(new RelativeSizeSpan(0.5f), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new UnderlineSpan(), 1, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        nowPrice.setText(spannableString);

        nowDiscount.setText(StringUtil.formatDiscount(trade.itemRef.expectable.price, trade.itemSnapshot.promoPrice));

        if (!TextUtils.isEmpty(trade.itemRef.expectable.messageForBuy)) {
            spannableString = new SpannableString("● " + trade.itemRef.expectable.messageForBuy);
            hint.setText(spannableString);
        }
    }

    @OnClick(R.id.close)
    public void close() {
        if (mDismissed) {
            return;
        }
        mDismissed = true;
        getFragmentManager().popBackStack();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.remove(this);
        ft.commit();
    }

    public void onEventMainThread(ShareTradeEvent event) {

        submitBtn.setEnabled(true);
        if (event.shareByCreateUser) {
            TradeShareCommand.share(trade._id, new Callback() {
                @Override
                public void onComplete() {
                    Intent intent = new Intent(getActivity(), S17PayActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(S17PayActivity.INPUT_ITEM_ENTITY, trade);
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                }
            });
        }
    }


    @OnClick(R.id.submitBtn)
    public void submit() {
        submitBtn.setEnabled(false);
        if (QSApplication.instance().getWxApi().isWXAppInstalled()) {
            ShareUtil.shareTradeToWX(_id, ValueUtil.SHARE_TRADE, getActivity(), true);
            EventBus.getDefault().post(trade);
        } else {
            ToastUtil.showShortToast(getActivity(), getString(R.string.need_install_wx));
        }
    }

    public void getDataFromNet(String _id) {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getTradeApi(_id), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(S11NewTradeFragment.class.getSimpleName(), "response:" + response);
                submitBtn.setEnabled(true);
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(getActivity(), MetadataParser.getError(response));
                    return;
                }
                LinkedList<MongoTrade> trades = TradeParser.parseQuery(QSGsonFactory.cateGoryBuilder().create(), response);
                trade = trades.get(0);

                if (null != trade) {
                    initProps();
                    initDes();
                }
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        mGroup.removeView(rootView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("S11NewTradeNotifyFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("S11NewTradeNotifyFragment");
    }
}
