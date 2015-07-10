package com.focosee.qingshow.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

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
import com.focosee.qingshow.model.S20Bitmap;
import com.focosee.qingshow.model.vo.mongo.MongoCategories;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.widget.ConfirmDialog;
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
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/7/1.
 */
public class S20MatcherActivity extends BaseActivity {

    @InjectView(R.id.canvas)
    QSCanvasView canvas;
    @InjectView(R.id.selectRV)
    RecyclerView selectRV;

    public static final String S20_REFS = "S20_REFS";

    private S20SelectAdapter adapter;
    private List<MongoItem> datas;
    private List<MongoCategories> categories;

    private Map<String, Select> allSelect;
    private List<String> categoryRefs;
    private List<String> lastCategoryRefs;

    private String categoryRef = "";
    private int pagaSize = 10;

    private boolean hasDraw = false;

    @Override
    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s20_matcher);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);

        allSelect = new HashMap<>();
        categoryRefs = new ArrayList<>();
        lastCategoryRefs = new ArrayList<>();

        initSelectRV();
        initCanvas();

        initCategoryRef();
    }

    private void initCategoryRef() {
        getCategoryFromNet();
    }

    private void addItemsToCanvas(final String categoryRef, String url, int x, int y) {

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        final QSImageView itemView = new QSImageView(this);
        itemView.setLayoutParams(layoutParams);
        itemView.setX(x);
        itemView.setY(y);
        ImageLoader.getInstance().displayImage(url, itemView.getImageView(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                itemView.setContainerHeight(itemView.getImageView().getDrawable().getIntrinsicHeight());
                itemView.setContainerWidth(itemView.getImageView().getDrawable().getIntrinsicWidth());
                if (allSelect.containsKey(categoryRef)) {
                    checkBorder(itemView);
                } else {
                    return;
                }
            }
        });

        itemView.setCategoryId(categoryRef);
        itemView.setOnDelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ConfirmDialog dialog = new ConfirmDialog();
                dialog.setTitle(getResources().getString(R.string.s20_dialog)).setConfirm(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        canvas.detach(itemView);
                        allSelect.remove(categoryRef);
                        categoryRefs.remove(categoryRef);
                        dialog.dismiss();
                    }
                }).setCancel(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }).show(getSupportFragmentManager());

            }
        });

        canvas.attach(itemView);

        if (allSelect.containsKey(categoryRef)) {
            allSelect.put(categoryRef, allSelect.get(categoryRef).setView(itemView).setPageNo(1));
        } else {
            Select select = new Select().setView(itemView).setPageNo(1);
            allSelect.put(categoryRef, select);
        }
        if (!categoryRefs.contains(categoryRef)) {
            categoryRefs.add(categoryRef);
        }
    }

    private void initCanvas() {
        canvas.setOnCheckedChangeListener(new QSCanvasView.OnCheckedChangeListener() {
            @Override
            public void checkedChanged(QSImageView view) {
                if (categoryRef != view.getCategoryId()) {
                    categoryRef = view.getCategoryId();
                    if (allSelect.containsKey(categoryRef)) {
                        allSelect.get(categoryRef).view.showDelBtn();
                        allSelect.get(categoryRef).view.bringToFront();
                        List<MongoItem> mongoItems = allSelect.get(categoryRef).data;
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
                if (!allSelect.containsKey(categoryRef)) {
                    return;
                }
                ImageLoader.getInstance().displayImage(datas.thumbnail, allSelect.get(categoryRef).view.getImageView(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        if (allSelect.containsKey(categoryRef)) {
                            checkBorder(allSelect.get(categoryRef).view);
                        } else {
                            return;
                        }
                        allSelect.get(categoryRef).view.setContainerHeight(allSelect.get(categoryRef).view.getImageView().getDrawable().getIntrinsicHeight());
                        allSelect.get(categoryRef).view.setContainerWidth(allSelect.get(categoryRef).view.getImageView().getDrawable().getIntrinsicWidth());
                    }
                });
                if (allSelect.containsKey(categoryRef)) {
                    allSelect.put(categoryRef, allSelect.get(categoryRef).setLastChecked(view).setSelectPos(position));
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
                if (allSelect.containsKey(categoryRef)) {
                    int pagaNo = allSelect.get(categoryRef).pageNo;
                    ++pagaNo;
                    getDataFromNet(pagaNo, pagaSize, categoryRef);
                    allSelect.put(categoryRef, allSelect.get(categoryRef).setPageNo(pagaNo));
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
                if (allSelect.containsKey(categoryRef)) {
                    allSelect.put(categoryRef, allSelect.get(categoryRef).setData(datas));
                } else {
                    allSelect.put(categoryRef, new Select().setData(datas));
                }

                int left = 0;
                int top = 0;
                switch (column) {
                    case 0:
                        left = 0;
                        break;
                    case 1:
                        left = canvas.getWidth() / 2;
                        break;
                }

                if (row != 0) {
                    top = (canvas.getHeight() / (row + 1)) * row;
                }

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

                List<MongoItem> mongoItems = allSelect.get(categoryRef).data;
                mongoItems.addAll(datas);

                if (allSelect.containsKey(categoryRef)) {
                    allSelect.put(categoryRef, allSelect.get(categoryRef).setData(mongoItems));
                } else {
                    allSelect.put(categoryRef, new Select().setData(mongoItems));
                }
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
                categories = CategoryParser.parseQuery(response);

                for (MongoCategories category : categories) {
                    if (category.matchInfo != null) {
                        if (category.matchInfo.enabled) {
                            getDataFromNet(category._id, category.matchInfo.row, category.matchInfo.column);
                        }
                    }
                }
            }
        }, null);
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(S21CategoryEvent event) {
        categoryRefs = event.getCategories();
        for (String ref : categoryRefs) {
            if (!lastCategoryRefs.contains(ref)) {
                Random random = new Random();
                getDataFromNet(ref, random.nextInt(3) + 1, random.nextInt(2));
            }
        }

        for (String ref : lastCategoryRefs) {
            if (!categoryRefs.contains(ref)) {
                if (allSelect.containsKey(ref)) {
                    canvas.detach(allSelect.get(ref).view);
                    allSelect.remove(ref);
                }
            }
        }
    }

    @OnClick(R.id.selectBtn)
    public void select() {
        Intent intent = new Intent(S20MatcherActivity.this, S21CategoryActivity.class);
        lastCategoryRefs.clear();
        lastCategoryRefs.addAll(categoryRefs);
        startActivity(intent);
    }

    @OnClick(R.id.submitBtn)
    public void submit() {
        if (!checkOverlap(0.7f)) {
            Toast.makeText(this, getResources().getString(R.string.s20_notify_more), Toast.LENGTH_SHORT).show();
            return;
        }

        if (hasDraw) {
            canvas.destroyDrawingCache();
        }
        canvas.notifyChildrenUnClick();
        canvas.setDrawingCacheEnabled(true);
        canvas.buildDrawingCache();
        Bitmap bitmap = canvas.getDrawingCache();
        S20Bitmap.INSTANCE.setBitmap(bitmap);
        hasDraw = true;

        Intent intent = new Intent(S20MatcherActivity.this, S20MatchPreviewActivity.class);
        startActivity(intent);
    }

    private boolean checkOverlap(float limit) {
        for (String s : allSelect.keySet()) {
            QSImageView view = allSelect.get(s).view;
            if (canvas.calcUnOverlapArea(view) / view.getArea() < limit) {
                return false;
            }
        }
        return true;
    }

    private class Select {
        public RadioLayout lastChecked;
        public int selectPos;
        public QSImageView view;
        public int pageNo;
        public List<MongoItem> data;

        public Select setData(List<MongoItem> data) {
            this.data = data;
            return this;
        }

        public Select setPageNo(int pageNo) {
            this.pageNo = pageNo;
            return this;
        }

        public Select setView(QSImageView view) {
            this.view = view;
            return this;
        }

        public Select setSelectPos(int selectPos) {
            this.selectPos = selectPos;
            return this;
        }

        public Select setLastChecked(RadioLayout lastChecked) {
            this.lastChecked = lastChecked;
            return this;
        }
    }

}
