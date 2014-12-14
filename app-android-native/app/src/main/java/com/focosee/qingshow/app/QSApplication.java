package com.focosee.qingshow.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

public class QSApplication extends Application {

    private static ImageLoader _imageLoader;
    private static RequestQueue _requestQueue;
    private static String _userId;

    @Override
    public void onCreate() {
        super.onCreate();

        if (null == _imageLoader){
            configImageLoader();
        }
        _requestQueue = Volley.newRequestQueue(this);
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

    public static RequestQueue QSRequestQueue() {
        return _requestQueue;
    }

    public static String QSUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("personal", Context.MODE_PRIVATE);
        _userId = sharedPreferences.getString("id", null);
        return _userId;
    }

}
