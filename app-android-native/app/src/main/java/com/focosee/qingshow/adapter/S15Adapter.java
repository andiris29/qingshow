package com.focosee.qingshow.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.IMongoChosen;
import com.focosee.qingshow.model.vo.mongo.MongoChosenShow;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/3/31.
 */
public class S15Adapter extends AbsWaterfallAdapter<IMongoChosen> {


    public S15Adapter(Context context) {
        this(context,0, null);
    }

    public S15Adapter(Context context, int resourceId, ImageLoader mImageFetcher) {
        super(context, resourceId, mImageFetcher);
    }

    @Override
    public void refreshDate(JSONObject response) {

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        IMongoChosen item = _data.get(position);

        if (null == convertView) {
            convertView = LayoutInflater.from(_context).inflate(R.layout.item_s12_topic, null);
            viewHolder = new ViewHolder();
            viewHolder.image = (SimpleDraweeView) convertView.findViewById(R.id.s12_image);
            viewHolder.title = (TextView) convertView.findViewById(R.id.s12_title);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (item.refCollection.equals("shows")){
            MongoChosenShow itemShow = (MongoChosenShow) item;

            viewHolder.image.setImageURI(Uri.parse(itemShow.ref.cover));
        }

        viewHolder.image.setAspectRatio(1.37f);

//        viewHolder.title.setText(item.title);
//        viewHolder.subtitle.setText(item.subtitle);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(_context, S13TopicActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable(S13TopicActivity.KEY, item);
//                intent.putExtras(bundle);
//                _context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder extends AbsViewHolder {
        public SimpleDraweeView image;
        public TextView title;
        public TextView subtitle;
    }
}
