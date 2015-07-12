package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.U01UserActivity;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.U01Model;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.adapter.AbsViewHolder;
import java.util.List;

/**
 * Created by Administrator on 2015/7/3.
 */
public class U01FollowerFragAdapter extends U01BaseAdapter<MongoPeople>{


    public U01FollowerFragAdapter(@NonNull List<MongoPeople> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    /**
     * 0:R.layout.item_u01_push
     * 1:R.layout.item_u01_fan_and_followers
     * @param position
     * @return
     */

    @Override
    public void onBindViewHolder(final AbsViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(0 == position) return;
        final MongoPeople people = getItemData(position);
        holder.getView(R.id.item_u01_fans_follow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null == people.__context){
                    Toast.makeText(context, "出错，请重试！", Toast.LENGTH_SHORT).show();
                    return;
                }
                String url = people.__context.followedByCurrentUser ? QSAppWebAPI.getPeopleFollowApi() : QSAppWebAPI.getPeopleUnfollowApi();
                UserCommand.likeOrFollow(url, people._id, new Callback(){
                    @Override
                    public void onComplete() {
                        super.onComplete();
                        String msg = "";
                        if(people.__context.followedByCurrentUser){
                            msg = "取消关注";
                            holder.getView(R.id.item_u01_fans_follow).setBackgroundResource(R.drawable.follow_pink);
                        }else{
                            msg = "添加关注";
                            holder.getView(R.id.item_u01_fans_follow).setBackgroundResource(R.drawable.follow);
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(int errorCode) {
                        ErrorHandler.handle(context, errorCode);
                    }
                });
            }
        });
        if(null != people.portrait && !"".equals(people.portrait))
            holder.setImgeByUrl(R.id.item_u01_fans_image, people.portrait);
        holder.setText(R.id.item_u01_fans_name, people.nickname);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                U01Model.INSTANCE.setUser(people);
                context.startActivity(new Intent(context, U01UserActivity.class));
            }
        });
//        holder.setText(R.id.item_u01_fans_cloth_number, people.)
    }

    @Override
    public int getItemCount() {
        return null == datas ? 1 : datas.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return 0 == position ? 0 : position - 1;
    }

    @Override
    public MongoPeople getItemData(int position) {
        return 0 == position ? null : datas.get(position - 1);
    }
}
