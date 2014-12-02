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

class CommentViewHolder {
    public ImageView commentImage;
    public TextView commentName;
    public TextView commentContent;
    public TextView commentTime;
}

public class S04CommentListAdapter extends BaseAdapter {

    private CommentEntity.Data.ShowComments[] data;
    private Context context;
    private ImageLoader imageLoader;

    public S04CommentListAdapter(Context context, CommentEntity.Data.ShowComments[] comments, ImageLoader imageLoader){
        this.context = context;
        this.data = comments;
        this.imageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
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
//        this.imageLoader.displayImage(data[position].authorRef);
        return null;
    }
}
