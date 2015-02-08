package com.focosee.qingshow.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.focosee.qingshow.app.QSApplication;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zenan on 12/31/14.
 */
public class QSStringRequest extends StringRequest {
    private Map<String, String> params = null;

    public QSStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public QSStringRequest(Map<String, String> params, int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.params = params;
    }

    public QSStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();
        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }
        QSApplication.get().addSessionCookie(headers);
        headers.put("version", QSApplication.get().getVersionName());
        return headers;
    }

    @Override
    protected Map<String, String> getParams() {
        if (params == null) {
            params = new HashMap<String, String>();
        }
        return params;
    }
}
