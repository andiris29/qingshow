package com.focosee.qingshow.activity.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.U01UserActivity;
import com.focosee.qingshow.activity.U06LoginActivity;
import com.focosee.qingshow.activity.U09TradeListActivity;
import com.focosee.qingshow.activity.U10AddressListActivity;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.QSMultipartEntity;
import com.focosee.qingshow.httpapi.request.QSMultipartRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.GoToWhereAfterLoginModel;
import com.focosee.qingshow.model.PushModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.U02Model;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.persist.CookieSerializer;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.widget.ActionSheet;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class U02SettingsFragment extends MenuFragment implements View.OnFocusChangeListener, ActionSheet.ActionSheetListener {

    private static final String[] sexArgs = {"男", "女"};
    private static final String[] bodyTypeArgs = {"A型", "H型", "V型", "X型"};
    private static final String[] dressStyles = {"日韩系", "欧美系"};
    private static final String[] expectations = {"显瘦", "显高", "显身材", "遮臀部", "遮肚腩", "遮手臂"};
    private static final String TAG_BODYTYPE = "bodyType";
    private static final String TAG_SEX = "sex";
    private static final String TAG_DRESSSTYLE = "dressStyle";
    private static final String TAG_EXPECTATIONS = "expectations";
    private static final int TYPE_PORTRAIT = 10000;//上传头像
    private static final int TYPE_BACKGROUD = 10001;//上传背景
    @InjectView(R.id.backTextView)
    ImageView backTextView;
    @InjectView(R.id.ageEditText)
    EditText ageEditText;
    @InjectView(R.id.quitButton)
    Button quitButton;
    @InjectView(R.id.navigation_btn_match)
    ImageButton navigationBtnMatch;
    @InjectView(R.id.navigation_btn_good_match)
    ImageButton navigationBtnGoodMatch;
    @InjectView(R.id.u01_people)
    ImageButton u01People;

    private Context context;
    private RequestQueue requestQueue;

    @InjectView(R.id.personalRelativeLayout)
    RelativeLayout personalRelativeLayout;
    @InjectView(R.id.backgroundRelativeLayout)
    RelativeLayout backgroundRelativeLayout;
    @InjectView(R.id.sexRelativeLayout)
    RelativeLayout sexRelativeLayout;
    @InjectView(R.id.bodyTypeRelativeLayout)
    RelativeLayout bodyTypeRelativeLayout;
    @InjectView(R.id.changePasswordRelativeLayout)
    RelativeLayout changePasswordRelativeLayout;
    @InjectView(R.id.tradelistRelativeLayout)
    RelativeLayout tradeRelativeLayout;
    @InjectView(R.id.addresslist_RelativeLayout)
    RelativeLayout addresslistRelativeLayout;
    @InjectView(R.id.dressStyleEelativeLayout)
    RelativeLayout dressStyleRelativeLayout;
    @InjectView(R.id.effectEelativeLayout)
    RelativeLayout effectRelativeLayout;
    @InjectView(R.id.portraitImageView)
    ImageView portraitImageView;
    @InjectView(R.id.backgroundImageView)
    ImageView backgroundImageView;
    @InjectView(R.id.nameEditText)
    EditText nameEditText;
    @InjectView(R.id.sexTextView)
    TextView sexTextView;
    @InjectView(R.id.heightEditText)
    EditText heightEditText;
    @InjectView(R.id.weightEditText)
    EditText weightEditText;
    @InjectView(R.id.bodyTypeTextView)
    TextView bodyTypeTextView;
    @InjectView(R.id.dressStyleEditText)
    TextView dressStyleEditText;
    @InjectView(R.id.effectEditText)
    TextView effectEditText;
    @InjectView(R.id.u02_change_pw_text)
    TextView changePwText;
    public static U02SettingsFragment instance;

    private MongoPeople people;

    public static U02SettingsFragment newIntance() {
        return new U02SettingsFragment();
    }

    public U02SettingsFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != savedInstanceState) {
            people = (MongoPeople) savedInstanceState.getSerializable("people");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_u02_settings, container, false);
        ButterKnife.inject(this, view);
        context = getActivity().getApplicationContext();
        requestQueue = RequestQueueManager.INSTANCE.getQueue();

        getUser();
        setJumpListener();
        initDrawer();

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                QSModel.INSTANCE.removeUser();

                Map map = new HashMap();
                Log.i("JPush_QS", "logout" + PushModel.INSTANCE.getRegId());
                map.put("registrationId", PushModel.INSTANCE.getRegId());
                QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.USER_LOGOUT, new JSONObject(map), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (MetadataParser.hasError(response)) {
                            ErrorHandler.handle(getActivity(), MetadataParser.getError(response));
                            return;
                        }
                        CookieSerializer.INSTANCE.saveCookie("");
                    }
                });
                RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
                Toast.makeText(context, "已退出登录", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), U06LoginActivity.class);
                startActivity(intent);
                GoToWhereAfterLoginModel.INSTANCE.set_class(U01UserActivity.class);
                getActivity().finish();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
            Toast.makeText(context, "未知错误，请重试！（只能传本地图片）", Toast.LENGTH_LONG)
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
                API, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MongoPeople user = UserParser._parsePeople(response);
                if (user == null) {
                    ErrorHandler.handle(context, MetadataParser.getError(response));
                } else {
                    getUser();
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
            if (null != people.portrait) {
                portraitImageView.setImageURI(Uri.parse(people.portrait));
                portraitImageView.setAlpha(1f);
            }
            if (null != people.background) {
                backgroundImageView.setImageURI(Uri.parse(people.background));
                backgroundImageView.setAlpha(1f);
            }
            if (null != people.nickname)
                nameEditText.setText(people.nickname);
            if (0 != people.age)
                ageEditText.setText(String.valueOf(people.age));
            if (0 != people.height)
                heightEditText.setText(String.valueOf(people.height));
            if (0 != people.height)
                weightEditText.setText(String.valueOf(people.weight));
            sexTextView.setText(sexArgs[people.gender]);
            sexTextView.setTag(people.gender);

            bodyTypeTextView.setText(bodyTypeArgs[people.bodyType]);
            bodyTypeTextView.setTag(people.bodyType);
            dressStyleEditText.setText(dressStyles[people.dressStyle]);
            dressStyleEditText.setTag(people.dressStyle);
            if (null != people.expectations && people.expectations.length != 0) {
                String effectStr = "";
                for (int index : people.expectations) {
                    effectStr += expectations[index] + "|";
                }
                if (effectStr.length() > 0)
                    effectStr = effectStr.substring(0, effectStr.length() - 1);
                effectEditText.setText(effectStr);
                effectEditText.setTag(people.expectations);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getUser();
        outState.putSerializable("people", people);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        commitForm();
        UserCommand.refresh();
    }

    //获得用户信息
    private void getUser() {

        UserCommand.refresh(new Callback() {
            @Override
            public void onComplete() {
                super.onComplete();
                people = QSModel.INSTANCE.getUser();
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

        if (TAG_BODYTYPE.equals(type)) {
            ActionSheet.createBuilder(getActivity(), getFragmentManager())
                    .setTag(TAG_BODYTYPE)
                    .setCancelButtonTitle("取消")
                    .setOtherButtonTitles(bodyTypeArgs)
                    .setCancelableOnTouchOutside(true).setListener(this).show();
        }

        if (TAG_DRESSSTYLE.equals(type)) {
            ActionSheet.createBuilder(getActivity(), getFragmentManager())
                    .setTag(TAG_DRESSSTYLE)
                    .setCancelButtonTitle("取消")
                    .setOtherButtonTitles(dressStyles)
                    .setCancelableOnTouchOutside(true).setListener(this).show();
        }

        if (TAG_EXPECTATIONS.equals(type)) {
            ActionSheet.createBuilder(getActivity(), getFragmentManager())
                    .setTag(TAG_EXPECTATIONS)
                    .setCancelButtonTitle("取消")
                    .setOtherButtonTitles(expectations)
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

        if (TAG_BODYTYPE.equals(String.valueOf(actionSheet.getTag()))) {
            bodyTypeTextView.setText(bodyTypeArgs[index]);
            bodyTypeTextView.setTag(index);
        }

        if (TAG_DRESSSTYLE.equals(String.valueOf(actionSheet.getTag()))) {
            dressStyleEditText.setText(dressStyles[index]);
            dressStyleEditText.setTag(index);
        }

        if (TAG_EXPECTATIONS.equals(String.valueOf(actionSheet.getTag()))) {
            effectEditText.setText(expectations[index]);
            effectEditText.setTag(index);
        }

        commitForm();
    }

    private void commitForm() {
        Map params = new HashMap();
        if (!nameEditText.getText().toString().equals(""))
            params.put("name", nameEditText.getText().toString());
        if (!ageEditText.getText().toString().equals(""))
            params.put("age", ageEditText.getText().toString());
        if (!heightEditText.getText().toString().equals(""))
            params.put("height", heightEditText.getText().toString());
        if (!weightEditText.getText().toString().equals(""))
            params.put("weight", weightEditText.getText().toString());
        if (null != sexTextView.getTag())
            params.put("gender", sexTextView.getTag().toString());
        if (null != bodyTypeTextView.getTag())
            params.put("bodyType", bodyTypeTextView.getTag().toString());
        if (null != dressStyleEditText.getTag())
            params.put("dressStyle", dressStyleEditText.getTag().toString());
        UserCommand.update(params, new Callback() {
            @Override
            public void onComplete() {
                super.onComplete();
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
        navigationBtnMatch.setOnClickListener(this);
        navigationBtnGoodMatch.setOnClickListener(this);
        u01People.setOnClickListener(this);
        settingBtn.setOnClickListener(this);

        ageEditText.setOnFocusChangeListener(this);
        heightEditText.setOnFocusChangeListener(this);
        weightEditText.setOnFocusChangeListener(this);

        backTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                commitForm();
                menuSwitch();
            }
        });

        personalRelativeLayout.setOnClickListener(new View.OnClickListener(){
              @Override
              public void onClick(View view) {
                  Intent intent = new Intent();
                  intent.setType("image/*");
                  intent.setAction(Intent.ACTION_GET_CONTENT);
                  startActivityForResult(Intent.createChooser(intent, "请选择头像"), TYPE_PORTRAIT);
              }
        });
        backgroundRelativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "请选择背景"), TYPE_BACKGROUD);
            }
        });

        sexRelativeLayout.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View view) {
                 getActivity().setTheme(R.style.ActionSheetStyleIOS7);
                 showActionSheet(TAG_SEX);
             }
        });

        bodyTypeRelativeLayout.setOnClickListener(new View.OnClickListener(){
              @Override
              public void onClick(View view) {
                  getActivity().setTheme(R.style.ActionSheetStyleIOS7);
                  showActionSheet(TAG_BODYTYPE);
              }
        });

        changePasswordRelativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                U02ChangePasswordFragment fragment = new U02ChangePasswordFragment();
                getFragmentManager().beginTransaction().setCustomAnimations(R.anim.push_left_in, 0, R.anim.push_left_in, 0).
                        replace(R.id.settingsScrollView, fragment).commit();
                U02Model.INSTANCE.set_class(U02ChangePasswordFragment.class);
            }
        });

        tradeRelativeLayout.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getActivity(), U09TradeListActivity.class);
               startActivity(intent);
           }
       });

        addresslistRelativeLayout.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(getActivity(), U10AddressListActivity.class);
                 startActivity(intent);
             }
         });

        dressStyleRelativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getActivity().setTheme(R.style.ActionSheetStyleIOS7);
                showActionSheet(TAG_DRESSSTYLE);
            }
        });

        effectRelativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                U02SelectExceptionFragment fragment = new U02SelectExceptionFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", people);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().setCustomAnimations(R.anim.push_left_in, 0, R.anim.push_left_in, 0).
                        replace(R.id.settingsScrollView, fragment).commit();
                U02Model.INSTANCE.set_class(U02SelectExceptionFragment.class);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
