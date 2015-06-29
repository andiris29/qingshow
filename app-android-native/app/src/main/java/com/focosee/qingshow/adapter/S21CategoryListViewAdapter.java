package com.focosee.qingshow.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoCategories;
import com.focosee.qingshow.util.ImgUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/6/17.
 */
public class S21CategoryListViewAdapter extends BaseAdapter {
    private ArrayList<MongoCategories> categories;
    private ArrayList<ArrayList<MongoCategories>> items;
    private LayoutInflater mInflater;
    private Context context;
    private Holder holder;
    private TextView[] tempTv;
    private SimpleDraweeView[] tempImg;
    private String[] tempUri;
    private int[] tempMemory;
    private boolean[] tempPage;
    private TextView tv1 = null;
    private TextView tv2 = null;
    private TextView tv3 = null;
    private TextView tv4 = null;
    private TextView tv5 = null;
    private TextView tv6 = null;
    private SimpleDraweeView img1 = null;
    private SimpleDraweeView img2 = null;
    private SimpleDraweeView img3 = null;
    private SimpleDraweeView img4 = null;
    private SimpleDraweeView img5 = null;
    private SimpleDraweeView img6 = null;

    public S21CategoryListViewAdapter(Context context, ArrayList<MongoCategories> categories, ArrayList<ArrayList<MongoCategories>> items) {
        this.categories = categories;
        this.items = items;
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int lines = categories.size();
        tempTv = new TextView[lines];
        tempImg = new SimpleDraweeView[lines];
        tempUri = new String[lines];
        tempMemory = new int[lines];
        tempPage = new boolean[lines];
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
            img1 = (SimpleDraweeView) view1.findViewById(R.id.item_s21_category_1);
            img2 = (SimpleDraweeView) view1.findViewById(R.id.item_s21_category_2);
            img3 = (SimpleDraweeView) view1.findViewById(R.id.item_s21_category_3);
            tv1 = (TextView) view1
                    .findViewById(R.id.item_s21_category_11);
            tv2 = (TextView) view1
                    .findViewById(R.id.item_s21_category_21);
            tv3 = (TextView) view1
                    .findViewById(R.id.item_s21_category_31);
            View view2 = mInflater.inflate(
                    R.layout.item_s21_category_viewpager, null);
            img4 = (SimpleDraweeView) view2.findViewById(R.id.item_s21_category_1);
            img5 = (SimpleDraweeView) view2.findViewById(R.id.item_s21_category_2);
            img6 = (SimpleDraweeView) view2.findViewById(R.id.item_s21_category_3);
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
                    addData(tv1, img1, item, 1, position);
                    addData(tv2, img2, item, 2, position);
                    img3.setVisibility(View.INVISIBLE);
                    tv3.setVisibility(View.INVISIBLE);
                    mViews.add(view1);
                    break;
                case 3:
                    addData(tv1, img1, item, 1, position);
                    addData(tv2, img2, item, 2, position);
                    addData(tv3, img3, item, 3, position);
                    mViews.add(view1);
                    break;
                case 4:
                    addData(tv1, img1, item, 1, position);
                    addData(tv2, img2, item, 2, position);
                    addData(tv3, img3, item, 3, position);
                    addData(tv4, img4, item, 4, position);
                    img5.setVisibility(View.INVISIBLE);
                    img6.setVisibility(View.INVISIBLE);
                    tv5.setVisibility(View.INVISIBLE);
                    tv6.setVisibility(View.INVISIBLE);
                    mViews.add(view1);
                    mViews.add(view2);
                    break;
                case 5:
                    addData(tv1, img1, item, 1, position);
                    addData(tv2, img2, item, 2, position);
                    addData(tv3, img3, item, 3, position);
                    addData(tv4, img4, item, 4, position);
                    addData(tv5, img5, item, 5, position);
                    img6.setVisibility(View.INVISIBLE);
                    tv6.setVisibility(View.INVISIBLE);
                    mViews.add(view1);
                    mViews.add(view2);
                    break;
                case 6:
                    addData(tv1, img1, item, 1, position);
                    addData(tv2, img2, item, 2, position);
                    addData(tv3, img3, item, 3, position);
                    addData(tv4, img4, item, 4, position);
                    addData(tv5, img5, item, 5, position);
                    addData(tv6, img6, item, 6, position);
                    mViews.add(view1);
                    mViews.add(view2);
                    break;

                default:
                    break;
            }


            switch ((int) tempMemory[position]) {
                case 1:
                    memory(tv1, img1, ImgUtil.changeImgUri(tempUri[position]));
                    tempTv[position] = tv1;
                    tempImg[position] = img1;
                    break;
                case 2:
                    memory(tv2, img2, ImgUtil.changeImgUri(tempUri[position]));
                    tempTv[position] = tv2;
                    tempImg[position] = img2;
                    break;
                case 3:
                    memory(tv3, img3, ImgUtil.changeImgUri(tempUri[position]));
                    tempTv[position] = tv3;
                    tempImg[position] = img3;
                    break;
                case 4:
                    memory(tv4, img4, ImgUtil.changeImgUri(tempUri[position]));
                    tempTv[position] = tv4;
                    tempImg[position] = img4;
                    break;
                case 5:
                    memory(tv5, img5, ImgUtil.changeImgUri(tempUri[position]));
                    tempTv[position] = tv5;
                    tempImg[position] = img5;
                    break;
                case 6:
                    memory(tv6, img6, ImgUtil.changeImgUri(tempUri[position]));
                    tempTv[position] = tv6;
                    tempImg[position] = img6;
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

    private void addData(TextView view, SimpleDraweeView img, ArrayList<MongoCategories> item, int index, int position) {
        view.setText(item.get(index - 1).getName());
        img.setImageURI(Uri.parse(item.get(index - 1).getIcon()));
        img.setTag(index);
        img.setOnClickListener(new CategoryListener(position, view, item.get(index - 1).getIcon()));
    }


    private void memory(TextView tv, SimpleDraweeView img, Uri uri) {
        tv.setTextColor(context.getResources().getColor(R.color.s21_pink));
        img.setImageURI(uri);
    }


    class CategoryListener implements View.OnClickListener {
        private int position;
        private TextView tv;
        private String uri;

        public CategoryListener(int position, TextView tv, String uri) {
            this.position = position;
            this.tv = tv;
            this.uri = uri;
        }

        @Override
        public void onClick(View v) {
            SimpleDraweeView img = (SimpleDraweeView) v;
            int index = (Integer) img.getTag();
            tempMemory[position] = index;
            if (index > 3) {
                tempPage[position] = true;
            } else {
                tempPage[position] = false;
            }
            tv.setTextColor(context.getResources().getColor(R.color.s21_pink));
            img.setImageURI(ImgUtil.changeImgUri(uri));
            if (tempTv[position] == null) {
                tempTv[position] = tv;
                tempImg[position] = img;
                tempUri[position] = uri;

            } else {

                reset(tempTv[position], tempImg[position], tempUri[position]);
                if (uri != tempUri[position]) {
                    tempTv[position] = tv;
                    tempImg[position] = img;
                    tempUri[position] = uri;
                } else {
                    tempMemory[position] = 0;
                    tempTv[position] = null;
                    tempImg[position] = null;
                    tempUri[position] = null;
                }
            }

        }

        private void reset(TextView tv, SimpleDraweeView img, String uri) {
            tv.setTextColor(context.getResources().getColor(R.color.black));
            img.setImageURI(Uri.parse(uri));

        }

    }

}
