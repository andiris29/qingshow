package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S14FashionMsgActivity;
import com.focosee.qingshow.model.vo.mongo.IMongoChosen;
import com.focosee.qingshow.model.vo.mongo.MongoChosenPreview;
import com.focosee.qingshow.model.vo.mongo.MongoPreview;

import java.util.ArrayList;
import java.util.LinkedList;

public class S08TrendListAdapter extends BaseAdapter {

    public static final String PREVIEWS = "previews";
//    private final String
    public Context context;
    private LinkedList<IMongoChosen> datas;

    public S08TrendListAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return null == datas ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        S08ItemViewHolder viewHolder;
        IMongoChosen item = datas.get(position);

        if(null == convertView){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_s08_trend_list, null);
            viewHolder = new S08ItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        viewHolder = (S08ItemViewHolder)convertView.getTag();

        final IMongoChosen chosen = datas.get(position);

        final Bundle bundle = new Bundle();

        if(chosen.refCollection.equals(PREVIEWS)){

            MongoPreview preview = ((MongoChosenPreview) item).ref;

            viewHolder.imageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, preview.getHeight()));
            viewHolder.imageView.setAspectRatio(preview.getWidth() / preview.getHeight());
            viewHolder.imageView.setImageURI(Uri.parse(preview.imageMetadata.url));

            viewHolder.describe.setText(preview.getDescription(0));

            bundle.putSerializable("entity", preview);

        }

        viewHolder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, S14FashionMsgActivity.class);
                intent.putExtras(bundle);
                intent.putExtra("refCollection", chosen.refCollection);
                context.startActivity(intent);
            }
        });

//        String uri = "";
//        if(chosen.c.equals("") || null == chosen.getCover()){
//            uri = chosen.getHorizontalCover();
//            viewHolder.imageView.setAspectRatio(chosen.getHorizontalCoverWidth() / chosen.getHorizontalCoverHeight());
//        }else{
//            uri = chosen.getCover();
//            viewHolder.imageView.setAspectRatio(chosen.getCoverWidth() / chosen.getCoverHeight());
//        }

//        viewHolder.imageView.setImageURI(Uri.parse(uri));
        //TODO

        return convertView;
    }

    public void resetData(LinkedList<IMongoChosen> datas){
        this.datas = datas;
    }

    public void addItemLast(LinkedList<IMongoChosen> datas){
        this.datas.addAll(datas);
    }


    public static class S08ItemViewHolder {
        public SimpleDraweeView imageView;
        public TextView describe;
        public TextView btn;

        public S08ItemViewHolder(View view){
            imageView = (SimpleDraweeView) view.findViewById(R.id.item_s08_imageView);
            describe = (TextView) view.findViewById(R.id.item_s08_describe);
            btn = (TextView) view.findViewById(R.id.item_s08_btn);
        }
    }
}
