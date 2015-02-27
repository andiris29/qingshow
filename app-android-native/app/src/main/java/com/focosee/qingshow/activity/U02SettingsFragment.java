package com.focosee.qingshow.activity;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.QSMultipartEntity;
import com.focosee.qingshow.httpapi.request.QSMultipartRequest;
import com.focosee.qingshow.httpapi.request.QSStringRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.persist.CookieSerializer;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.widget.ActionSheet;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class U02SettingsFragment extends Fragment implements View.OnFocusChangeListener, ActionSheet.ActionSheetListener {

    private static final String[] sexArgs = {"男", "女"};
    private static final String[] hairArgs = {"所有", "长发", "超长发", "中长发", "短发"};
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

    private TextView backTextView;
    private RelativeLayout personalRelativeLayout;
    private RelativeLayout backgroundRelativeLayout;
    private RelativeLayout birthRelativeLayout;
    private RelativeLayout sexRelativeLayout;
    private RelativeLayout hairRelativeLayout;
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
    private EditText birthEditText;
    private EditText heightEditText;
    private EditText weightEditText;
    private EditText hairTextView;
    private EditText shoesSizeEditText;
    private EditText clothesSizeEditText;
    private TextView favoriteBrandText;
    private TextView changePwText;
    public static U02SettingsFragment instance;
    private float textWidth;

    private MongoPeople people;

    public static U02SettingsFragment newIntance() {
        if (null == instance) {
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

        matchUI();

        getUser();


        setJumpListener();

        Button quitButton = (Button) getActivity().findViewById(R.id.quitButton);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CookieSerializer.INSTANCE.saveCookie("");
                Toast.makeText(context, "已退出登录", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), U06LoginActivity.class);
                startActivity(intent);
                getActivity().sendBroadcast(new Intent(U01PersonalActivity.LOGOUT_ACTOIN));
                getActivity().finish();
                QSStringRequest stringRequest = new QSStringRequest(Request.Method.POST, QSAppWebAPI.LOGOUT_SERVICE_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                QSModel.INSTANCE.setUser(null);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                }) {
                };
                requestQueue.add(stringRequest);
            }
        });
    }

    private void matchUI() {

        portraitImageView = (ImageView) getActivity().findViewById(R.id.portraitImageView);
        backgroundImageView = (ImageView) getActivity().findViewById(R.id.backgroundImageView);

        shoesSizeEditText = (EditText) getActivity().findViewById(R.id.shoesSizeEditText);
        shoesSizeEditText.setOnFocusChangeListener(this);
        clothesSizeEditText = (EditText) getActivity().findViewById(R.id.clothesSizeEditText);
        clothesSizeEditText.setOnFocusChangeListener(this);

        nameEditText = (EditText) getActivity().findViewById(R.id.nameEditText);
        nameEditText.setOnFocusChangeListener(this);

        sexTextView = (TextView) getActivity().findViewById(R.id.sexTextView);

        birthEditText = (EditText) getActivity().findViewById(R.id.birthDayEditText);
        birthEditText.setOnFocusChangeListener(this);

        heightEditText = (EditText) getActivity().findViewById(R.id.heightEditText);
        heightEditText.setOnFocusChangeListener(this);

        weightEditText = (EditText) getActivity().findViewById(R.id.weightEditText);
        weightEditText.setOnFocusChangeListener(this);

        hairTextView = (EditText) getActivity().findViewById(R.id.hairTextView);

        favoriteBrandText = (TextView) getActivity().findViewById(R.id.brandTextView);

        changePwText = (TextView) getActivity().findViewById(R.id.u02_change_pw_text);

        textWidth = changePwText.getPaint().measureText(changePwText.getText().toString());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (resultCode == Activity.RESULT_OK && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);

                cursor.close();

                if (requestCode == TYPE_PORTRAIT) {
                    uploadImage(imgDecodableString, TYPE_PORTRAIT);
                }

                if (requestCode == TYPE_BACKGROUD) {
                    uploadImage(imgDecodableString, TYPE_BACKGROUD);
                }

            } else {
                Toast.makeText(context, "您未选择图片！",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            //e.printStackTrace();
            Toast.makeText(context, "未知错误，请重试！", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void uploadImage(final String imgUri, final int type) {

        String api = "";

        if (type == TYPE_PORTRAIT) {
            api = QSAppWebAPI.getUserUpdateportrait();
        } else {
            api = QSAppWebAPI.getUserUpdatebackground();
        }
        String API = api;
        QSMultipartRequest multipartRequest = new QSMultipartRequest(Request.Method.POST,
                API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MongoPeople user = UserParser.parseUpdate(response);
                if (user == null) {
                    ErrorHandler.handle(context, MetadataParser.getError(response));
                } else {
                    QSModel.INSTANCE.setUser(user);
                    context.sendBroadcast(new Intent(U01PersonalActivity.USER_UPDATE));
                }
                if (type == TYPE_PORTRAIT) {
                    ImageLoader.getInstance().displayImage(user.getPortrait(), portraitImageView, AppUtil.getPortraitDisplayOptions());
                }
                if (type == TYPE_BACKGROUD) {
                    ImageLoader.getInstance().displayImage(user.getPortrait(), portraitImageView, AppUtil.getModelBackgroundDisplayOptions());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
// 获取MultipartEntity对象
        QSMultipartEntity multipartEntity = multipartRequest.getMultiPartEntity();
        multipartEntity.addStringPart("content", "hello");
// 文件参数
        File file = new File(imgUri);
        multipartEntity.addFilePart("image", file);
        multipartEntity.addStringPart("filename", file.getName());


// 构建请求队列
// 将请求添加到队列中
        requestQueue.add(multipartRequest);
    }

    //进入页面时，给字段赋值
    private void setData() {
        if (null != people) {

            ImageLoader.getInstance().displayImage(people.portrait, portraitImageView, AppUtil.getPortraitDisplayOptions());
            ImageLoader.getInstance().displayImage(people.background, backgroundImageView, AppUtil.getModelBackgroundDisplayOptions());


            nameEditText.setText(people.name);
            if (!("".equals(people.birthday) || null == people.birthday)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Calendar calendar = TimeUtil.getStringToCal(people.birthday);
                Date date = calendar.getTime();
                birthEditText.setText(simpleDateFormat.format(date));
            }
            heightEditText.setText(people.height);
            weightEditText.setText(people.weight);
            sexTextView.setText(sexArgs[people.gender]);
            sexTextView.setTag(people.gender);
            hairTextView.setText(hairArgs[people.hairType]);
            hairTextView.setTag(people.hairType);
            shoesSizeEditText.setText(people.shoeSize + "");
            clothesSizeEditText.setTag(people.clothingSize);
            clothesSizeEditText.setText(clothesSize[people.clothingSize]);
            favoriteBrandText.setText(people.favoriteBrand);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        commitForm();
        getUser();
        UserCommand.refresh();
    }

    //获得用户信息
    private void getUser() {

        people = QSModel.INSTANCE.getUser();
        if(people != null ) setData();
        else UserCommand.refresh(new Callback(){
            @Override
            public void onComplete() {
                super.onComplete();
                setData();
            }
            @Override
            public void onError() {
                super.onError();
                Toast.makeText(U02SettingsFragment.this.getActivity(), "网络请求错误", Toast.LENGTH_SHORT).show();
            }
        });

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
        }

        if (TAG_CLOTHESSIZE.equals(String.valueOf(actionSheet.getTag()))) {
            clothesSizeEditText.setText(clothesSize[index]);
            clothesSizeEditText.setTag(index);
        }
        commitForm();
    }

    private void commitForm() {
        Map<String, String> params = new HashMap<String, String>();
        if (nameEditText != null && !nameEditText.getText().toString().equals(""))
            params.put("name", nameEditText.getText().toString());
        if (birthEditText != null && !birthEditText.getText().toString().equals(""))
            params.put("birthday", birthEditText.getText().toString());
        if (heightEditText != null && !heightEditText.getText().toString().equals(""))
            params.put("height", heightEditText.getText().toString());
        if (weightEditText != null && !weightEditText.getText().toString().equals(""))
            params.put("weight", weightEditText.getText().toString());
        if (shoesSizeEditText != null && !shoesSizeEditText.getText().toString().equals(""))
            params.put("shoeSize", shoesSizeEditText.getText().toString());
        if (null != sexTextView.getTag()) {
            params.put("gender", sexTextView.getTag().toString());
        }
        if (null != hairTextView.getTag()) {
            params.put("hairType", hairTextView.getTag().toString());
        }
        if (null != clothesSizeEditText.getTag()) {
            params.put("clothingSize", clothesSizeEditText.getTag().toString());
        }
        if (!"".equals(favoriteBrandText.getText())) {
            params.put("favoriteBrand", favoriteBrandText.getText().toString());
        }

        UserCommand.update(params,new Callback(){
            @Override
            public void onComplete() {
                super.onComplete();
                context.sendBroadcast(new Intent(U01PersonalActivity.USER_UPDATE));
            }

            @Override
            public void onError(int errorCode) {
                super.onError(errorCode);
                ErrorHandler.handle(context, errorCode);
            }

            @Override
            public void onError() {
                super.onError();
                Toast.makeText(context, "请检查网络", Toast.LENGTH_LONG).show();
            }
        });
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
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "请选择背景"), TYPE_BACKGROUD);
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
        birthRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.birthRelativeLayout);
        birthRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] dateStr = new String[3];
                String dateString = people.birthday;
                if (("".equals(dateString)) || null == dateString) {
                    dateString = "1970/01/01";
                }
                dateStr = dateString.split("/");
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                        birthEditText.setText(i + "/" + (i2 + 1) + "/" + i3);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(i, i2, i3);
                        birthEditText.setTag(calendar.getTime());
                        commitForm();
                    }
                }, Integer.parseInt(dateStr[0]), Integer.parseInt(dateStr[1])-1, Integer.parseInt(dateStr[2]));

                datePickerDialog.show();
            }
        });


        changePasswordRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.changePasswordRelativeLayout);
        changePasswordRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02ChangePasswordFragment fragment = new U02ChangePasswordFragment();
                getFragmentManager().beginTransaction().setCustomAnimations(R.anim.push_left_in, 0,R.anim.push_left_in, 0).
                        replace(R.id.settingsScrollView, fragment).commit();
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
}
