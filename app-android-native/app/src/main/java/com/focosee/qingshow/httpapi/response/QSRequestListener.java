package com.focosee.qingshow.httpapi.response;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Response;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.activity.BaseActivity;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.widget.ConfirmDialog;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/9/6.
 */
public class QSRequestListener implements Response.Listener<JSONObject> {

    private Response.Listener<JSONObject> childListener;

    public QSRequestListener(Response.Listener<JSONObject> childListener){
        this.childListener = childListener;
    }

    @Override
    public void onResponse(JSONObject response) {

        if(MetadataParser.hasError(response)){
            if(MetadataParser.getError(response) == 2000){
                QSApplication.instance().sendBroadcast(new Intent(BaseActivity.UPDATE_APP));
            }
        }

        childListener.onResponse(response);

    }
}
