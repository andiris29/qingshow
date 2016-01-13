package com.focosee.qingshow.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;

import com.focosee.qingshow.R;
import com.focosee.qingshow.QSApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

public class AppUtil {

    private static DisplayImageOptions showDisplayOptions = null;
    private static DisplayImageOptions portraitDisplayOptions = null;
    private static DisplayImageOptions modelBackgroundDisplayOptions = null;
    private static DisplayImageOptions simapleDisplayOptions = null;

    //获取版本号
    public static String getVersion() {
        try {
            Context context = QSApplication.instance();
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "1.0.0";
        }
    }

    public static String getVersionCode() {
        try {
            Context context = QSApplication.instance();
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return String.valueOf(pi.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "1";
        }
    }

    public static DisplayImageOptions getShowDisplayOptions() {
        if (null == showDisplayOptions) {
            showDisplayOptions = new DisplayImageOptions.Builder()
//                    .showImageOnLoading(R.drawable.root_cell_placehold_image1) //设置图片在下载期间显示的图片
//                    .showImageForEmptyUri(R.drawable.root_cell_placehold_image2)//设置图片Uri为空或是错误的时候显示的图片
//                    .showImageOnFail(R.drawable.root_cell_placehold_image2)  //设置图片加载/解码过程中错误时候显示的图片
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                            //.bitmapConfig(Bitmap.Config.RGB_565)
//            .considerExifParams(true)
//            .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
//            .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
                    .build();//构建完成
        }
        return showDisplayOptions;
    }

    public static DisplayImageOptions getModelBackgroundDisplayOptions() {
        if (null == modelBackgroundDisplayOptions) {
            modelBackgroundDisplayOptions = new DisplayImageOptions.Builder()
//                    .showImageForEmptyUri(R.drawable.user_background_default)
//                    .showImageOnLoading(R.drawable.user_background_default)
//                    .showImageOnFail(R.drawable.user_background_default)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
        }
        return modelBackgroundDisplayOptions;
    }

    public static boolean checkNetWork(Context context) {
        boolean flag = false;
        ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager.getActiveNetworkInfo() != null)
            flag = cwjManager.getActiveNetworkInfo().isAvailable();
        return flag;
    }

    public static Point getScreenSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static float transformToDip(float i, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, context.getResources().getDisplayMetrics());
    }

    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName())) {
            return true;
        }
        return false;
    }

    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }
}
