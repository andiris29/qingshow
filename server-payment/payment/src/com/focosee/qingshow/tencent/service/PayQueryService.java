package com.focosee.qingshow.tencent.service;

import org.apache.log4j.Logger;

import com.focosee.qingshow.tencent.common.Configure;
import com.focosee.qingshow.tencent.common.Util;
import com.focosee.qingshow.tencent.common.bean.PayQueryReqBean;
import com.focosee.qingshow.tencent.common.bean.PayQueryResBean;

public class PayQueryService extends BaseService {
    
    private static final Logger log = Logger.getLogger(PayQueryService.class);
    
    public PayQueryService() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        super(Configure.PAY_QUERY_API);
    }
    
    /**
     * 请求支付查询服务
     * @param scanPayQueryReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
    public PayQueryResBean request(PayQueryReqBean scanPayQueryReqData) throws Exception {

        //--------------------------------------------------------------------
        //发送HTTPS的Post请求到API地址
        //--------------------------------------------------------------------
        String responseString = sendPost(scanPayQueryReqData);
        log.debug("wechat response body:" + responseString);
        PayQueryResBean bean = (PayQueryResBean) Util.getObjectFromXML(responseString, PayQueryResBean.class);
        return bean;
    }

}
