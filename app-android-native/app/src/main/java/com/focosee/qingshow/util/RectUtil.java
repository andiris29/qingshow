package com.focosee.qingshow.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.focosee.qingshow.widget.QSImageView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/10.
 */
public class RectUtil {
    public static List<Rect> clipRect(Rect targetRect, Rect rect) {
        if (rect.contains(targetRect)) {
            return null;
        }
        List<Rect> rects = new ArrayList<>();

        if (rect.right < targetRect.left || rect.top > targetRect.bottom || rect.left > targetRect.right || rect.bottom < targetRect.top) {
            rects.add(targetRect);
            return rects;
        }

        Rect interRect = getRectIntersection(targetRect, rect);

        if (interRect.left > targetRect.left) {
            rects.add(new Rect(targetRect.left, targetRect.top, interRect.left, targetRect.bottom));
            targetRect = new Rect(interRect.left, targetRect.top, targetRect.right, targetRect.bottom);
        }

        if (interRect.top > targetRect.top) {
            rects.add(new Rect(targetRect.left, targetRect.top, targetRect.right, interRect.top));
            targetRect = new Rect(targetRect.left, interRect.top, targetRect.right, targetRect.bottom);
        }

        if (interRect.right < targetRect.right) {
            rects.add(new Rect(interRect.right, targetRect.top, targetRect.right, targetRect.bottom));
            targetRect = new Rect(targetRect.left, targetRect.top, interRect.right, targetRect.bottom);
        }

        if (interRect.bottom < targetRect.bottom) {
            rects.add(new Rect(targetRect.left, interRect.bottom, targetRect.right, targetRect.bottom));
        }

        return rects;
    }

    public static Rect getRectIntersection(Rect targetRect, Rect rect) {
        Rect resultRect = new Rect();

        if (rect.right < targetRect.left || rect.top > targetRect.bottom || rect.left > targetRect.right || rect.bottom < targetRect.top) {
            return null;
        }

        if (targetRect.left > rect.left) {
            resultRect.left = targetRect.left;
        } else {
            resultRect.left = rect.left;
        }

        if (targetRect.top > rect.top) {
            resultRect.top = targetRect.top;
        } else {
            resultRect.top = rect.top;
        }

        if (targetRect.right < rect.right) {
            resultRect.right = targetRect.right;
        } else {
            resultRect.right = rect.right;
        }

        if (targetRect.bottom < rect.bottom) {
            resultRect.bottom = targetRect.bottom;
        } else {
            resultRect.bottom = rect.bottom;
        }

        return resultRect;
    }

    public static boolean checkBorder(Rect rect, Rect parentRect, float scaleFactor, int barHeight) {
        rect.top -= barHeight;

        float scaleX = rect.width() * (1 - scaleFactor) / 2;
        float scaleY = rect.height() * (1 - scaleFactor) / 2;

        if (!(rect.left > 0) || !(rect.right < parentRect.width()) || !(rect.top > 0) || !(rect.bottom < parentRect.height())) {
            return false;
        }

        if (!(rect.left + scaleX > 0)) {
            return false;
        }

        if (rect.right + scaleX > parentRect.width()) {
            return false;
        }

        if (!(rect.top + scaleY > 0)) {
            return false;
        }

        if (rect.bottom + scaleY > parentRect.height()) {
            return false;
        }

        return true;
    }


    public static Rect getRect(View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        return rect;
    }

    public static Rect getParentRect(View view) {
        return getRect((ViewGroup) view.getParent());
    }


    public static float getRectArea(Rect rect) {
        return Math.abs(rect.width()) * Math.abs(rect.height());
    }

    public static String rectSerializer(Rect rect){
        int arrs[] = new int[4];
        arrs[0] = rect.left;
        arrs[1] = rect.top;
        arrs[2] = rect.width();
        arrs[3] = rect.height();
        return new Gson().toJson(arrs);
    }

    public static void locateView(Rect rect,QSImageView view){
        moveView(view, view.getX(), view.getY(), rect.left, rect.top);
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);

        float width = view.getImageView().getDrawable().getIntrinsicWidth();
        float height = view.getImageView().getDrawable().getIntrinsicHeight();
        float maxWidth = rect.width();
        float maxHeight = rect.height();
        float radio = (maxWidth / width) > (maxHeight / height) ? (maxHeight / height) : (maxWidth / width);
        view.setScaleX(radio);
        view.setScaleY(radio);
    }

    public static void moveView(View view, float startX, float startY, float nextX, float nextY) {
        ObjectAnimator x = ObjectAnimator.ofFloat(view, "x", startX, nextX);
        ObjectAnimator y = ObjectAnimator.ofFloat(view, "y", startY, nextY);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(x, y);
        animatorSet.setDuration(0);
        animatorSet.start();
        if (view instanceof QSImageView){
            ((QSImageView) view).setLastCentroid(new Point(view.getLeft() + ((QSImageView) view).getImageView().getDrawable().getIntrinsicWidth() / 2, view.getTop() + ((QSImageView) view).getImageView().getDrawable().getIntrinsicHeight() / 2));
        }
    }

}
