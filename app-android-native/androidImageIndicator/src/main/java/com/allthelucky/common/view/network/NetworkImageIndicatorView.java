package com.allthelucky.common.view.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.allthelucky.common.view.ImageIndicatorView;
import com.app.library.common.view.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

//import com.nostra13.universalimageloader.core.ImageLoader;

//import com.android.http.WebImageView;

/**
 * Network ImageIndicatorView, by urls
 * 
 * @author steven-pan
 * 
 */
public class NetworkImageIndicatorView extends ImageIndicatorView {

    private boolean hasFirstBitmapSign = false;

	public NetworkImageIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NetworkImageIndicatorView(Context context) {
		super(context);
	}

	/**
	 * 设置显示图片URL列表
	 * 
	 * @param urlList
	 *            URL列表
	 */
	public void setupLayoutByImageUrl(final List<String> urlList, ImageLoader mImageLoader, DisplayImageOptions options) {
		if (urlList == null)
			throw new NullPointerException();

		final int len = urlList.size();
		if (len > 0) {
			for (int index = 0; index < len; index++) {
				final ImageView pageItem = new ImageView(getContext());
				pageItem.setScaleType(ScaleType.CENTER_CROP);
//                pageItem.setAdjustViewBounds(true);
                pageItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                pageItem.setBackgroundColor(Color.WHITE);
                mImageLoader.displayImage(urlList.get(index), pageItem, options, new AnimateFirstDisplayListener());
                pageItem.setTag(urlList.get(index));
				addViewItem(pageItem);
			}
		}
	}

//    public void removeAllViews() {
//        removeAllViews();
//    }

    public void addBitmapAtFirst(Bitmap firstImage, ImageLoader mImageLoader, DisplayImageOptions options) {
        if (hasFirstBitmapSign) {
            removeViewItemAtIndex(0);
        }
        final ImageView pageItem = new ImageView(getContext());
        pageItem.setScaleType(ScaleType.CENTER_CROP);
        pageItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        pageItem.setImageBitmap(firstImage);
//        mImageLoader.displayImage("http://img.uutuu.com/data6/a/ph/large/080412/c593300067964cbbb6b3ce45eed9c58f.jpg", pageItem);

        addViewItemAtIndex(pageItem, 0);
        hasFirstBitmapSign=true;
    }

    /** 图片加载监听事件 **/
    private static class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500); // 设置image隐藏动画500ms
                    displayedImages.add(imageUri); // 将图片uri添加到集合中
                }
            }
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

;       }
    }

}
