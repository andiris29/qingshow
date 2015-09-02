package com.focosee.qingshow.activity.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.umeng.analytics.MobclickAgent;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link WelComeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelComeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private int backgroundValue;

    private TextView title;
    private TextView describe;
    private TextView describe1;
    private ImageView background;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment WelComeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WelComeFragment newInstance(int param1) {
        WelComeFragment fragment = new WelComeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public WelComeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            backgroundValue = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wel_come, container, false);

        background = (ImageView) view.findViewById(R.id.g02_backgroud);
        background.setImageResource(backgroundValue);

        return view;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("WelComeFragment"); //统计页面
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("WelComeFragment");
    }

}
