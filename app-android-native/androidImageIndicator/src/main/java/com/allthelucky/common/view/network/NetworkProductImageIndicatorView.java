package com.allthelucky.common.view.network;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.allthelucky.common.view.ProductImageIndicatorView;
import com.app.library.common.view.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class NetworkProductImageIndicatorView extends ProductImageIndicatorView {
    public NetworkProductImageIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NetworkProductImageIndicatorView(Context context) {
        super(context);
    }

    public void setupData(final List<String> urlList, final List<String> descriptionList, final List<String> brandList, ImageLoader mImageLoader) {
        if (urlList == null)
            throw new NullPointerException();

        final int len = urlList.size();
        if (len > 0) {
            for (int index = 0; index < len; index++) {
                final ImageView pageItem = new ImageView(getContext());
                pageItem.setScaleType(ImageView.ScaleType.CENTER_CROP);
                pageItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                mImageLoader.displayImage(urlList.get(index), pageItem);
                addViewItem(pageItem);
            }
        }

        setDescriptionList(descriptionList);
        setBrandList(brandList);
    }

}
