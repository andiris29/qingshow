package com.focosee.qingshow.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.focosee.qingshow.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class AppUtil {

    private static DisplayImageOptions showDisplayOptions = null;
    private static DisplayImageOptions portraitDisplayOptions = null;
    private static DisplayImageOptions modelBackgroundDisplayOptions = null;

    public static boolean getAppUserLoginStatus(Context context) {
        SharedPreferences prefs;// = PreferenceManager.getDefaultSharedPreferences(context) ;
        prefs = context.getSharedPreferences("personal", Context.MODE_PRIVATE);
        String userId = prefs.getString("id", null);
        return userId!=null;
    }

    public static String getAppUserId(Context context) {
        SharedPreferences preferences;
        preferences = context.getSharedPreferences("personal", Context.MODE_PRIVATE);
        return preferences.getString("_id", null);
    }

    //获取版本号
    public static String getAppVersionName(Context context) {
        try {
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "1.0.0";
        }
    }

    public static DisplayImageOptions getShowDisplayOptions() {
        if (null == showDisplayOptions) {
            showDisplayOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.root_cell_placehold_image1) //设置图片在下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.root_cell_placehold_image2)//设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.root_cell_placehold_image2)  //设置图片加载/解码过程中错误时候显示的图片
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
//            .considerExifParams(true)
//            .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
//            .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
                    .build();//构建完成
        }
        return showDisplayOptions;
    }

    public static DisplayImageOptions getPortraitDisplayOptions() {
        if (null == portraitDisplayOptions) {
            portraitDisplayOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.user_head_default)
                    .showImageForEmptyUri(R.drawable.user_head_default)
                    .showImageOnFail(R.drawable.user_head_default)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
        }
        return portraitDisplayOptions;
    }

    public static DisplayImageOptions getModelBackgroundDisplayOptions() {
        if (null == modelBackgroundDisplayOptions) {
            modelBackgroundDisplayOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.user_bg_default)
                    .showImageForEmptyUri(R.drawable.user_bg_default)
                    .showImageOnFail(R.drawable.user_bg_default)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
        }
        return modelBackgroundDisplayOptions;
    }
}
