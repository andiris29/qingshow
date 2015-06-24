package com.focosee.qingshow.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoCategories;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/6/17.
 */
public class S21CategoryListViewAdapter extends BaseAdapter {
    private ArrayList<MongoCategories> categories;
    private ArrayList<ArrayList<MongoCategories>> items;
    private LayoutInflater mInflater;
    private Context context;
    private Holder holder;
    private TextView[] tempReset = new TextView[8];//每行最大暂定为6列
    private int[] tempMem = new int[8];
    private boolean[] tempPage = new boolean[8];
    TextView tv1 = null;
    TextView tv2 = null;
    TextView tv3 = null;
    TextView tv4 = null;
    TextView tv5 = null;
    TextView tv6 = null;
    com.facebook.drawee.view.SimpleDraweeView img1 = null;
    com.facebook.drawee.view.SimpleDraweeView img2 = null;
    com.facebook.drawee.view.SimpleDraweeView img3 = null;
    com.facebook.drawee.view.SimpleDraweeView img4 = null;
    com.facebook.drawee.view.SimpleDraweeView img5 = null;
    com.facebook.drawee.view.SimpleDraweeView img6 = null;


    public S21CategoryListViewAdapter(Context context, ArrayList<MongoCategories> categories, ArrayList<ArrayList<MongoCategories>> items) {

        this.categories = categories;
        this.items = items;
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private class Holder {
        TextView titleName;
        ViewPager viewPager;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
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
        ArrayList<MongoCategories> item = items.get(position);

        if (item != null) {
            String item_name = categories.get(position).getName();
            List<View> mViews = new ArrayList<View>();
            View view1 = mInflater.inflate(
                    R.layout.item_s21_category_viewpager, null);
            img1 = (com.facebook.drawee.view.SimpleDraweeView) view1.findViewById(R.id.item_s21_category_1);
            img2 = (com.facebook.drawee.view.SimpleDraweeView) view1.findViewById(R.id.item_s21_category_2);
            img3 = (com.facebook.drawee.view.SimpleDraweeView) view1.findViewById(R.id.item_s21_category_3);
            tv1 = (TextView) view1
                    .findViewById(R.id.item_s21_category_11);
            tv2 = (TextView) view1
                    .findViewById(R.id.item_s21_category_21);
            tv3 = (TextView) view1
                    .findViewById(R.id.item_s21_category_31);
            View view2 = mInflater.inflate(
                    R.layout.item_s21_category_viewpager, null);
            img4 = (com.facebook.drawee.view.SimpleDraweeView) view2.findViewById(R.id.item_s21_category_1);
            img5 = (com.facebook.drawee.view.SimpleDraweeView) view2.findViewById(R.id.item_s21_category_2);
            img6 = (com.facebook.drawee.view.SimpleDraweeView) view2.findViewById(R.id.item_s21_category_3);
            tv4 = (TextView) view2
                    .findViewById(R.id.item_s21_category_11);
            tv5 = (TextView) view2
                    .findViewById(R.id.item_s21_category_21);
            tv6 = (TextView) view2
                    .findViewById(R.id.item_s21_category_31);
            switch (item.size()) {
                case 1:
                    addData(tv1, img1, item, 1, position);
                    img2.setVisibility(View.INVISIBLE);
                    img3.setVisibility(View.INVISIBLE);
                    tv2.setVisibility(View.INVISIBLE);
                    tv3.setVisibility(View.INVISIBLE);
                    mViews.add(view1);
                    break;
                case 2:
                    addData(tv1, img1,item, 1, position);
                    addData(tv2, img2,item, 2, position);
                    img3.setVisibility(View.INVISIBLE);
                    tv3.setVisibility(View.INVISIBLE);
                    mViews.add(view1);
                    break;
                case 3:
                    addData(tv1, img1,item, 1, position);
                    addData(tv2, img2,item, 2, position);
                    addData(tv3, img3,item, 3, position);
                    mViews.add(view1);
                    break;
                case 4:
                    addData(tv1, img1,item, 1, position);
                    addData(tv2, img2,item, 2, position);
                    addData(tv3, img3,item, 3, position);
                    addData(tv4, img4,item, 4, position);
                    img5.setVisibility(View.INVISIBLE);
                    img6.setVisibility(View.INVISIBLE);
                    tv5.setVisibility(View.INVISIBLE);
                    tv6.setVisibility(View.INVISIBLE);
                    mViews.add(view1);
                    mViews.add(view2);
                    break;
                case 5:
                    addData(tv1, img1,item, 1, position);
                    addData(tv2, img2,item, 2, position);
                    addData(tv3, img3,item, 3, position);
                    addData(tv4, img4,item, 4, position);
                    addData(tv5, img5,item, 5, position);
                    img6.setVisibility(View.INVISIBLE);
                    tv6.setVisibility(View.INVISIBLE);
                    mViews.add(view1);
                    mViews.add(view2);
                    break;
                case 6:
                    addData(tv1, img1,item, 1, position);
                    addData(tv2, img2,item, 2, position);
                    addData(tv3, img3,item, 3, position);
                    addData(tv4, img4,item, 4, position);
                    addData(tv5, img5,item, 5, position);
                    addData(tv6, img6,item, 6, position);
                    mViews.add(view1);
                    mViews.add(view2);
                    break;

                default:
                    break;
            }


//            switch ((int) tempMem[position]) {
//                case 1:
//                    memory(tv1);
//                    tempReset[position] = tv1;
//                    break;
//                case 2:
//                    memory(tv2);
//                    tempReset[position] = tv2;
//                    break;
//                case 3:
//                    memory(tv3);
//                    tempReset[position] = tv3;
//                    break;
//                case 4:
//                    memory(tv4);
//                    tempReset[position] = tv4;
//                    break;
//                case 5:
//                    memory(tv5);
//                    tempReset[position] = tv5;
//                    break;
//                case 6:
//                    memory(tv6);
//                    tempReset[position] = tv6;
//                    break;
//
//                default:
//                    break;
//            }
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

    public void addData(TextView view,com.facebook.drawee.view.SimpleDraweeView img, ArrayList<MongoCategories> item, int index, int position) {
        view.setText(item.get(index - 1).getName());
        img.setImageURI(Uri.parse(item.get(index - 1).getIcon()));
        img.setTag(index);
//        img.setOnClickListener(new CategoryListener(position));
    }

    private void memory(TextView tv) {
        tv.setTextColor(context.getResources().getColor(R.color.s21_pink));
//        Drawable pic = context.getResources().getDrawable(R.drawable.bg_photpchoose_eg02);
//        pic.setBounds(0, 0, pic.getMinimumWidth(), pic.getMinimumHeight());
//        tv.setCompoundDrawables(null, pic, null, null);
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
