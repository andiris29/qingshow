package com.allthelucky.common.view.network;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.allthelucky.common.view.ImageIndicatorView;
import com.app.library.common.view.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

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
				pageItem.setImageResource(R.drawable.ic_launcher);
                mImageLoader.displayImage(urlList.get(index), pageItem, options);
				addViewItem(pageItem);
			}
		}
	}

}
