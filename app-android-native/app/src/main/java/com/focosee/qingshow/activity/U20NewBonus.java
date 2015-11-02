package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.focosee.qingshow.R;
import com.focosee.qingshow.constants.config.QSPushAPI;
import com.focosee.qingshow.util.user.UnreadHelper;
import com.umeng.analytics.MobclickAgent;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class U20NewBonus extends Activity {

    @InjectView(R.id.close)
    ImageView close;
    @InjectView(R.id.u20_submit_btn)
    Button u20SubmitBtn;
    @InjectView(R.id.u20_heads)
    LinearLayout u20Heads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u20_new_bonus);
        ButterKnife.inject(this);

        showUserHeads();

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

    private void showUserHeads() {

        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemParams.weight = 1;
        LinearLayout.LayoutParams parentParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
        , 0);
        parentParams.weight = 1;
        for (int i = 0; i < 2; i++) {

            LinearLayout linearLayout = new LinearLayout(this);

            linearLayout.setLayoutParams(parentParams);
            for (int j = 0; j < 10; j++) {

                ImageView imageView = new ImageView(this);
                imageView.setPadding(5, 0, 5, 0);
                imageView.setImageResource(R.drawable.weixin_login);
                linearLayout.addView(imageView, itemParams);
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
}
