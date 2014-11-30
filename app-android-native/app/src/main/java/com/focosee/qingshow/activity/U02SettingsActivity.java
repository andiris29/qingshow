package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.focosee.qingshow.R;

import java.util.HashMap;
import java.util.Map;


public class U02SettingsActivity extends Activity {
    private Context context;
    private RequestQueue requestQueue;
    private TextView saveTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        context = getApplicationContext();

        saveTextView = (TextView) findViewById(R.id.saveTextView);

        requestQueue = Volley.newRequestQueue(context);

        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        "http://121.41.162.102:30001/services/user/update",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("TAG", response);
                                Toast.makeText(context, "success", Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "wrong", Toast.LENGTH_LONG).show();
                        Log.e("TAG", error.getMessage(), error);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();

                        map.put("mail", "");
                        map.put("currentPassword", "");
                        map.put("password", "");
                        map.put("name", "");
                        map.put("portrait", "");
                        map.put("gender", "");
                        map.put("height", "");
                        map.put("weight", "");
                        map.put("roles", "");
                        map.put("hairTypes", "");
                        return map;
                    }
                };

                requestQueue.add(stringRequest);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
