package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import com.android.volley.Request;
import com.android.volley.Response;
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
import com.focosee.qingshow.util.bonus.BonusHelper;
import com.focosee.qingshow.widget.ConfirmDialog;
import com.focosee.qingshow.widget.LoadingDialogs;
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
    @InjectView(R.id.u15_withDrawBtn)
    QSButton withDrawBtn;

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
    }

    public void setData() {
        if (null == people) return;
        if (null == people.bonuses) return;
        u15Balance.setText(BonusHelper.getBonusesNotWithDraw(people.bonuses));
        u15Total.setText(BonusHelper.getTotalBonuses(people.bonuses));
        Log.d(U15BonusActivity.class.getSimpleName(), "bonus:" + people.bonuses.size());
        if(!(null == people.bonuses || people.bonuses.size() == 0))
            u15AlipayAccount.setText(people.bonuses.get(0).alipayId);
        if(BonusHelper.getBonusesWithFloat(people.bonuses) > 0){
            isCanWithDrwa = true;
        }
    }

    private void getUser() {
        dialogs.show();
        UserCommand.refresh(new Callback() {
            @Override
            public void onComplete() {
                dialogs.dismiss();
                people = QSModel.INSTANCE.getUser();
                setData();
            }
        });
    }

    @Override
    public void reconn() {
        getUser();
    }

    public void onEventMainThread(ShareBonusEvent event) {
        if (event.errorCode == SendMessageToWX.Resp.ErrCode.ERR_OK) {

            Map<String, String> params = new HashMap<>();
            params.put("alipayId", u15AlipayAccount.getText().toString());

            QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST
                    , QSAppWebAPI.getUserBonusWithdrawApi(), new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(U15BonusActivity.class.getSimpleName(), "response:" + response);
                    if (MetadataParser.hasError(response)) {
                        ToastUtil.showShortToast(getApplicationContext(), "提现失败，请重试");
                        return;
                    }
                    UserCommand.refresh();
                    final ConfirmDialog dialog = new ConfirmDialog(U15BonusActivity.this);
                    dialog.setTitle(getString(R.string.bonus_share_successed_hint));
                    dialog.setConfirm(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    dialog.show();
                    dialog.hideCancel();
                }
            });
            RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
        }
    }

    public void onEventMainThread(String event){
        if(ValueUtil.BONUES_COMING.equals(event)){
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
                if(!isCanWithDrwa){
                    if(u15ErrorText.isShown())return;
                    showError("您没有可提现的佣金");
                    return;
                }
                if (TextUtils.isEmpty(u15AlipayAccount.getText())) {
                    showError(getString(R.string.u15_hint_edit));
                    return;
                }
                ShareUtil.shareBonusToWX(QSModel.INSTANCE.getUserId(), ValueUtil.SHARE_BONUS
                        , U15BonusActivity.this, true);
                break;
        }
    }

    private void showError(String msg){
        if(!TextUtils.isEmpty(msg)){
            u15ErrorText.setText(msg);
            withDrawBtn.setEnabled(false);
        }
        u15ErrorText.setVisibility(View.VISIBLE);
        u15ErrorText.postDelayed(new Runnable() {
            @Override
            public void run() {
                u15ErrorText.setVisibility(View.GONE);
                withDrawBtn.setEnabled(true);
            }
        }, ValueUtil.SHOW_ERROR_TIME);
        return;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
