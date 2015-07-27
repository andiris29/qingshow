package com.focosee.qingshow.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
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
import com.focosee.qingshow.util.AppUtil;
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
public class S20MatcherActivity extends MenuActivity {

    @InjectView(R.id.canvas)
    QSCanvasView canvas;
    @InjectView(R.id.selectRV)
    RecyclerView selectRV;

    public static final String S20_ITEMREFS = "S20_ITEMREFS";
    public static final String S20_SELECT_CATEGORYREFS = "S20_SELECT_CATEGORYREFS";

    private S20SelectAdapter adapter;
    private List<MongoItem> datas;
    private List<MongoCategories> categories;

    private Map<String, Select> allSelect;
    private List<String> categoryRefs;
    private ArrayList<String> lastCategoryRefs;

    private String categoryRef = "";
    private int pagaSize = 10;
    private int rows[] = new int[]{1, 2};

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

        initDrawer();

        allSelect = new HashMap<>();
        categoryRefs = new ArrayList<>();
        lastCategoryRefs = new ArrayList<>();

        initSelectRV();
        initCanvas();

        initCategoryRef();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canvas.views.size() != 0) {
            canvas.reselectView();
        }
    }

    private void initCategoryRef() {
        getCategoryFromNet();
    }

    private void addItemsToCanvas(final String categoryRef, String url, final int row, final int column) {

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        final QSImageView itemView = new QSImageView(this);
        itemView.setLayoutParams(layoutParams);
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int barHeight = frame.top;
        itemView.setBarHeight(barHeight);
        ImageLoader.getInstance().displayImage(url, itemView.getImageView(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                formatPlace(itemView, row, column, true);
                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0, 1.0f);
                animator.setDuration(500);
                animator.start();
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

    private void formatPlace(QSImageView view, int row, int column, boolean moveToFrame) {

        float left = 0;
        float top = 0;

        int minus;
        float lastScaleFactor = view.getLastScaleFactor();

        int width = view.getImageView().getDrawable().getIntrinsicWidth();
        int height = view.getImageView().getDrawable().getIntrinsicHeight();
        float halfCanvasWidth = canvas.getWidth() / 2.0f;
        float avgCanvasHeight = canvas.getHeight() / (rows[column] + 1);


        switch (column) {
            case 0:
                left = 0;
                break;
            case 1:
                left = canvas.getWidth() / 2;
                break;
        }

        if (rows[column] != 0) {
            top = avgCanvasHeight * row;
        }

        if (width > halfCanvasWidth || height > avgCanvasHeight) {
            minus = -1;
        } else {
            minus = 1;
        }
        float ratio = Math.abs(avgCanvasHeight / height) < Math.abs(halfCanvasWidth / width)
                ? Math.abs(avgCanvasHeight / height) : Math.abs(halfCanvasWidth / width);
        view.setScaleX(ratio);
        view.setScaleY(ratio);
        view.setLastScaleFactor(ratio);

        if (moveToFrame) {
            if (column == 0) {
                left = minus * Math.abs(width * (1.0f - ratio) / 2.0f);
            }
            if (column == 1) {
                left = (halfCanvasWidth + minus * Math.abs(width * (1.0f - ratio) / 2.0f));
            }

            top += minus * Math.abs(height * (1.0f - ratio) / 2.0f);

            moveView(view, 0, 0, left, top);
        } else {
            float nextX = view.getX();
            float nextY = view.getY();

            float x = view.getX();
            float y = view.getY();
            float dx = width * (ratio - 1) / 2;
            float dy = height * (ratio - 1) / 2;
            moveView(view, 0, 0, x + dx, y + dy);

//            Log.d("tag", "width: " + width + " height: " + height + " x: " + x + " y: " + y + " dx: " + dx + " dy: " + dy + " ldx: " + lastdx + " ldy: " + lastdy);

            if (x + width + dx > canvas.getWidth()) {
                nextX = x - ((x + width + dx) - canvas.getWidth());
            }

            if (y + height + dy > canvas.getHeight()) {
                nextY = y - ((y + height + dy) - canvas.getHeight());
            }

            if (x - dx < 0) {
                nextX = Math.abs(x - dx);
            }

            if (y - dy < 0) {
                nextY = Math.abs(y - dy);
            }

            moveView(view, 0, 0, nextX, nextY);
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
        allSelect.containsKey(categoryRef);
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
                            formatPlace(allSelect.get(categoryRef).view, allSelect.get(categoryRef).row, allSelect.get(categoryRef).column, false);
                        } else {
                            return;
                        }
                    }
                });
                allSelect.put(categoryRef, allSelect.get(categoryRef).setLastChecked(view).setSelectPos(position).setItem(datas));

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
                    allSelect.put(categoryRef, allSelect.get(categoryRef).setData(datas).setItem(datas.get(0)).setRow(row).setColumn(column));
                } else {
                    allSelect.put(categoryRef, new Select().setData(datas).setItem(datas.get(0)).setRow(row).setColumn(column));
                }

                addItemsToCanvas(categoryRef, datas.get(0).thumbnail, row, column);
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
                        if (category.matchInfo.defaultOnCanvas) {
                            if (category.matchInfo.row > rows[category.matchInfo.column]) {
                                rows[category.matchInfo.column] = category.matchInfo.row;
                            }
                        }
                    }
                }

                for (MongoCategories category : categories) {
                    if (category.matchInfo != null) {
                        if (category.matchInfo.defaultOnCanvas) {
                            getDataFromNet(category._id, category.matchInfo.row, category.matchInfo.column);
                        }
                    }
                }
            }
        }, null);
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    public void checkBorder(QSImageView view) {

        float scaleFactor = view.getLastScaleFactor();
        float nextX = view.getX();
        float nextY = view.getY();

        float intrinsicWidth = view.getImageView().getDrawable().getIntrinsicWidth();
        float intrinsicHeight = view.getImageView().getDrawable().getIntrinsicHeight();

        float dx = Math.abs(intrinsicWidth * (1 - scaleFactor) / 2);
        float dy = Math.abs(intrinsicHeight * (1 - scaleFactor) / 2);

        if (view.getX() + intrinsicWidth + dx > canvas.getWidth()) {
            nextX = canvas.getWidth() - intrinsicWidth * scaleFactor;
        }
        if (view.getY() + intrinsicHeight + dy > canvas.getHeight()) {
            nextY = canvas.getHeight() - intrinsicHeight * scaleFactor;
        }
        if (view.getX() - dx < 0) {
            nextX = dx;
        }
        if (view.getY() - dy < 0) {
            nextY = dy;
        }

        moveView(view, 0, 0, nextX, nextY);
    }

    private void moveView(View view, float startX, float startY, float nextX, float nextY) {
        ObjectAnimator x = ObjectAnimator.ofFloat(view, "x", startX, nextX);
        ObjectAnimator y = ObjectAnimator.ofFloat(view, "y", startY, nextY);
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
                getDataFromNet(ref, random.nextInt(3), random.nextInt(2));
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

    @OnClick(R.id.menu)
    public void menuOpen() {
        menuSwitch();
    }

    @OnClick(R.id.selectBtn)
    public void select() {
        Intent intent = new Intent(S20MatcherActivity.this, S21CategoryActivity.class);
        lastCategoryRefs.clear();
        lastCategoryRefs.addAll(categoryRefs);
        intent.putStringArrayListExtra(S20_SELECT_CATEGORYREFS, lastCategoryRefs);
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
        ArrayList<String> itemRefs = new ArrayList<>();
        for (String s : allSelect.keySet()) {
            itemRefs.add(allSelect.get(s).item._id);
        }
        intent.putStringArrayListExtra(S20_ITEMREFS, itemRefs);
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
        public MongoItem item;
        public int row;
        public int column;

        public Select setRow(int row) {
            this.row = row;
            return this;
        }

        public Select setColumn(int column) {
            this.column = column;
            return this;
        }

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

        public Select setItem(MongoItem item) {
            this.item = item;
            return this;
        }
    }

}
