package com.focosee.qingshow.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.widget.QSTextView;
import com.focosee.qingshow.widget.flow.FlowRadioGroup;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class U20NewBonus extends Activity {

    @InjectView(R.id.close)
    ImageView close;
    @InjectView(R.id.u20_item_image)
    SimpleDraweeView u20ItemImage;
    @InjectView(R.id.u20_user_head)
    SimpleDraweeView u20UserHead;
    @InjectView(R.id.u20_nickname)
    QSTextView u20Nickname;
    @InjectView(R.id.u20_msg_line1)
    TextView u20MsgLine1;
    @InjectView(R.id.u20_msg_line2)
    TextView u20MsgLine2;
    @InjectView(R.id.u20_submit_btn)
    Button u20SubmitBtn;
    @InjectView(R.id.u20_msg3)
    TextView u20Msg3;
    @InjectView(R.id.u20_msg3_layout)
    LinearLayout u20Msg3Layout;
    @InjectView(R.id.u20_heads)
    LinearLayout u20Heads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u20_new_bonus);
        ButterKnife.inject(this);

        showUserHeads();
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

}
