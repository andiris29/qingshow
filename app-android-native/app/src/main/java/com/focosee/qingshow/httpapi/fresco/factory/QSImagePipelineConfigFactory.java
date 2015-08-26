package com.focosee.qingshow.httpapi.fresco.factory;

import android.content.Context;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Sets;
import com.facebook.common.internal.Supplier;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.focosee.qingshow.httpapi.fresco.QSFrescoConfigConstants;

/**
 * Created by Administrator on 2015/8/4.
 */
public class QSImagePipelineConfigFactory {

    public static final String IMAGE_PIPELINE_CACHE_DIR = "qingshow.imagepipeline_cache";

    private static QSImagePipelineConfigFactory instance;
    private ImagePipelineConfig imagePipelineConfig;
    private Context context;

    private QSImagePipelineConfigFactory(Context context) {
        this.context = context;
    }

    public static QSImagePipelineConfigFactory getInstance(Context context) {
        if (instance == null)
            instance = new QSImagePipelineConfigFactory(context);
        return instance;
    }

    public ImagePipelineConfig getImagePipelineConfig() {
        if (imagePipelineConfig == null) {
            ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(context);
            configureCaches(configBuilder, context);
            configureLoggingListeners(configBuilder);
            imagePipelineConfig = configBuilder.build();
        }
        return imagePipelineConfig;
    }

    private static void configureCaches(
            ImagePipelineConfig.Builder configBuilder,
            Context context) {
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                QSFrescoConfigConstants.MAX_MEMORY_CACHE_SIZE, // Max total size of elements in the cache
                Integer.MAX_VALUE,                     // Max entries in the cache
                QSFrescoConfigConstants.MAX_MEMORY_CACHE_SIZE, // Max total size of elements in eviction queue
                Integer.MAX_VALUE,                     // Max length of eviction queue
                Integer.MAX_VALUE);                    // Max cache entry size
        configBuilder
                .setBitmapMemoryCacheParamsSupplier(
                        new Supplier<MemoryCacheParams>() {
                            public MemoryCacheParams get() {
                                return bitmapCacheParams;
                            }
                        })
                .setMainDiskCacheConfig(
                        DiskCacheConfig.newBuilder()
                                .setBaseDirectoryPath(context.getApplicationContext().getCacheDir())
                                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)
                                .setMaxCacheSize(QSFrescoConfigConstants.MAX_DISK_CACHE_SIZE)
                                .build());
    }

    private static void configureLoggingListeners(ImagePipelineConfig.Builder configBuilder) {
        configBuilder.setRequestListeners(
                Sets.newHashSet((RequestListener) new RequestLoggingListener()));
    }
}
