package com.focosee.qingshow.model.vo.remix;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Administrator on 2015/11/24.
 */
public class QSRect {
    public float xPercent;
    public float yPercent;
    public float widthPercent;
    public float heightPercent;

    public QSRect(float xPercent, float yPercent, float widthPercent, float heightPercent) {
        this.xPercent = xPercent;
        this.yPercent = yPercent;
        this.widthPercent = widthPercent;
        this.heightPercent = heightPercent;
    }

    public RectF getRect(Point point){
        float maxWidth = point.x;
        float maxHeight = point.y;
        float left = maxWidth * xPercent / 100;
        float top = maxHeight * yPercent /100;
        float right = maxWidth * (xPercent + widthPercent) / 100;
        float bottom = maxHeight * (yPercent + heightPercent) / 100;
        return new RectF(left, top, right, bottom);
    }
}
