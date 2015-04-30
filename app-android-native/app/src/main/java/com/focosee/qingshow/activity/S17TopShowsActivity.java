package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S17TopAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by DylanJiang on 15/4/30.
 */

public class S17TopShowsActivity extends BaseActivity{

    @InjectView(R.id.s17_recycler)
    RecyclerView recyclerView;

    @InjectView(R.id.title)
    TextView title;

    @Override
    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s17_topshows);
        ButterKnife.inject(this);

        title.setText(R.string.s17_title_name);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new S17TopAdapter(null,this,R.layout.item_s17_singular,R.layout.item_s17_plural));
    }
}
