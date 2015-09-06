package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.T01HihghtedTradeListAdapter;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.widget.QSTextView;
import java.util.ArrayList;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class T01HighlightedTradeListActivity extends MenuActivity {

    @InjectView(R.id.left_btn)
    ImageButton leftBtn;
    @InjectView(R.id.title)
    QSTextView title;
    @InjectView(R.id.t01_recycler)
    RecyclerView t01Recycler;

    private T01HihghtedTradeListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t01_highlighted_trade_list);
        ButterKnife.inject(this);
        initDrawer();
        title.setText(getText(R.string.title_t01));
        leftBtn.setImageResource(R.drawable.nav_btn_menu_n);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuSwitch();
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(T01HighlightedTradeListActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        t01Recycler.setLayoutManager(manager);
        adapter = new T01HihghtedTradeListAdapter(new ArrayList<MongoTrade>()
                , T01HighlightedTradeListActivity.this, R.layout.item_t01_higthlighted_tradelist);

        t01Recycler.setAdapter(adapter);

    }

    @Override
    public void reconn() {

    }
}
