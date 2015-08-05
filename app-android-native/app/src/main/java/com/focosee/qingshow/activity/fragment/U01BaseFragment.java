package com.focosee.qingshow.activity.fragment;


import android.os.Bundle;
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

    MongoPeople user;

    int currentPageN0 = 1;
    @InjectView(R.id.u01_recyclerview)
    RecyclerView recyclerView;
    @InjectView(R.id.u01_refresh)
    BGARefreshLayout mRefreshLayout;

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
