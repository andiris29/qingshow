package com.focosee.qingshow.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.focosee.qingshow.activity.U01PersonalActivity;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.LoginResponse;
import com.focosee.qingshow.entity.People;
import com.focosee.qingshow.error.ErrorCode;
import com.focosee.qingshow.request.MJsonObjectRequest;
import com.focosee.qingshow.util.AppUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class QSApplication extends Application {
    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    private static final String SESSION_COOKIE = "connect.sid";

    private static QSApplication _instance;
    private static ImageLoader _imageLoader;
    private static RequestQueue _requestQueue;
    private static String _userId;
    private SharedPreferences _preferences;
    private static People people = null;

    private String versionName;

    public static QSApplication get() {
        return _instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO Remove _imageLoader?
        if (null == _imageLoader){
            configImageLoader();
        }
        _requestQueue = Volley.newRequestQueue(this);
        _instance = this;
        // TODO _preferences set * 2?
        _preferences = PreferenceManager.getDefaultSharedPreferences(this);

        _preferences = getSharedPreferences("personal", Context.MODE_PRIVATE);

        // config app version name
        versionName = AppUtil.getAppVersionName(this);


    }

    public void configImageLoader() {
        File cacheDir = StorageUtils.getCacheDirectory(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .diskCacheExtraOptions(480, 800, null)
                .threadPoolSize(3) // default
                .threadPriority(Thread.NORM_PRIORITY - 2) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiscCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(this)) // default
//                .imageDecoder(new BaseImageDecoder()) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
    }

    // TODO QSRequestQueue name convension
    public RequestQueue QSRequestQueue() {
        if (_requestQueue == null) {
            _requestQueue = Volley.newRequestQueue(this);
        }
        return _requestQueue;
    }

    // TODO QSUserId public?
    public String QSUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("personal", Context.MODE_PRIVATE);
        _userId = sharedPreferences.getString("id", null);
        return _userId;
    }


    /**
     * Checks the response headers for session cookie and saves it
     * if it finds it.
     * @param headers Response Headers.
     */
    public final void checkSessionCookie(Map<String, String> headers) {
        if (headers.containsKey(SET_COOKIE_KEY)
                && headers.get(SET_COOKIE_KEY).startsWith(SESSION_COOKIE)) {
            String cookie = headers.get("Set-Cookie").split(";")[0];
            if (cookie.length() > 0) {
                SharedPreferences.Editor prefEditor = _preferences.edit();
                prefEditor.putString(SESSION_COOKIE, cookie);
                prefEditor.commit();
            }
        }
    }

    /**
     * Adds session cookie to headers if exists.
     * @param headers
     */
    public final void addSessionCookie(Map<String, String> headers) {
        String sessionId = _preferences.getString("Cookie", "");
        if (sessionId.length() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(sessionId);
            headers.put(COOKIE_KEY, builder.toString());
        }
    }

    public People getPeople() {
        return people;
    }

    public void setPeople(People p) {
        people = p;
    }

    public String getVersionName() {
        return versionName;
    }

    public void refreshPeople(Context context){
        //final Context _context = context;
//        JSONObject jsonObject = new JSONObject();
//        Map map = new HashMap();
//        map.put("_id", people._id);
//        jsonObject.put("")
        MJsonObjectRequest jsonObjectRequest = new MJsonObjectRequest(Request.Method.GET, QSAppWebAPI.getUerApi(QSUserId(context)), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    people = People.getPeopleEntitis(response);
                    QSApplication.get().setPeople(people);
                } catch (Exception e) {
//                    showMessage(S03SHowActivity.this, e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("点赞失败", "点赞失败");
            }
        });

        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

}
