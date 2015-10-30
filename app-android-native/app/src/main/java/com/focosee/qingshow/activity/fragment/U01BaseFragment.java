package com.focosee.qingshow.activity.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class U01BaseFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate{

    final int REFRESH_FINISH = 1;
    final int LOAD_FINISH = 2;
    final int ERROR = 3;
    MongoPeople user;

    int currentPageN0 = 1;
    @InjectView(R.id.u01_recyclerview)
    RecyclerView recyclerView;
    @InjectView(R.id.u01_refresh)
    BGARefreshLayout mRefreshLayout;

    Handler handler = new Handler(new Handler.Callback(){

        @Override
        public boolean handleMessage(Message msg) {

            if(msg.arg1 == REFRESH_FINISH){
                mRefreshLayout.endRefreshing();
                return true;
            }

            if(msg.arg1 == LOAD_FINISH){
                mRefreshLayout.endLoadingMore();
                return true;
            }

            if(msg.arg1 == ERROR){
                mRefreshLayout.endRefreshing();
                mRefreshLayout.endLoadingMore();
                return true;
            }

            return false;
        }
    });

    public U01BaseFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (MongoPeople) getArguments().getSerializable("user");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_u01, container, false);
        ButterKnife.inject(this, view);
        initRefreshLayout();
        return view;
    }

    public abstract void refresh();

    public abstract void loadMore();

    private void initRefreshLayout() {
        mRefreshLayout.setDelegate(this);
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getActivity(), true);
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        refresh();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        loadMore();
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
