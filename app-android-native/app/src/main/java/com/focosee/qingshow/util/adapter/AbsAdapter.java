package com.focosee.qingshow.util.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2015/4/23.
 */
public abstract class AbsAdapter<T> extends RecyclerView.Adapter<AbsViewHolder> {

    protected List<T> datas;
    protected Context context;
    protected int[] layoutId;
    protected AbsViewHolder[] viewHolders;

    public AbsAdapter(List<T> datas, Context context, int... layoutId) {
        this.datas = datas;
        this.context = context;
        this.layoutId = layoutId;
        viewHolders = new AbsViewHolder[layoutId.length];
    }

    @Override
    public AbsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        for (int i = 0; i < layoutId.length; i++) {
            viewHolders[i] =  new AbsViewHolder(LayoutInflater.from(context).inflate(layoutId[i],parent,false));
        }
        return viewHolders[viewType];
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public T getItemData(int position){
        return datas.get(position);
    }

    public Context getContext(){
        return context;
    }

    @Override
    public abstract int getItemViewType(int position);

    @Override
    abstract public void onBindViewHolder(AbsViewHolder holder, int position);
}
