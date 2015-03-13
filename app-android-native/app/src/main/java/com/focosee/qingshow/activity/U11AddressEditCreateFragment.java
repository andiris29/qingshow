package com.focosee.qingshow.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.focosee.qingshow.R;

/**
 * Created by 华榕 on 2015/3/11.
 */
public class U11AddressEditCreateFragment extends Fragment {

    public static U11AddressEditCreateFragment newInstace(){
        return new U11AddressEditCreateFragment();
    }

    public U11AddressEditCreateFragment(){}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_u11_edit_create, container, false);

        view.findViewById(R.id.U11ec_back_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });



        return view;
    }
}
