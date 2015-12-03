package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S21CategoryListViewAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.CategoryParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoCategories;
import com.focosee.qingshow.model.vo.mongo.MongoParentCategories;
import com.focosee.qingshow.util.ComparatorList;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/6/17.
 */
public class S21CategoryActivity extends BaseActivity {

    public static final String TYPE = "S21CategoryActivity_TYPE";
    public static final int TYPE_SEARCH = 0x1;

    private ListView s21_listview;
    private ArrayList<MongoCategories> categories = new ArrayList<>();
    private ArrayList<ArrayList<MongoCategories>> items = new ArrayList<>();
    private List<String> selectCategories;

    private int type = 0x0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s21_category_selector);
        ButterKnife.inject(this);

        s21_listview = (ListView) findViewById(R.id.s21_listview);
        type = getIntent().getIntExtra(TYPE, type);
        selectCategories = getIntent().getStringArrayListExtra(S20MatcherActivity.S20_SELECT_CATEGORYREFS);
        getDataFromNet();
        s21_listview.setDividerHeight(0);
    }

    @Override
    public void reconn() {

    }


    @OnClick(R.id.submit)
    public void submit() {
        S21CategoryEvent event = new S21CategoryEvent(selectCategories);
        EventBus.getDefault().post(event);
        this.finish();
    }

    public void back(View view) {
        this.finish();
    }


    private void getDataFromNet() {
        final QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getQueryCategories(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S21CategoryActivity.this, MetadataParser.getError(response));
                    return;
                }
                ArrayList<MongoCategories> arrayList = CategoryParser.parseQuery(response);
                for (MongoCategories ca : arrayList) {
                    MongoParentCategories parentRef = ca.parentRef;
                    if (parentRef == null) {
                        categories.add(ca);
                    }
                }
                Collections.sort(categories, ComparatorList.categoriesComparator());
                for (int i = 0; i < categories.size(); i++) {
                    String id = categories.get(i).get_id();
                    ArrayList<MongoCategories> item = new ArrayList<>();
                    for (MongoCategories cas : arrayList) {
                        MongoParentCategories parentRef = cas.parentRef;
                        if (parentRef != null) {
                            if (id.equals(parentRef._id)) {
                                item.add(cas);
                            }
                        }
                    }
                    if (item.size() > 0) {
                        items.add(item);
                    }
                }
                show();
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void show() {
        S21CategoryListViewAdapter adapter = new S21CategoryListViewAdapter(S21CategoryActivity.this, categories, items,
                selectCategories);
        adapter.setOnSelectChangeListener(new S21CategoryListViewAdapter.OnSelectChangeListener() {
            @Override
            public void onSelectChanged(List<String> selectRefs) {
                selectCategories = selectRefs;
            }
        });
        s21_listview.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("S21CategoryActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("S21CategoryActivity");
        MobclickAgent.onPause(this);
    }
}
