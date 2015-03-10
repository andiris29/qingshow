package com.focosee.qingshow.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * 自动全屏的VideoView
 */
public class MFullScreenVideoView extends VideoView {

	private int videoWidth;
	private int videoHeight;
    private ImageButton palyButton;


	public MFullScreenVideoView(Context context) {
		super(context);
	}

	public MFullScreenVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MFullScreenVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

    @Override
    public void destroyDrawingCache() {
        Log.i("tag","videoDestroy");
    }


    @Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(videoWidth, widthMeasureSpec);
		int height = getDefaultSize(videoHeight, heightMeasureSpec);
		if (videoWidth > 0 && videoHeight > 0) {
			if (videoWidth * height > width * videoHeight) {
				height = width * videoHeight / videoWidth;
			} else if (videoWidth * height < width * videoHeight) {
				width = height * videoWidth / videoHeight;
			}
		}
		setMeasuredDimension(width, height);
	}


    public int getVideoWidth() {
		return videoWidth;
	}

	public void setVideoWidth(int videoWidth) {
		this.videoWidth = videoWidth;
	}

	public int getVideoHeight() {
		return videoHeight;
	}

	public void setVideoHeight(int videoHeight) {
		this.videoHeight = videoHeight;
	}

}
