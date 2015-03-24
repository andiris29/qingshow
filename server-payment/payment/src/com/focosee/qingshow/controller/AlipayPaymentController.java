package com.focosee.qingshow.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alipay.util.AlipayNotify;
import com.focosee.qingshow.bean.AlipayCallbackEntity;

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
    
    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    public Object callback(@ModelAttribute AlipayCallbackEntity entity, HttpServletRequest request,
            HttpServletResponse response) {
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
            params.put(name, valueStr);
        }

        if (!AlipayNotify.verify(params)) {
            return FAIL;
        }

        // ////////////////////////////////////////////////////////////////////////////////////////
        // 请在这里加上商户的业务逻辑程序代码
        // ——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
        if (entity.getTrade_status().equals(TRADE_FINISHED)) {
            // 判断该笔订单是否在商户网站中已经做过处理
            // 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
            // 如果有做过处理，不执行商户的业务程序

            // 注意：
            // 该种交易状态只在两种情况下出现
            // 1、开通了普通即时到账，买家付款成功后。
            // 2、开通了高级即时到账，从该笔交易成功时间算起，过了签约时的可退款时限（如：三个月以内可退款、一年以内可退款等）后。
        } else if (entity.getTrade_status().equals(TRADE_SUCCESS)) {
            // 判断该笔订单是否在商户网站中已经做过处理
            // 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
            // 如果有做过处理，不执行商户的业务程序

            // 注意：
            // 该种交易状态只在一种情况下出现——开通了高级即时到账，买家付款成功后。
        } else if (entity.getTrade_status().equals(WAIT_BUYER_PAY)) {
            
        } else if (entity.getTrade_status().equals(TRADE_CLOSED)) {
            
        }

        // ——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
        return SUCCESS;
    }

}
