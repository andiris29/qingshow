package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.T01HihghtedTradeListAdapter;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.widget.QSButton;
import com.focosee.qingshow.widget.QSTextView;
import java.util.ArrayList;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

public class T01HighlightedTradeListActivity extends MenuActivity {

    @InjectView(R.id.left_btn)
    ImageButton leftBtn;
    @InjectView(R.id.title)
    QSTextView title;
    @InjectView(R.id.right_btn)
    QSButton rightBtn;
    @InjectView(R.id.title_layout)
    PercentRelativeLayout titleLayout;
    @InjectView(R.id.t01_recycler)
    RecyclerView t01Recycler;
    @InjectView(R.id.s01_refresh)
    BGARefreshLayout s01Refresh;
    @InjectView(R.id.navigation_btn_match)
    ImageButton navigationBtnMatch;
    @InjectView(R.id.navigation_btn_match_tv)
    QSTextView navigationBtnMatchTv;
    @InjectView(R.id.navigation_btn_good_match)
    ImageButton navigationBtnGoodMatch;
    @InjectView(R.id.navigation_btn_good_match_tv)
    QSTextView navigationBtnGoodMatchTv;
    @InjectView(R.id.navigation_btn_discount)
    ImageButton navigationBtnDiscount;
    @InjectView(R.id.navigation_btn_discount_tv)
    QSTextView navigationBtnDiscountTv;
    @InjectView(R.id.u01_bonusList)
    ImageButton u01BonusList;
    @InjectView(R.id.u01_bonusList_tv)
    QSTextView u01BonusListTv;
    @InjectView(R.id.u01_people)
    ImageButton u01People;
    @InjectView(R.id.u01_people_tv)
    QSTextView u01PeopleTv;
    @InjectView(R.id.s17_settting)
    ImageButton s17Settting;
    @InjectView(R.id.s17_settting_tv)
    QSTextView s17SetttingTv;

    private T01HihghtedTradeListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t01_highlighted_trade_list);
        ButterKnife.inject(this);

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
