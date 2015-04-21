package com.focosee.qingshow.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.LinkedList;


/**
 * Created by Administrator on 2015/3/27.
 */
public class S13VideoAdapter extends RecyclerView.Adapter<S13VideoAdapter.S13ViewHodler> {

    private LinkedList list;


    @Override
    public S13ViewHodler onCreateViewHolder(ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(S13ViewHodler s13ViewHodler, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    class S13ViewHodler extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public S13ViewHodler(View itemView) {
            super(itemView);
        }
    }
}
