package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.HomeWaterfallAdapter;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.ShowEntity;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class S01HomeActivity extends Activity {

//    private MNavigationView _navigationView;
    private LinearLayout _popView;

    private MPullRefreshListView _wfPullRefreshView;
    private MultiColumnListView _wfListView;
    private HomeWaterfallAdapter _adapter;
    private int _currentPageIndex = 1;

    private SimpleDateFormat _mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s01_home);

//        _navigationView = (MNavigationView) findViewById(R.id.S01_navigation);
//        _navigationView.setLeft_drawable(R.drawable.nav_btn_menu);
//        _navigationView.setRight_drawable(R.drawable.nav_btn_account);
//        _navigationView.setLogo_drawable(R.drawable.nav_btn_image_logo);

        // TODO: Improve code to add menu here
        ((ImageView)findViewById(R.id.S01_nav_icon_flash)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S01HomeActivity.this, S02ShowClassify.class);
                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 0);
                startActivity(intent);
                _popView.setVisibility(View.GONE);
            }
        });
        ((ImageView)findViewById(R.id.S01_nav_icon_match)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S01HomeActivity.this, S02ShowClassify.class);
                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 1);
                startActivity(intent);
                _popView.setVisibility(View.GONE);
            }
        });
        ((ImageView)findViewById(R.id.S01_nav_icon_people)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S01HomeActivity.this, S02ShowClassify.class);
                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 2);
                startActivity(intent);
                _popView.setVisibility(View.GONE);
            }
        });
        ((ImageView)findViewById(R.id.S01_nav_icon_design)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S01HomeActivity.this, S02ShowClassify.class);
                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 3);
                startActivity(intent);
                _popView.setVisibility(View.GONE);
            }
        });
        ((ImageView)findViewById(R.id.S01_nav_icon_brand)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S01HomeActivity.this, S02ShowClassify.class);
                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 4);
                startActivity(intent);
                _popView.setVisibility(View.GONE);
            }
        });

        _popView = (LinearLayout) findViewById(R.id.S01_pop_menu);

        _popView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _popView.setVisibility(View.INVISIBLE);
            }
        });

        ((ImageView)findViewById(R.id.S01_title_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_popView.getVisibility() != View.VISIBLE)
                    _popView.setVisibility(View.VISIBLE);
                else
                    _popView.setVisibility(View.GONE);
            }
        });
        ((ImageView)findViewById(R.id.S01_title_account)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S01HomeActivity.this, U06LoginActivity.class);
                startActivity(intent);
            }
        });

        _wfPullRefreshView = (MPullRefreshListView) findViewById(R.id.S01_waterfall_content);
        _wfListView = _wfPullRefreshView.getRefreshableView();
        _adapter = new HomeWaterfallAdapter(this, R.layout.item_showlist, ImageLoader.getInstance());

        _wfListView.setAdapter(_adapter);

        _wfPullRefreshView.setPullLoadEnabled(true);
        _wfPullRefreshView.setScrollLoadEnabled(true);

        _wfPullRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<MultiColumnListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<MultiColumnListView> refreshView) {
                doRefreshTask();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<MultiColumnListView> refreshView) {
                doGetMoreTask();
            }
        });

        _wfListView.setOnItemClickListener(new PLA_AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(S01HomeActivity.this, S03SHowActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(S03SHowActivity.INPUT_SHOW_ENTITY, _adapter.getItemDataAtIndex(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        setLastUpdateTime();

        _wfPullRefreshView.doPullRefreshing(true, 500);

    }

    private void setLastUpdateTime() {
        String text = formatDateTime(System.currentTimeMillis());
        _wfPullRefreshView.setLastUpdatedLabel(text);
    }

    private String formatDateTime(long time) {
        if (0 == time) {
            return "";
        }

        return _mDateFormat.format(new Date(time));
    }

    private void doRefreshTask() {
        _getDataFromNet(true, "1", "10");
    }

    private void doGetMoreTask() {
        _getDataFromNet(false, String.valueOf(_currentPageIndex+1), "10");
    }

    private void _getDataFromNet(boolean refreshSign, String pageNo, String pageSize) {
        final boolean _tRefreshSign = refreshSign;
        JsonObjectRequest jor = new JsonObjectRequest(QSAppWebAPI.getShowListApi(Integer.valueOf(pageNo), Integer.valueOf(pageSize)), null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String resultStr = ((JSONObject) response.get("data")).get("shows").toString();
                    LinkedList<ShowEntity> results = ShowEntity.getLinkedListFromString(resultStr);
                    if (_tRefreshSign) {
                        _adapter.addItemTop(results);
                        _currentPageIndex = 1;
                    } else {
                        _adapter.addItemLast(results);
                        _currentPageIndex++;
                    }
                    _adapter.notifyDataSetChanged();
                    _wfPullRefreshView.onPullDownRefreshComplete();
                    _wfPullRefreshView.onPullUpRefreshComplete();
                    _wfPullRefreshView.setHasMoreData(true);
                    setLastUpdateTime();

                }catch (Exception error){
                    Toast.makeText(S01HomeActivity.this, "Error:"+error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    _wfPullRefreshView.onPullDownRefreshComplete();
                    _wfPullRefreshView.onPullUpRefreshComplete();
                    _wfPullRefreshView.setHasMoreData(true);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(S01HomeActivity.this, "Error:"+error.toString(), Toast.LENGTH_SHORT).show();
                _wfPullRefreshView.onPullDownRefreshComplete();
                _wfPullRefreshView.onPullUpRefreshComplete();
                _wfPullRefreshView.setHasMoreData(true);
            }
        });
        QSApplication.QSRequestQueue().add(jor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_s01_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
