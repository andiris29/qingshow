package com.focosee.qingshow.activity;

import android.os.Bundle;

import com.focosee.qingshow.R;

/**
 * Created by 华榕 on 2015/3/11.
 */
public class U11EditAddressActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_address_edit);

        getFragmentManager().beginTransaction().replace(R.id.address_fragment, U11AddressEditFragment.newInstace()).commit();

    }

    @Override
    public void reconn() {

    }
}
