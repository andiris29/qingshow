package com.focosee.qingshow.httpapi.fresco.factory;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by Administrator on 2015/8/4.
 */
public class QSDraweeControllerFactory {

    public static DraweeController create(String uri, SimpleDraweeView simpleDraweeView) {
        return createBuilder(uri, simpleDraweeView).build();
    }

    public static PipelineDraweeControllerBuilder createBuilder(String uri, SimpleDraweeView simpleDraweeView) {
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(uri))
                .setImageRequest(QSImageRequestFactory.createBuilder(uri).build())
                .setAutoPlayAnimations(false)
                .setTapToRetryEnabled(false)
                .setOldController(simpleDraweeView.getController());
        return builder;
    }

}
