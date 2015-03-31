package com.focosee.qingshow.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.focosee.qingshow.bean.Metadata;
import com.focosee.qingshow.bean.ResponseJsonEntity;
import com.focosee.qingshow.tencent.common.Util;
import com.focosee.qingshow.tencent.common.bean.PayQueryReqBean;
import com.focosee.qingshow.tencent.common.bean.PayQueryResBean;
import com.focosee.qingshow.tencent.common.bean.UnifiedOrderReqBean;
import com.focosee.qingshow.tencent.common.bean.UnifiedOrderResBean;
import com.focosee.qingshow.tencent.service.CallbackReqBean;
import com.focosee.qingshow.tencent.service.CallbackResBean;
import com.focosee.qingshow.tencent.service.PayQueryService;
import com.focosee.qingshow.tencent.service.UnifiedOrderService;
import com.focosee.qingshow.util.ServerError;
import com.focosee.qingshow.util.ServletHelper;
import com.google.gson.Gson;

/**
 * 微信支付Controller
 * 
 * @author qusheng
 */
@RestController
@RequestMapping("/wechat")
public class WeChatPaymentController {

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(WeChatPaymentController.class);
    
    private static final String SUCCESS = "SUCCESS";
    private static final String FAIL = "FAIL";
    
    @Value("#{setting['weixin.notify_url']}")
    private String notifyUrl;
    
    @Value("#{setting['qingshow.appserver.wechat.callback']}")
    private String appServerCallbackUrl;

    /**
     * 生成预支付订单 <br>
     * url: /wechat/prepay <br>
     * method: get <br>
     * 
     * @param id 请求参数:商户订单号(使用trade的 _id)
     * @param totolPrice 请求参数:订单总金额
     * @param orderName 请求参数:订单商品名，（如果orders是复数个的话，逗号分隔）
     * @param request Http Servlet Request(Spring framework autowrite)
     * @param response Http Servlet Response(Spring framework autowrite)
     * @return
     */
    @RequestMapping(value = "/prepay", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object handlePrePay(@RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "totalFee", required = true) String totalFee,
            @RequestParam(value = "orderName", required = true) String orderName,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        log.info("wechat/prepay start");
        
        ResponseJsonEntity returnEntity = new ResponseJsonEntity();
        
        try {
            UnifiedOrderReqBean bean = new UnifiedOrderReqBean();
            bean.setOut_trade_no(id);
            bean.setBody(orderName);
            bean.setFee_type("CNY");
            bean.setTotal_fee(StringUtils.remove(totalFee, "."));
            bean.setSpbill_create_ip(ServletHelper.getIpAddr(request));
            bean.setNotify_url(this.notifyUrl);
            bean.setTrade_type("APP");
            bean.sign();
            
            UnifiedOrderService service = new UnifiedOrderService();
            UnifiedOrderResBean responseBody = service.request(bean);
            if (SUCCESS.compareTo(responseBody.getReturn_code()) != 0) {
                log.error("generate prepay_id failure. reason:" + responseBody.getReturn_msg());
                Metadata metadata = new Metadata();
                metadata.setError(ServerError.ERROR_GET_PREPAY_FAIL_CD);
                metadata.setDevInfo(ServerError.ERROR_GET_PREPAY_FAIL_MSG);
                returnEntity.setMetadata(metadata);
                return returnEntity;
            } 
            if (SUCCESS.compareTo(responseBody.getResult_code()) != 0) {
                log.error("generate prepay_id error. err_code:[" + responseBody.getErr_code() + "],err_msg:[" + responseBody.getErr_code_des() + "]");
                Metadata metadata = new Metadata();
                metadata.setError(ServerError.ERROR_GET_PREPAY_FAIL_CD);
                metadata.setDevInfo(responseBody.getErr_code() + "/" + responseBody.getErr_code_des());
                returnEntity.setMetadata(metadata);
                return returnEntity;
            }
            
            returnEntity.setData(responseBody);
        } catch (Exception ex) {
            log.error("generate prepay_id exception.", ex);
            Metadata metadata = new Metadata();
            metadata.setError(ServerError.ERROR_GET_PREPAY_FAIL_CD);
            metadata.setDevInfo(ServerError.ERROR_GET_PREPAY_FAIL_MSG);
            returnEntity.setMetadata(metadata);
        }
        
        return returnEntity;
    }
    
    /**
     * 微信服务器的回调接口
     * @param postData xml data in request’s body
     * @param request http servlet request
     * @param response http servlet response
     * @return json object
     */
    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    public String callback(@RequestBody String postData,
            HttpServletRequest request, HttpServletResponse response) {
        
        log.info("wechat callback start");        
        
        CallbackReqBean reqBean = (CallbackReqBean) Util.getObjectFromXML(postData, CallbackReqBean.class);
        CallbackResBean resBean = new CallbackResBean();
        Map<String, Object> params = reqBean.toMap();
  
        RestTemplate restClient = new RestTemplate();
        ResponseJsonEntity appRes = restClient.postForObject(appServerCallbackUrl, params, ResponseJsonEntity.class);
        Gson gson = new Gson();
        log.debug("app-server return:" + gson.toJson(appRes));
        if ((appRes.getData() == null) || ((appRes.getMetadata() !=null) && StringUtils.isNotEmpty(appRes.getMetadata().getError()))) {
            resBean.setReturn_code(FAIL);
            resBean.setReturn_msg(appRes.getMetadata().getError());
            return resBean.toString();
        }
        
        
        resBean.setReturn_code(SUCCESS);
        resBean.setReturn_msg("OK");
        return resBean.toString();
    }
    
    /**
     * 向微信服务器查询订单
     * 
     * @param id trade's id
     * @param request Http Servlet Request
     * @param response Http Servlet Response
     * @return json object
     */
    @RequestMapping(value = "/queryOrder", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object queryOrder(@RequestParam(value = "id", required = true) String id) {

        log.info("wechat/querOrder start");

        PayQueryReqBean requestBean = new PayQueryReqBean(StringUtils.EMPTY, id);
        ResponseJsonEntity returnEntity = new ResponseJsonEntity();
        
        try {
            PayQueryService service = new PayQueryService();
            PayQueryResBean bean = service.request(requestBean);
            
            if (SUCCESS.compareTo(bean.getReturn_code()) != 0) {
                log.error("queru order fail. message:" + bean.getReturn_msg());
                Metadata metadata = new Metadata();
                metadata.setError(ServerError.ERROR_QUERY_ORDER_FAIL_CD);
                metadata.setDevInfo(bean.getReturn_msg());
                returnEntity.setMetadata(metadata);
                return returnEntity;
            }
            if (SUCCESS.compareTo(bean.getResult_code()) != 0) {
                log.error("generate prepay_id error. err_code:[" + bean.getErr_code() + "],err_msg:[" + bean.getErr_code_des() + "]");
                Metadata metadata = new Metadata();
                metadata.setError(ServerError.ERROR_GET_PREPAY_FAIL_CD);
                metadata.setDevInfo(bean.getErr_code() + "/" + bean.getErr_code_des());
                returnEntity.setMetadata(metadata);
                return returnEntity;
            }
            
            returnEntity.setData(bean);
        } catch (Exception e) {
            log.error("query order exception.", e);
            Metadata metadata = new Metadata();
            metadata.setError(ServerError.ERROR_QUERY_ORDER_FAIL_CD);
            metadata.setDevInfo(ServerError.ERROR_QUERY_ORDER_FAIL_MSG);
            returnEntity.setMetadata(metadata);
        }        
        return returnEntity;
    }

    /**
     * 向微信服务器发送发货通知
     * 
     * @param openid 购买用户的 OpenId
     * @param transid 交易单号
     * @param out_trade_no trade's id
     * @param deliver_status 发货状态，1表示成功，0表示失败，失败时需要再在deliver_msg填上失败原因
     * @param deliver_msg 发货状态信息，UTF8编码
     * @param request Http Servlet Request
     * @param response Http Servlet Response
     * @return json object
     */
//    @RequestMapping(value = "deliverNotify", method = RequestMethod.GET)
//    private Object deliverNotify(
//            @RequestParam(value = "openid", required = true) String openid,
//            @RequestParam(value = "transid", required = true) String transid,
//            @RequestParam(value = "out_trade_no", required = true) String out_trade_no,
//            @RequestParam(value = "deliver_status", required = false) String deliver_status,
//            @RequestParam(value = "deliver_msg", required = false) String deliver_msg,
//            HttpServletRequest request, HttpServletResponse response) {
//
//        log.info("wechat/deliverNotify start");
//
//        ResponseJsonEntity returnEntity = new ResponseJsonEntity();
//
//        initWechatApiSDK(request, response);
//        this.getToken();
//        
//        String deliver_timestamp = Sha1Util.getTimeStamp();
//
//        SortedMap<String, String> params = new TreeMap<String, String>(); 
//        params.put("appid", this.appId);
//        params.put("deliver_msg", deliver_msg);
//        params.put("deliver_status", deliver_status);
//        params.put("deliver_timestamp", deliver_timestamp);
//        params.put("openid", openid);
//        params.put("out_trade_no", out_trade_no);
//        params.put("transid", transid);
//
//        String sign = StringUtils.EMPTY;
//
//        try {
//            sign = Sha1Util.createSHA1Sign(params);
//            log.debug("sign app_signature result:[" + sign + "]");
//        } catch (Exception e) {
//            log.error("generate package error", e);
//            Metadata metadata = new Metadata();
//            metadata.setError(ServerError.ERROR_SIGN_PREPAY_PACKAGE_CD);
//            metadata.setDevInfo(ServerError.ERROR_SIGN_PREPAY_PACKAGE_MSG);
//            returnEntity.setMetadata(metadata);
//            return returnEntity;
//        }
//        params.put("app_signature", sign);
//        params.put("sign_method", "sha1");
//
//        Map<String, String> errorInfo = this.requestHandler.sendDeliverNotify(params);
//        if (errorInfo == null) {
//            Metadata metadata = new Metadata();
//            metadata.setError(ServerError.ERROR_DELIVER_FAILURE_CD);
//            metadata.setDevInfo(ServerError.ERROR_DELIVER_FAILURE_MSG);
//            returnEntity.setMetadata(metadata);
//            return returnEntity;
//        }
//        
//        returnEntity.setData(errorInfo);
//        return returnEntity;
//    }
//    
}
