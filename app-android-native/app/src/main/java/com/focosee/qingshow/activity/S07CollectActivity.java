package com.focosee.qingshow.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S07ListAdapter;
import com.focosee.qingshow.entity.mongo.MongoBrand;
import com.focosee.qingshow.entity.mongo.MongoItem;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

public class S07CollectActivity extends BaseActivity {

    public static final String INPUT_ITEMS = "S07CollectActivity_input_items";
    public static final String INPUT_BACK_IMAGE = "S07CollectActivity_input_back_image";
    public static final String INPUT_BRAND_TEXT = "S07CollectActivity_input_brand_text";
    public static final String INPUT_BRAND_ENTITY = "S07CollectActivity_INPUT_BRAND_ENTITY";
    public static boolean isOpened = false;

    private ListView listView;
    private S07ListAdapter adapter;

    private String brandText = null;
    //private MongoShowD.RefBrand brandEntity = null;
    private MongoBrand brandEntity = null;
    private ArrayList<MongoItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s07_collect);

        findViewById(R.id.S07_back_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S07CollectActivity.this.finish();
            }
        });

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        items = (ArrayList<MongoItem>) bundle.getSerializable(INPUT_ITEMS);
        brandText = intent.getStringExtra(INPUT_BRAND_TEXT);
        //brandEntity = (MongoShowD.RefBrand) bundle.getSerializable(INPUT_BRAND_ENTITY);
        //brandEntity = (BrandEntity) bundle.getSerializable(INPUT_BRAND_ENTITY);
        listView = (ListView) findViewById(R.id.S07_item_list);

        adapter = new S07ListAdapter(this, (null != brandText), items, getScreenSize().y);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent(S07CollectActivity.this, P04BrandActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable(P04BrandActivity.INPUT_BRAND, items.get(position).getBrandRef());
                bundle1.putSerializable(P04BrandActivity.INPUT_ITEM, items.get(position));
                //bundle1.putSerializable(P04BrandActivity.INPUT_BRAND, brandEntity);
                intent1.putExtras(bundle1);
                startActivity(intent1);

            }
        });

//        ImageLoader.getInstance().displayImage(getIntent().getStringExtra(INPUT_BACK_IMAGE),(ImageView)findViewById(R.id.S07_background_image));
        if (null != brandText) {
            ((TextView)findViewById(R.id.S07_brand_tv)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.S07_brand_tv)).setText(brandText);
        } else {
            ((TextView)findViewById(R.id.S07_brand_tv)).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isOpened = false;
    }

    private Point getScreenSize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("S07Collocation"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("S07Collocation"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}
