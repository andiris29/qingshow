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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WelComeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WelComeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelComeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private int titleValue;
    private int describeValue;
    private int backgroundValue;

    private TextView title;
    private TextView describe;
    private ImageView background;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WelComeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WelComeFragment newInstance(int param1, int param2, int param3) {
        WelComeFragment fragment = new WelComeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3, param3);
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
            titleValue = getArguments().getInt(ARG_PARAM1);
            describeValue = getArguments().getInt(ARG_PARAM2);
            backgroundValue = getArguments().getInt(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wel_come, container, false);

        title = (TextView) view.findViewById(R.id.g02_title);
        describe = (TextView) view.findViewById(R.id.g02_describe);
        background = (ImageView) view.findViewById(R.id.g02_backgroud);

        title.setText(titleValue);
        describe.setText(describeValue);
        background.setImageResource(backgroundValue);

        return view;
    }



}
