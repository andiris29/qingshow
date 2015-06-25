package com.focosee.qingshow.util.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.util.ImgUtil;

/**
 * Created by Administrator on 2015/4/23.
 */
public class AbsViewHolder extends RecyclerView.ViewHolder{

    private SparseArray<View> views;
    private View itemView;
    private View.OnClickListener onClickListener;

    public AbsViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        views = new SparseArray<View>();
    }

    public <T extends View> T getView(int id){
        View view;
        if(null != views.get(id)){
            view =  views.get(id);
        }else{
            view = itemView.findViewById(id);
            views.put(id, view);
        }
        return (T) view;
    }

    public AbsViewHolder setText(int id,CharSequence charSequence){
        View view;
        TextView textView;
        if(null != (view = getView(id))){
            textView = (TextView) view;
            textView.setText(charSequence);
        }
        return this;
    }

    public AbsViewHolder setImgeByUrl(int id,String url){
        return this.setImgeByUrl(id, url, 0);
    }

    public AbsViewHolder setImgeByUrl(int id,String url,float ratdio){
        View view;
        SimpleDraweeView draweeView;
        if(null != (view = getView(id))){
            draweeView = (SimpleDraweeView) view;
            draweeView.setImageURI(Uri.parse(ImgUtil.getImgSrc(url,-1)));
            if (ratdio != 0){
                draweeView.setAspectRatio(ratdio);
            }
        }
        return this;
    }

    public AbsViewHolder setImgeByRes(int id,int res){
        View view;
        ImageView imageView;
        if(null != (view = getView(id))){
            imageView = (ImageView) view;
            imageView.setImageResource(res);
        }
        return this;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        if (null != onClickListener){
            itemView.setOnClickListener(onClickListener);
        }
    }
}
