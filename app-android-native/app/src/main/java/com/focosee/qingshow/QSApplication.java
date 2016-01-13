package com.focosee.qingshow;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.graphics.Point;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;
import com.focosee.qingshow.activity.BaseActivity;
import com.focosee.qingshow.constants.config.ShareConfig;
import com.focosee.qingshow.httpapi.fresco.factory.QSImagePipelineConfigFactory;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.exception.CrashHandler;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

public class QSApplication extends Application {
    private static QSApplication _instance;
    private IWXAPI wxApi;
    private final long TIME_LOGIN = 15000;

    public static QSApplication instance() {
        return _instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;

        Stetho.initializeWithDefaults(this);

        wxApi = WXAPIFactory.createWXAPI(getApplicationContext(), ShareConfig.APP_ID, true);
        wxApi.registerApp(ShareConfig.APP_ID);
        Fresco.initialize(getApplicationContext(), QSImagePipelineConfigFactory.getInstance(getApplicationContext()).getImagePipelineConfig());
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        configImageLoader();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        if(AppUtil.isApkInDebug(this)){
        }
        showLoginGuide();
    }

    public void configImageLoader() {
        File cacheDir = StorageUtils.getCacheDirectory(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .diskCacheExtraOptions(480, 800, null)
                .threadPoolSize(3) // default
                .threadPriority(Thread.NORM_PRIORITY - 2) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiscCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(this)) // default
//                .imageDecoder(new BaseImageDecoder()) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
    }

    public IWXAPI getWxApi() {
        return wxApi;
    }

    public SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    public String getDeviceUid(){
        return ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    public Point getScreenSize(Context context){
        Display display = ((BaseActivity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    Timer timer;
    private void showLoginGuide(){

        if(QSModel.INSTANCE.getUserStatus() == MongoPeople.MATCH_FINISHED
                || (QSModel.INSTANCE.getUserStatus() == MongoPeople.LOGIN_GUIDE_FINISHED && QSModel.INSTANCE.isGuest()) || !QSModel.INSTANCE.loggedin()) {
            timer = new Timer(true);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    sendBroadcast(new Intent(BaseActivity.SHOW_GUIDE));
                    timer.cancel();
                    timer.purge();
                    timer = null;
                }
            };
            timer.schedule(timerTask, TIME_LOGIN);
        }
    }


}

