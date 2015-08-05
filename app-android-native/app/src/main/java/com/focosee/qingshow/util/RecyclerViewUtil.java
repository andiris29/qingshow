package com.focosee.qingshow.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2015/8/5.
 */
public class RecyclerViewUtil {

    public static void setBackTop(final RecyclerView recyclerView, final View view , final LinearLayoutManager layoutManager){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(0);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(layoutManager.findFirstVisibleItemPosition() == 0){
                    view.setVisibility(View.GONE);
                }else {
                    view.setVisibility(View.VISIBLE);
                }
            }
        });
    }


}
