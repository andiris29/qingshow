package com.focosee.qingshow.wxapi;

import com.tencent.mm.sdk.modelbase.BaseResp;

/**
 * Created by Administrator on 2015/7/23.
 */
public class WXPayEvent {
    public BaseResp baseResp;

    public WXPayEvent(BaseResp baseResp) {
        this.baseResp = baseResp;
    }
}
