package com.focosee.qingshow.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
    private Holder holder;
    private String[] listKeys;
    private TextView[] tempReset = new TextView[8];
    private TextView[] tempMemory = new TextView[8];


    public S21CategoryListViewAdapter(Context context, List<Map<String, String>> list, String[] listKeys) {


        this.list = list;
        this.context = context;
        this.listKeys = listKeys;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            convertView = mInflater.inflate(R.layout.item_s21_category_listview, null);
            holder = new Holder();
            holder.titleName = (TextView) convertView.findViewById(R.id.item_s21_name);
            holder.viewPager = (ViewPager) convertView.findViewById(R.id.item_s21_middle);
            Map<String, String> info = list.get(position);
            if (info != null) {
                String item_name = (String) info.get(listKeys[0]);
                List<View> mViews = new ArrayList<View>();
                View view1 = mInflater.inflate(R.layout.item_s21_category_viewpager, null);
                TextView tv1 = (TextView) view1.findViewById(R.id.item_s21_category_1);
                TextView tv2 = (TextView) view1.findViewById(R.id.item_s21_category_2);
                TextView tv3 = (TextView) view1.findViewById(R.id.item_s21_category_3);
                tv1.setOnClickListener(new CategoryListener(position));
                tv2.setOnClickListener(new CategoryListener(position));
                tv3.setOnClickListener(new CategoryListener(position));
                tv1.setText("1");
                tv2.setText("2");
                tv3.setText("3");

                mViews.add(view1);
                if (info.size() > 4) {
                    View view2 = mInflater.inflate(R.layout.item_s21_category_viewpager, null);
                    TextView tv4 = (TextView) view2.findViewById(R.id.item_s21_category_1);
                    TextView tv5 = (TextView) view2.findViewById(R.id.item_s21_category_2);
                    TextView tv6 = (TextView) view2.findViewById(R.id.item_s21_category_3);
                    tv4.setOnClickListener(new CategoryListener(position));
                    tv5.setOnClickListener(new CategoryListener(position));
                    tv6.setOnClickListener(new CategoryListener(position));
                    tv4.setText("4");
                    tv5.setText("5");
                    tv6.setText("6");

                    mViews.add(view2);
                }
                holder.titleName.setText(item_name);
                holder.viewPager.setAdapter(new S21CategoryViewPagerAdapter(mViews));
                convertView.setTag(holder);
            }
        }

        return convertView;
    }


    class CategoryListener implements View.OnClickListener {
        private int position;

        public CategoryListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            TextView tv = (TextView) v;
            tv.setTextColor(context.getResources().getColor(R.color.pink));
            Drawable pic = context.getResources().getDrawable(R.drawable.bg_photpchoose_eg02);
            pic.setBounds(0, 0, pic.getMinimumWidth(), pic.getMinimumHeight());
            tv.setCompoundDrawables(null, pic, null, null);
            if (tempReset[position] == null) {
                tempReset[position] = tv;

            } else {
                reset(tempReset[position]);
                tempReset[position] = tv;
            }

        }

        private void reset(TextView tv) {
            tv.setTextColor(context.getResources().getColor(R.color.black));
            Drawable pic = context.getResources().getDrawable(R.drawable.bg_photpchoose_eg01);
            pic.setBounds(0, 0, pic.getMinimumWidth(), pic.getMinimumHeight());
            tv.setCompoundDrawables(null, pic, null, null);
        }

    }

}
