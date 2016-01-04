package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S03SHowActivity;
import com.focosee.qingshow.activity.S24ShowsDateActivity;
import com.focosee.qingshow.activity.S25ShowHrefActivity;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.aggregation.FeedingAggregationLatest;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.model.vo.mongo.MongoStickyShow;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.adapter.AbsAdapter;
import com.focosee.qingshow.util.adapter.AbsViewHolder;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Administrator on 2015/11/27.
 */
public class S01MatchNewAdapter extends AbsAdapter<FeedingAggregationLatest>{

    private GregorianCalendar calendar;
    private  MongoStickyShow info = null;;
    public S01MatchNewAdapter(@NonNull List<FeedingAggregationLatest> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
        calendar = new GregorianCalendar();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
       FeedingAggregationLatest data = datas.get(position);
        int[] imgId = new int[]{R.id.item_new_img1, R.id.item_new_img2, R.id.item_new_img3};
        int[] pimgId= new int[]{R.id.item_new_pg_img1,R.id.item_new_pg_img2,R.id.item_new_pg_img3};
        int[] layoutId = new int[]{R.id.layout1,R.id.layout2,R.id.layout3};
        List<MongoShow> topShows = data.topShows;
        Object stickyShow = data.stickyShow;

        for (int id : imgId) {
            holder.setVisibility(id, View.INVISIBLE);  
        }

        for (int id : pimgId) {
            holder.setVisibility(id, View.INVISIBLE);
        }

        for (int id : layoutId) {
                holder.setVisibility(id, View.INVISIBLE);
        }

        for (int i = 0; i < topShows.size(); i++) {
            if (i > 3) break;
          holder.setImgeByUrl(imgId[i], ImgUtil.getImgSrc(topShows.get(i).cover, ImgUtil.Meduim)).setVisibility(imgId[i], View.VISIBLE);
            holder.setImgeByUrl(pimgId[i], ImgUtil.getImgSrc(topShows.get(i).coverForeground, ImgUtil.Meduim)).setVisibility(pimgId[i], View.VISIBLE);
            holder.setVisibility(layoutId[i],View.VISIBLE);
        }
        if(!"null".equals(stickyShow) && null != stickyShow  && "null" != stickyShow){
            Gson gson = new Gson();
             String json = gson.toJson(stickyShow);
          //  Log.e("test_i", "stickyShow.toString() --> " + json);
            info  = gson.fromJson(json , MongoStickyShow.class);
            if(info != null && !TextUtils.isEmpty(info.stickyCover)){
                holder.setVisibility(R.id.iv_item_matchnew_s03, View.VISIBLE);
               holder.setImgeByUrl(R.id.iv_item_matchnew_s03,info.stickyCover,1.86f);

            } else{
                holder.setVisibility(R.id.iv_item_matchnew_s03,View.GONE);
            }
        }else {
            holder.setVisibility(R.id.iv_item_matchnew_s03,View.GONE);
        }

        final int key = Integer.parseInt(data.key);
        final int from;

        String timeTemplate = "S:00~E:00";

        if (key < 0){
            from = 24 + key;
            holder.setText(R.id.day,  (calendar.get(Calendar.DAY_OF_MONTH) - 1) + "." +  TimeUtil.formatManthInfo(calendar.get(Calendar.MONTH)));

        }else {
            from = key;
            holder.setText(R.id.day,  "TODAY | " + calendar.get(Calendar.DAY_OF_MONTH) + "." +  TimeUtil.formatManthInfo(calendar.get(Calendar.MONTH)));
        }
        final int to = from + 1;

        holder.setText(R.id.time, timeTemplate.replace("S", from + "").replace("E", to + ""));
        if(QSModel.INSTANCE.getUser() != null){
            if (data.numViewOfCurrentUser >= 0){
                holder.setImgeByUrl(R.id.current_head, ImgUtil.getImgSrc(QSModel.INSTANCE.getUser().portrait,"50"));
                holder.setText(R.id.rank,data.numViewOfCurrentUser + "");
            }else{
                holder.setVisibility(R.id.current_head, View.GONE);
                holder.setVisibility(R.id.rank, View.GONE);
            }
        }else {
            holder.setVisibility(R.id.current_head, View.GONE);
            holder.setVisibility(R.id.rank, View.GONE);
        }

        // top owner
        holder.setText(R.id.num_owners, data.numOwners + "");
        RecyclerView portraits = holder.getView(R.id.top_owner);
        portraits.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        portraits.setAdapter(new TopOwnerAdapter(data.topOwners, context, R.layout.item_portrait));
        //jump
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()){
                    case R.id.iv_item_matchnew_s03:
                        if(info != null ){
                            if(!TextUtils.isEmpty(info.href)){
                                Intent intent = new Intent(context , S25ShowHrefActivity.class);
                                intent.putExtra("url" , info.href);
                               context.startActivity(intent);
                            }else {
//                                Intent intent = new Intent(context , S03SHowActivity.class);
//                                intent.putExtra("url" , info.href);
//                                context.startActivity(intent);
                            }
                        }
                        break;
                    default:
                        Intent intent = new Intent(context, S24ShowsDateActivity.class);
                        if (key > 0) {

                            intent.putExtra("MATCH_NEW_FROM", new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), from, 0));
                            intent.putExtra("MATCH_NEW_TO", new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), to, 0));
                        } else {
                            intent.putExtra("MATCH_NEW_FROM", new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) - 1, from, 0));
                            intent.putExtra("MATCH_NEW_TO", new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) - 1, to, 0));
                        }
                        context.startActivity(intent);
                        break;
                }


            }
        });
    }
}
