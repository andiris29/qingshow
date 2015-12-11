package com.focosee.qingshow.tencent.service;

import org.apache.log4j.Logger;

import com.focosee.qingshow.tencent.common.Configure;
import com.focosee.qingshow.tencent.common.Util;
import com.focosee.qingshow.tencent.common.bean.UnifiedOrderReqBean;
import com.focosee.qingshow.tencent.common.bean.UnifiedOrderResBean;

public class UnifiedOrderService extends BaseService {
    private static final Logger log = Logger.getLogger(UnifiedOrderService.class);

    public UnifiedOrderService() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        super(Configure.PREPAY_API);
    }

    /**
     * 请求支付查询服务
     * 
     * @param scanPayQueryReqData
     *            这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
    public UnifiedOrderResBean request(UnifiedOrderReqBean bean) throws Exception {

        // --------------------------------------------------------------------
        // 发送HTTPS的Post请求到API地址
        // --------------------------------------------------------------------
        String responseString = sendPost(bean);
        log.debug("wechat response body:" + responseString);
        UnifiedOrderResBean responseBody = (UnifiedOrderResBean) Util.getObjectFromXML(responseString, UnifiedOrderResBean.class);
        return responseBody;
    }
}
