package com.focosee.qingshow.httpapi.request;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by focosee on 15/2/12.
 */
public class QSMultipartRequest extends JsonObjectRequest {

    QSMultipartEntity mMultiPartEntity = new QSMultipartEntity();
    Response.Listener<JSONObject> mListener;

    public QSMultipartRequest(int method, String url, JSONObject jsonObject, Response.Listener<JSONObject> mListener, Response.ErrorListener listener) {
        super(method, url, jsonObject, mListener, listener);
        this.mListener = mListener;
    }

    public QSMultipartRequest(int method, String url, Response.Listener<JSONObject> mListener, Response.ErrorListener listener) {
        super(method, url, null, mListener, listener);
        this.mListener = mListener;
    }

    /**
     * @return
     */
    public QSMultipartEntity getMultiPartEntity() {
        return mMultiPartEntity;
    }

    @Override
    public String getBodyContentType() {
        return mMultiPartEntity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            // 将mMultiPartEntity中的参数写入到bos中
            mMultiPartEntity.writeTo(bos);
        } catch (IOException e) {
            Log.e("", "IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }


    @Override
    protected void deliverResponse(JSONObject response) {
        mListener.onResponse(response);

    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        RequestHelper.beforeParseNetworkResponse(response.headers);
//        String parsed;
//        try {
//            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//        } catch (UnsupportedEncodingException e) {
//            parsed = new String(response.data);
//        }
//        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
        return super.parseNetworkResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return RequestHelper.beforeGetHeaders(super.getHeaders());
    }

}
