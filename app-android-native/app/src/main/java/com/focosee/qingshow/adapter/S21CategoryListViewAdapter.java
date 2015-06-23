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
    private TextView[] tempReset = new TextView[6];//每行最大暂定为6列
    private int[] tempMem = new int[6];
    private boolean[] tempPage = new boolean[6];
    TextView tv1 = null;
    TextView tv2 = null;
    TextView tv3 = null;
    TextView tv4 = null;
    TextView tv5 = null;
    TextView tv6 = null;


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
        }
        Map<String, String> info = list.get(position);
        if (info != null) {
            String item_name = (String) info.get(listKeys[0]);
            List<View> mViews = new ArrayList<View>();
            View view1 = mInflater.inflate(
                    R.layout.item_s21_category_viewpager, null);
            tv1 = (TextView) view1
                    .findViewById(R.id.item_s21_category_1);
            tv2 = (TextView) view1
                    .findViewById(R.id.item_s21_category_2);
            tv3 = (TextView) view1
                    .findViewById(R.id.item_s21_category_3);
            tv1.setTag(1);
            tv2.setTag(2);
            tv3.setTag(3);
            tv1.setOnClickListener(new CategoryListener(position));
            tv2.setOnClickListener(new CategoryListener(position));
            tv3.setOnClickListener(new CategoryListener(position));
            tv1.setText("1");
            tv2.setText("2");
            tv3.setText("3");

            mViews.add(view1);

            if (info.size() > 4) {
                View view2 = mInflater.inflate(
                        R.layout.item_s21_category_viewpager, null);
                tv4 = (TextView) view2
                        .findViewById(R.id.item_s21_category_1);
                tv5 = (TextView) view2
                        .findViewById(R.id.item_s21_category_2);
                tv6 = (TextView) view2
                        .findViewById(R.id.item_s21_category_3);
                tv4.setTag(4);
                tv5.setTag(5);
                tv6.setTag(6);
                tv4.setOnClickListener(new CategoryListener(position));
                tv5.setOnClickListener(new CategoryListener(position));
                tv6.setOnClickListener(new CategoryListener(position));
                tv4.setText("4");
                tv5.setText("5");
                tv6.setText("6");

                mViews.add(view2);

            }
            switch ((int) tempMem[position]) {
                case 1:
                    memory(tv1);
                    tempReset[position] = tv1;
                    break;
                case 2:
                    memory(tv2);
                    tempReset[position] = tv2;
                    break;
                case 3:
                    memory(tv3);
                    tempReset[position] = tv3;
                    break;
                case 4:
                    memory(tv4);
                    tempReset[position] = tv4;
                    break;
                case 5:
                    memory(tv5);
                    tempReset[position] = tv5;
                    break;
                case 6:
                    memory(tv6);
                    tempReset[position] = tv6;
                    break;

                default:
                    break;
            }
            holder.titleName.setText(item_name);
            holder.viewPager
                    .setAdapter(new S21CategoryViewPagerAdapter(mViews));
            if (tempPage[position]) {
                holder.viewPager.setCurrentItem(1);
            }
            convertView.setTag(holder);
        }

        return convertView;
    }

    private void memory(TextView tv) {
        tv.setTextColor(context.getResources().getColor(R.color.s21_pink));
        Drawable pic = context.getResources().getDrawable(R.drawable.bg_photpchoose_eg02);
        pic.setBounds(0, 0, pic.getMinimumWidth(), pic.getMinimumHeight());
        tv.setCompoundDrawables(null, pic, null, null);
    }


    class CategoryListener implements View.OnClickListener {
        private int position;

        public CategoryListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            TextView tv = (TextView) v;
            int index = (Integer) v.getTag();
            tempMem[position] = index;
            if (index > 3) {
                tempPage[position] = true;
            } else {
                tempPage[position] = false;
            }
            tv.setTextColor(context.getResources().getColor(R.color.s21_pink));
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
