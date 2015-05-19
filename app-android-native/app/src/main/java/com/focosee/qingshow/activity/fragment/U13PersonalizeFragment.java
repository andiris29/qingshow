package com.focosee.qingshow.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.focosee.qingshow.R;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.InjectViews;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_u13_personalize, container, false);
        initSeekBar(age,ageText,1,1,100);
        initSeekBar(height,heightText,150,1,50);
        initSeekBar(weight,weightText,30,1,70);
        return view;
    }

    private void initSeekBar(SeekBar bar,final TextView text, final int startNum,final int offset,int copies) {
        bar.setProgress(startNum);
        bar.setMax(copies);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                text.setText(i + startNum + offset + "");
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
