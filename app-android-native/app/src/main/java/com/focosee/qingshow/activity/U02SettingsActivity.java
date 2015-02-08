package com.focosee.qingshow.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.focosee.qingshow.R;


public class U02SettingsActivity extends BaseActivity {
    private Context context;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);

        U02SettingsFragment settingsFragment = new U02SettingsFragment();
        getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, settingsFragment, "settingsFragment").commit();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK) {
//            U02SettingsFragment settingsFragment = new U02SettingsFragment();
//            getFragmentManager().findFragmentByTag("settingsFragment");
//            getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, settingsFragment).commit();
//            return false;
//        } else {
//            return super.onKeyDown(keyCode, event);
//        }
//    }

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
