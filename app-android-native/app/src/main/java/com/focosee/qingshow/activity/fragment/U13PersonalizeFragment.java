package com.focosee.qingshow.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.widget.radio.MyRadioGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

/**
 * Created by DylanJiang on 15/5/6.
 */
public class U13PersonalizeFragment extends Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_u13_personalize, container, false);
        ButterKnife.inject(this, view);
        initSeekBar(age, ageText, 1, 100);
        initSeekBar(height, heightText, 150, 50);
        initSeekBar(weight, weightText, 30, 70);
        return view;
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
