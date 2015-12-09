package com.focosee.qingshow.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.constants.config.QSPushAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.user.UnreadHelper;
import com.focosee.qingshow.widget.QSTextView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class U20NewBonus extends BaseActivity {

    private final int QUERY_PEOPLE_FINISH = 0x1;

    @InjectView(R.id.close)
    ImageView close;
    @InjectView(R.id.u20_submit_btn)
    Button u20SubmitBtn;
    @InjectView(R.id.u20_heads)
    LinearLayout u20Heads;
    @InjectView(R.id.u20_item_image)
    SimpleDraweeView u20ItemImage;
    @InjectView(R.id.u20_user_head)
    SimpleDraweeView u20UserHead;
    @InjectView(R.id.u20_nickname)
    QSTextView u20Nickname;
    @InjectView(R.id.u20_msg_line2)
    TextView u20MsgLine2;
    private List<MongoPeople> peoples;
    private MongoPeople.Bonuses bonuses;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == QUERY_PEOPLE_FINISH) {
                showUserHeads();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u20_new_bonus);
        ButterKnife.inject(this);

        init();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        u20SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(U20NewBonus.this, U15BonusActivity.class));
                finish();
            }
        });
    }

    private void init() {
        bonuses = QSModel.INSTANCE.getUser().bonuses.get(QSModel.INSTANCE.getUser().bonuses.size() - 1);
        u20ItemImage.setImageURI(Uri.parse(bonuses.icon));
        u20ItemImage.setAspectRatio(0.5f);

        u20UserHead.setImageURI(Uri.parse(QSModel.INSTANCE.getUser().portrait));
        u20Nickname.setText(QSModel.INSTANCE.getUser().nickname);

        SpannableString spannableString = new SpannableString("获得了￥" + bonuses.money + "的佣金");

        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pink_deep))
                , "获得了￥".length() - 1, ("获得了￥" + bonuses.money).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        u20MsgLine2.setText(spannableString);

        getPeoplesFromNet();
    }

    private void getPeoplesFromNet() {
        String[] pIds = bonuses.participants;

        if (pIds.length == 0 || pIds == null) return;

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getPeopleQueryApi(pIds), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(U20NewBonus.class.getSimpleName(), "response:" + response);
                if (!MetadataParser.hasError(response)) {
                    peoples = UserParser._parsePeoples(response);
                    handler.sendEmptyMessage(QUERY_PEOPLE_FINISH);
                }
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void showUserHeads() {

        int screenWidth = QSApplication.instance().getScreenSize(this).x;

        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(screenWidth / 10, screenWidth / 10);
        LinearLayout.LayoutParams parentParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , 0);
        parentParams.weight = 1;
        parentParams.gravity = Gravity.CENTER;

        int lineLength = 10;
        RoundingParams roundingParams = new RoundingParams();
        roundingParams.setRoundAsCircle(true);
        roundingParams.setBorder(getResources().getColor(R.color.white), 5);

        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setRoundingParams(roundingParams)
                .build();

        for (int i = 0; i < 2; i++) {

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setLayoutParams(parentParams);
            for (int j = 0; j < lineLength; j++) {
                int index = i * lineLength + j;
                if (index < peoples.size()) {
                    if (TextUtils.isEmpty(peoples.get(index).portrait)) continue;
                    SimpleDraweeView imageView = new SimpleDraweeView(this);
                    imageView.setPadding(5, 0, 5, 0);
                    imageView.setImageURI(Uri.parse(peoples.get(index).portrait));
                    imageView.setAspectRatio(1f);
                    imageView.setHierarchy(hierarchy);
                    linearLayout.addView(imageView, itemParams);
                }
            }
            linearLayout.setLayoutParams(parentParams);
            u20Heads.addView(linearLayout);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U20NewBonus");
        MobclickAgent.onResume(this);
        if (UnreadHelper.hasMyNotificationCommand(QSPushAPI.NEW_BONUSES))
            UnreadHelper.userReadNotificationCommand(QSPushAPI.NEW_BONUSES);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U20NewBonus");
        MobclickAgent.onPause(this);
    }

    @Override
    public void reconn() {

    }
}
