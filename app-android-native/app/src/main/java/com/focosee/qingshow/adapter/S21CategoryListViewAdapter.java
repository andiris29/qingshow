package com.focosee.qingshow.adapter;

import android.content.Context;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoCategories;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.widget.QSTextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/6/17.
 */
public class S21CategoryListViewAdapter extends BaseAdapter {
    private static final String ACC = "5593b3df38dadbed5a998b69";

    private ArrayList<MongoCategories> categories;
    private ArrayList<ArrayList<MongoCategories>> items;
    private LayoutInflater mInflater;
    private Context context;

    private List<String> selectRefs;
    private List<SelectInfo> selectInfos;
    private List<List<ItemViewHolder>> itemViewHolders;

    private int position;
    private int multiSelectPostion;

    private OnSelectChangeListener onSelectChangeListener;

    public interface OnSelectChangeListener {
        void onSelectChanged(List<String> selectRefs);
    }

    public S21CategoryListViewAdapter(Context context, ArrayList<MongoCategories> categories, ArrayList<ArrayList<MongoCategories>> items, List<String> selcetRefs) {
        this.categories = categories;
        this.items = items;
        this.context = context;
        this.selectRefs = selcetRefs;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        selectInfos = new ArrayList<>();
        itemViewHolders = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            itemViewHolders.add(new ArrayList<ItemViewHolder>());
            if (categories.get(i)._id.equals(ACC)) {
                multiSelectPostion = i;
            }
        }
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
        final Holder holder;
        position = pos % categories.size();
        if (convertView != null) {
            holder = (Holder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.item_s21_category_listview, null);
            holder = new Holder();
            holder.titleName = (TextView) convertView.findViewById(R.id.item_s21_name);
            holder.viewPager = (ViewPager) convertView.findViewById(R.id.item_s21_middle);
            holder.last = (FrameLayout) convertView.findViewById(R.id.last);
            holder.next = (FrameLayout) convertView.findViewById(R.id.next);
        }
        ArrayList<MongoCategories> item = items.get(position);
        holder.titleName.setText(categories.get(position).name);
        initViewPager(holder, item);

        convertView.setTag(holder);

        return convertView;
    }

    private void initViewPager(Holder holder, List item) {
        final ViewPager viewPager = holder.viewPager;
        final int pageCount;

        if (selectInfos.size() <= position || selectInfos.get(position) == null) {
            selectInfos.add(position, new SelectInfo());
        }

        if (item.size() % 3 != 0) {
            pageCount = (new Double(item.size() / 3)).intValue() + 1;
        } else {
            pageCount = (new Double(item.size() / 3)).intValue();
        }

        List<View> views = new ArrayList<>();
        final SelectInfo selectInfo = selectInfos.get(position);
        for (int i = 0; i < pageCount; i++) {
            PercentRelativeLayout rootView = (PercentRelativeLayout) mInflater.inflate(R.layout.page_item_s21, null);
            if (i == pageCount - 1 && item.size() % 3 != 0) {
                addToPage(rootView, item, i, item.size() % 3);
            } else {
                addToPage(rootView, item, i, 3);
            }
            views.add(rootView);
        }
        int pageNo = selectInfo.pageNo == -1 ? 0 : selectInfo.pageNo;
        viewPager.setAdapter(new S21CategoryViewPagerAdapter(views));

//        viewPager.setCurrentItem(pageNo);
        viewPager.setCurrentItem(0);
        holder.last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() > 0) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                }
            }
        });

        holder.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() < pageCount - 1) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
            }
        });
    }

    private void addToPage(PercentRelativeLayout rootView, List item, int i, int count) {
        List<ItemViewHolder> itemViewHolder = this.itemViewHolders.get(this.position);
        for (int j = 0; j < count; j++) {

            LinearLayout itemView = (LinearLayout) mInflater.inflate(R.layout.item_s21_page, rootView, false);
            SimpleDraweeView img = (SimpleDraweeView) itemView.findViewById(R.id.img);
            QSTextView des = (QSTextView) itemView.findViewById(R.id.des);

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
            itemViewHolder.add(i * 3 + j, new ItemViewHolder(des, img));
            rootView.addView(itemView, params);
        }
    }

    private void addData(final QSTextView tv, SimpleDraweeView img, final List item, final int index) {
        MongoCategories category = (MongoCategories) item.get(index);
        tv.setText(category.getName());
        final SelectInfo selectInfo = selectInfos.get(position);
        boolean activate;
        if (category.matchInfo == null) {
            activate = true;
        } else {
            activate = category.matchInfo.enabled;
        }
        if (activate == false) {
            img.setImageURI(ImgUtil.changeImgUri(category.getIcon(), ImgUtil.CategoryImgType.DISABLED));
            tv.setTextColor(context.getResources().getColor(R.color.gary));
            img.setClickable(false);
        } else {
            img.setImageURI(ImgUtil.changeImgUri(category.getIcon(), ImgUtil.CategoryImgType.NORMAL));
            img.setOnClickListener(new ItemOnClick(tv, position, category, index));
        }

        for (int i = 0; i < selectRefs.size(); i++) {
            if (category._id.equals(selectRefs.get(i))) {
                selectInfo.index = index;
                selectInfo.pageNo = index / 3;
                selectInfo.id = category._id;
                checkItem(tv, img, category.icon);
            }
        }
        img.setTag(selectInfo);
    }

    private void checkItem(TextView tv, SimpleDraweeView img, String url) {
        tv.setTextColor(context.getResources().getColor(R.color.master_blue));
        img.setImageURI(ImgUtil.changeImgUri(url, ImgUtil.CategoryImgType.SELECTED));
    }

    private void disCheckItem(TextView tv, SimpleDraweeView img, String url) {
        tv.setTextColor(context.getResources().getColor(R.color.black));
        img.setImageURI(ImgUtil.changeImgUri(url, ImgUtil.CategoryImgType.NORMAL));
    }

    public void setOnSelectChangeListener(OnSelectChangeListener onSelectChangeListener) {
        this.onSelectChangeListener = onSelectChangeListener;
    }

    private class SelectInfo {
        public boolean fristIn = true;
        public int index = -1;
        public int pageNo = -1;
        public String id = "";
    }

    public class Holder {
        public TextView titleName;
        public ViewPager viewPager;
        public FrameLayout last;
        public FrameLayout next;
    }

    public class ItemViewHolder {
        public TextView tv;
        public SimpleDraweeView img;

        public ItemViewHolder(TextView tv, SimpleDraweeView img) {
            this.tv = tv;
            this.img = img;
        }
    }

    private class ItemOnClick implements View.OnClickListener {

        private final MongoCategories category;
        private int position;
        private int index;
        private TextView tv;

        private ItemOnClick(TextView tv, int position, MongoCategories category, int index) {
            this.position = position;
            this.category = category;
            this.tv = tv;
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            if (selectRefs.contains(category._id)) {
                disCheckItem(tv, (SimpleDraweeView) v, category.icon);
                selectInfos.get(position).pageNo = -1;
                selectInfos.get(position).index = -1;
                selectInfos.get(position).id = "";
                selectRefs.remove(category._id);
            } else {
                    selectRefs.add(category._id);
                    checkItem(tv, (SimpleDraweeView) v, category.icon);
                    onSelectChangeListener.onSelectChanged(selectRefs);
                    return;
            }
            if (null != onSelectChangeListener)
                onSelectChangeListener.onSelectChanged(selectRefs);
        }
    }
}
