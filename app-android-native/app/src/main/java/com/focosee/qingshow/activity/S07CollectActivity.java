package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S07ListAdapter;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.CategoriesCommand;
import com.focosee.qingshow.model.CategoriesModel;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.util.ComparatorList;
import com.focosee.qingshow.util.filter.Filter;
import com.focosee.qingshow.util.filter.FilterHepler;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;
import java.util.Collections;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class S07CollectActivity extends BaseActivity {

    private final int GET_CATEGORIES = 0x1;

    public static final String INPUT_ITEMS = "S07CollectActivity_input_items";
    public static boolean isOpened = false;
    @InjectView(R.id.s07_back_icon)
    ImageButton s07BackIcon;
    @InjectView(R.id.s07_item_list)
    RecyclerView recyclerView;

    private S07ListAdapter adapter;

    private ArrayList<MongoItem> items;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == GET_CATEGORIES) {
                init();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s07_collect);
        ButterKnife.inject(this);

        if (null == CategoriesModel.INSTANCE.getCategories()) {
            CategoriesCommand.getCategories(new Callback() {
                @Override
                public void onComplete() {
                    handler.sendEmptyMessage(GET_CATEGORIES);
                }
            });
        } else {
            init();
        }
    }

    private void init() {
        s07BackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S07CollectActivity.this.finish();
            }
        });

        final Bundle bundle = getIntent().getExtras();
        items = (ArrayList<MongoItem>) bundle.getSerializable(INPUT_ITEMS);
        Collections.sort(items, ComparatorList.itemComparator());
        FilterHepler.filterList(items, new Filter<MongoItem>() {
            @Override
            public boolean filtrate(MongoItem item) {
                if (null != item.delist)
                    return true;
                return false;
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(S07CollectActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new S07ListAdapter(items, S07CollectActivity.this, getIntent().getStringExtra(S10ItemDetailActivity.PROMOTRER), R.layout.item_s07_item_list, R.layout.item_s07_text);
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isOpened = false;
    }

    @Override
    public void reconn() {

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
