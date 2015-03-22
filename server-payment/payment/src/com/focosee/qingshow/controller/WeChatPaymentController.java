package com.focosee.qingshow.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wechat")
public class WeChatPaymentController {
    
    private static final Logger log = Logger.getLogger(WeChatPaymentController.class);
    
    @Value("${setting['weixin.app_id']}")
    private String appid;
    
    @Value("${setting['weixin.app_secret']}")
    private String appSecret;
    
    @Value("${setting['weixin.app_key']}")
    private String appKey;
    
    @Value("${setting['weixin.parent_key']}")
    private String parentKey;

    @RequestMapping(value = "/prepay", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object handlePrePay(@RequestParam(value = "tradeId", required = true) String tradeId) {
        log.info("handle prepay");
        return null;
    }
}
