package com.focosee.qingshow.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.focosee.qingshow.R;
import com.focosee.qingshow.model.Cityinfo;
import com.focosee.qingshow.widget.QSTextView;
import java.util.List;

/**
 * Created by Administrator on 2015/3/16.
 */
public class CityListAdapter extends BaseAdapter {

    private List<Cityinfo> datas;
    private Context context;

    public CityListAdapter(@NonNull List<Cityinfo> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return null == datas ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null == datas ? null : datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(null == convertView){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_city_activity, null);
            ViewHold viewHold = new ViewHold(convertView);
            convertView.setTag(viewHold);
        }
        ViewHold viewHold = (ViewHold)convertView.getTag();
        viewHold.textView.setText(datas.get(position).getCity_name());
        return convertView;
    }

    public void setDatas(List<Cityinfo> datas) {
        this.datas = datas;
    }

    public List<Cityinfo> getDatas() {
        return datas;
    }

    class ViewHold {
        public QSTextView textView;
        public ViewHold(View view){
            textView = (QSTextView) view.findViewById(R.id.item_city_text);
        }
    }


}

