package com.focosee.qingshow.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.focosee.qingshow.util.RectUtil;
import com.focosee.qingshow.widget.ConfirmDialog;
import com.focosee.qingshow.widget.QSCanvasView;
import com.focosee.qingshow.widget.QSImageView;
import com.focosee.qingshow.widget.radio.RadioLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
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
    @InjectView(R.id.navigation_btn_good_match)
    ImageButton navigationBtnGoodMatch;
    @InjectView(R.id.navigation_btn_good_match_tv)
    TextView navigationBtnGoodMatchTv;

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
        navigationBtnGoodMatch.setImageResource(R.drawable.root_menu_match_gray);
        navigationBtnGoodMatchTv.setTextColor(getResources().getColor(R.color.darker_gray));
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
        ImageLoader.getInstance().displayImage(url, itemView.getImageView(), AppUtil.getShowDisplayOptions(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                formatPlace(itemView, row, column, true);
                canvas.notifyCheckedChange();
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
    }


    private void formatPlace(QSImageView view, int row, int column, boolean moveToFrame) {

        float left = 0;
        float top = 0;

        int minus;

        int width = view.getImageView().getDrawable().getIntrinsicWidth();
        int height = view.getImageView().getDrawable().getIntrinsicHeight();
        float halfCanvasWidth = canvas.getWidth() / 2.0f;
        float avgCanvasHeight = canvas.getHeight() / (rows[column] + 1);

        switch (column) {
            case 0:
                left = 0;
                break;
            case 1:
                left = halfCanvasWidth;
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
                ? Math.abs(avgCanvasHeight / (height + 20)) : Math.abs(halfCanvasWidth / (width + 20));
        view.setScaleX(ratio);
        view.setScaleY(ratio);
        view.setLastScaleFactor(ratio);

        if (moveToFrame) {
//            if (column == 0) {
//                left = minus * Math.abs(width * (1.0f - ratio) / 2.0f);
//            }
//            if (column == 1) {
//                left = (halfCanvasWidth + minus * Math.abs(width * (1.0f - ratio) / 2.0f));
//            }
//
//            top += minus * Math.abs(height * (1.0f - ratio) / 2.0f);
//            moveView(view, 0, 0, left, top);
        } else {
            Point lastCentroid = view.getLastCentroid();
            float dx = view.getLeft() + width / 2 - lastCentroid.x;
            float dy = view.getTop() + height / 2 - lastCentroid.y;
            moveView(view, view.getX(), view.getY(), view.getX() - dx, view.getY() - dy);
        }
    }




    public void checkBorder(QSImageView view, View prent) {
        float scaleFactor = view.getLastScaleFactor();
        float dx = view.getWidth() * (scaleFactor - 1) / 2;
        float dy = view.getHeight() * (scaleFactor - 1) / 2;
        float actrulLeft = view.getLeft() - dx;
        float actrulRight = view.getRight() + dx;
        float actrulTop = view.getTop() - dy;
        float actrulBottom = view.getBottom() + dy;
        if (actrulLeft < 0) {
            checkBorder(view, prent);
        }

        if (actrulRight > prent.getWidth()) {
            scaleTo(prent.getWidth() - Math.abs(view.getRight()), view, 0);
            checkBorder(view, prent);
        }

        if (actrulTop < 0) {
            scaleTo(Math.abs(actrulTop) + view.getTop(), view, 1);
            checkBorder(view, prent);
        }

        if (actrulBottom > prent.getHeight()) {
            scaleTo(prent.getHeight() - Math.abs(view.getBottom()), view, 1);
            checkBorder(view, prent);
        }
    }

    public void scaleTo(float offset, QSImageView view, int i) {
        float scale;
        switch (i) {
            case 0:
                scale = (offset * 2 + view.getWidth()) / view.getWidth();
                break;
            case 1:
                scale = (offset * 2 + view.getHeight()) / view.getHeight();
                break;
            default:
                scale = 1.0f;
        }
        view.setScaleX(scale);
        view.setScaleY(scale);
        view.setLastScaleFactor(scale);
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
                if (!categoryRefs.contains(categoryRef)) {
                    categoryRefs.add(categoryRef);
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
                            if (!categoryRefs.contains(category._id)) {
                                categoryRefs.add(category._id);
                            }
                            getDataFromNet(category._id, category.matchInfo.row, category.matchInfo.column);
                        }
                    }
                }
            }
        }, null);
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void moveView(View view, float startX, float startY, float nextX, float nextY) {
        ObjectAnimator x = ObjectAnimator.ofFloat(view, "x", startX, nextX);
        ObjectAnimator y = ObjectAnimator.ofFloat(view, "y", startY, nextY);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(x, y);
        animatorSet.setDuration(0);
        animatorSet.start();
        ((QSImageView) view).setLastCentroid(new Point(view.getLeft() + ((QSImageView) view).getImageView().getDrawable().getIntrinsicWidth() / 2, view.getTop() + ((QSImageView) view).getImageView().getDrawable().getIntrinsicHeight() / 2));
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
