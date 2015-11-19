package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S24Adapter;
import com.focosee.qingshow.httpapi.QSRxApi;
import com.focosee.qingshow.httpapi.request.QSSubscriber;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.adapter.AbsAdapter;

import java.util.Date;
import java.util.List;

import butterknife.InjectView;

public class S24ShowsDateActivity extends BaseActivity {


    @InjectView(R.id.s24_rv)
    RecyclerView rv;

    private AbsAdapter adapter;

    private int pageNo = 1;
    private int pageSize = 30;
    private Date from;
    private Date to;
    @Override
    public void reconn() {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s24_shows_date);
        Date from = (Date) getIntent().getSerializableExtra("MATCH_NEW_FROM");
        Date to  = (Date) getIntent().getSerializableExtra("MATCH_NEW_TO");

        adapter = new S24Adapter(null, this, R.layout.item_match);
        rv.setAdapter(adapter);
        bindDataFromNet(pageNo, pageSize, null, null);
    }

    private void bindDataFromNet(int pageNo, int pageSize, Date from, Date to){
        QSRxApi.createFeedingMatchNewRequest(pageNo, pageSize, from, to)
                .subscribe(new QSSubscriber<List<MongoShow>>() {
                    @Override
                    public void onNetError(int message) {
                        ErrorHandler.handle(S24ShowsDateActivity.this, message);
                    }

                    @Override
                    public void onNext(List<MongoShow> mongoShows) {
                        adapter.addDataAtTop(mongoShows);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
