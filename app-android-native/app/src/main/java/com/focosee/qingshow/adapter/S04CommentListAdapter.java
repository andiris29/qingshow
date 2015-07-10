package com.focosee.qingshow.adapter;

import android.content.Context;
import android.view.View;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S04CommentActivity;
import com.focosee.qingshow.model.vo.mongo.MongoComment;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;

import java.util.LinkedList;

public class S04CommentListAdapter extends AbsAdapter<MongoComment> {

    public S04CommentListAdapter(LinkedList datas, Context context, int... layoutId){
        super(datas, context, layoutId);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, final int position) {

        MongoComment comment = getItemData(position);
        holder.setImgeByUrl(R.id.item_comment_user_image, comment.getAuthorImage());
        holder.setText(R.id.item_comment_user_name, comment.getAuthorName());
        holder.setText(R.id.item_comment_content, comment.getCommentContent());

        holder.setText(R.id.item_comment_time, TimeUtil.formatDateTime_CN(comment.create));

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.setTheme(R.style.ActionSheetStyleIOS7);
                if(context instanceof S04CommentActivity)
                    ((S04CommentActivity)context).showActionSheet(position);
            }
        });
    }

}
