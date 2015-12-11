package com.focosee.qingshow.httpapi.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.httpapi.response.QSRequestListener;
import com.focosee.qingshow.httpapi.response.error.QSResponseErrorListener;
import org.json.JSONObject;
import java.util.Map;

import rx.Observable;
import rx.functions.Func1;

public class QSJsonObjectRequest extends JsonObjectRequest {

    public QSJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, new QSRequestListener(listener), new QSResponseErrorListener(errorListener));
    }

    public QSJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener) {
        super(method, url, jsonRequest, new QSRequestListener(listener), new QSResponseErrorListener());
    }

    public QSJsonObjectRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener) {
        super(url, jsonRequest, new QSRequestListener(listener), new QSResponseErrorListener());
    }

    public QSJsonObjectRequest(String url, Response.Listener<JSONObject> listener) {
        super(url, null, new QSRequestListener(listener), new QSResponseErrorListener());
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        RequestHelper.beforeParseNetworkResponse(response.headers);
        return super.parseNetworkResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return RequestHelper.beforeGetHeaders(super.getHeaders());

    }
}
