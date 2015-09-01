package com.focosee.qingshow.activity.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S01MatchShowsActivity;
import com.focosee.qingshow.activity.U01UserActivity;
import com.focosee.qingshow.activity.U10AddressListActivity;
import com.focosee.qingshow.activity.UserUpdatedEvent;
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
import com.focosee.qingshow.widget.LoadingDialogs;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class U02SettingsFragment extends MenuFragment implements View.OnFocusChangeListener, ActionSheet.ActionSheetListener {

    private static final String TAG = U02SettingsFragment.class.getSimpleName();

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
    ImageView backTextView;
    EditText ageEditText;
    Button quitButton;
    ImageButton navigationBtnMatch;
    ImageButton navigationBtnGoodMatch;
    ImageButton navigationBtnDiscount;
    ImageButton u01People;
    RelativeLayout personalRelativeLayout;
    RelativeLayout backgroundRelativeLayout;
    RelativeLayout bodyTypeRelativeLayout;
    RelativeLayout changePasswordRelativeLayout;
    RelativeLayout addresslistRelativeLayout;
    RelativeLayout dressStyleRelativeLayout;
    RelativeLayout effectRelativeLayout;
    ImageView portraitImageView;
    ImageView backgroundImageView;
    EditText nameEditText;
    EditText heightEditText;
    EditText weightEditText;
    EditText bustEditText;
    EditText shoulderEditText;
    EditText waistlineEditText;
    EditText hiplineEditText;
    TextView bodyTypeTextView;
    TextView dressStyleEditText;
    TextView effectEditText;
    TextView changePwText;
    public static U02SettingsFragment instance;

    private MongoPeople people;
    private LoadingDialogs dialog;

    public static U02SettingsFragment newIntance() {
        return new U02SettingsFragment();
    }

    public U02SettingsFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        dialog = new LoadingDialogs(getActivity(),R.style.dialog);
        if (null != savedInstanceState) {
            people = (MongoPeople) savedInstanceState.getSerializable("people");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_u02_settings, container, false);
        ((ImageView) view.findViewById(R.id.s17_settting)).setImageResource(R.drawable.root_menu_setting_gray);
        ((TextView) view.findViewById(R.id.s17_settting_tv)).setTextColor(getResources().getColor(R.color.darker_gray));
        matchUI(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUser();
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
                Toast.makeText(getActivity(), "已退出登录", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), S01MatchShowsActivity.class);
                startActivity(intent);
                GoToWhereAfterLoginModel.INSTANCE.set_class(U01UserActivity.class);
                getActivity().finish();
            }
        });
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

            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "未知错误，请重试！（只能传本地图片）", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void uploadImage(final String imgUri, final int type) {

        String api = "";
        File file = new File(imgUri);
        if (type == TYPE_PORTRAIT) {
            api = QSAppWebAPI.getUserUpdateportrait();
        } else {
            api = QSAppWebAPI.getUserUpdatebackground();
        }
        String API = api;

        dialog.show();
        QSMultipartRequest multipartRequest = new QSMultipartRequest(Request.Method.POST,
                API, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "response:" + response);
                dialog.dismiss();
                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(getActivity(), MetadataParser.getError(response));
                    return;
                }
                MongoPeople user = UserParser._parsePeople(response);
                if (user != null) {
                    if(TYPE_PORTRAIT == type){
                        portraitImageView.setImageURI(Uri.parse(ImgUtil.getImgSrc(user.portrait, ImgUtil.PORTRAIT_LARGE)));
                    }else{
                        backgroundImageView.setImageURI(Uri.parse(user.background));
                    }
                    UserCommand.refresh();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        QSMultipartEntity multipartEntity = multipartRequest.getMultiPartEntity();
        multipartEntity.addStringPart("content", "hello");
        multipartEntity.addFilePart("image", file);
        multipartEntity.addStringPart("filename", file.getName());
        RequestQueueManager.INSTANCE.getQueue().add(multipartRequest);
    }

    private void matchUI(View view){
        backTextView = (ImageView) view.findViewById(R.id.backTextView);
        ageEditText = (EditText) view.findViewById(R.id.ageEditText);
        quitButton = (Button) view.findViewById(R.id.quitButton);
        navigationBtnMatch = (ImageButton) view.findViewById(R.id.navigation_btn_match);
        navigationBtnGoodMatch = (ImageButton) view.findViewById(R.id.navigation_btn_good_match);
        navigationBtnDiscount = (ImageButton) view.findViewById(R.id.navigation_btn_discount);
        u01People = (ImageButton) view.findViewById(R.id.u01_people);
        personalRelativeLayout = (RelativeLayout) view.findViewById(R.id.personalRelativeLayout);
        backgroundRelativeLayout = (RelativeLayout) view.findViewById(R.id.backgroundRelativeLayout);
        bodyTypeRelativeLayout = (RelativeLayout) view.findViewById(R.id.bodyTypeRelativeLayout);
        changePasswordRelativeLayout = (RelativeLayout) view.findViewById(R.id.changePasswordRelativeLayout);
        addresslistRelativeLayout = (RelativeLayout) view.findViewById(R.id.addresslist_RelativeLayout);
        dressStyleRelativeLayout = (RelativeLayout) view.findViewById(R.id.dressStyleEelativeLayout);
        effectRelativeLayout = (RelativeLayout) view.findViewById(R.id.effectEelativeLayout);
        portraitImageView = (ImageView) view.findViewById(R.id.portraitImageView);
        backgroundImageView = (ImageView) view.findViewById(R.id.backgroundImageView);
        nameEditText = (EditText) view.findViewById(R.id.nameEditText);
        heightEditText = (EditText) view.findViewById(R.id.heightEditText);
        weightEditText = (EditText) view.findViewById(R.id.weightEditText);
        bustEditText = (EditText) view.findViewById(R.id.bustEditText);
        shoulderEditText = (EditText) view.findViewById(R.id.shoulderEditText);
        waistlineEditText = (EditText) view.findViewById(R.id.waistlineEditText);
        hiplineEditText = (EditText) view.findViewById(R.id.hiplineEditText);
        bodyTypeTextView = (TextView) view.findViewById(R.id.bodyTypeTextView);
        dressStyleEditText = (TextView) view.findViewById(R.id.dressStyleEditText);
        effectEditText = (TextView) view.findViewById(R.id.effectEditText);
        changePwText = (TextView) view.findViewById(R.id.u02_change_pw_text);

        drawer = (DrawerLayout) view.findViewById(R.id.drawer);
        navigation = (LinearLayout) view.findViewById(R.id.navigation);
        blur = (ImageView) view.findViewById(R.id.blur);
        right = (LinearLayout) view.findViewById(R.id.context);
        settingBtn = (ImageView) view.findViewById(R.id.s17_settting);
    }

    //进入页面时，给字段赋值
    private void setData() {
        if (null != people) {
            if (null != people.portrait) {
                portraitImageView.setImageURI(Uri.parse(ImgUtil.getImgSrc(people.portrait, ImgUtil.PORTRAIT_LARGE)));
                portraitImageView.setAlpha(1f);
            }
            if (null != people.background) {
                backgroundImageView.setImageURI(Uri.parse(people.background));
                backgroundImageView.setAlpha(1f);
            }
            if (null != people.nickname)
                nameEditText.setText(people.nickname);
            if (null != people.age)
                ageEditText.setText(String.valueOf(people.age));
            if (null != people.height)
                heightEditText.setText(String.valueOf(people.height));
            if (null != people.weight)
                weightEditText.setText(String.valueOf(people.weight));
            if(null != people.measureInfo){
                if(null != people.measureInfo.bust){
                    bustEditText.setText(String.valueOf(people.measureInfo.bust));
                }
                if(null != people.measureInfo.shoulder){
                    shoulderEditText.setText(String.valueOf(people.measureInfo.shoulder));
                }
                if(null != people.measureInfo.waist){
                    waistlineEditText.setText(String.valueOf(people.measureInfo.waist));
                }
                if(null != people.measureInfo.hips){
                    hiplineEditText.setText(String.valueOf(people.measureInfo.hips));
                }
            }
            bodyTypeTextView.setText(bodyTypeArgs[people.bodyType]);
            bodyTypeTextView.setTag(people.bodyType);
            dressStyleEditText.setText(dressStyles[people.dressStyle]);
            dressStyleEditText.setTag(people.dressStyle);
            if (null != people.expectations && people.expectations.length != 0) {
                setEffectEditText(people.expectations);
            }
        }
    }

    private void setEffectEditText(int[] args){
        String effectStr = "";
        for (int index : args) {
            effectStr += expectations[index] + " ";
        }
        if (effectStr.length() > 0)
            effectStr = effectStr.substring(0, effectStr.length() - 1);
        effectEditText.setText(effectStr);
        effectEditText.setTag(expectations);
    }

    public void onEventMainThread(UserUpdatedEvent event){
        if(null != event.user && null != effectEditText){
            setEffectEditText(event.user.expectations);
            initUser();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("people", people);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(v.getId() == R.id.quitButton){
            if(v.isFocused())return;
        }
        commitForm();
        UserCommand.refresh();
    }

    //获得用户信息
    private void initUser() {

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
        Map measureInfo = new HashMap();
        if (!TextUtils.isEmpty(nameEditText.getText().toString()))
            params.put("nickname", nameEditText.getText().toString());
        if (!TextUtils.isEmpty(ageEditText.getText().toString()))
            params.put("age", ageEditText.getText().toString());
        if (!TextUtils.isEmpty(heightEditText.getText().toString()))
            params.put("height", heightEditText.getText().toString());
        if (!TextUtils.isEmpty(weightEditText.getText().toString()))
            params.put("weight", weightEditText.getText().toString());
        if (!TextUtils.isEmpty(bustEditText.getText().toString()))
            measureInfo.put("bust", bustEditText.getText().toString());
        if (!TextUtils.isEmpty(shoulderEditText.getText().toString()))
            measureInfo.put("shoulder", shoulderEditText.getText().toString());
        if (!TextUtils.isEmpty(waistlineEditText.getText().toString()))
            measureInfo.put("waist", waistlineEditText.getText().toString());
        if (!TextUtils.isEmpty(hiplineEditText.getText().toString()))
            measureInfo.put("hips", hiplineEditText.getText().toString());
        if (null != bodyTypeTextView.getTag())
            params.put("bodyType", bodyTypeTextView.getTag().toString());
        if (null != dressStyleEditText.getTag())
            params.put("dressStyle", dressStyleEditText.getTag().toString());

        params.put("measureInfo", measureInfo);
        UserCommand.update(params, new Callback() {
            @Override
            public void onComplete() {
                super.onComplete();
            }

            @Override
            public void onError(int errorCode) {
                super.onError(errorCode);
                ErrorHandler.handle(getActivity(), errorCode);
            }

            @Override
            public void onError() {
                super.onError();
                Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setJumpListener() {
        navigationBtnMatch.setOnClickListener(this);
        navigationBtnGoodMatch.setOnClickListener(this);
        navigationBtnDiscount.setOnClickListener(this);
        u01People.setOnClickListener(this);
        settingBtn.setOnClickListener(this);

        ageEditText.setOnFocusChangeListener(this);
        heightEditText.setOnFocusChangeListener(this);
        weightEditText.setOnFocusChangeListener(this);
        bustEditText.setOnFocusChangeListener(this);
        shoulderEditText.setOnFocusChangeListener(this);
        waistlineEditText.setOnFocusChangeListener(this);
        hiplineEditText.setOnFocusChangeListener(this);

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
                U02ChangePasswordFragment fragment;
                if(null == getFragmentManager().findFragmentByTag(U02ChangePasswordFragment.class.getSimpleName())) {
                    fragment = new U02ChangePasswordFragment();
                }else{
                    fragment = (U02ChangePasswordFragment)getFragmentManager().findFragmentByTag(U02ChangePasswordFragment.class.getSimpleName());
                }
                getFragmentManager().beginTransaction().setCustomAnimations(R.anim.push_right_in, 0, R.anim.push_left_out, 0).
                        replace(R.id.settingsScrollView, fragment).commit();
                U02Model.INSTANCE.set_class(U02ChangePasswordFragment.class);
            }
        });

        addresslistRelativeLayout.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(getActivity(), U10AddressListActivity.class);
                 startActivity(intent);
             }
         });

        dressStyleRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setTheme(R.style.ActionSheetStyleIOS7);
                showActionSheet(TAG_DRESSSTYLE);
            }
        });

        effectRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                U02SelectExceptionFragment fragment;
                if (null == getFragmentManager().findFragmentByTag(U02SelectExceptionFragment.class.getSimpleName()))
                    fragment = new U02SelectExceptionFragment();
                else
                    fragment = (U02SelectExceptionFragment) getFragmentManager().findFragmentByTag(U02SelectExceptionFragment.class.getSimpleName());
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", people);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().setCustomAnimations(R.anim.push_right_in, 0, 0, 0).
                        replace(R.id.settingsScrollView, fragment).commit();
                U02Model.INSTANCE.set_class(U02SelectExceptionFragment.class);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U02SettingsFragment"); //统计页面
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U02SettingsFragment");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}