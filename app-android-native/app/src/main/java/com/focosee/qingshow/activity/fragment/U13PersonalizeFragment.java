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

    @InjectViews({R.id.body_a,R.id.body_h,R.id.body_v,R.id.body_x})
    List<RadioButton> bodys;

    private int checkBodyNum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_u13_personalize, container, false);
        ButterKnife.inject(this, view);
        initSeekBar(age, ageText, 1, 100);
        initSeekBar(height,heightText,150,50);
        initSeekBar(weight, weightText, 30, 70);
        return view;
    }

    @OnClick({R.id.body_a,R.id.body_h,R.id.body_v,R.id.body_x})
    public void initBodyStyle(RadioButton radio) {
        for (int i = 0; i < bodys.size(); i++) {
            RadioButton body = bodys.get(i);
            if (radio.getId() == body.getId()){
                checkBodyNum = i;
                body.setChecked(true);
            }else {
                body.setChecked(false);
            }
        }
    }

    private void initSeekBar(SeekBar bar,final TextView text, final int startNum,int copies) {
        text.setText(startNum + "");
        bar.setProgress(1);
        bar.setMax(copies);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                text.setText(i + startNum  + "");
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
