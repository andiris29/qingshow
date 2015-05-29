package com.focosee.qingshow.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.AppUtil;

import java.util.ArrayList;

/**
 * Created by 华榕 on 2015/5/23.
 */

class ViewHolder extends RecyclerView.ViewHolder{

    public SimpleDraweeView modelImage;
    public GridLayout gridLayout;


    public ViewHolder(View itemView) {
        super(itemView);
        modelImage = (SimpleDraweeView) itemView.findViewById(R.id.item_u14_modelImage);
        gridLayout = (GridLayout) itemView.findViewById(R.id.item_u14_grid);
    }
}

public class U14CollectionAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context context;
    private ArrayList<MongoPeople> datas;
    private View gridView;

    public U14CollectionAdapter(Context context){
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_u14_collection_list, null);

        ViewHolder viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    public void refreshDatas(ArrayList<MongoPeople> datas){
        this.datas = datas;
    }

    public void addDatas(ArrayList<MongoPeople> datas){
        this.datas.addAll(datas);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        gridView = LayoutInflater.from(context).inflate(R.layout.u14_product, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(80, 100);
        params.setMargins(5, 0, 0, 0);
        SimpleDraweeView productImage = (SimpleDraweeView) gridView.findViewById(R.id.u14_product_image);
        productImage.setLayoutParams(params);
        TextView price = (TextView) gridView.findViewById(R.id.u14_product_price);
        price.setText(String.valueOf(position + 128));
        holder.gridLayout.setUseDefaultMargins(true);
        holder.gridLayout.setColumnOrderPreserved(true);
        holder.gridLayout.addView(gridView, new ViewGroup.LayoutParams(200,200));

    }

    @Override
    public int getItemCount() {
        return 10;
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
//            if(parent.getChildPosition(view) == 0)
//                outRect.top = space;
        }
    }




}
