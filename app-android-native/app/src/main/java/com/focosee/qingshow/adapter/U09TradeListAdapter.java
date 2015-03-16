package com.focosee.qingshow.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.focosee.qingshow.R;

/**
 * Created by Administrator on 2015/3/16.
 */
public class U09TradeListAdapter extends RecyclerView.Adapter<U09TradeListAdapter.ViewHolder>{

    private Context context;
    private OnViewHolderListener onViewHolderListener;

    public String[] datas = null;
    public U09TradeListAdapter(String[] datas, Context context) {
        this.datas = datas;
        this.context = context;
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_addreslist,viewGroup,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        //加载更多
        if (i == getItemCount() - 1) onViewHolderListener.onRequestedLastItem();

    }
    //获取数据的数量
    @Override
    public int getItemCount() {
        return 10;
    }

    public void setOnViewHolderListener(OnViewHolderListener onViewHolderListener){
        this.onViewHolderListener = onViewHolderListener;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView chooseBtn;
        public LinearLayout delLayout;
        public LinearLayout editLayout;
        public TextView nameTV;
        public TextView phoneTV;
        public TextView addressTV;
        public ViewHolder(View view){
            super(view);
            chooseBtn = (ImageView) view.findViewById(R.id.item_addresslist_choose_btn);
            delLayout = (LinearLayout) view.findViewById(R.id.item_addresslist_del_layout);
            editLayout = (LinearLayout) view.findViewById(R.id.item_addresslist_edit_layout);
            nameTV = (TextView) view.findViewById(R.id.item_addresslist_name);
            phoneTV = (TextView) view.findViewById(R.id.item_addresslist_phone);
            addressTV = (TextView) view.findViewById(R.id.item_addresslist_address);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public SpacesItemDecoration getItemDecoration(int space){
        return new SpacesItemDecoration(space);
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            outRect.left = space;
//            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if(parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }

    public interface OnViewHolderListener {
        void onRequestedLastItem();
    }

}
