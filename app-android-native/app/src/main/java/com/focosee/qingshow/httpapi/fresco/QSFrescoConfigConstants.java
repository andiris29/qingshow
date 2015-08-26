package com.focosee.qingshow.httpapi.fresco;

import com.facebook.common.util.ByteConstants;

/**
 * Created by Administrator on 2015/8/4.
 */
public class QSFrescoConfigConstants {
    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();

    public static final int MAX_DISK_CACHE_SIZE = 40 * ByteConstants.MB;
    public static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 5;
}