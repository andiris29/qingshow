package com.focosee.qingshow.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by zenan on 12/31/14.
 */
public class QXStringRequest extends StringRequest {
    public QXStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

}
