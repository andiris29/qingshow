package com.focosee.qingshow.model.vo.context;

import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by Administrator on 2015/11/24.
 */
public class QSRect {
    public int xPercent;
    public int yPercent;
    public int widthPercent;
    public int heightPercent;

    public QSRect(int xPercent, int yPercent, int widthPercent, int heightPercent) {
        this.xPercent = xPercent;
        this.yPercent = yPercent;
        this.widthPercent = widthPercent;
        this.heightPercent = heightPercent;
    }

    public Rect getRect(Point point){
        int maxWidth = point.x;
        int maxHeight = point.y;
        int left = maxWidth * xPercent / 100;
        int top = maxHeight * yPercent /100;
        int right = maxWidth * (xPercent + widthPercent) / 100;
        int bottom = maxHeight * (yPercent + heightPercent) / 100;
        return new Rect(left, top, right, bottom);
    }
}
