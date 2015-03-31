package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S03SHowActivity;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.model.vo.mongo.MongoTopic;
import com.focosee.qingshow.widget.MImageView_OriginSize;

import java.util.LinkedList;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/3/31.
 */
public class S13TopicAdapter extends RecyclerView.Adapter {

    public static final String ASK_FINISH = "S13_FINISH";
    private Context context;
    private LinkedList<MongoShow> datas;
    private MongoTopic topic;


    public S13TopicAdapter(Context context, MongoTopic topic){
        this.context = context;
        this.topic = topic;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 0 : 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        if(i == 0){
            View view = LayoutInflater.from(context).inflate(R.layout.head_s13_topic, null);
            HeadViewHolder headViewHolder = new HeadViewHolder(view);
            return headViewHolder;
        }

        View view = LayoutInflater.from(context).inflate(R.layout.item_s13_video, null);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

        if(i == 0){
            if(viewHolder instanceof HeadViewHolder){
                HeadViewHolder headViewHolder = (HeadViewHolder)viewHolder;
                headViewHolder.backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(ASK_FINISH);
                    }
                });

                headViewHolder.background.setImageURI(Uri.parse(topic.horizontalCover));
                headViewHolder.background.setAspectRatio(1.3f);

            }
            return;
        }

        final int position = i-1;

        if(viewHolder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder = (ItemViewHolder)viewHolder;
            itemViewHolder.background.setImageURI(Uri.parse(datas.get(position).getHorizontalCover()));
            itemViewHolder.background.setAspectRatio(1.3f);
            itemViewHolder.playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, S03SHowActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(S03SHowActivity.INPUT_SHOW_ENTITY_ID, datas.get(position)._id);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return null == datas ? 1 : datas.size() + 1;
    }

    public void resetDatas(LinkedList<MongoShow> datas){
        this.datas = datas;
    }

    public void addDatas(LinkedList<MongoShow> datas){
        this.datas.addAll(datas);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        public SimpleDraweeView background;
        public ImageView playBtn;

        public ItemViewHolder(View itemView) {
            super(itemView);

            this.background = (SimpleDraweeView) itemView.findViewById(R.id.item_s13_video_image);
            this.playBtn = (ImageView) itemView.findViewById(R.id.item_s13_play_btn);

        }
    }

    public static class HeadViewHolder extends RecyclerView.ViewHolder{

        public SimpleDraweeView background;
        public ImageButton backBtn;

        public HeadViewHolder(View itemView) {
            super(itemView);

            this.background = (SimpleDraweeView) itemView.findViewById(R.id.s13_head_background);
            this.backBtn = (ImageButton) itemView.findViewById(R.id.s13_head_back_btn);

        }
    }



}
