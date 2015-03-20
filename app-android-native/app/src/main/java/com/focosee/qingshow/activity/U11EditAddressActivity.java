package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.fragment.U11AddressEditFragment;

/**
 * Created by 华榕 on 2015/3/11.
 */
public class U11EditAddressActivity extends BaseActivity {

    private U11AddressEditFragment fragment;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_address_edit);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        fragment = U11AddressEditFragment.newInstace();
        if(null != id && !"".equals(id)){
            Bundle bundle = new Bundle();
            bundle.putString("id", getIntent().getStringExtra("id"));
            fragment.setArguments(bundle);
        }

        getFragmentManager().beginTransaction().replace(R.id.address_fragment, fragment).commit();

    }

    @Override
    public void reconn() {

    }
}
