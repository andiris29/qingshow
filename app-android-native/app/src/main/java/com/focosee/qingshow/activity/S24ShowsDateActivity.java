package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S24Adapter;
import com.focosee.qingshow.httpapi.QSRxApi;
import com.focosee.qingshow.httpapi.request.QSSubscriber;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.adapter.AbsAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.InjectView;

public class S24ShowsDateActivity extends BaseActivity {


    @InjectView(R.id.s24_rv)
    RecyclerView dataRv;
    @InjectView(R.id.s24_time)
    TextView timeTv;

    private AbsAdapter<MongoShow> adapter;

    private int pageNo = 1;
    private int pageSize = 30;
    @Override
    public void reconn() {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s24_shows_date);
        GregorianCalendar from = (GregorianCalendar) getIntent().getSerializableExtra("MATCH_NEW_FROM");
        GregorianCalendar to  = (GregorianCalendar) getIntent().getSerializableExtra("MATCH_NEW_TO");

        from = new GregorianCalendar(2015,11,20);
        String time = TimeUtil.formatDateTime_CN_Pre(from);
        timeTv.setText(time);
        adapter = new S24Adapter(new ArrayList<MongoShow>(), this, R.layout.item_match);
        dataRv.setAdapter(adapter);
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
