package com.focosee.qingshow.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alipay.util.AlipayNotify;
import com.focosee.qingshow.bean.AlipayCallbackEntity;
import com.focosee.qingshow.bean.ResponseJsonEntity;

@RestController
@RequestMapping("/alipay")
public class AlipayPaymentController {
    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(AlipayPaymentController.class);

    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";
    private static final String TRADE_FINISHED = "TRADE_FINISHED";
    private static final String TRADE_SUCCESS = "TRADE_SUCCESS";
    private static final String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
    private static final String TRADE_CLOSED = "TRADE_CLOSED";
    
    private static final String REFUND_SUCCESS = "REFUND_SUCCESS";
    
    @Value("$setting['qingshow.appserver.alipay.callback']")
    private String appServerCallbackUrl;
    
    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    public Object callback(@ModelAttribute AlipayCallbackEntity entity, HttpServletRequest request,
            HttpServletResponse response) {
        log.debug("alipay callback start");
        
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            log.debug("request[" + name + "]=" + valueStr);
            params.put(name, valueStr);
        }

        if (!AlipayNotify.verify(params)) {
            return FAIL;
        }
        
        log.debug("alipay TradeStatus=" + entity.getTrade_status());
        log.debug("alipay RefundStatus=" + StringUtils.trimToNull(entity.getRefund_status()));
        
        RestTemplate restClient = new RestTemplate();
        
        // ////////////////////////////////////////////////////////////////////////////////////////
        // 请在这里加上商户的业务逻辑程序代码
        // ——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
        if (entity.getTrade_status().equals(TRADE_FINISHED)) {            
            params.put("status", "5");
            ResponseJsonEntity appRes = restClient.postForObject(appServerCallbackUrl, params, ResponseJsonEntity.class);
            if (appRes.getData() == null) {
                return FAIL;
            }
            return SUCCESS;
        } else if (entity.getTrade_status().equals(TRADE_SUCCESS)) {
            params.put("status", "1");
            ResponseJsonEntity appRes = restClient.postForObject(appServerCallbackUrl, params, ResponseJsonEntity.class);
            if (appRes.getData() == null) {
                return FAIL;
            }
            return SUCCESS;
        } else if (entity.getTrade_status().equals(WAIT_BUYER_PAY)) {
            // 该状态不处理
        } else if (entity.getTrade_status().equals(TRADE_CLOSED)) {
            if (StringUtils.equals(REFUND_SUCCESS, entity.getRefund_status())) {
                // 全额退款情况
                params.put("status", "9");
                ResponseJsonEntity appRes = restClient.postForObject(appServerCallbackUrl, params, ResponseJsonEntity.class);
                if (appRes.getData() == null) {
                    return FAIL;
                }
                return SUCCESS;
            }
        }

        // ——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
        return SUCCESS;
    }

}
