package com.focosee.qingshow.activity;

import android.content.Intent;
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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.BonusCommand;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.constants.config.QSPushAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.request.RxRequest;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.aggregation.BonusAmount;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.AppUtil;
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
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
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

    private MongoPeople people;
    private boolean isCanWithDrwa = false;
    private LoadingDialogs dialogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u15_bonus);
        ButterKnife.inject(this);
        dialogs = new LoadingDialogs(U15BonusActivity.this);
        EventBus.getDefault().register(this);
        reconn();
        matchUI();
    }

    public void matchUI() {
        title.setText(getText(R.string.bonus_activity_settings));
        leftBtn.setOnClickListener(this);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText(getText(R.string.u15_title_right_btn));
        rightBtn.setOnClickListener(this);
        rightBtn.setTextColor(getResources().getColor(android.R.color.black));
        String hint = getString(R.string.u15_hint);
        SpannableString ss = new SpannableString(hint);
        Drawable drawable = getResources().getDrawable(R.drawable.u15_notify);
        assert drawable != null;
        drawable.setBounds(0, 0, (int)AppUtil.transformToDip(30f,this), (int)AppUtil.transformToDip(30f,this));
        ss.setSpan(new ImageSpan(drawable), hint.length() - 3, hint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        u15HintText.setText(ss);

        setData();
    }

    public void setData() {
        UserCommand.refresh(new Callback(){
            @Override
            public void onComplete() {
                BonusAmount amountByStatus = QSModel.INSTANCE.getUser().__context.bonusAmountByStatus;
                float total = 0;
                float draw = 0;
                if (amountByStatus.bonuses.containsKey("0")){
                    total += amountByStatus.bonuses.get("0").floatValue();
                    draw += amountByStatus.bonuses.get("0").floatValue();
                }
                if (amountByStatus.bonuses.containsKey("1")){
                    total += amountByStatus.bonuses.get("1").floatValue();
                }
                if (amountByStatus.bonuses.containsKey("2")){
                    total += amountByStatus.bonuses.get("2").floatValue();
                }
                u15Total.setText(StringUtil.FormatPrice(total));
                u15Draw.setText(StringUtil.FormatPrice(draw));
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
                withDrawBtn.setEnabled(false);
                ShareUtil.shareBonusToWX(U15BonusActivity.this);
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U13PersonalizeActivity");
        MobclickAgent.onResume(this);
        if (UnreadHelper.hasMyNotificationCommand(QSPushAPI.NEW_BONUSES))
            UnreadHelper.userReadNotificationCommand(QSPushAPI.NEW_BONUSES);
        if (UnreadHelper.hasMyNotificationCommand(QSPushAPI.BONUS_WITHDRAW_COMPLETE))
            UnreadHelper.userReadNotificationCommand(QSPushAPI.BONUS_WITHDRAW_COMPLETE);
        if(UnreadHelper.hasMyNotificationCommand(QSPushAPI.NEW_PARTICIPANT_BONUS))
            UnreadHelper.userReadNotificationCommand(QSPushAPI.NEW_PARTICIPANT_BONUS);
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
