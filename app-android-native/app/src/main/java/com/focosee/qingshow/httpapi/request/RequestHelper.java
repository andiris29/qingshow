package com.focosee.qingshow.httpapi.request;

import android.os.Build;

import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.persist.CookieSerializer;
import com.focosee.qingshow.util.AppUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by i068020 on 2/23/15.
 */
public class RequestHelper {
    private static final String VERSION_KEY_REQ = "qs-version";
    private static final String VERSION_CODE_REQ = "qs-version-code";
    private static final String TYPE_REQ = "qs-type";
    private static final String DEVICE_UID_REQ = "qs-device-uid";
    private static final String DEVICE_MODEL_REQ = "qs-device-model";
    private static final String OS_TYPE_ERQ = "qs-os-type";
    private static final String OS_VERSION_REQ = "qs-os-version";
    private static final String COOKIE_KEY_REQ = "Cookie";
    private static final String COOKIE_KEY_RES = "Set-Cookie";

    public static void beforeParseNetworkResponse(Map<String, String> headers) {
        _parseCookie(headers);
    }

    public static Map<String, String> beforeGetHeaders(Map<String, String> headers) {
        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<>();
        }
        _appendCookie(headers);
        _appendVersion(headers);
        return headers;
    }

    private static void _parseCookie(Map<String, String> headers) {
        Iterator iterator = headers.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (COOKIE_KEY_RES.equalsIgnoreCase(key)) {
                String cookie = headers.get(key);
                CookieSerializer.INSTANCE.saveCookie(cookie);
            }
        }
    }

    private static void _appendCookie(Map<String, String> headers) {
        String cookie = CookieSerializer.INSTANCE.loadCookie();
        if (cookie.length() > 0) {
            headers.put(COOKIE_KEY_REQ, cookie);
        }
    }

    private static void _appendVersion(Map<String, String> headers) {
        headers.put(VERSION_KEY_REQ, AppUtil.getVersion());
        headers.put(VERSION_CODE_REQ, AppUtil.getVersionCode());
        headers.put(TYPE_REQ, "app-android");
        headers.put(DEVICE_UID_REQ, QSApplication.instance().getDeviceUid());
        headers.put(DEVICE_MODEL_REQ, Build.MODEL);
        headers.put(OS_TYPE_ERQ, "android");
        headers.put(OS_VERSION_REQ, Build.VERSION.RELEASE);
    }
}
