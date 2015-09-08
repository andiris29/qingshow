package com.focosee.qingshow.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.ShareUtil;
import com.focosee.qingshow.util.ToastUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.util.people.PeopleHelper;
import com.focosee.qingshow.widget.QSButton;
import com.focosee.qingshow.widget.QSEditText;
import com.focosee.qingshow.widget.QSTextView;
import com.focosee.qingshow.wxapi.ShareBonusEvent;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class U15BonusActivity extends BaseActivity implements View.OnClickListener {

    private final String ALIPAYID = "alipayId";

    @InjectView(R.id.title)
    QSTextView title;
    @InjectView(R.id.right_btn)
    QSButton rightBtn;
    @InjectView(R.id.u15_total)
    QSTextView u15Total;
    @InjectView(R.id.u15_balance)
    QSTextView u15Balance;
    @InjectView(R.id.u15_hint_text)
    QSTextView u15HintText;
    @InjectView(R.id.left_btn)
    ImageButton leftBtn;
    @InjectView(R.id.u15_alipay_account)
    QSEditText u15AlipayAccount;
    @InjectView(R.id.u15_error_text)
    QSTextView u15ErrorText;

    private MongoPeople people;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u15_bonus);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        getUser();
        matchUI();
    }

    public void matchUI() {
        title.setText(getText(R.string.bonus_activity_settings));
        leftBtn.setOnClickListener(this);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText(getText(R.string.u15_title_right_btn));
        rightBtn.setOnClickListener(this);
    }

    public void setData() {
        if (null == people) return;
        if (null == people.bonuses) return;
        u15Balance.setText(PeopleHelper.getBonusesNotWithDraw(people.bonuses));
        u15Total.setText(PeopleHelper.getTotalBonuses(people.bonuses));
        u15AlipayAccount.setText(people.bonuses.get(0).alipayId);
    }

    private void getUser() {
        UserCommand.refresh(new Callback() {
            @Override
            public void onComplete() {
                people = QSModel.INSTANCE.getUser();
                setData();
            }
        });
    }

    @Override
    public void reconn() {

    }

    public void onEventMainThread(ShareBonusEvent event) {
        if (event.errorCode == SendMessageToWX.Resp.ErrCode.ERR_OK) {

            Map<String, String> params = new HashMap<>();
            params.put("alipayId", u15AlipayAccount.getText().toString());

            QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST
                    , QSAppWebAPI.getUserBonusWithdrawApi(), new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (MetadataParser.hasError(response)) {
                        ToastUtil.showShortToast(getApplicationContext(), "提现失败，请重试");
                    }
                    ToastUtil.showShortToast(getApplicationContext(), "已发送提现申请");
                    finish();
                }
            });
            RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
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
                if (TextUtils.isEmpty(u15AlipayAccount.getText())) {
                    u15ErrorText.setVisibility(View.VISIBLE);
                    u15ErrorText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            u15ErrorText.setVisibility(View.GONE);
                        }
                    }, ValueUtil.SHOW_ERROR_TIME);
                    return;
                }
                ShareUtil.shareBonusToWX(QSModel.INSTANCE.getUserId(), ValueUtil.SHARE_BONUS
                        , U15BonusActivity.this, true);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
