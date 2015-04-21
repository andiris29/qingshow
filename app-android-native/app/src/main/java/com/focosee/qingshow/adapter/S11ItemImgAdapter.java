package com.focosee.qingshow.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedList;

/**
 * Created by Administrator on 2015/3/16.
 */
public class S11ItemImgAdapter extends RecyclerView.Adapter<S11ItemImgAdapter.ViewHolder> {

    private Context context;
    private LinkedList<MongoItem.TaoBaoInfo.SKU> skus;

    public S11ItemImgAdapter(LinkedList<MongoItem.TaoBaoInfo.SKU> skus) {
        this.skus = skus;
    }

    @Override
    public S11ItemImgAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_s11_details_img, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        ImageLoader.getInstance().displayImage(skus.get(i).properties_thumbnail,viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.s11_details_img);
        }
    }
}

