package com.focosee.qingshow.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    private TextView[] tempTv;
    private SimpleDraweeView[] tempImg;
    private String[] tempUri;
    private int[] tempMemory;
    private boolean[] tempPage;
    private List<String> selectRefs;

    private int position;

    private OnSelectChangeListener onSelectChangeListener;

    public interface OnSelectChangeListener {
        void onSelectChanged(int[] tempMemory);
    }

    public S21CategoryListViewAdapter(Context context, ArrayList<MongoCategories> categories, ArrayList<ArrayList<MongoCategories>> items, List<String> selcetRefs) {
        this.categories = categories;
        this.items = items;
        this.context = context;
        this.selectRefs = selcetRefs;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int lines = categories.size();
        tempTv = new TextView[lines];
        tempImg = new SimpleDraweeView[lines];
        tempUri = new String[lines];
        tempMemory = new int[lines];
        tempPage = new boolean[lines];

    }


    @Override
    public int getCount() {
        return categories.size();
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
    public View getView(int pos, View convertView, ViewGroup parent) {
        Holder holder;
        position = pos % categories.size();
        if (convertView != null) {
            holder = (Holder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.item_s21_category_listview, null);
            holder = new Holder();
            holder.titleName = (TextView) convertView.findViewById(R.id.item_s21_name);
            holder.viewPager = (ViewPager) convertView.findViewById(R.id.item_s21_middle);
        }
        ArrayList<MongoCategories> item = items.get(position);

        initViewPager(holder, item);

        holder.titleName.setText(categories.get(position).name);

        convertView.setTag(holder);

        return convertView;
    }

    private void initViewPager(Holder holder, List item) {
        ViewPager viewPager = holder.viewPager;
        int pageCount;

        if (item.size() % 3 != 0) {
            pageCount = (new Double(item.size() / 3)).intValue() + 1;
        } else {
            pageCount = (new Double(item.size() / 3)).intValue();
        }

        List<View> views = new ArrayList<>();

        for (int i = 0; i < pageCount; i++) {
            PercentRelativeLayout rootView = (PercentRelativeLayout) mInflater.inflate(R.layout.page_item_s21, null);
            if (i == pageCount - 1 && item.size() % 3 != 0) {
                addToPage(rootView, item, i, item.size() % 3);
            } else {
                addToPage(rootView, item, i, 3);
            }
            views.add(rootView);
        }

        viewPager.setAdapter(new S21CategoryViewPagerAdapter(views));
    }

    private void addToPage(PercentRelativeLayout rootView, List item, int i, int count) {
        for (int j = 0; j < count; j++) {

            LinearLayout itemView = (LinearLayout) mInflater.inflate(R.layout.item_s21_page, rootView, false);
            SimpleDraweeView img = (SimpleDraweeView) itemView.findViewById(R.id.img);
            TextView des = (TextView) itemView.findViewById(R.id.des);

            addData(des, img, item, i * 3 + j);
            PercentRelativeLayout.LayoutParams params = (PercentRelativeLayout.LayoutParams) itemView.getLayoutParams();
            switch (j) {
                case 1:
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    break;
                case 2:
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    break;
            }
            rootView.addView(itemView, params);
        }
    }

    private void addData(TextView tv, SimpleDraweeView img, List item, int index) {
        MongoCategories category = (MongoCategories) item.get(index);
        tv.setText(category.getName());
        img.setImageURI(ImgUtil.changeImgUri(category.getIcon(), ImgUtil.CategoryImgType.NORMAL));
        for (int i = 0; i < selectRefs.size(); i++) {
            if (category._id.equals(selectRefs.get(i))){
                tv.setTextColor(context.getResources().getColor(R.color.s21_pink));
                img.setImageURI(ImgUtil.changeImgUri(category.getIcon(), ImgUtil.CategoryImgType.SELECTED));
            }
        }
        //img.setOnClickListener(new CategoryListener(position, tv, category.getIcon()));
    }

    private void memory(TextView tv, SimpleDraweeView img, int index) {
        tempTv[index] = tv;
        tempImg[index] = img;
    }

    private void selectItem() {

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
            img.setImageURI(ImgUtil.changeImgUri(uri, ImgUtil.CategoryImgType.SELECTED));
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


            if (onSelectChangeListener != null) {
                onSelectChangeListener.onSelectChanged(tempMemory);
            }

        }

        private void reset(TextView tv, SimpleDraweeView img, String uri) {
            tv.setTextColor(context.getResources().getColor(R.color.black));
            img.setImageURI(ImgUtil.changeImgUri(uri, ImgUtil.CategoryImgType.NORMAL));
        }

    }

    public void setOnSelectChangeListener(OnSelectChangeListener onSelectChangeListener) {
        this.onSelectChangeListener = onSelectChangeListener;
    }

    public class Holder {
        public TextView titleName;
        public ViewPager viewPager;
    }

}
