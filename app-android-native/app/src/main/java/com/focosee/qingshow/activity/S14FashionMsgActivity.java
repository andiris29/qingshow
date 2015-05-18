package com.focosee.qingshow.activity;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S08TrendListAdapter;
import com.focosee.qingshow.model.vo.mongo.MongoPreview;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.widget.SharePopupWindow;
import com.focosee.qingshow.widget.indicator.NetworkImageIndicatorView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class S14FashionMsgActivity extends BaseActivity {

    private NetworkImageIndicatorView imageIndicatorView;
    private SharePopupWindow sharePopupWindow;
    private TextView describe;
    private ImageView shareBtn;
    private ImageView likeBtn;
    private ImageView playBtn;

    private TextView likeNumText;

    private SimpleDraweeView videoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s14_fashion_msg_pictures);

        shareBtn = (ImageView) findViewById(R.id.s14_share_btn);
        likeBtn = (ImageView) findViewById(R.id.s14_like_btn);
        playBtn = (ImageView) findViewById(R.id.s14_play_btn);
        videoImage = (SimpleDraweeView) findViewById(R.id.s14_imageview);
        likeNumText = (TextView) findViewById(R.id.s14_like_text_view);
        imageIndicatorView = (NetworkImageIndicatorView) findViewById(R.id.s14_image_indicator);

        if(getIntent().getStringExtra("refCollection").equals(S08TrendListAdapter.PREVIEWS)){
            MongoPreview preview = (MongoPreview) getIntent().getExtras().getSerializable("entity");

            describe = (TextView) findViewById(R.id.s14_describe);
            List<String> imageList = new ArrayList<String>();
            for (MongoPreview.Image image : preview.images){
                imageList.add(image.url);
            }
            imageIndicatorView.setVisibility(View.VISIBLE);
            imageIndicatorView.setupLayoutByImageUrl(imageList, ImageLoader.getInstance());
            imageIndicatorView.show();
            imageIndicatorView.getViewPager().setCurrentItem(0, false);
            describe.setText(preview.getDescription(0));
            likeNumText.setText(String.valueOf(preview.numLike));

        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            MongoShow show = (MongoShow) getIntent().getExtras().getSerializable("entity");
            videoImage.setImageURI(Uri.parse(show.cover));
            videoImage.setVisibility(View.VISIBLE );
        }

        findViewById(R.id.s14_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sharePopupWindow = new SharePopupWindow(S14FashionMsgActivity.this, new ShareClickListener());
//                sharePopupWindow.setAnimationStyle(R.style.popwin_anim_style);
//                sharePopupWindow.showAtLocation(S03SHowActivity.this.findViewById(R.id.S03_share_btn), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });
    }

    @Override
    public void reconn() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.meun_s14_fashion_msg, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
