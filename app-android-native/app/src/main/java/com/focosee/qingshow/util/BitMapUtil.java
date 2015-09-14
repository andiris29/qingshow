package com.focosee.qingshow.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2015/1/12.
 */
public class BitMapUtil {

    public static Bitmap convertToBlur(Bitmap bmp, Context context){
        final int radius = 20;
        if (Build.VERSION.SDK_INT > 16) {
            Log.d(BitMapUtil.class.getSimpleName(), "VERSION.SDK_INT " + Build.VERSION.SDK_INT);
            Bitmap bitmap = bmp.copy(bmp.getConfig(), true);

            final RenderScript rs = RenderScript.create(context);
            final Allocation input = Allocation.createFromBitmap(rs, bmp, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(radius /* e.g. 3.f */);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);
            return bitmap;
        }
        return bmp;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle, Bitmap.CompressFormat type) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(type, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
