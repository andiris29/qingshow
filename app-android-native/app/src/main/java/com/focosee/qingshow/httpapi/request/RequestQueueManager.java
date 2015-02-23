package com.focosee.qingshow.httpapi.request;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.focosee.qingshow.app.QSApplication;

/**
 * Created by i068020 on 2/24/15.
 */
public enum RequestQueueManager {
    INSTANCE;

    private RequestQueue queue;

    RequestQueueManager() {
        queue = Volley.newRequestQueue(QSApplication.instance());
    }

    public RequestQueue getQueue() {
        return queue;
    }
}
