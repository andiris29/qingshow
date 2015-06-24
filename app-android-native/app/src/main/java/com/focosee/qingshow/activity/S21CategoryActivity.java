package com.focosee.qingshow.activity;

import android.app.Activity;
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

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/6/17.
 */
public class S21CategoryActivity extends Activity {
    private ListView s21_listview;
    private ArrayList<MongoCategories> categories = new ArrayList<MongoCategories>();
    private ArrayList<ArrayList<MongoCategories>> items = new ArrayList<ArrayList<MongoCategories>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s21_category_selector);
        s21_listview = (ListView) findViewById(R.id.s21_listview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromNet();
        s21_listview.setDividerHeight(0);


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
                    String parentRef = ca.getParentRef();
                    boolean activate = ca.isActivate();
                    if (activate && (parentRef == null)) {
                        categories.add(ca);
                    }
                }
                for (int i = 0; i < categories.size(); i++) {
                    String id=categories.get(i).get_id();
                    ArrayList<MongoCategories> item = new ArrayList<MongoCategories>();
                    for (MongoCategories cas : arrayList) {
                        String parentRef = cas.getParentRef();
                        boolean activate = cas.isActivate();

                        if (activate && (id.equals(parentRef))) {
                            item.add(cas);
                        }
                    }
                    items.add(item);
                }
                show();

            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void show() {
        S21CategoryListViewAdapter adapter = new S21CategoryListViewAdapter(S21CategoryActivity.this, categories, items);

        s21_listview.setAdapter(adapter);
    }
}
