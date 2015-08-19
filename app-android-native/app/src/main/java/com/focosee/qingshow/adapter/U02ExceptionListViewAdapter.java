package com.focosee.qingshow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2015/7/20.
 */
public class U02ExceptionListViewAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

//    private Context context;
//    private MongoPeople user;
//
//    private int i = 0;
//    private CheckedTextView[] itemViews = new CheckedTextView[datas.length];
//    private List<Integer> exceptions;
//
//    public U02ExceptionListViewAdapter(Context context, MongoPeople user) {
//        this.context = context;
//        this.user = user;
//        exceptions = new ArrayList<>();
//        if(null == user.expectations)return;
//        for (int i = 0; i < user.expectations.length; i++) {
//            exceptions.add(user.expectations[i]);
//        }
//    }
//
//    @Override
//    public int getCount() {
//        return null == datas ? 0 : datas.length;
//    }
//
//    @Override
//    public Object getItem(int arg0) {
//        return arg0;
//    }
//
//    public CheckedTextView getItemView(int index){
//        return itemViews[index];
//    }
//
//    @Override
//    public long getItemId(int arg0) {
//        return arg0;
//    }
//
//    @Override
//    public View getView(int position, View arg1, ViewGroup viewGroup) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_u02_exception,
//                null);
//        final CheckedTextView ctv = (CheckedTextView) view
//                .findViewById(R.id.list_row_ctv);
//        ctv.setText(datas[position]);
//        ctv.setTag(position);
//        if (exceptions.contains(position)) {
//            ctv.setChecked(true);
//        }
//        ctv.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                ctv.toggle();// 反转
//            }
//        });
//        itemViews[position] = ctv;
//        return view;
//    }
//
//    public CheckedTextView[] getItemViews(){
//        return itemViews;
//    }
}
