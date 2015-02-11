package com.focosee.qingshow.activity;


import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.mongo.MongoItem;
import com.focosee.qingshow.entity.mongo.MongoPeople;
import com.focosee.qingshow.httpapi.response.dataparser.PeopleParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.request.QSJsonObjectRequest;
import com.focosee.qingshow.request.QSStringRequest;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.BitMapUtil;
import com.focosee.qingshow.widget.ActionSheet;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.RequestContent;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class U02SettingsFragment extends Fragment implements View.OnFocusChangeListener, ActionSheet.ActionSheetListener {

    private static final String[] sexArgs = {"男", "女"};
    private static final String[] hairArgs = {"长发", "超长发", "中长发", "短发", "光头", "其他"};
    private static final String[] shoesSize = {"34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44"};
    private static final String[] clothesSize = {"XXS", "XS", "S", "M", "L", "XL", "XXL", "3XL"};
    private static final String TAG_SEX = "sex";
    private static final String TAG_HAIR = "hair";
    private static final String TAG_SHOESIZE = "shoeSize";
    private static final String TAG_CLOTHESSIZE = "clothSize";
    private static final int TYPE_PORTRAIT = 10000;//上传头像
    private static final int TYPE_BACKGROUD = 10001;//上传背景

    private Context context;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;

    private TextView backTextView;
    private RelativeLayout personalRelativeLayout;
    private RelativeLayout backgroundRelativeLayout;
    private RelativeLayout sexRelativeLayout;
    private RelativeLayout hairRelativeLayout;
    private RelativeLayout shoeSizeLayout;
    private RelativeLayout clothSizeLayout;
    private RelativeLayout changePasswordRelativeLayout;
    private RelativeLayout changeEmailRelativeLayout;
    private RelativeLayout informRelativeLayout;
    private RelativeLayout rulesRelativeLayout;
    private RelativeLayout helpRelativeLayout;
    private RelativeLayout aboutVIPRelativeLayout;

    private ImageView portraitImageView;
    private ImageView backgroundImageView;

    private EditText nameEditText;
    private TextView sexTextView;
    private EditText ageEditText;
    private EditText heightEditText;
    private EditText weightEditText;
    private EditText hairTextView;
    private EditText shoesSizeEditText;
    private EditText clothesSizeEditText;
    private TextView favoriteBrandText;
    public static U02SettingsFragment instance;

    private MongoPeople people;

    private Thread thread;

    public static U02SettingsFragment newIntance(){
        if(null == instance) {
            instance = new U02SettingsFragment();
        }
        return instance;
    }

    public U02SettingsFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_u02_settings, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (Context) getActivity().getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
        sharedPreferences = getActivity().getSharedPreferences("personal", Context.MODE_PRIVATE);

        getUser();

        matchUI();

        setJumpListener();

        Button quitButton = (Button) getActivity().findViewById(R.id.quitButton);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.edit().clear().commit();
                Toast.makeText(context, "已退出登录", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), U06LoginActivity.class);
                startActivity(intent);
                getActivity().sendBroadcast(new Intent(U01PersonalActivity.LOGOUT_ACTOIN));
                getActivity().finish();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, QSAppWebAPI.LOGOUT_SERVICE_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        String rawCookie = sharedPreferences.getString("Cookie", "");
                        if (rawCookie != null && rawCookie.length() > 0) {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Cookie", rawCookie);
                            return headers;
                        }
                        return super.getHeaders();
                    }
                };
                requestQueue.add(stringRequest);
            }
        });
    }

    private void matchUI(){

        portraitImageView = (ImageView) getActivity().findViewById(R.id.portraitImageView);
        backgroundImageView = (ImageView) getActivity().findViewById(R.id.backgroundImageView);

        shoesSizeEditText = (EditText) getActivity().findViewById(R.id.shoesSizeEditText);
        shoesSizeEditText.setOnFocusChangeListener(this);
        clothesSizeEditText = (EditText) getActivity().findViewById(R.id.clothesSizeEditText);
        clothesSizeEditText.setOnFocusChangeListener(this);

        nameEditText = (EditText) getActivity().findViewById(R.id.nameEditText);
        nameEditText.setOnFocusChangeListener(this);

        sexTextView = (TextView) getActivity().findViewById(R.id.sexTextView);

        ageEditText = (EditText) getActivity().findViewById(R.id.ageEditText);
        ageEditText.setOnFocusChangeListener(this);

        heightEditText = (EditText) getActivity().findViewById(R.id.heightEditText);
        heightEditText.setOnFocusChangeListener(this);

        weightEditText = (EditText) getActivity().findViewById(R.id.weightEditText);
        weightEditText.setOnFocusChangeListener(this);

        hairTextView = (EditText) getActivity().findViewById(R.id.hairTextView);

        favoriteBrandText = (TextView) getActivity().findViewById(R.id.brandTextView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
//            Uri uri = data.getData();
//            ContentResolver contentResolver = getActivity().getContentResolver();
//        }
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (resultCode == Activity.RESULT_OK && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();

                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);



                cursor.close();

                if(requestCode == TYPE_PORTRAIT) {

//                ImageView imgView = (ImageView) getfindViewById(R.id.imgView);
//                // Set the Image in ImageView after decoding the String
//                portraitImageView.setImageBitmap(BitmapFactory
//                        .decodeFile(imgDecodableString));
                    uploadImage(imgDecodableString, TYPE_PORTRAIT);
                }

                if(requestCode == TYPE_BACKGROUD){
                    uploadImage(imgDecodableString, TYPE_BACKGROUD);
                }


            } else {
                Toast.makeText(context, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Something went wrong : " + e.toString(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void uploadImage(final String imgUri, final int type){

        final Bitmap bitmap = BitmapFactory.decodeFile(imgUri);

        Toast.makeText(getActivity(), "fdskajflkdsajlfdsa", Toast.LENGTH_SHORT).show();

        //byte[] bitmapArray = BitMapUtil.bmpToByteArray(bitmap, true);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);

        String api,key;

        if (type == TYPE_PORTRAIT) {
            api = QSAppWebAPI.getUserUpdateportrait();
            key = "portrait";
        } else {
            api = QSAppWebAPI.getUserUpdatebackground();
            key = "background";
        }
        final String API = api;
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put(key, output);
//        } catch (JSONException e) {
//            Toast.makeText(context, "上传文件失败，请重试！", Toast.LENGTH_SHORT).show();
//        }
//        Map params = new HashMap();
//        params.put(key, bitmapArray);


//        QSJsonObjectRequest qsJsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, api
//            , jsonObject, new Response.Listener<JSONObject>(){
//
//            @Override
//            public void onResponse(JSONObject response) {
//
//                if(MetadataParser.hasError(response) || null == UserParser.parseGet(response)){
//                    ErrorHandler.handle(getActivity(), MetadataParser.getError(response));
//                    return;
//                }
//
//                QSApplication.get().setPeople(UserParser.parseGet(response));
//                if(type == TYPE_PORTRAIT)
//                    ImageLoader.getInstance().displayImage(people.getPortrait(), portraitImageView, AppUtil.getPortraitDisplayOptions());
//                else
//                    ImageLoader.getInstance().displayImage(people.getBackground(), backgroundImageView, AppUtil.getModelBackgroundDisplayOptions());
//
//                Toast.makeText(context, "上传成功！", Toast.LENGTH_SHORT).show();
//
//            }
//        }, new Response.ErrorListener(){
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
//            }
//        });

        thread = new Thread(){
            @Override
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(API);


                InputStreamEntity reqEntity = null;
                HttpResponse response = null;
                SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(QSApplication.get());

                _preferences = QSApplication.get().getSharedPreferences("personal", Context.MODE_PRIVATE);
                httppost.addHeader("Cookie", _preferences.getString("Cookie", ""));

                try {
                    reqEntity = new InputStreamEntity( new FileInputStream(imgUri), -1);
                    reqEntity.setContentType("binary/octet-stream");
                    reqEntity.setChunked(true);
                    httppost.setEntity(reqEntity);
                    //Toast.makeText(context, "reqEntity: " + reqEntity, Toast.LENGTH_SHORT).show();
                    response = httpclient.execute(httppost);
                    Log.d("response:", response.toString());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if((response.getStatusLine().toString()).equals("HTTP/1.1 200 OK")){
                    // Successfully Uploaded
                    Log.i("uploaded", response.getStatusLine().toString());

                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                        StringBuilder sb = new StringBuilder();



                        String line = null;

                        while ((line = reader.readLine()) != null) {

                            sb.append(line + "/n");

                        }
                        Log.d("uploaded", sb.toString());

                    } catch (IOException e) {

                        e.printStackTrace();

                    }

                    //Toast.makeText(context, "uploaded:" + response, Toast.LENGTH_SHORT).show();
                }
                else{
                    // Did not upload. Add your logic here. Maybe you want to retry.
                    Log.i(" not uploaded", response.getStatusLine().toString());
                    //Toast.makeText(context, "not uploaded", Toast.LENGTH_SHORT).show();
                }

                httpclient.getConnectionManager().shutdown();
            }
        };

        new UploadTask().execute("");
//        QSStringRequest qxStringRequest = new QSStringRequest(params, Request.Method.POST, api, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                MongoPeople user = UserParser.parseUpdate(response);
//                if (user == null) {
//                    ErrorHandler.handle(context, MetadataParser.getError(response));
//                } else {
//                    QSApplication.get().setPeople(user);
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(context, "请检查网络" + error.toString(), Toast.LENGTH_LONG).show();
//            }
//        });
//        requestQueue.add(qxStringRequest);
//        QSApplication.get().QSRequestQueue().add(qsJsonObjectRequest);



    }

    //进入页面时，给字段赋值
    private void setData() {
        if (null != people) {

            ImageLoader.getInstance().displayImage(people.portrait, portraitImageView, AppUtil.getPortraitDisplayOptions());
            ImageLoader.getInstance().displayImage(people.background, backgroundImageView, AppUtil.getModelBackgroundDisplayOptions());

            nameEditText.setText(people.name);
            ageEditText.setText("");
            heightEditText.setText(people.height);
            weightEditText.setText(people.weight);
            sexTextView.setText(sexArgs[people.gender]);
            sexTextView.setTag(people.gender);
            hairTextView.setText(hairArgs[people.hairType]);
            hairTextView.setTag(people.hairType);
            shoesSizeEditText.setText(shoesSize[people.shoeSize]);
            shoesSizeEditText.setTag(people.shoeSize);
            //Toast.makeText(getActivity(), clothesSize[Integer.valueOf(people.clothingSize)], Toast.LENGTH_LONG).show();
            clothesSizeEditText.setTag(people.clothingSize);
            clothesSizeEditText.setText(clothesSize[people.clothingSize]);
            favoriteBrandText.setText(people.favoriteBrand);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        commitForm();
    }

    //获得用户信息
    private void getUser() {

        QSJsonObjectRequest jor = new QSJsonObjectRequest(QSAppWebAPI.getUerApi(sharedPreferences.getString("id", "")), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    people = UserParser.parseGet(response);
                    setData();
                } catch (Exception error) {
                    Log.i("test", "error" + error.toString());
                    Toast.makeText(U02SettingsFragment.this.getActivity(), "Error:" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(U02SettingsFragment.this.getActivity(), "Error:" + error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        QSApplication.get().QSRequestQueue().add(jor);

    }

    public void showActionSheet(String type) {

        if (TAG_SEX.equals(type)) {

            ActionSheet.createBuilder(getActivity(), getFragmentManager())
                    .setTag(TAG_SEX)
                    .setCancelButtonTitle("取消")
                    .setOtherButtonTitles(sexArgs)
                    .setCancelableOnTouchOutside(true).setListener(this).show();
        }

        if (TAG_HAIR.equals(type)) {
            ActionSheet.createBuilder(getActivity(), getFragmentManager())
                    .setTag(TAG_HAIR)
                    .setCancelButtonTitle("取消")
                    .setOtherButtonTitles(hairArgs)
                    .setCancelableOnTouchOutside(true).setListener(this).show();
        }

        if (TAG_SHOESIZE.equals(type)) {
            ActionSheet.createBuilder(getActivity(), getFragmentManager())
                    .setTag(TAG_SHOESIZE)
                    .setCancelButtonTitle("取消")
                    .setOtherButtonTitles(shoesSize)
                    .setCancelableOnTouchOutside(true).setListener(this).show();
        }

        if (TAG_CLOTHESSIZE.equals(type)) {
            ActionSheet.createBuilder(getActivity(), getFragmentManager())
                    .setTag(TAG_CLOTHESSIZE)
                    .setCancelButtonTitle("取消")
                    .setOtherButtonTitles(clothesSize)
                    .setCancelableOnTouchOutside(true).setListener(this).show();
        }
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {

        if (TAG_SEX.equals(String.valueOf(actionSheet.getTag()))) {
            sexTextView.setText(sexArgs[index]);
            sexTextView.setTag(index);
        }

        if (TAG_HAIR.equals(String.valueOf(actionSheet.getTag()))) {
            hairTextView.setText(hairArgs[index]);
            hairTextView.setTag(index);
        }

        if (TAG_SHOESIZE.equals(String.valueOf(actionSheet.getTag()))) {
            shoesSizeEditText.setText(shoesSize[index]);
            shoesSizeEditText.setTag(index);
        }

        if (TAG_CLOTHESSIZE.equals(String.valueOf(actionSheet.getTag()))) {
            clothesSizeEditText.setText(clothesSize[index]);
            clothesSizeEditText.setTag(index);
        }
        commitForm();
    }

    private void commitForm() {
        Map<String, String> params = new HashMap<String, String>();
        if (!nameEditText.getText().toString().equals(""))
            params.put("name", nameEditText.getText().toString());
        if (!ageEditText.getText().toString().equals(""))
            params.put("age", ageEditText.getText().toString());
        if (!heightEditText.getText().toString().equals(""))
            params.put("height", heightEditText.getText().toString());
        if (!weightEditText.getText().toString().equals(""))
            params.put("weight", weightEditText.getText().toString());
        if (null != sexTextView.getTag()) {
            params.put("gender", sexTextView.getTag().toString());
        }
        if (null != hairTextView.getTag()) {
            params.put("hairType", hairTextView.getTag().toString());
        }
        if (null != shoesSizeEditText.getTag()) {
            params.put("shoeSize", shoesSizeEditText.getTag().toString());
        }
        if (null != clothesSizeEditText.getTag()) {
            params.put("clothingSize", clothesSizeEditText.getTag().toString());
        }
        if (!"".equals(favoriteBrandText.getText())) {
            params.put("favoriteBrand", favoriteBrandText.getText().toString());
        }

        QSStringRequest qxStringRequest = new QSStringRequest(params, Request.Method.POST, QSAppWebAPI.UPDATE_SERVICE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MongoPeople user = UserParser.parseUpdate(response);
                if (user == null) {
                    ErrorHandler.handle(context, MetadataParser.getError(response));
                } else {
                    QSApplication.get().setPeople(user);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "请检查网络", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(qxStringRequest);
    }

    private void setJumpListener() {
        backTextView = (TextView) getActivity().findViewById(R.id.backTextView);
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commitForm();
                getActivity().finish();
            }
        });

        personalRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.personalRelativeLayout);
        personalRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "请选择头像"), TYPE_PORTRAIT);
            }
        });
        backgroundRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.backgroundRelativeLayout);
        backgroundRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);

                startActivityForResult(Intent.createChooser(intent,"请选择背景"), TYPE_BACKGROUD);
            }
        });

        sexRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.sexRelativeLayout);
        sexRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().setTheme(R.style.ActionSheetStyleIOS7);
                showActionSheet(TAG_SEX);
            }
        });
        hairRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.hairRelativeLayout);
        hairRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().setTheme(R.style.ActionSheetStyleIOS7);
                showActionSheet(TAG_HAIR);
            }
        });
        shoeSizeLayout = (RelativeLayout) getActivity().findViewById(R.id.shoesSizeRelativeLayout);
        shoeSizeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setTheme(R.style.ActionSheetStyleIOS7);
                showActionSheet(TAG_SHOESIZE);
            }
        });
        clothSizeLayout = (RelativeLayout) getActivity().findViewById(R.id.clothesSizeRelativeLayout);
        clothSizeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setTheme(R.style.ActionSheetStyleIOS7);
                showActionSheet(TAG_CLOTHESSIZE);
            }
        });

        changePasswordRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.changePasswordRelativeLayout);
        changePasswordRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02ChangePasswordFragment fragment = new U02ChangePasswordFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, fragment).commit();
            }
        });
        changeEmailRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.changeEmailRelativeLayout);
        changeEmailRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02ChangeIdFragment fragment = new U02ChangeIdFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, fragment).commit();
            }
        });
        informRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.informRelativeLayout);
        informRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02NoticeFragment fragment = new U02NoticeFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, fragment).commit();
            }
        });
        rulesRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.rulesRelativeLayout);
        rulesRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02TermsFragment hairFragment = new U02TermsFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, hairFragment).commit();
            }
        });
        helpRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.helpRelativeLayout);
        helpRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02HelpFragment fragment = new U02HelpFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, fragment).commit();
            }
        });
        aboutVIPRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.aboutVIPRelativeLayout);
        aboutVIPRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02AboutVIPFragment fragment = new U02AboutVIPFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, fragment).commit();
            }
        });
    }

    class UploadTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            thread.start();
            return null;
        }
    }

}
