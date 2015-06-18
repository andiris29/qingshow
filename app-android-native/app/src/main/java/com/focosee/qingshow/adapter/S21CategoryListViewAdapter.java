package com.focosee.qingshow.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.focosee.qingshow.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/6/17.
 */
public class S21CategoryListViewAdapter extends BaseAdapter {
    private static List<Map<String, String>> list;
    private LayoutInflater mInflater;
    private Context context;
    private String[] listKey;
    private int[] id;
    private Holder holder;
    private int layout;
    private View view;

    public S21CategoryListViewAdapter(Context context,
                                      List<Map<String, String>> list, int layout, String[] listKey, int[] id) {

        this.list = list;
        this.context = context;
        this.layout = layout;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listKey = listKey;
        this.id = id;
    }

    private class Holder {
        TextView titleName;
        ViewPager viewPager;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            holder = (Holder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(layout, null);
            holder = new Holder();
            holder.titleName = (TextView) convertView.findViewById(id[0]);
            holder.viewPager = (ViewPager) convertView.findViewById(id[1]);
            convertView.setTag(holder);
        }

        Map<String, String> info = list.get(position);
        if (info != null) {
            String item_name = (String) info.get(listKey[0]);
            List<View> mViews = new ArrayList<View>();
            view = mInflater.inflate(R.layout.item_s21_category_viewpager, null);
            mViews.add(view);
            if (info.size() > 4) {
                mViews.add(view);
            }
            holder.titleName.setText(item_name);
            holder.viewPager.setAdapter(new S21CategoryViewPagerAdapter(mViews));

        }
        return convertView;
    }
}
