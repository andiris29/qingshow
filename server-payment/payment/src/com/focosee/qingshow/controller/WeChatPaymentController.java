package com.focosee.qingshow.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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

import com.focosee.qingshow.bean.AccessToken;
import com.focosee.qingshow.bean.Metadata;
import com.focosee.qingshow.bean.ResponseJsonEntity;
import com.focosee.qingshow.util.ServerError;
import com.focosee.qingshow.util.ServletHelper;
import com.wxap.RequestHandler;
import com.wxap.ResponseHandler;
import com.wxap.util.Sha1Util;

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
    
    private static final String TOKEN_KEY = "WECHAT_TOKEN";

    /**
     * 标识申请的应用
     */
    @Value("${setting['weixin.app_id']}")
    private String appId;

    /**
     * API 的权限获取所需密钥 Key。
     */
    @Value("${setting['weixin.app_secret']}")
    private String appSecret;

    /**
     * 支付请求中用于加密的密钥 Key,可验证商户唯一身份, PaySignKey 对应
     * 于支付场景中的 appKey 值。
     */
    @Value("${setting['weixin.app_key']}")
    private String appKey;

    /**
     * 财付通商户权限密钥 Key
     */
    @Value("${setting['weixin.parent_key']}")
    private String parentKey;
    
    
    /**
     * 财付通商户身份的标识
     */
    @Value("${setting['weixin.parent_id']}")
    private String parentId;
    
    @Value("${setting['weixin.notify_url']}")
    private String notifyUrl;
    
    /**
     * WeChatOpenPlatform's API SDK
     */
    private RequestHandler requestHandler;

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
        ResponseJsonEntity returnEntity = new ResponseJsonEntity();
        
        // initialze sdk
        initWechatApiSDK(request, response);
        
        // get access_token
        AccessToken token = getToken();
        
        // get access_token failure.
        if (token == null) {
            Metadata metadata = new Metadata();
            metadata.setError(ServerError.ERROR_GET_TOKEN_FAIL_CD);
            metadata.setMessage(ServerError.ERROR_GET_TOKEN_FAIL_MSG);
            returnEntity.setMetadata(metadata);
            return returnEntity;
        }
        
        //=========================
        //生成预支付单
        //=========================
        //设置package订单参数
        SortedMap<String, String> packageParams = new TreeMap<String, String>();
        packageParams.put("bank_type", "WX"); //银行通道类型
        packageParams.put("body", orderName); //商品描述
        packageParams.put("notify_url", this.notifyUrl); //接收财付通通知的URL
        packageParams.put("partner", this.parentId); //商户号
        packageParams.put("out_trade_no", id); //商家订单号
        packageParams.put("total_fee", totalFee); //商品金额,以分为单位
        packageParams.put("spbill_create_ip", ServletHelper.getIpAddr(request)); //订单生成的机器IP，指用户浏览器端IP
        packageParams.put("fee_type", "1"); //币种，取值:1(人民币),暂只支持 1;
        packageParams.put("input_charset", "UTF-8"); //字符编码
        
        //获取package包
        String packageValue = StringUtils.EMPTY;
        try {
            packageValue = this.requestHandler.genPackage(packageParams);
            log.debug("generate package result:[" + packageValue+"]");
        } catch (UnsupportedEncodingException e) {
            log.error("generate package error", e);
            Metadata metadata = new Metadata();
            metadata.setError(ServerError.ERROR_GENERATE_PREPAY_PACKAGE_CD);
            metadata.setMessage(ServerError.ERROR_GENERATE_PREPAY_PACKAGE_MSG);
            returnEntity.setMetadata(metadata);
            return returnEntity;
        }

        String noncestr = Sha1Util.getNonceStr();
        String timestamp = Sha1Util.getTimeStamp();
        String traceid = id;    // 由开发者自定义,可用于订单的查询与跟踪,建议根据支付用户信息生成此 id

        //设置支付参数
        SortedMap<String, String> signParams = new TreeMap<String, String>();
        signParams.put("appid", this.appId);
        signParams.put("appkey", this.appKey);
        signParams.put("noncestr", noncestr);
        signParams.put("package", packageValue);
        signParams.put("timestamp", timestamp);
        signParams.put("traceid", traceid);

        //生成支付签名，要采用URLENCODER的原始值进行SHA1算法！
        String sign = StringUtils.EMPTY;
        try {
            sign = Sha1Util.createSHA1Sign(signParams);
            log.debug("sign app_signature result:[" + sign + "]");
        } catch (Exception e) {
            log.error("generate package error", e);
            Metadata metadata = new Metadata();
            metadata.setError(ServerError.ERROR_SIGN_PREPAY_PACKAGE_CD);
            metadata.setMessage(ServerError.ERROR_SIGN_PREPAY_PACKAGE_MSG);
            returnEntity.setMetadata(metadata);
            return returnEntity;
        }
        //增加非参与签名的额外参数
        signParams.put("app_signature", sign);
        signParams.put("sign_method", "sha1");

        //获取prepayId
        String prepayid = this.requestHandler.sendPrepay(signParams);
        
        if (StringUtils.isEmpty(prepayid)) {
            log.error("get prepay id failure");
            Metadata metadata = new Metadata();
            metadata.setError(ServerError.ERROR_GET_PREPAY_FAIL_CD);
            metadata.setMessage(ServerError.ERROR_GET_PREPAY_FAIL_MSG);
            returnEntity.setMetadata(metadata);
            return returnEntity;
        }
        
        //签名参数列表
        SortedMap<String, String> prePayParams = new TreeMap<String, String>();
        prePayParams.put("appid", this.appId);
        prePayParams.put("appkey", this.appKey);
        prePayParams.put("noncestr", noncestr);
        prePayParams.put("package", "Sign=WXPay");
        prePayParams.put("partnerid", this.parentId);
        prePayParams.put("prepayid", prepayid);
        prePayParams.put("timestamp", timestamp);
        //生成签名
        try {
            sign = Sha1Util.createSHA1Sign(prePayParams);
            log.debug("sign app_signature result:[" + sign + "]");
        } catch (Exception e) {
            log.error("get prepay id failure");
            Metadata metadata = new Metadata();
            metadata.setError(ServerError.ERROR_SIGN_PREPAY_PACKAGE_CD);
            metadata.setMessage(ServerError.ERROR_SIGN_PREPAY_PACKAGE_MSG);
            returnEntity.setMetadata(metadata);
        }

       
        //输出参数
        Map<String, Object> data = new TreeMap<String, Object>();
        data.put("appid", this.appId);
        data.put("partnerid", this.parentId);
        data.put("noncestr", noncestr);
        data.put("package", "Sign=WXPay");
        data.put("prepayid", prepayid);
        data.put("sign", sign);

        returnEntity.setData(data);
        return returnEntity;
    }
    
    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    public Object callback(
            @RequestParam(value = "", required = false) String sign_type,
            @RequestParam(value = "", required = false) String input_charset,
            @RequestParam(value = "", required = false) String sign,
            @RequestParam(value = "", required = false) Integer trade_mode,
            @RequestParam(value = "", required = false) Integer trade_state,
            @RequestParam(value = "", required = false) String partner,
            @RequestParam(value = "", required = false) String bank_type,
            @RequestParam(value = "", required = false) String bank_billno,
            @RequestParam(value = "", required = false) Double total_fee,
            @RequestParam(value = "", required = false) Integer fee_type,
            @RequestParam(value = "", required = false) String notify_id,
            @RequestParam(value = "", required = false) String transaction_id,
            @RequestParam(value = "", required = false) String out_trade_no,
            @RequestParam(value = "", required = false) String attach,
            @RequestParam(value = "", required = false) String time_end,
            @RequestParam(value = "", required = false) Integer transport_fee, 
            @RequestBody String postData,
            HttpServletRequest request, HttpServletResponse response) {
        
        //---------------------------------------------------------
        //微信支付通知（后台通知）示例，商户按照此文档进行开发即可
        //---------------------------------------------------------

        //创建支付应答对象
        ResponseHandler resHandler = new ResponseHandler(request, response);
        //创建请求对象
        initWechatApiSDK(request, response);
        
        //通知签名验证失败
        if (resHandler.isTenpaySign() != true) {
            try {
                resHandler.sendToCFT("fail");
            } catch (IOException e) {
                log.error("send pay status", e);
            }
            return null;
        }
        
        // TODO need app-server's callback

        
        
        try {
            resHandler.sendToCFT("fail");
        } catch (IOException e) {
            log.error("send pay status", e);
        }
        return null;
    }
    
    @RequestMapping(value = "/queryOrder", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object queryOrder(@RequestParam(value = "id", required = true) String id, HttpServletRequest request, HttpServletResponse response) {
        ResponseJsonEntity returnEntity = new ResponseJsonEntity();
        
        // initialze sdk
        initWechatApiSDK(request, response);
        
        // get access_token
        AccessToken token = getToken();
        
        // get access_token failure.
        if (token == null) {
            Metadata metadata = new Metadata();
            metadata.setError(ServerError.ERROR_GET_TOKEN_FAIL_CD);
            metadata.setMessage(ServerError.ERROR_GET_TOKEN_FAIL_MSG);
            returnEntity.setMetadata(metadata);
            return returnEntity;
        }
        
        //=========================
        //生成预支付单
        //=========================
        //设置package订单参数
        SortedMap<String, String> packageParams = new TreeMap<String, String>();
        packageParams.put("partner", this.parentId); //商户号
        packageParams.put("out_trade_no", id); //商家订单号
        packageParams.put("input_charset", "UTF-8"); //字符编码
        
        //获取package包
        String packageValue = StringUtils.EMPTY;
        try {
            packageValue = this.requestHandler.genPackage(packageParams);
            log.debug("generate package result:[" + packageValue+"]");
        } catch (UnsupportedEncodingException e) {
            log.error("generate package error", e);
            Metadata metadata = new Metadata();
            metadata.setError(ServerError.ERROR_GENERATE_PREPAY_PACKAGE_CD);
            metadata.setMessage(ServerError.ERROR_GENERATE_PREPAY_PACKAGE_MSG);
            returnEntity.setMetadata(metadata);
            return returnEntity;
        }
        
        String timestamp = Sha1Util.getTimeStamp();

        //设置支付参数
        SortedMap<String, String> signParams = new TreeMap<String, String>();
        signParams.put("appid", this.appId);
        signParams.put("appkey", this.appKey);
        signParams.put("package", packageValue);
        signParams.put("timestamp", timestamp);
        
        //生成支付签名，要采用URLENCODER的原始值进行SHA1算法！
        String sign = StringUtils.EMPTY;
        try {
            sign = Sha1Util.createSHA1Sign(signParams);
            log.debug("sign app_signature result:[" + sign + "]");
        } catch (Exception e) {
            log.error("generate package error", e);
            Metadata metadata = new Metadata();
            metadata.setError(ServerError.ERROR_SIGN_PREPAY_PACKAGE_CD);
            metadata.setMessage(ServerError.ERROR_SIGN_PREPAY_PACKAGE_MSG);
            returnEntity.setMetadata(metadata);
            return returnEntity;
        }
        //增加非参与签名的额外参数
        signParams.put("app_signature", sign);
        signParams.put("sign_method", "sha1");
        
        Map<String, Object> data = this.requestHandler.sendOrderQuery(signParams);
        
        if (data == null) {
            Metadata metadata = new Metadata();
            metadata.setError(ServerError.ERROR_QUERY_ORDER_FAIL_CD);
            metadata.setMessage(ServerError.ERROR_QUERY_ORDER_FAIL_MSG);
            returnEntity.setMetadata(metadata);
            return returnEntity;
        }
        
        returnEntity.setData(data);
        
        return returnEntity;
    }
    
    
    /**
     * initialize WeChatApiSdk
     *
     * @param request Http Servlet Request(Spring framework autowrite)
     * @param response Http Servlet Response(Spring framework autowrite)
     */
    private void initWechatApiSDK(HttpServletRequest request, HttpServletResponse response) {
        this.requestHandler = new RequestHandler(request, response);
        this.requestHandler.init();
        this.requestHandler.init(this.appId, this.appSecret, this.appKey, this.parentId, this.parentKey);
    }
    
    /**
     * get access token object
     * @return access token object
     */
    private AccessToken getToken() {
        AccessToken token = (AccessToken) this.requestHandler.getRequest().getServletContext().getAttribute(TOKEN_KEY);
        
        if ((token != null) && !token.isExprie() && StringUtils.isNotEmpty(token.getAccessToken())) {
            log.debug("use cached access_token");
            return token;
        }
        log.info(">>>>>WeChat Server[Get access_token]");
        token = this.requestHandler.GetToken();
        this.requestHandler.getRequest().getServletContext().setAttribute(TOKEN_KEY, token);
        
        return token;
    }
}
