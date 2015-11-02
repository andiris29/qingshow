package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.widget.QSTextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class U21NewParticipantBonus extends Activity {

    @InjectView(R.id.close)
    ImageView close;
    @InjectView(R.id.u21_submit_btn)
    Button u21SubmitBtn;

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
    }

}
