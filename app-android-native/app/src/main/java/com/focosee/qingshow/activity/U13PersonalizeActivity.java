package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    List<CheckedTextView> results;

    private int checkBodyNum;
    private int checkClothesNum;


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
        checkBodyNum = initBodyTypeSingleChecked(bodys, radio);
    }

    @OnClick({R.id.style_j, R.id.style_u})
    public void onClothesStyleClick(RadioButton radio) {
        checkClothesNum = initStyleSingleChecked(clothes, radio);
    }

    @OnClick({R.id.result_0, R.id.result_1, R.id.result_2, R.id.result_3, R.id.result_4, R.id.result_5})
    public void onResultClick(CheckedTextView ctv) {

        for (int i = 0; i < results.size(); i++) {
            CheckedTextView result = results.get(i);
            if (ctv.getId() == result.getId()) {
                result.setChecked(ctv.isChecked());
                if (result.isChecked()) {
                    result.setChecked(false);
                    ctv.setTextColor(getResources().getColor(R.color.white));
                } else {
                    result.setChecked(true);
                    ctv.setTextColor(getResources().getColor(R.color.master_pink));
                }
            }
        }
    }

    @OnClick(R.id.submit)
    public void submitToNet() {
        Map map = new HashMap();
        map.put("age", ageText.getText().toString());
        map.put("height", heightText.getText().toString());
        map.put("weight", weightText.getText().toString());
        map.put("bodyType", checkBodyNum);
        map.put("dressStyle", checkClothesNum);
        List<Integer> exceptions = new ArrayList<>();
        int i = 0;
        for (CheckedTextView ctv : results){
            if(ctv.isChecked()){
                exceptions.add(i);
            }
            i++;
        }
        map.put("expectations", exceptions);
        UserCommand.update(map, new Callback() {
            @Override
            public void onComplete() {
                super.onComplete();
                U13PersonalizeActivity.this.finish();
            }

            @Override
            public void onError(int errorCode) {
                super.onError(errorCode);
                ErrorHandler.handle(U13PersonalizeActivity.this, errorCode);
            }
        });
    }

    private int initBodyTypeSingleChecked(List<RadioButton> radios, View view) {
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

    private int initStyleSingleChecked(List<RadioButton> radios, View view) {
        int checkIndex = 0;
        for (int i = 0; i < radios.size(); i++) {
            RadioButton item = radios.get(i);
            if (view.getId() == item.getId()) {
                checkIndex = i;
                item.setChecked(true);
                item.setTextColor(getResources().getColor(R.color.master_pink));
            } else {
                item.setChecked(false);
                item.setTextColor(getResources().getColor(R.color.white));
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
