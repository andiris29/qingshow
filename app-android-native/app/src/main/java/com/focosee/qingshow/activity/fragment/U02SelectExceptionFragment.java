package com.focosee.qingshow.activity.fragment;


import android.support.v4.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.U02SettingsActivity;
import com.focosee.qingshow.adapter.U02ExceptionListViewAdapter;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.model.U02Model;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class U02SelectExceptionFragment extends Fragment {
    @InjectView(R.id.backTextView)
    TextView backTextView;
    @InjectView(R.id.fragment_u02_select_exception_listview)
    ListView listView;


    private Context context;
    private RequestQueue requestQueue;

    private MongoPeople user;
    private U02ExceptionListViewAdapter adapter;
    List<Integer> expectations = new ArrayList<>();

    public U02SelectExceptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            user = (MongoPeople) getArguments().getSerializable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_u02_select_exception, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity().getApplicationContext();
        requestQueue = RequestQueueManager.INSTANCE.getQueue();

        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUser();
            }
        });

        adapter= new U02ExceptionListViewAdapter(getActivity(), user);
        listView.setAdapter(adapter);
    }

    private void saveUser(){

        for (CheckedTextView ctv : adapter.getItemViews()) {
            if(ctv.isChecked()) {
                expectations.add((Integer)ctv.getTag());
            }
        }
        Map params = new HashMap();
        params.put("expectations", expectations);
        U02Model.INSTANCE.set_class(U02SettingsFragment.class);
        UserCommand.update(params, new Callback(){
            @Override
            public void onComplete() {
                super.onComplete();
                U02SettingsFragment settingsFragment = new U02SettingsFragment();
                getFragmentManager().beginTransaction().setCustomAnimations(R.anim.push_right_in, 0, R.anim.push_right_in, 0).
                        replace(R.id.settingsScrollView, settingsFragment).commit();
            }

            @Override
            public void onError() {
                super.onError();
                U02SettingsFragment settingsFragment = new U02SettingsFragment();
                getFragmentManager().beginTransaction().setCustomAnimations(R.anim.push_right_in, 0, R.anim.push_right_in, 0).
                        replace(R.id.settingsScrollView, settingsFragment).commit();
            }
        });
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U08ChangePassword"); //统计页面
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U08ChangePassword");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
