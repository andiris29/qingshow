package com.focosee.qingshow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.entity.CommentEntity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

class CommentViewHolder {
    public ImageView commentImage;
    public TextView commentName;
    public TextView commentContent;
    public TextView commentTime;
}

public class S04CommentListAdapter extends BaseAdapter {

    private ArrayList<CommentEntity> data;
    private Context context;
    private ImageLoader imageLoader;

    public S04CommentListAdapter(Context context, ArrayList<CommentEntity> comments, ImageLoader imageLoader){
        this.context = context;
        this.data = comments;
        this.imageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        return (null != data) ? data.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
            convertView = layoutInflator.inflate(R.layout.comment_item_list, null);
            holder = new CommentViewHolder();
            holder.commentImage = (ImageView) convertView.findViewById(R.id.item_comment_user_image);
            holder.commentName = (TextView) convertView.findViewById(R.id.item_comment_user_name);
            holder.commentContent = (TextView) convertView.findViewById(R.id.item_comment_content);
            holder.commentTime = (TextView) convertView.findViewById(R.id.item_comment_time);
            convertView.setTag(holder);
        }
        holder = (CommentViewHolder)convertView.getTag();
        this.imageLoader.displayImage(data.get(position).authorRef.portrait, holder.commentImage);
        holder.commentName.setText(data.get(position).authorRef.name);
        holder.commentContent.setText(data.get(position).comment);
        holder.commentTime.setText(data.get(position).create);
        return null;
    }

    public void resetData(ArrayList<CommentEntity> commentEntities) {
        this.data = commentEntities;
    }

    public void addDataInTail(ArrayList<CommentEntity> commentEntities) {
        this.data.addAll(this.data.size(), commentEntities);
    }
}
