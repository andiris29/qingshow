package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S04CommentActivity;
import com.focosee.qingshow.entity.TrendEntity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

public class S08TrendListAdapter extends BaseAdapter {

    private Context context;
    private LinkedList<TrendEntity> data;

    public S08TrendListAdapter(Context context, LinkedList<TrendEntity> trendEntities) {
        this.context = context;
        this.data = trendEntities;
    }

    @Override
    public int getCount() {

        return (null != this.data) ? this.data.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return (null != this.data) ? this.data.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void addItemTop(LinkedList<TrendEntity> datas){this.data.addAll(datas);}
    public void addItemLast(LinkedList<TrendEntity> datas){this.data.addAll(datas);}

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        HolderView holderView;

        if (null == convertView) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.item_s08_trend_list, null);

            holderView = new HolderView();
            holderView.backImageView = (ImageView) convertView.findViewById(R.id.S08_background_image_view);
            holderView.nameTextView = (TextView) convertView.findViewById(R.id.S08_item_name);
            holderView.descriptionTextView = (TextView) convertView.findViewById(R.id.S08_item_description);
            holderView.priceTextView = (TextView) convertView.findViewById(R.id.S08_item_price);

            holderView.shareImageButton = (ImageButton) convertView.findViewById(R.id.S08_item_share_btn);
            holderView.likeImageButton = (ImageButton) convertView.findViewById(R.id.S08_item_like_btn);
            holderView.likeTextView = (TextView) convertView.findViewById(R.id.S08_item_like_text_view);
            holderView.messageImageButton = (ImageButton) convertView.findViewById(R.id.S08_item_comment_btn);
            holderView.messageTextView = (TextView) convertView.findViewById(R.id.S08_item_comment_text_view);

            convertView.setTag(holderView);
        } else {
            holderView = (HolderView)convertView.getTag();
        }
        holderView.backImageView.setMinimumHeight(data.get(position).getHeight());
        ImageLoader.getInstance().displayImage(data.get(position).getCover(), holderView.backImageView);
        //Toast.makeText(convertView.getContext(),data.get(position).getCover()+"%%%",Toast.LENGTH_LONG).show();
        holderView.nameTextView.setText(data.get(position).getNameDescription());
        holderView.descriptionTextView.setText(data.get(position).getBrandDescription());
        holderView.priceTextView.setText(data.get(position).getPriceDescription());

        holderView.shareImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("image*//*");
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                intent.putExtra(Intent.EXTRA_TEXT, "测试内容!!!");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(Intent.createChooser(intent, context.getPackageName()));
            }
        });
        holderView.messageTextView.setText("444444444444");
        holderView.messageImageButton.setTag(position);
        holderView.messageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mPosition = Integer.valueOf(((Button) v).getTag().toString());
                if (null != data.get(mPosition) && null != data.get(mPosition).get_id()) {
                    Intent intent = new Intent(context, S04CommentActivity.class);
                    intent.putExtra(S04CommentActivity.INPUT_SHOW_ID, data.get(position).get_id());
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Plese NPC!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holderView.likeTextView.setText("55555555555555");
        holderView.likeImageButton.setTag(position);
        holderView.likeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }

    class HolderView {
        public ImageView backImageView;
        public TextView nameTextView;
        public TextView descriptionTextView;
        public TextView priceTextView;

        public ImageButton shareImageButton;
        public ImageButton likeImageButton;
        public TextView likeTextView;
        public ImageButton messageImageButton;
        public TextView messageTextView;
    }

}
