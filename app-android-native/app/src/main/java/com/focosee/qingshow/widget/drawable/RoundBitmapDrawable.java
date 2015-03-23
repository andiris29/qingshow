package com.focosee.qingshow.widget.drawable;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2015/3/23.
 */
public class RoundBitmapDrawable extends Drawable {
    private Paint paint;
    private Bitmap bitmap;
    private int width;
    private int height;

    private RectF rectF;
    private int rx;
    private int ry;

    public RoundBitmapDrawable(Bitmap bitmap, int rx, int ry) {
        this.bitmap = bitmap;
        this.rx = rx;
        this.ry = ry;
        paint = new Paint();
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        rectF = new RectF(left, top, right, bottom);
    }

    @Override
    public void draw(Canvas canvas) {
        paint.setAntiAlias(true);
        width = canvas.getWidth();
        height = canvas.getHeight();
        paint.setShader(new BitmapShader(Bitmap.createScaledBitmap(bitmap, width, height, true),
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(rectF, rx, ry, paint);
    }

    @Override
    public int getIntrinsicWidth() {
        return width;
    }

    @Override
    public int getIntrinsicHeight() {
        return height;
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

}
