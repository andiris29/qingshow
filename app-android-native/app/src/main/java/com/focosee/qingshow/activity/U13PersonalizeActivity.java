package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;

import org.json.JSONObject;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

/**
 * Created by DylanJiang on 15/5/22.
 */
public class U13PersonalizeActivity extends BaseActivity {
    @Override
    public void reconn() {

    }

    @InjectView(R.id.age)
    SeekBar age;
    @InjectView(R.id.height)
    SeekBar height;
    @InjectView(R.id.weight)
    SeekBar weight;

    @InjectView(R.id.age_text)
    TextView ageText;
    @InjectView(R.id.height_text)
    TextView heightText;
    @InjectView(R.id.weight_text)
    TextView weightText;

    @InjectViews({R.id.body_a, R.id.body_h, R.id.body_v, R.id.body_x})
    List<RadioButton> bodys;
    @InjectViews({R.id.style_j, R.id.style_u})
    List<RadioButton> clothes;
    @InjectViews({R.id.result_0, R.id.result_1, R.id.result_2, R.id.result_3, R.id.result_4, R.id.result_5})
    List<RadioButton> results;

    private int checkBodyNum;
    private int checkClothesNum;
    private int[] checkResultNums;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_u13_personalize);
        ButterKnife.inject(this);
        initSeekBar(age, ageText, 18, 17);
        initSeekBar(height, heightText, 150, 20);
        initSeekBar(weight, weightText, 40, 40);
    }

    @OnClick({R.id.body_a, R.id.body_h, R.id.body_v, R.id.body_x})
    public void onBodyStyleClick(RadioButton radio) {
        checkBodyNum = initSingleChecked(bodys, radio);
    }

    @OnClick({R.id.style_j, R.id.style_u})
    public void onClothesStyleClick(RadioButton radio) {
        checkClothesNum = initSingleChecked(clothes, radio);
    }

    @OnClick({R.id.result_0, R.id.result_1, R.id.result_2, R.id.result_3, R.id.result_4, R.id.result_5})
    public void onResultClick(RadioButton radio) {
        for (int i = 0; i < results.size(); i++) {
            RadioButton result = results.get(i);
            if (radio.getId() == result.getId()) {
                if (result.isChecked()) {
                    result.setChecked(false);
                } else {
                    result.setChecked(true);
                }
            }
        }
    }

    @OnClick(R.id.submit)
    public void submitToNet(){
        JSONObject jsonObject = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, QSAppWebAPI.getUpdateServiceUrl(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        },null);
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private int initSingleChecked(List<RadioButton> radios, View view) {
        int checkIndex = 0;
        for (int i = 0; i < radios.size(); i++) {
            RadioButton item = radios.get(i);
            if (view.getId() == item.getId()) {
                checkIndex = i;
                item.setChecked(true);
            } else {
                item.setChecked(false);
            }
        }
        return checkIndex;
    }

    private void initSeekBar(SeekBar bar, final TextView text, final int startNum, int copies) {
        text.setText(startNum + "");
        bar.setProgress(1);
        bar.setMax(copies);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                text.setText(i + startNum + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
