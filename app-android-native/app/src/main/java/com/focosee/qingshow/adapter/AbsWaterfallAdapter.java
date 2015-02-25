package com.focosee.qingshow.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.LinkedList;

abstract class AbsViewHolder {
}

public abstract class AbsWaterfallAdapter<T> extends BaseAdapter {

    protected LinkedList<T> _data;

    protected Context _context;
    protected int _resourceId;
    protected ImageLoader _mImageFetcher;


    public AbsWaterfallAdapter(Context context, int resourceId, ImageLoader mImageFetcher) {
        _context = context;
        _resourceId = resourceId;
        _mImageFetcher = mImageFetcher;
        _data = new LinkedList<T>();
    }

    public T getItemDataAtIndex(int index) {
        if (index >= _data.size()) return null;
        return _data.get(index);
    }

    public void addItemLast(LinkedList<T> datas) {
        _data.addAll(datas);
    }

    public void addItemTop(LinkedList datas) {
        _data.clear();
        _data.addAll(datas);
    }

    public abstract void refreshDate(JSONObject response);

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
