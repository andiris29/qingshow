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
import com.focosee.qingshow.activity.P02ModelActivity;
import com.focosee.qingshow.constants.code.PeopleTypeInU01PersonalActivity;
import com.focosee.qingshow.constants.code.RolesCode;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.widget.MCircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class P02ModelFollowPeopleListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MongoPeople> data;

    public ArrayList<MongoPeople> getData() {
        return data;
    }

    private P02ModelActivity p02ModelActivity;
    private int type = PeopleTypeInU01PersonalActivity.NOREMOVEITEM.getIndex();

    public P02ModelFollowPeopleListAdapter(Context context, ArrayList<MongoPeople> data) {
        this.context = context;
        this.data = data;
    }

    public void setP02ModelActivity(P02ModelActivity p02ModelActivity) {
        this.p02ModelActivity = p02ModelActivity;
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
        HolderView holderView;

        if (null == convertView) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.item_p02_follow_people_list, null);
            holderView = new HolderView();

            holderView.imageView = (MCircularImageView) convertView.findViewById(R.id.item_p02_follow_people_image);
            holderView.fansFollowedImageView = (ImageView) convertView.findViewById(R.id.fans_followed);
            holderView.nameTextView = (TextView) convertView.findViewById(R.id.item_p02_follow_people_name);
            holderView.showNumberTextView = (TextView) convertView.findViewById(R.id.item_p02_follow_people_show);
            holderView.likedNumberTextView = (TextView) convertView.findViewById(R.id.item_p02_follow_people_like);
            holderView.followButton = (Button) convertView.findViewById(R.id.item_p02_model_follow);

            convertView.setTag(holderView);
        } else {
            holderView = (HolderView)convertView.getTag();
        }

        ImageLoader.getInstance().displayImage(data.get(position).getPeoplePortrait(), holderView.imageView, AppUtil.getPortraitDisplayOptions());
        holderView.nameTextView.setText(data.get(position).getPeopleName());
        holderView.showNumberTextView.setText(String.valueOf(data.get(position).get__context().numFollowBrands));
        holderView.likedNumberTextView.setText(String.valueOf(data.get(position).get__context().numFollowPeoples));
        int[] roles = data.get(position).getRoles();
        for(int role : roles){
            if(role == RolesCode.MODEL.getIndex()) {
                holderView.followButton.setVisibility(View.VISIBLE);
                holderView.fansFollowedImageView.setBackgroundResource(R.drawable.model_cell_icon01_cloth);
            }
        }
        if (this.data.get(position).getModelIsFollowedByCurrentUser()) {
            holderView.followButton.setBackgroundResource(R.drawable.people_list_unfollow);
        } else {
            holderView.followButton.setBackgroundResource(R.drawable.people_list_follow);
        }
        holderView.followButton.setOnClickListener(new FollowButtonOnClickListener());


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
                        v.setBackgroundResource(R.drawable.people_list_unfollow);
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
                        if (type == (PeopleTypeInU01PersonalActivity.MYSELF.getIndex()) && null != p02ModelActivity) {
                            data.remove(position);
                            notifyDataSetChanged();
                            p02ModelActivity.refreshWatchNum();
                        } else
                            v.setBackgroundResource(R.drawable.people_list_follow);
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
    class HolderView {
        public MCircularImageView imageView;
        public ImageView fansFollowedImageView;
        public TextView nameTextView;
        public TextView showNumberTextView;
        public TextView likedNumberTextView;
        public Button followButton;
    }
}
