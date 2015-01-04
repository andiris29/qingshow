package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S04CommentActivity;
import com.focosee.qingshow.entity.TrendEntity;

import java.util.LinkedList;

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

    public void addItemTop(LinkedList<TrendEntity> datas) {
        this.data.addAll(datas);
    }

    public void addItemLast(LinkedList<TrendEntity> datas) {
        this.data.addAll(datas);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final HolderView holderView;

        if (null == convertView) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.item_s08_trend_list, parent,false);

            holderView = new HolderView();
            holderView.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.s08_relative_layout);
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
            holderView = (HolderView) convertView.getTag();
        }
        holderView.backImageView.setMinimumHeight(data.get(position).getHeight());
        //Toast.makeText(convertView.getContext(),data.get(position).getHeight()+"&&&",Toast.LENGTH_LONG).show();
        //设置relativeLayout高度
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(holderView.backImageView.getLayoutParams().width
                ,data.get(position).getHeight());
        holderView.relativeLayout.setLayoutParams(params);

        ImageLoader.getInstance().displayImage(data.get(position).getCover(), holderView.backImageView);
        //Toast.makeText(convertView.getContext(),data.get(position).getCover()+"%%%",Toast.LENGTH_LONG).show();
        holderView.nameTextView.setText(data.get(position).getNameDescription());
        holderView.descriptionTextView.setText(data.get(position).getBrandDescription());
        holderView.priceTextView.setText(data.get(position).getPriceDescription());

        holderView.shareImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image*//*");
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                intent.putExtra(Intent.EXTRA_TEXT, "测试内容!!!");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(Intent.createChooser(intent, context.getPackageName()));
            }
        });
        holderView.messageTextView.setText(data.get(position).getNumComments()+"");
        holderView.messageImageButton.setTag(position);
        holderView.messageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mPosition = Integer.valueOf(((ImageButton) v).getTag().toString());
                if (null != data.get(mPosition) && null != data.get(mPosition).get_id()) {
                    Intent intent = new Intent(context, S04CommentActivity.class);
                    intent.putExtra(S04CommentActivity.INPUT_SHOW_ID, data.get(position).get_id());
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Plese NPC!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holderView.likeTextView.setText(data.get(position).numLike+"");
        holderView.likeImageButton.setTag(position);
        holderView.likeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mPosition = Integer.valueOf(((ImageButton) v).getTag().toString());
                if (null != data.get(mPosition) && null != data.get(mPosition).get_id()) {
                    holderView.likeTextView.setText(
                            Integer.parseInt(holderView.likeTextView.getText().toString())+1+"");
                }
            }
        });

        return convertView;
    }

    class HolderView {
        public RelativeLayout relativeLayout;
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
