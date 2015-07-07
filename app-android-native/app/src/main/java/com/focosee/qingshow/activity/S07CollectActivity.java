package com.focosee.qingshow.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S07ListAdapter;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.util.QSComponent;
import com.focosee.qingshow.widget.RecyclerView.SpacesItemDecoration;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class S07CollectActivity extends BaseActivity {

    public static final String INPUT_ITEMS = "S07CollectActivity_input_items";
    public static final String INPUT_BACK_IMAGE = "S07CollectActivity_input_back_image";
    public static boolean isOpened = false;
    @InjectView(R.id.s07_back_icon)
    ImageButton s07BackIcon;
    @InjectView(R.id.s07_tv)
    TextView s07Tv;
    @InjectView(R.id.s07_brand_tv)
    TextView s07BrandTv;
    @InjectView(R.id.s07_top_line)
    View s07TopLine;
    @InjectView(R.id.s07_item_list)
    RecyclerView recyclerView;

    private S07ListAdapter adapter;

    private ArrayList<MongoItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s07_collect);
        ButterKnife.inject(this);

        s07BackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S07CollectActivity.this.finish();
            }
        });

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        items = (ArrayList<MongoItem>) bundle.getSerializable(INPUT_ITEMS);

        LinearLayoutManager layoutManager = new LinearLayoutManager(S07CollectActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new S07ListAdapter(items, S07CollectActivity.this, R.layout.item_s07_item_list);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(5));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isOpened = false;
    }

    @Override
    public void reconn() {

    }

    private Point getScreenSize() {
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
