package com.focosee.qingshow.httpapi.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.error.QSResponseErrorListener;
import org.json.JSONObject;
import java.util.Map;
import java.util.Objects;

public class QSJsonObjectRequest extends JsonObjectRequest {

    private Map<String, String> _params;

//    public QSJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Context context) {
//        super(method, url, jsonRequest, listener, new QSResponseErrorListener(this, context));
//    }
    public QSJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public QSJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener) {
        super(method, url, jsonRequest, listener, new QSResponseErrorListener());
    }

    public QSJsonObjectRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener) {
        super(url, jsonRequest, listener, new QSResponseErrorListener());
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
