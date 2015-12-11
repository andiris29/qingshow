package com.focosee.qingshow.controller;

import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
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
import com.focosee.qingshow.tencent.common.bean.SendRedPackReqBean;
import com.focosee.qingshow.tencent.common.bean.SendRedPackResBean;
import com.focosee.qingshow.tencent.common.bean.UnifiedOrderReqBean;
import com.focosee.qingshow.tencent.common.bean.UnifiedOrderResBean;
import com.focosee.qingshow.tencent.service.CallbackReqBean;
import com.focosee.qingshow.tencent.service.CallbackResBean;
import com.focosee.qingshow.tencent.service.PayQueryService;
import com.focosee.qingshow.tencent.service.SendRedPackService;
import com.focosee.qingshow.tencent.service.UnifiedOrderService;
import com.focosee.qingshow.util.ServerError;
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
     * @param notifyUrl 回调地址
     * @param request Http Servlet Request(Spring framework autowrite)
     * @param response Http Servlet Response(Spring framework autowrite)
     * @return
     */
    @RequestMapping(value = "/prepay", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object handlePrePay(@RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "totalFee", required = true) String totalFee,
            @RequestParam(value = "orderName", required = true) String orderName,
            @RequestParam(value = "clientIp", required = true) String clientIp,
            @RequestParam(value = "notifyUrl", required = true) String notifyUrl,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        log.info("wechat/prepay start");
        
        ResponseJsonEntity returnEntity = new ResponseJsonEntity();
        log.debug("id=" + id);
        log.debug("totalFee=" + totalFee);
        log.debug("orderName=" + orderName);
        log.debug("clientIp=" + clientIp);
        
        try {
            UnifiedOrderReqBean bean = new UnifiedOrderReqBean();
            bean.setOut_trade_no(id);
            bean.setBody(orderName);
            bean.setFee_type("CNY");
            double totalFeeOriginal = NumberUtils.toDouble(totalFee, 0);
            totalFeeOriginal *= 100;
            int totalFeeFeng = NumberUtils.createBigDecimal(String.valueOf(totalFeeOriginal)).intValue();
            bean.setTotal_fee(String.valueOf(totalFeeFeng));
            bean.setSpbill_create_ip(clientIp);
            String callbackURL = URLDecoder.decode(notifyUrl, "UTF-8");
            log.debug("callbackURL=" + callbackURL);
            bean.setNotify_url(callbackURL);
            bean.setTrade_type("APP");
            bean.sign();
            
            UnifiedOrderService service = new UnifiedOrderService();
            UnifiedOrderResBean responseBody = service.request(bean);
            if (SUCCESS.compareTo(responseBody.getReturn_code()) != 0) {
                log.error("generate prepay_id failure. reason:" + responseBody.getReturn_msg());
                Metadata metadata = new Metadata();
                metadata.setError(ServerError.ERROR_GET_PREPAY_FAIL_CD);
                metadata.setDevInfo(responseBody.getReturn_msg());
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
        log.debug(postData);
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
            log.debug("callback return:" + resBean.toString());
            return resBean.toString();
        }
        
        
        resBean.setReturn_code(SUCCESS);
        resBean.setReturn_msg("OK");
        log.debug("callback return:" + resBean.toString());
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
                log.error("generate order error. err_code:[" + bean.getErr_code() + "],err_msg:[" + bean.getErr_code_des() + "]");
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
     * 向微信用户发送红包
     * 
     * @param mchBillno 商户订单号
     * @param openid 用户openid
     * @param totalAmount 付款金额
     * @param ip Ip地址
     * @param actName 活动名称
     * @param wishing 红包祝福语
     * @param remark 备注
     * @return
     */
    @RequestMapping(value = "/sendRedPack", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object sendRedPack(
            @RequestParam(value = "id", required = true)String mchBillno,
            @RequestParam(value = "openid", required = true)String openid, 
            @RequestParam(value = "amount", required = true)String totalAmount, 
            @RequestParam(value = "clientIp", required = true)String ip, 
            @RequestParam(value = "event", required = true)String actName,
            @RequestParam(value = "message", required = true)String wishing, 
            @RequestParam(value = "note", required = true)String remark) {
        log.info("wechat/sendRedPack");

        ResponseJsonEntity returnEntity = new ResponseJsonEntity();
        
        log.debug("id=" + mchBillno);
        log.debug("openid=" + openid);
        log.debug("amount=" + totalAmount);
        log.debug("clientIp=" + ip);
        log.debug("event=" + actName);
        log.debug("message=" + wishing);
        log.debug("note=" + remark);
        
        try {
            SendRedPackReqBean bean = new SendRedPackReqBean();
            double totalAmountOriginal = NumberUtils.toDouble(totalAmount, 0);
            totalAmountOriginal *= 100;
            int totalAmountFeng = NumberUtils.createBigDecimal(String.valueOf(totalAmountOriginal)).intValue();
            String sendName = "倾秀";
            bean.setTotal_amount(String.valueOf(totalAmountFeng));
            bean.setRe_openid(openid);
            bean.setTotal_num("1");
            bean.setWishing(wishing);
            bean.setClient_ip(ip);
            bean.setAct_name(actName);
            bean.setRemark(remark);
            bean.setMch_billno(mchBillno);
            bean.setSend_name(sendName);
            bean.sign();

            SendRedPackService service = new SendRedPackService();
            SendRedPackResBean responseBody = service.request(bean);
            if (SUCCESS.compareTo(responseBody.getReturn_code()) != 0) {
                log.error("send redpack failure. reason:" + responseBody.getReturn_msg());
                Metadata metadata = new Metadata();
                metadata.setError("9007");
                metadata.setDevInfo(responseBody.getReturn_msg());
                returnEntity.setMetadata(metadata);
                return returnEntity;
            } 
            if (SUCCESS.compareTo(responseBody.getResult_code()) != 0) {
                log.error("send redpack error. err_code:[" + responseBody.getErr_code() + "],err_msg:[" + responseBody.getErr_code_des() + "]");
                Metadata metadata = new Metadata();
                metadata.setError("9007");
                metadata.setDevInfo(responseBody.getErr_code() + "/" + responseBody.getErr_code_des());
                returnEntity.setMetadata(metadata);
                return returnEntity;
            }
            
            returnEntity.setData(responseBody);

        } catch (Exception ex) {
            log.error("send red pack exception.", ex);
            Metadata metadata = new Metadata();
            metadata.setError("9007");
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
