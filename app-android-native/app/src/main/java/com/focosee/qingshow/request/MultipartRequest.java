package com.focosee.qingshow.request;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.entity.httpEntity.MultipartEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by focosee on 15/2/12.
 */
public class MultipartRequest extends Request<String> {

    MultipartEntity mMultiPartEntity = new MultipartEntity();
    Response.Listener<String> mListener;

    public MultipartRequest(int method, String url, Response.Listener<String> mListener, Response.ErrorListener listener) {
        super(method, url, listener);
        this.mListener = mListener;
    }

    /**
     * @return
     */
    public MultipartEntity getMultiPartEntity() {
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
    protected void deliverResponse(String response) {
        mListener.onResponse(response);

    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }

        QSApplication.get().addSessionCookie(headers);

        headers.put("version", QSApplication.get().getVersionName());

        Log.i("cookie", "json request: " + headers.toString());

        return headers;
    }

}
