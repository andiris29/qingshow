package com.focosee.qingshow.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.focosee.qingshow.R;

/**
 * Created by Administrator on 2015/3/11.
 */
public class S11ReceiptFragment extends Fragment{

    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_s11_receipt,container,false);
        return rootView;
    }

}
