package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S07ListAdapter;
import com.focosee.qingshow.entity.ShowDetailEntity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class S07CollectActivity extends Activity {

    public static final String INPUT_ITEMS = "S07CollectActivity_input_items";
    public static final String INPUT_BACK_IMAGE = "S07CollectActivity_input_back_image";

    private ListView listView;
    private S07ListAdapter adapter;

    private ArrayList<ShowDetailEntity.RefItem> items;

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
        Bundle bundle = intent.getExtras();
        items = (ArrayList<ShowDetailEntity.RefItem>)bundle.getSerializable(INPUT_ITEMS);

        listView = (ListView) findViewById(R.id.S07_item_list);

        adapter = new S07ListAdapter(this, items);
        listView.setAdapter(adapter);

        ImageLoader.getInstance().displayImage(getIntent().getStringExtra(INPUT_BACK_IMAGE),(ImageView)findViewById(R.id.S07_background_image));
    }


}
