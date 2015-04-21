package com.focosee.qingshow.activity.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.focosee.qingshow.R;

public class U02AboutVIPFragment extends Fragment {
    private TextView backTextView;

    public U02AboutVIPFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_u02_about_vi, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        backTextView = (TextView) getActivity().findViewById(R.id.backTextView);
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02SettingsFragment settingsFragment = new U02SettingsFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, settingsFragment).commit();
            }
        });
    }

}
