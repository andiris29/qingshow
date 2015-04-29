package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.U01PersonalActivity;
import com.focosee.qingshow.activity.fragment.U01WatchFragment;
import com.focosee.qingshow.constants.code.PeopleTypeInU01PersonalActivity;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.AppUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/1.
 */
public class ModelListAdapter_HasModelHeadRelativeLayout extends BaseAdapter {

    private ArrayList<MongoPeople> data;
    private ImageLoader imageLoader;
    private Context context;
    private FollowButtonOnClickListener followButtonOnClickListener;
    private U01PersonalActivity u01PersonalActivity;

    public ModelListAdapter_HasModelHeadRelativeLayout(Context context, ArrayList<MongoPeople> data, ImageLoader imageLoader) {
        this.context = context;
        this.data = data;
        this.imageLoader = imageLoader;
        followButtonOnClickListener = new FollowButtonOnClickListener();
    }

    public void setU01PersonActivity(U01PersonalActivity u01PersonalActivity) {
        this.u01PersonalActivity = u01PersonalActivity;
    }

    public MongoPeople getItemData(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return null == data ? 1 : this.data.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return this.data.get(position + 1);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 0 : 1;
    }

    public ArrayList<MongoPeople> getData(){
        return data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(position == 0){
            RelativeLayout headRelativeLayout;
            if(null == convertView){
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                convertView = layoutInflater.inflate(R.layout.activity_p02_model, null);
                headRelativeLayout = (RelativeLayout)convertView.findViewById(R.id.P02_head_relative);
                headRelativeLayout.removeAllViews();
                convertView.setTag(headRelativeLayout);
            }
            return convertView;
        }

        position--;

//        P01ModelHolderView holderView;
//        if (null == convertView) {
//            LayoutInflater inflater = LayoutInflater.from(this.context);
//            convertView = inflater.inflate(R.layout.item_modellist, null);
//            holderView = new P01ModelHolderView();
//            holderView.modelImageView = (ImageView) convertView.findViewById(R.id.item_model_image);
//            holderView.nameTextView = (TextView) convertView.findViewById(R.id.item_model_name);
//            holderView.heightTextView = (TextView) convertView.findViewById(R.id.item_model_height);
//            holderView.weightTextView = (TextView) convertView.findViewById(R.id.item_model_weight);
//            holderView.clothNumberTextView = (TextView) convertView.findViewById(R.id.item_model_cloth_number);
//            holderView.likeNumberTextView = (TextView) convertView.findViewById(R.id.item_model_like_number);
//            holderView.followButton = (Button) convertView.findViewById(R.id.item_model_follow);
//
//            convertView.setTag(holderView);
//        }
//        holderView = (P01ModelHolderView) convertView.getTag();
//
//        this.imageLoader.displayImage(this.data.get(position).getPortrait(), holderView.modelImageView, AppUtil.getPortraitDisplayOptions());
//        holderView.nameTextView.setText(this.data.get(position).getName());
//        holderView.heightTextView.setText(this.data.get(position).getHeight() + this.data.get(position).getWeight());
//        //holderView.weightTextView.setText(this.data.get(position).getWeight());
//        holderView.clothNumberTextView.setText(String.valueOf(this.data.get(position).getNumberShows()));
//        holderView.likeNumberTextView.setText(String.valueOf(this.data.get(position).getNumberFollowers()));
//        holderView.followButton.setTag(String.valueOf(position));
//        int[] roles = data.get(position).getRoles();
//        if (this.data.get(position).getModelIsFollowedByCurrentUser()) {
//            holderView.followButton.setBackgroundResource(R.drawable.people_list_unfollow);
//        } else {
//            holderView.followButton.setBackgroundResource(R.drawable.people_list_follow);
//        }
//        holderView.followButton.setOnClickListener(followButtonOnClickListener);

        return convertView;
    }

    public void resetData(ArrayList<MongoPeople> newData) {
        this.data = newData;
    }

    public void addData(ArrayList<MongoPeople> moreData) {
        this.data.addAll(this.data.size(), moreData);
    }

    public class FollowButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            if (data.get(Integer.valueOf(v.getTag().toString()).intValue()).getModelIsFollowedByCurrentUser()) {
                unFollowModel((Button) v);
            } else {
                followModel((Button) v);
            }
        }
    }

    private void followModel(final Button v) {
        Map<String, String> followData = new HashMap<String, String>();
        followData.put("_id", data.get(Integer.valueOf(v.getTag().toString()).intValue()).get_id());
        JSONObject jsonObject = new JSONObject(followData);

        QSJsonObjectRequest mJsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getPeopleFollowApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (!MetadataParser.hasError(response)) {
                        showMessage(context, "关注成功");
                        data.get(Integer.valueOf(v.getTag().toString()).intValue()).setModelIsFollowedByCurrentUser(true);
                        data.get(Integer.valueOf(v.getTag().toString()).intValue()).get__context().numFollowers =
                                data.get(Integer.valueOf(v.getTag().toString()).intValue()).get__context().numFollowers + 1;
                        v.setBackgroundResource(R.drawable.people_list_unfollow);
                        notifyDataSetChanged();
                    } else {
                        showMessage(context, "关注失败");
                    }
                } catch (Exception e) {
                    showMessage(context, e.toString());
                }
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(mJsonObjectRequest);
    }

    private void unFollowModel(final Button v) {
        Map<String, String> followData = new HashMap<String, String>();
        followData.put("_id", data.get(Integer.valueOf(v.getTag().toString()).intValue()).get_id());
        JSONObject jsonObject = new JSONObject(followData);

        QSJsonObjectRequest mJsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getPeopleUnfollowApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (!MetadataParser.hasError(response)) {
                        showMessage(context, "取消关注成功");
                        int position = Integer.valueOf(v.getTag().toString()).intValue();
                        data.get(position).setModelIsFollowedByCurrentUser(false);
                    } else {
                        showMessage(context, "取消关注失败");
                    }
                } catch (Exception e) {
                    showMessage(context, e.toString());
                }
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(mJsonObjectRequest);
    }

    private void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        Log.i(context.getPackageName(), message);
    }
}
