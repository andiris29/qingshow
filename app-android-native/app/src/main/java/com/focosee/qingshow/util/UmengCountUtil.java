package com.focosee.qingshow.util;

import android.content.Context;
import com.umeng.analytics.MobclickAgent;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/2/9.
 */
public class UmengCountUtil {


    public static final String COUNTID_PLAY_VIDEO =  "playVideo";
    public static final String COUNTID_SHARE_SHOW = "shareShow";
    public static final String COUNTID_VIEW_ITEM_SOURCE = "viewItemSource";

    public static void countEvent(Context context, String eventId,HashMap<String,String> map){
        MobclickAgent.onEvent(context,eventId,map);
    }

    public static void computeEvnet(Context context, String eventId, Map<String,String> map, int du){
        MobclickAgent.onEventValue(context,eventId,map,du);
    }

    public static void countPlayVideo(Context context,String showId,int length){
        Map<String,String> map = new HashMap<String, String>();
        map.put("showId",showId);
        computeEvnet(context,COUNTID_PLAY_VIDEO,map,length);
    }

    public static void countShareShow(Context context,String snsName){
        HashMap<String,String> map = new HashMap<String, String>();
        map.put("snsName",snsName);
        countEvent(context,COUNTID_SHARE_SHOW,map);

    }

    public static void countViewItemSource(Context context,String itemId){
        HashMap<String,String> map = new HashMap<String, String>();
        map.put("itemId",itemId);
        countEvent(context,COUNTID_VIEW_ITEM_SOURCE,map);
    }
}
