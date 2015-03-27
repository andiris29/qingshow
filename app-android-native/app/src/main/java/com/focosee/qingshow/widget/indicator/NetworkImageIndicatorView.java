package com.focosee.qingshow.widget.indicator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

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
	public void setupLayoutByImageUrl(final List<String> urlList, ImageLoader mImageLoader) {
		if (urlList == null)
			throw new NullPointerException();

		final int len = urlList.size();
		if (len > 0) {
			for (int index = 0; index < len; index++) {
				final ImageView pageItem = new ImageView(getContext());
				pageItem.setScaleType(ScaleType.CENTER_CROP);
//                pageItem.setAdjustViewBounds(true);
                pageItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                pageItem.setBackgroundColor(Color.WHITE);
                mImageLoader.displayImage(urlList.get(index), pageItem);
				addViewItem(pageItem);
			}
		}
	}


    public void addViewAtFirst(View view,boolean hasFirstBitmapSign){
        if (hasFirstBitmapSign) {
            removeViewItemAtIndex(0);
        }
        if(view != null){
            addViewItemAtIndex(view,0);
        }
    }

    public void addBitmapAtFirst(Bitmap firstImage) {
        if (hasFirstBitmapSign) {
            removeViewItemAtIndex(0);
        }
        final ImageView pageItem = new ImageView(getContext());
        pageItem.setScaleType(ScaleType.CENTER_CROP);
        pageItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if(!firstImage.isRecycled() && firstImage != null){
            pageItem.setImageBitmap(firstImage);
            addViewItemAtIndex(pageItem, 0);
        }
        hasFirstBitmapSign=true;
    }

}
