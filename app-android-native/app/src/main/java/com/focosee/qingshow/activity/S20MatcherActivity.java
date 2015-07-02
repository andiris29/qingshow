package com.focosee.qingshow;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;

import com.focosee.qingshow.activity.BaseActivity;
import com.focosee.qingshow.adapter.S20SelectAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/7/1.
 */
public class S20MatcherActivity extends BaseActivity {

    @InjectView(R.id.canvas)
    View canvas;
    @InjectView(R.id.selectRV)
    RecyclerView selectRV;
    @InjectView(R.id.selectBtn)
    Button selectBtn;
    @InjectView(R.id.submitBtn)
    Button submitBtn;

    S20SelectAdapter selectAdapter;

    @Override
    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s20_matcher);
        ButterKnife.inject(this);
        initSelectRV();
    }

    private void initSelectRV(){
        selectAdapter = new S20SelectAdapter(null,this,R.layout.item_s20_select);
        selectRV.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        selectRV.setAdapter(selectAdapter);
    }


}
