package com.focosee.qingshow.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.model.S20Bitmap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2015/7/9.
 */
public class S20MatchPreviewActivity extends BaseActivity {

    @InjectView(R.id.image)
    ImageView image;
    @Override
    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s20_preview);
        ButterKnife.inject(this);
        initImage();
    }

    private void initImage() {
        Bitmap bitmap = S20Bitmap.INSTANCE.getBitmap();
        if (bitmap != null) {
            image.setImageBitmap(bitmap);
        }
    }

    @OnClick(R.id.submitBtn)
    public void sublit(){

    }


}
