package com.focosee.qingshow.wxapi;

import com.tencent.mm.sdk.modelbase.BaseResp;

/**
 * Created by DylanJiang on 15/8/10.
 */
public class PushEvent {
    public BaseResp baseResp;

    public PushEvent(BaseResp baseResp) {
        this.baseResp = baseResp;
    }
}
