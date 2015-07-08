package com.focosee.qingshow.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S20SelectAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.CategoryParser;
import com.focosee.qingshow.httpapi.response.dataparser.ItemFeedingParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoCategories;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.widget.QSCanvasView;
import com.focosee.qingshow.widget.QSImageView;
import com.focosee.qingshow.widget.radio.RadioLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2015/7/1.
 */
public class S20MatcherActivity extends BaseActivity {

    @InjectView(R.id.canvas)
    QSCanvasView canvas;
    @InjectView(R.id.selectRV)
    RecyclerView selectRV;

    private S20SelectAdapter adapter;
    private List<MongoItem> datas;

    private Map<String, List<MongoItem>> allDatas;
    private Map<String, QSImageView> allViews;
    private Map<String, Select> allSelect;
    private Map<String, Integer> allPageNo;

    private List<String> categoryRefs;

    private String categoryRef = "";
    private int pagaSize = 10;

    @Override
    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s20_matcher);
        ButterKnife.inject(this);

        allDatas = new HashMap<>();
        allViews = new HashMap<>();
        allSelect = new HashMap<>();
        allPageNo = new HashMap<>();

        categoryRefs = new ArrayList<>();

        initSelectRV();
        initCanvas();

        initCategoryRef();
    }

    private void initCategoryRef() {
        getCategoryFromNet();
    }

    private void addItemsToCanvas(String categoryRef, String url, int x, int y) {

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        final QSImageView itemView = new QSImageView(this);
        itemView.setLayoutParams(layoutParams);
        itemView.setX(x);
        itemView.setY(y);
        ImageLoader.getInstance().displayImage(url, itemView.getImageView(),new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                itemView.setContainerHeight(itemView.getImageView().getDrawable().getIntrinsicHeight());
                itemView.setContainerWidth(itemView.getImageView().getDrawable().getIntrinsicWidth());
                checkBorder(itemView);
            }
        });

        itemView.setCategoryId(categoryRef);
        itemView.setOnDelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvas.detach(itemView);
            }
        });

        allViews.put(categoryRef, itemView);
        canvas.attach(itemView);

        allSelect.put(categoryRef, new Select());
        allPageNo.put(categoryRef, 1);
    }

    private void initCanvas() {
        canvas.setOnCheckedChangeListener(new QSCanvasView.OnCheckedChangeListener() {
            @Override
            public void checkedChanged(QSImageView view) {
                if (categoryRef != view.getCategoryId()) {
                    categoryRef = view.getCategoryId();
                    if (allDatas.containsKey(categoryRef)) {
                        allViews.get(categoryRef).showDelBtn();
                        allViews.get(categoryRef).bringToFront();
                        List<MongoItem> mongoItems = allDatas.get(categoryRef);
                        adapter.setSelectPos(allSelect.get(categoryRef).selectPos);
                        adapter.setLastChecked(allSelect.get(categoryRef).lastChecked);
                        adapter.addDataAtTop(mongoItems);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }


    private void initSelectRV() {
        datas = new LinkedList<>();
        adapter = new S20SelectAdapter(datas, this, R.layout.item_s20_select);
        adapter.setOnCheckedChangeListener(new S20SelectAdapter.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(MongoItem datas, int position, RadioLayout view) {
                ImageLoader.getInstance().displayImage(datas.thumbnail, allViews.get(categoryRef).getImageView(),new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        checkBorder(allViews.get(categoryRef));
                        allViews.get(categoryRef).setContainerHeight(allViews.get(categoryRef).getImageView().getDrawable().getIntrinsicHeight());
                        allViews.get(categoryRef).setContainerWidth(allViews.get(categoryRef).getImageView().getDrawable().getIntrinsicWidth());
                    }
                });
                if (allSelect.containsKey(categoryRef)) {
                    allSelect.get(categoryRef).lastChecked = view;
                    allSelect.get(categoryRef).selectPos = position;
                }
            }
        });
        selectRV.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        selectRV.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int[] lastPositions;
            private int lastVisibleItemPosition;
            private int currentScrollState = 0;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                if (lastPositions == null) {
                    lastPositions = new int[layoutManager.getSpanCount()];
                }
                layoutManager.findLastVisibleItemPositions(lastPositions);
                lastVisibleItemPosition = findMax(lastPositions);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                currentScrollState = newState;
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                if ((visibleItemCount > 0 && currentScrollState == RecyclerView.SCROLL_STATE_IDLE &&
                        (lastVisibleItemPosition) >= totalItemCount - 1)) {
                    onBottom();
                }
            }

            public void onBottom() {
                if (allPageNo.containsKey(categoryRef)) {
                    int pagaNo = allPageNo.get(categoryRef);
                    ++pagaNo;
                    getDataFromNet(pagaNo, pagaSize, categoryRef);
                    allPageNo.put(categoryRef, pagaNo);
                }
            }

            private int findMax(int[] lastPositions) {
                int max = lastPositions[0];
                for (int value : lastPositions) {
                    if (value > max) {
                        max = value;
                    }
                }
                return max;
            }
        });
        selectRV.setAdapter(adapter);

    }


    private void getDataFromNet(final String categoryRef, final int row, final int column) {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSJsonObjectRequest.Method.GET, QSAppWebAPI.getQueryItems(1, 20, categoryRef), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S20MatcherActivity.this, MetadataParser.getError(response));
                    return;
                }
                datas = ItemFeedingParser.parse(response);
                allDatas.put(categoryRef, datas);

                int left = 0;
                int top;
                switch (column) {
                    case 0:
                        left = 0;
                        break;
                    case 1:
                        left = canvas.getWidth() / 2;
                        break;
                }

                top = (canvas.getHeight() / (row + 1)) * row;

                addItemsToCanvas(categoryRef, datas.get(0).thumbnail, left, top);
            }
        }, null);
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void getDataFromNet(int pageNo, int pageSize, final String categoryRef) {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSJsonObjectRequest.Method.GET, QSAppWebAPI.getQueryItems(pageNo, pageSize, categoryRef), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S20MatcherActivity.this, MetadataParser.getError(response));
                    return;
                }
                datas = ItemFeedingParser.parse(response);
                adapter.addDataAtLast(datas);

                List<MongoItem> mongoItems = allDatas.get(categoryRef);
                mongoItems.addAll(datas);

                allDatas.put(categoryRef, mongoItems);
                adapter.notifyDataSetChanged();
            }
        }, null);
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void getCategoryFromNet() {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSJsonObjectRequest.Method.GET, QSAppWebAPI.getQueryCategories(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S20MatcherActivity.this, MetadataParser.getError(response));
                    return;
                }
                List<MongoCategories> categories = CategoryParser.parseQuery(response);

                    for (MongoCategories category : categories) {
                        if (category.matchInfo != null){
                            if (category.matchInfo.enabled) {
                                getDataFromNet(category._id, category.matchInfo.row, category.matchInfo.column);
                            }
                        }
                    }
            }
        }, null);
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private class Select {
        public RadioLayout lastChecked;
        public int selectPos;
    }

    private void checkBorder(QSImageView view) {
        float nextX = view.getX();
        float nextY = view.getY();
        int intrinsicWidth = view.getImageView().getDrawable().getIntrinsicWidth();
        int intrinsicHeight = view.getImageView().getDrawable().getIntrinsicHeight();

        if (view.getX() + intrinsicWidth > canvas.getWidth()) {
            nextX = canvas.getWidth() - intrinsicWidth;
        }
        if (view.getY() + intrinsicHeight > canvas.getHeight()) {
            nextY = canvas.getHeight() - intrinsicHeight;
        }
        if (view.getX() < 0) {
            nextX = 0;
        }
        if (view.getY() < 0) {
            nextY = 0;
        }

        ObjectAnimator y = ObjectAnimator.ofFloat(view, "y", view.getY(), nextY);
        ObjectAnimator x = ObjectAnimator.ofFloat(view, "x", view.getX(), nextX);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(x, y);
        animatorSet.setDuration(0);
        animatorSet.start();
    }

    public void onEventMainThread(S21CategoryEvent event) {
        categoryRefs = event.getCategories();
    }

    @OnClick(R.id.selectBtn)
    public void select(){

    }

    @OnClick(R.id.submitBtn)
    public void submit(){
        
    }
}
