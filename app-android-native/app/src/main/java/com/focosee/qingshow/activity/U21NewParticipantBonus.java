package com.focosee.qingshow.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Typeface;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.constants.config.QSPushAPI;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.user.UnreadHelper;
import com.focosee.qingshow.widget.QSTextView;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class U21NewParticipantBonus extends BaseActivity {

    @InjectView(R.id.close)
    ImageView close;
    @InjectView(R.id.u21_submit_btn)
    Button u21SubmitBtn;
    @InjectView(R.id.u21_item_image)
    SimpleDraweeView u21ItemImage;
    @InjectView(R.id.u21_item_layout)
    PercentRelativeLayout u21ItemLayout;
    @InjectView(R.id.u21_user_head)
    SimpleDraweeView u21UserHead;
    @InjectView(R.id.u21_nickname)
    QSTextView u21Nickname;
    @InjectView(R.id.u21_from)
    TextView u21From;
    @InjectView(R.id.u21_bonus)
    TextView u21Bonus;
    @InjectView(R.id.u21_msg_line1)
    QSTextView u21MsgLine1;
    @InjectView(R.id.u21_msg_line2)
    QSTextView u21MsgLine2;

    private MongoPeople.Bonuses bonuses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u21_new_participant_bonus);
        ButterKnife.inject(this);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        u21SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(U21NewParticipantBonus.this, U15BonusActivity.class));
                finish();
            }
        });

        init();
    }

    private void init() {

        bonuses = QSModel.INSTANCE.getUser().bonuses.get(QSModel.INSTANCE.getUser().bonuses.size() - 1);
        u21ItemImage.setImageURI(Uri.parse(bonuses.icon));
        u21ItemImage.setAspectRatio(0.5f);

        u21UserHead.setImageURI(Uri.parse(QSModel.INSTANCE.getUser().portrait));
        u21Nickname.setText(QSModel.INSTANCE.getUser().nickname);

        SpannableString spannableString = new SpannableString("您获得了来自" + "陈华榕" + "的共享佣金");
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, "您获得了来自".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), ("您获得了来自" + "陈华榕").length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        u21From.setText(spannableString);

        SpannableString price = new SpannableString("￥" + bonuses.money);
        price.setSpan(new UnderlineSpan(), 0, price.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        u21Bonus.setText(price);

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U21NewParticipantBonus");
        MobclickAgent.onResume(this);
        if (UnreadHelper.hasMyNotificationCommand(QSPushAPI.NEW_PARTICIPANT_BONUS))
            UnreadHelper.userReadNotificationCommand(QSPushAPI.NEW_PARTICIPANT_BONUS);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U21NewParticipantBonus");
        MobclickAgent.onPause(this);
    }

    @Override
    public void reconn() {

    }
}
