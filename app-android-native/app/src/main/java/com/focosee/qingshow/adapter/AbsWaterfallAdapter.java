package com.focosee.qingshow.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedList;

abstract class AbsViewHolder {
}

public abstract class AbsWaterfallAdapter extends BaseAdapter {

    protected LinkedList<Object> _data;

    protected Context _context;
    protected int _resourceId;
    protected ImageLoader _mImageFetcher;


    public AbsWaterfallAdapter(Context context, int resourceId, ImageLoader mImageFetcher) {
        _context = context;
        _resourceId = resourceId;
        _mImageFetcher = mImageFetcher;
        _data = new LinkedList<Object>();
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

    @Override
    public int getCount() {
        return _data.size();
    }

    @Override
    public Object getItem(int arg0) {
        return _data.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

}
