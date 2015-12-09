package com.focosee.qingshow.tencent.service;

import org.apache.log4j.Logger;

import com.focosee.qingshow.tencent.common.Configure;
import com.focosee.qingshow.tencent.common.Util;
import com.focosee.qingshow.tencent.common.bean.SendRedPackReqBean;
import com.focosee.qingshow.tencent.common.bean.SendRedPackResBean;

public class SendRedPackService extends BaseService {
    private static final Logger log = Logger.getLogger(SendRedPackService.class);
    
    public SendRedPackService() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        super(Configure.SEND_REDPACK_API);
    }
    
    /**
     * 发送红包
     * 
     * @param bean
     *            这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
    public SendRedPackResBean request(SendRedPackReqBean bean) throws Exception {

        // --------------------------------------------------------------------
        // 发送HTTPS的Post请求到API地址
        // --------------------------------------------------------------------
        String responseString = sendPost(bean);
        log.debug("wechat response body:" + responseString);
        SendRedPackResBean responseBody = (SendRedPackResBean) Util.getObjectFromXML(responseString, SendRedPackResBean.class);
        return responseBody;
    }
}
