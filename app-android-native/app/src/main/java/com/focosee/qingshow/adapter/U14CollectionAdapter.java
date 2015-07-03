package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
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
import com.focosee.qingshow.activity.S03SHowActivity;
import com.focosee.qingshow.activity.S10ItemDetailActivity;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 华榕 on 2015/5/23.
 */


public class U14CollectionAdapter extends AbsAdapter<List<MongoShow>> {

    private LinkedList<MongoShow> datas;

    public U14CollectionAdapter(List datas, Context context, int... layoutId){
        super(datas, context, layoutId);
    }

    public void refreshDatas(LinkedList<MongoShow> datas){
        this.datas = datas;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {

        final MongoShow show = datas.get(position);

        SimpleDraweeView modelImage = holder.getView(R.id.item_u14_modelImage);
        TextView describe = holder.getView(R.id.item_u14_describe);

        RelativeLayout[] products = new RelativeLayout[4];

        products[0] = holder.getView(R.id.u14_product);
        products[1] = holder.getView(R.id.u14_product1);
        products[2] = holder.getView(R.id.u14_product2);
        products[3] = holder.getView(R.id.u14_product3);

        SimpleDraweeView[] imags = new SimpleDraweeView[4];

        imags[0] = holder.getView(R.id.u14_product_image);
        imags[1] = holder.getView(R.id.u14_product_image1);
        imags[2] = holder.getView(R.id.u14_product_image2);
        imags[3] = holder.getView(R.id.u14_product_image3);

        TextView[] prices = new TextView[4];
        prices[0] = holder.getView(R.id.u14_product_price);
        prices[1] = holder.getView(R.id.u14_product_price1);
        prices[2] = holder.getView(R.id.u14_product_price2);
        prices[3] = holder.getView(R.id.u14_product_price3);

        int i = 0;
        if(null != show.itemRefs) {
            for (final MongoItem item : show.itemRefs) {
                products[i].setVisibility(View.VISIBLE);
                products[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, S10ItemDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(S10ItemDetailActivity.INPUT_ITEM_ENTITY, item);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                });
                imags[i].setImageURI(Uri.parse(ImgUtil.getImgSrc(item.images.get(0).url,-1)));
                prices[i].setText(item.getSourcePrice());
                i++;
            }
        }

        modelImage.setImageURI(Uri.parse(ImgUtil.getImgSrc(show.cover,0)));
        modelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, S03SHowActivity.class);
                intent.putExtra(S03SHowActivity.INPUT_SHOW_ENTITY_ID, show._id);
                context.startActivity(intent);
            }
        });
        describe.setText(show.description);

    }

    @Override
    public int getItemCount() {
        return null == datas ? 0 : this.datas.size();
    }

    @Override
    public int getItemViewType(int position) {
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
//            if(parent.getChildPosition(view) == 0)
//                outRect.top = space;
        }
    }




}
