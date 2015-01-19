package com.focosee.qingshow.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.ModelEntity;
import com.focosee.qingshow.request.MJsonObjectRequest;
import com.focosee.qingshow.util.AppUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class P01ModelHolderView {
    public ImageView modelImageView;
    public TextView nameTextView;
    public TextView heightTextView;
    public TextView weightTextView;
    public TextView clothNumberTextView;
    public TextView likeNumberTextView;
    public Button followButton;
}

public class P01ModelListAdapter extends BaseAdapter {

    private ArrayList<ModelEntity> data;
    private ImageLoader imageLoader;
    private Context context;
    private FollowButtonOnClickListener followButtonOnClickListener;

    public P01ModelListAdapter(Context context, ArrayList<ModelEntity> data, ImageLoader imageLoader) {
        this.context = context;
        this.data = data;
        this.imageLoader = imageLoader;
        followButtonOnClickListener = new FollowButtonOnClickListener();
    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @Override
    public Object getItem(int position) {
        return this.data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        P01ModelHolderView holderView;
        if (null == convertView) {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.item_modellist, null);
            holderView = new P01ModelHolderView();
            holderView.modelImageView = (ImageView) convertView.findViewById(R.id.item_model_image);
            holderView.nameTextView = (TextView) convertView.findViewById(R.id.item_model_name);
            holderView.heightTextView = (TextView) convertView.findViewById(R.id.item_model_height);
            holderView.weightTextView = (TextView) convertView.findViewById(R.id.item_model_weight);
            holderView.clothNumberTextView = (TextView) convertView.findViewById(R.id.item_model_cloth_number);
            holderView.likeNumberTextView = (TextView) convertView.findViewById(R.id.item_model_like_number);
            holderView.followButton = (Button) convertView.findViewById(R.id.item_model_follow);

            convertView.setTag(holderView);
        }
        holderView = (P01ModelHolderView)convertView.getTag();

        this.imageLoader.displayImage(this.data.get(position).getPortrait(),holderView.modelImageView, AppUtil.getPortraitDisplayOptions());
        holderView.nameTextView.setText(this.data.get(position).getName());
        holderView.heightTextView.setText(this.data.get(position).getHeight()+this.data.get(position).getWeight());
        //holderView.weightTextView.setText(this.data.get(position).getWeight());
        holderView.clothNumberTextView.setText(String.valueOf(this.data.get(position).getNumberShows()));
        holderView.likeNumberTextView.setText(String.valueOf(this.data.get(position).getNumberFollowers()));
        holderView.followButton.setTag(String.valueOf(position));
        if (this.data.get(position).getModelIsFollowedByCurrentUser()) {
            holderView.followButton.setBackgroundResource(R.drawable.badge_unfollow_btn);
            holderView.followButton.setOnClickListener(followButtonOnClickListener);
        }else{
            holderView.followButton.setBackgroundResource(R.drawable.badge_follow_btn);
            holderView.followButton.setOnClickListener(followButtonOnClickListener);
        }

        return convertView;
    }

    public void resetData(ArrayList<ModelEntity> newData) {
        this.data = newData;
    }

    public void addData(ArrayList<ModelEntity> moreData) {
        this.data.addAll(this.data.size(), moreData);
    }

    public class FollowButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            if (data.get(Integer.valueOf(v.getTag().toString()).intValue()).getModelIsFollowedByCurrentUser()) {
                unFollowModel((Button) v);
            }else {
                followModel((Button) v);
            }
        }
    }

    private void followModel(final Button v) {
        Map<String, String> followData = new HashMap<String, String>();
        followData.put("_id", data.get(Integer.valueOf(v.getTag().toString()).intValue()).get_id());
        JSONObject jsonObject = new JSONObject(followData);

        MJsonObjectRequest mJsonObjectRequest = new MJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getPeopleFollowApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.get("metadata").toString().equals("{}")) {
                        showMessage(context, "关注成功");
                        data.get(Integer.valueOf(v.getTag().toString()).intValue()).setModelIsFollowedByCurrentUser(true);
                        v.setBackgroundResource(R.drawable.badge_unfollow_btn);
                    }else{
                        showMessage(context, "关注失败" + response.toString() + response.get("metadata").toString().length());
                    }
                }catch (Exception e) {
                    showMessage(context, e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showMessage(context, error.toString());
            }
        });

        QSApplication.get().QSRequestQueue().add(mJsonObjectRequest);
    }

    private void unFollowModel(final Button v) {
        Map<String, String> followData = new HashMap<String, String>();
        followData.put("_id", data.get(Integer.valueOf(v.getTag().toString()).intValue()).get_id());
        JSONObject jsonObject = new JSONObject(followData);

        MJsonObjectRequest mJsonObjectRequest = new MJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getPeopleUnfollowApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.get("metadata").toString().equals("{}")) {
                        showMessage(context, "取消关注成功");
                        data.get(Integer.valueOf(v.getTag().toString()).intValue()).setModelIsFollowedByCurrentUser(false);
                        v.setBackgroundResource(R.drawable.badge_follow_btn);
                    }else{
                        showMessage(context, "取消关注失败" + response.toString() + response.get("metadata").toString().length());
                    }
                }catch (Exception e) {
                    showMessage(context, e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showMessage(context, error.toString());
            }
        });

        QSApplication.get().QSRequestQueue().add(mJsonObjectRequest);
    }

    private void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        Log.i(context.getPackageName(), message);
    }
}
