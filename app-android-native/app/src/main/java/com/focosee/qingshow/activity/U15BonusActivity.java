package com.focosee.qingshow.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.facebook.stetho.common.LogUtil;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.BonusCommand;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.constants.config.QSPushAPI;
import com.focosee.qingshow.constants.config.ShareConfig;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.request.RxRequest;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.aggregation.BonusAmount;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.DialogUtils;
import com.focosee.qingshow.util.ShareUtil;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.ToastUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.util.bonus.BonusHelper;
import com.focosee.qingshow.util.user.UnreadHelper;
import com.focosee.qingshow.widget.ConfirmDialog;
import com.focosee.qingshow.widget.LoadingDialogs;
import com.focosee.qingshow.widget.QSButton;
import com.focosee.qingshow.widget.QSEditText;
import com.focosee.qingshow.widget.QSTextView;
import com.focosee.qingshow.wxapi.ShareBonusEvent;
import com.focosee.qingshow.wxapi.WXEntryActivity;
import com.focosee.qingshow.wxapi.WxLoginedEvent;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;
import rx.functions.Func1;

public class U15BonusActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.title)
    QSTextView title;
    @InjectView(R.id.right_btn)
    QSButton rightBtn;
    @InjectView(R.id.u15_total)
    QSTextView u15Total;
    @InjectView(R.id.u15_hint_text)
    QSTextView u15HintText;
    @InjectView(R.id.left_btn)
    ImageButton leftBtn;
    @InjectView(R.id.u15_withDrawBtn)
    QSButton withDrawBtn;
    @InjectView(R.id.u15_draw)
    QSTextView u15Draw;
    @InjectView(R.id.u15_qa)
    QSButton btnU15;

    private MongoPeople people;
    private boolean isCanWithDrwa = false;
    private LoadingDialogs dialogs;
    private IWXAPI wxApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u15_bonus);
        ButterKnife.inject(this);
        dialogs = new LoadingDialogs(U15BonusActivity.this);
        wxApi = QSApplication.instance().getWxApi();

        EventBus.getDefault().register(this);
    }

    public void matchUI() {
        title.setText(getText(R.string.bonus_activity_settings));
        leftBtn.setOnClickListener(this);
        btnU15.setOnClickListener(this);
        withDrawBtn.setOnClickListener(this);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText(getText(R.string.bonus_shouyi));
        rightBtn.setOnClickListener(this);
        rightBtn.setTextColor(getResources().getColor(android.R.color.black));
        String hint = getString(R.string.u15_hint);
        SpannableString ss = new SpannableString(hint);
        Drawable drawable = getResources().getDrawable(R.drawable.u15_notify);
        assert drawable != null;
        drawable.setBounds(0, 0, (int) AppUtil.transformToDip(30f, this), (int) AppUtil.transformToDip(30f, this));
        ss.setSpan(new ImageSpan(drawable), hint.length() - 3, hint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        u15HintText.setText(ss);
        setData();
    }
    float draw = 0;
    public void setData() {
        UserCommand.refresh(new Callback(){
            @Override
            public void onComplete() {
                BonusAmount amountByStatus = QSModel.INSTANCE.getUser().__context.bonusAmountByStatus;
                //0 和 1 代表未提现   故 可提现金额为 0+1   2代表已提现
                float total = 0;
                if (amountByStatus.bonuses.containsKey("0")){
                    total += amountByStatus.bonuses.get("0").floatValue();
                    draw += amountByStatus.bonuses.get("0").floatValue();
                }
                if (amountByStatus.bonuses.containsKey("1")){
                    draw =draw + amountByStatus.bonuses.get("1").floatValue();
                }
                if (amountByStatus.bonuses.containsKey("2")){
                    total += amountByStatus.bonuses.get("2").floatValue();
                }
                u15Total.setText(StringUtil.FormatPrice(total));
                u15Draw.setText(StringUtil.FormatPrice(draw));
                MongoPeople.WeiXinInfo weixin = QSModel.INSTANCE.getUser().userInfo.weixin;
                if (null != weixin && !TextUtils.isEmpty(weixin.openid)) {
                    withDrawBtn.setText("立即提现");
                    if (draw < 1 ){
                        withDrawBtn.setBackgroundResource(R.drawable.u15_gray_btn);
                    }else {
                        withDrawBtn.setBackgroundResource(R.drawable.u15_pink_btn);
                    }

                }else {
                    withDrawBtn.setText("登录微信后提现");
                }

            }
        });
 
    }

    @Override
    public void reconn() {
    }

    public void onEventMainThread(ShareBonusEvent event) {
        if (event.errorCode == SendAuth.Resp.ErrCode.ERR_OK) {
        }
    }

    public void onEventMainThread(String event) {
        if (ValueUtil.BONUES_COMING.equals(event)) {
            reconn();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                finish();
                break;
            case R.id.right_btn:
                startActivity(new Intent(U15BonusActivity.this, U16BonusListActivity.class));
                break;
            case R.id.u15_withDrawBtn:
                //如果用户没有微信信息，则跳转到“微信”登陆，返回“倾秀”后发送 user/bindWeixin，成功后调用 bonus/withdraw
               // 如果用户已经有微信信息，直接调用 bonus/withdraw
               if (null != QSModel.INSTANCE.getUser().userInfo.weixin && !TextUtils.isEmpty(QSModel.INSTANCE.getUser().userInfo.weixin.openid)){
                       if (draw < 1){
                           ToastUtil.showShortToast(this , "佣金需要1元以上才能发红包");
                       }else {
                           ShareUtil.shareBonusToWX(U15BonusActivity.this);
                           withDrawBtn.setEnabled(false);
                       }
               }else {
                   // send oauth request
                   if (!wxApi.isWXAppInstalled()) {
                       //提醒用户没有按照微信
                       ToastUtil.showShortToast(getApplicationContext(), "您还没有安装微信，请先安装微信");
                       return;
                   }
                 //  dialogs.show();
                   final SendAuth.Req req = new SendAuth.Req();
                   req.scope = "snsapi_userinfo";
                   req.state = "qingshow_wxbind";
                   wxApi.sendReq(req);
               }
                break;
            case R.id.u15_qa:
                SharedPreferences preferences = QSApplication.instance().getPreferences();
                String url = preferences.getString("faq","");
                if(!TextUtils.isEmpty(url)) {
                    DialogUtils.ShowDialog(U15BonusActivity.this, getResources().getString(R.string.u15_qa), url, new DialogUtils.DialogClickListener() {
                        @Override
                        public void confirm() {

                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }else {
                    Log.e("test_" ,"faq --> "+"null");
            }
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U13PersonalizeActivity");
        MobclickAgent.onResume(this);
        reconn();
        matchUI();
        if (UnreadHelper.hasMyNotificationCommand(QSPushAPI.NEW_BONUSES))
            UnreadHelper.userReadNotificationCommand(QSPushAPI.NEW_BONUSES);
        if (UnreadHelper.hasMyNotificationCommand(QSPushAPI.BONUS_WITHDRAW_COMPLETE))
            UnreadHelper.userReadNotificationCommand(QSPushAPI.BONUS_WITHDRAW_COMPLETE);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U13PersonalizeActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
