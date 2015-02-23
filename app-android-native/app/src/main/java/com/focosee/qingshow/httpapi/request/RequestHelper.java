package com.focosee.qingshow.httpapi.request;

import com.focosee.qingshow.app.QSApplication;
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

    private static final String COOKIE_KEY_REQ = "Cookie";
    private static final String COOKIE_KEY_RES = "Set-Cookie";

    public static void beforeParseNetworkResponse(Map<String, String> headers) {
        _parseCookie(headers);
    }

    public static Map<String, String> beforeGetHeaders(Map<String, String> headers) {
        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
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
                QSApplication.instance().saveCookie(cookie);
            }
        }
    }

    private static void _appendCookie(Map<String, String> headers) {
        String cookie = QSApplication.instance().loadCookie();
        if (cookie.length() > 0) {
            headers.put(COOKIE_KEY_REQ, cookie);
        }
    }

    private static void _appendVersion(Map<String, String> headers) {
        headers.put(VERSION_KEY_REQ, AppUtil.getVersion());
    }
}
