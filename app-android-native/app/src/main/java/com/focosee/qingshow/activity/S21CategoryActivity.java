package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Context;
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
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/6/17.
 */
public class S21CategoryActivity extends Activity {
    private ListView s21_listview;
    private final String ITEM_NAME = "titleName";
    private final String ITEM_CONTENT_1 = "category_1";
    private final String ITEM_CONTENT_2 = "category_2";
    private final String ITEM_CONTENT_3 = "category_3";
//    private final Charset UTF="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s21_category_selector);
        s21_listview = (ListView) findViewById(R.id.s21_listview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        s21_listview.setDividerHeight(0);
        String[] listkeys = {ITEM_NAME};
        S21CategoryListViewAdapter adapter = new S21CategoryListViewAdapter(this, getListInfo(), listkeys);

        s21_listview.setAdapter(adapter);
        getDataFromNet();
    }

    public void back(View view) {
        this.finish();
    }


    private List<Map<String, String>> getListInfo() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (int i = 0; i < 6; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(ITEM_NAME, "TITLE");
            map.put(ITEM_CONTENT_1, "CONTENT");
            map.put(ITEM_CONTENT_2, "CONTENT");
            map.put(ITEM_CONTENT_3, "CONTENT");
            map.put("545", "CONTENT");
            map.put("afa", "CONTENT");
            map.put("afs", "CONTENT");
            list.add(map);
        }
        return list;
    }

    private void getDataFromNet() {
        final QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getQueryCategories(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S21CategoryActivity.this, MetadataParser.getError(response));
                    return;
                }
                String s = CategoryParser.parseQuery(response).toString();//ok
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }
}
