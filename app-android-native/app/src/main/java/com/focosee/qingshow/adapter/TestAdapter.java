//package com.focosee.qingshow.adapter;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.focosee.qingshow.R;
//import com.focosee.qingshow.entity.AbsEntity;
//import com.focosee.qingshow.entity.ShowEntity;
//import com.nostra13.universalimageloader.core.ImageLoader;
//
//import java.util.LinkedList;
//
///**
// * Created by jackyu on 11/23/14.
// */
//public class TestAdapterextends extends BaseAdapter {
//
//    protected LinkedList<AbsEntity> _data;
//
//    protected Context _context;
//    protected int _resourceId;
//    protected ImageLoader _mImageFetcher;
//
//
//    public TestAdapterextends(Context context, int resourceId, ImageLoader mImageFetcher) {
//        _context = context;
//        _resourceId = resourceId;
//        _mImageFetcher = mImageFetcher;
//        _data = new LinkedList<AbsEntity>();
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//
//        if (position == 0) {
//            LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
//            convertView = layoutInflator.inflate(R.layout.item_refresh_independent, null);
//            return convertView;
//        }
//        position--;
//
//        HomeViewHolder holder;
//        ShowEntity showInfo = (ShowEntity) _data.get(position);
//
//        if (convertView == null) {
//            LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
//            convertView = layoutInflator.inflate(_resourceId, null);
//            holder = new HomeViewHolder();
//            holder.showIV = (ImageView) convertView.findViewById(R.id.item_show_image);
//            holder.modelIV = (ImageView) convertView.findViewById(R.id.item_show_model_image);
//            holder.modelNameTV = (TextView) convertView.findViewById(R.id.item_show_model_name);
//            holder.modelJobTV = (TextView) convertView.findViewById(R.id.item_show_model_job);
//            holder.modelHeightTV = (TextView) convertView.findViewById(R.id.item_show_model_height);
//            holder.modelWeightTV = (TextView) convertView.findViewById(R.id.item_show_model_weight);
//            holder.loveTV = (TextView) convertView.findViewById(R.id.item_show_love);
//            holder.modelStatusTV = (TextView) convertView.findViewById(R.id.item_show_model_status);
//            convertView.setTag(holder);
//        }
//        holder = (HomeViewHolder) convertView.getTag();
//
//        String _height = showInfo.getCoverHeight();
//        if (null != _height) {
//            int __height = Integer.valueOf(showInfo.getCoverHeight());
//            Log.i("error", "height:::::" + _height + "; int height:：：：" + String.valueOf(__height));
//            LinearLayout.LayoutParams _layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, __height);
//            holder.showIV.setLayoutParams(_layout);
//        }
//
////        _mImageFetcher.displayImage(showInfo.getShowCover(), holder.showIV);
////        _mImageFetcher.displayImage(showInfo.getModelImgSrc(), holder.modelIV);
////        holder.modelNameTV.setText(showInfo.getModelName());
////        holder.modelJobTV.setText(showInfo.getModelJob());
////        holder.modelHeightTV.setText(showInfo.getModelHeight());
////        holder.modelWeightTV.setText(showInfo.getModelWeight());
////        holder.loveTV.setText(showInfo.getShowNumLike());
////        holder.modelStatusTV.setText(showInfo.getModelStatus());
//
//        return convertView;
//    }
//
//    @Override
//    public int getCount() {
//        return _data.size();
//    }
//
//    @Override
//    public Object getItem(int arg0) {
//        return _data.get(arg0);
//    }
//
//    @Override
//    public long getItemId(int arg0) {
//        return arg0;
//    }
//
//
//    @Override
//    public int getCount() {
////        return (super.getCount() == 0) ? super.getCount() : super.getCount()+ 1; // one more to show refresh status;
//        return super.getCount();
//    }
//
//    public void addItemLast(LinkedList<ShowEntity> datas) {
//        _data.addAll(datas);
//    }
//
//    public void addItemTop(LinkedList<ShowEntity> datas) {
//        _data.clear();
//        _data.addAll(datas);
//    }
//
//}
