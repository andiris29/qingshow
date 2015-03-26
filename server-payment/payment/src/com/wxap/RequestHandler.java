package com.wxap;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.focosee.qingshow.bean.AccessToken;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wxap.client.TenpayHttpClient;
import com.wxap.util.MD5Util;
import com.wxap.util.TenpayUtil;

/*
 '微信支付服务器签名支付请求请求类
 '============================================================================
 'api说明：
 'init(app_id, app_secret, partner_key, app_key);
 '初始化函数，默认给一些参数赋值，如cmdno,date等。
 'setKey(key_)'设置商户密钥
 'getLasterrCode(),获取最后错误号
 'GetToken();获取Token
 'getTokenReal();Token过期后实时获取Token
 'createMd5Sign(signParams);生成Md5签名
 'genPackage(packageParams);获取package包
 'createSHA1Sign(signParams);创建签名SHA1
 'sendPrepay(packageParams);提交预支付
 'getDebugInfo(),获取debug信息
 '============================================================================
 '*/
public class RequestHandler {
	/** Token获取网关地址地址 */
	private String tokenUrl;
	/** 预支付网关url地址 */
	private String gateUrl;
	/** 查询支付通知网关URL */
	private String notifyUrl;
	/** 订单查询URL */
	private String orderQueryUrl;
	/** 发货通知网关URL */
	private String deliverNotifyUrl;
	/** 商户参数 */
	private String appid;
	private String appkey;
	private String partnerkey;
	private String appsecret;
	private String key;
	/** 请求的参数 */
	private SortedMap parameters;
	/** Token */
	private String Token;
	private String charset;
	/** debug信息 */
	private String debugInfo;
	private String last_errcode;

	private HttpServletRequest request;

	private HttpServletResponse response;
	
	private static final Logger log = Logger.getLogger(RequestHandler.class);

	/**
	 * 初始构造函数。
	 * 
	 * @return
	 */
	public RequestHandler(HttpServletRequest request,
			HttpServletResponse response) {
		this.last_errcode = "0";
		this.request = request;
		this.response = response;
		this.charset = "GBK";
		this.parameters = new TreeMap();
		// 获取Token网关
		tokenUrl = "https://api.weixin.qq.com/cgi-bin/token";
		// 提交预支付单网关
		gateUrl = "https://api.weixin.qq.com/pay/genprepay";
		// 验证notify支付订单网关
		notifyUrl = "https://gw.tenpay.com/gateway/simpleverifynotifyid.xml";
		// 订单查询网关
		this.orderQueryUrl = "https://api.weixin.qq.com/pay/orderquery";
	}

	/**
	 * 初始化函数。
	 */
	public void init(String app_id, String app_secret, String app_key,
			String partner, String key) {
		this.last_errcode = "0";
		this.Token = "token_";
		this.debugInfo = "";
		this.appkey = app_key;
		this.appid = app_id;
		this.partnerkey = partner;
		this.appsecret = app_secret;
		this.key = key;
	}

	public void init() {
	}

	/**
	 * 获取最后错误号
	 */
	public String getLasterrCode() {
		return last_errcode;
	}

	/**
	 *获取入口地址,不包含参数值
	 */
	public String getGateUrl() {
		return gateUrl;
	}

	/**
	 * 获取参数值
	 * 
	 * @param parameter
	 *            参数名称
	 * @return String
	 */
	public String getParameter(String parameter) {
		String s = (String) this.parameters.get(parameter);
		return (null == s) ? "" : s;
	}

	/**
	 * 设置密钥
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * 获取TOKEN，一天最多获取200次，需要所有用户共享值
	 */
	public AccessToken GetToken() {
		String requestUrl = tokenUrl + "?grant_type=client_credential&appid="
				+ appid + "&secret=" + appsecret;
		TenpayHttpClient httpClient = new TenpayHttpClient();
		httpClient.setReqContent(requestUrl);
		if (httpClient.call()) {
			String res = httpClient.getResContent();
			Gson gson = new Gson();
			TreeMap map = gson.fromJson(res, TreeMap.class);
			// 在有效期内直接返回access_token
			if (map.containsKey("access_token")) {
				String s = map.get("access_token").toString();
			}
			// 如果请求成功
			if (null != map) {
				try {
					if (map.containsKey("access_token")) {
						Token = map.get("access_token").toString();
						AccessToken token = new AccessToken();
						token.setAccessToken(Token);
						token.setExpires(NumberUtils.toInt(map.get("expires_in").toString(), 0));
						return token;
					}
				} catch (Exception e) {
					// 获取token失败
					log.debug("失败:" + map.get("errmsg"));
				}
			}

		}
		return null;

	}

	/**
	 * 实时获取token，并更新到application中
	 */
	public String getTokenReal() {
		String requestUrl = tokenUrl + "?grant_type=client_credential&appid="
				+ appid + "&secret=" + appsecret;
		try {
			// 发送请求，返回json
			TenpayHttpClient httpClient = new TenpayHttpClient();
			httpClient.setReqContent(requestUrl);
			String resContent = "";
			if (httpClient.callHttpPost(requestUrl, "")) {
				resContent = httpClient.getResContent();
				Gson gson = new Gson();
				Map<String, String> map = gson.fromJson(resContent,
						new TypeToken<Map<String, String>>() {
						}.getType());
				// 判断返回是否含有access_token
				if (map.containsKey("access_token")) {
					// 更新application值
					Token = map.get("access_token");
				} else {
					log.debug("get token err ,info ="
							+ map.get("errmsg"));
				}
				log.debug("res json=" + resContent);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return Token;
	}

	// 特殊字符处理
	public String UrlEncode(String src) throws UnsupportedEncodingException {
		return URLEncoder.encode(src, this.charset).replace("+", "%20");
	}

	// 获取package带参数的签名包
	public String genPackage(SortedMap<String, String> packageParams)
			throws UnsupportedEncodingException {
		String sign = createSign(packageParams);

		StringBuffer sb = new StringBuffer();
		Set es = packageParams.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			sb.append(k + "=" + UrlEncode(v) + "&");
		}

		// 去掉最后一个&
		String packageValue = sb.append("sign=" + sign).toString();
		log.debug("packageValue=" + packageValue);
		return packageValue;
	}

	/**
	 * 创建md5摘要,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
	 */
	public String createSign(SortedMap<String, String> packageParams) {
		StringBuffer sb = new StringBuffer();
		Set es = packageParams.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (null != v && !"".equals(v) && !"sign".equals(k)
					&& !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=" + this.getKey());
		log.debug("md5 sb:" + sb);
		String sign = MD5Util.MD5Encode(sb.toString(), this.charset)
				.toUpperCase();

		return sign;

	}

	// 提交预支付
	public String sendPrepay(SortedMap packageParams) {
		String prepayid = "";
		// 转换成json
		Gson gson = new Gson();
		/* String postData =gson.toJson(packageParams); */
		String postData = "{";
		Set es = packageParams.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (k != "appkey") {
				if (postData.length() > 1)
					postData += ",";
				postData += "\"" + k + "\":\"" + v + "\"";
			}
		}
		postData += "}";
		// 设置链接参数
		String requestUrl = this.gateUrl + "?access_token=" + this.Token;
		log.debug("post url=" + requestUrl);
		log.debug("post data=" + postData);
		TenpayHttpClient httpClient = new TenpayHttpClient();
		httpClient.setReqContent(requestUrl);
		String resContent = "";
		if (httpClient.callHttpPost(requestUrl, postData)) {
			resContent = httpClient.getResContent();
			Map<String, String> map = gson.fromJson(resContent,
					new TypeToken<Map<String, String>>() {
					}.getType());
			if ("0".equals(map.get("errcode"))) {
				prepayid = map.get("prepayid");
			} else {
				log.debug("get token err ,info =" + map.get("errmsg"));
			}
			// 设置debug info
			log.debug("res json=" + resContent);
		}
		return prepayid;
	}
	
	public Map<String, String> sendDeliverNotify(SortedMap<String, String> params) {
	    Gson gson = new Gson();
	    String requestUrl = this.deliverNotifyUrl + "?access_token=" + this.Token;
	    TenpayHttpClient httpClient = new TenpayHttpClient();
	    httpClient.setReqContent(requestUrl);
	    String resContent = "";
	    String postJson = gson.toJson(params, new TypeToken<SortedMap<String, String>>(){}.getType());
	    Map<String, String> queryResult = null;
	    if (httpClient.callHttpPost(requestUrl, postJson)) {
	        resContent = httpClient.getResContent();
	        log.debug("/delivernotify[response json]=" + resContent);
	        queryResult = gson.fromJson(resContent, new TypeToken<Map<String, String>>() {}.getType());
	    }
	    return queryResult;
	}
	
	
	public Map<String, Object> sendOrderQuery(SortedMap packageParams) {
	 // 转换成json
        Gson gson = new Gson();
        /* String postData =gson.toJson(packageParams); */
        String postData = "{";
        Set es = packageParams.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (k != "appkey") {
                if (postData.length() > 1)
                    postData += ",";
                postData += "\"" + k + "\":\"" + v + "\"";
            }
        }
        postData += "}";
        String requestUrl = this.orderQueryUrl + "?access_token=" + this.Token;
        log.debug("post url=" + requestUrl);
        log.debug("post data=" + postData);
        TenpayHttpClient httpClient = new TenpayHttpClient();
        httpClient.setReqContent(requestUrl);
        String resContent = "";
        Map<String, Object> queryResult = null;
        if (httpClient.callHttpPost(requestUrl, postData)) {
            resContent = httpClient.getResContent();
            Map<String, Object> map = gson.fromJson(resContent,
                    new TypeToken<Map<String, Object>>() {
                    }.getType());
            if ("0".equals(map.get("errcode"))) {
                queryResult = (Map<String, Object>) map.get("order_info");
            } else {
                log.debug("get token err ,info =" + map.get("errmsg"));
            }
            // 设置debug info
            log.debug("res json=" + resContent);
        }
        return queryResult;
	}

	/**
	 * 创建package签名
	 */
	public boolean createMd5Sign(String signParams) {
		StringBuffer sb = new StringBuffer();
		Set es = this.parameters.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (!"sign".equals(k) && null != v && !"".equals(v)) {
				sb.append(k + "=" + v + "&");
			}
		}

		// 算出摘要
		String enc = TenpayUtil.getCharacterEncoding(this.request,
				this.response);
		String sign = MD5Util.MD5Encode(sb.toString(), enc).toLowerCase();

		String tenpaySign = this.getParameter("sign").toLowerCase();

		// debug信息
		this.setDebugInfo(sb.toString() + " => sign:" + sign + " tenpaySign:"
				+ tenpaySign);

		return tenpaySign.equals(sign);
	}

	/**
	 * 设置debug信息
	 */
	protected void setDebugInfo(String debugInfo) {
		this.debugInfo = debugInfo;
	}
	public void setPartnerkey(String partnerkey) {
		this.partnerkey = partnerkey;
	}
	public String getDebugInfo() {
		return debugInfo;
	}
	public String getKey() {
		return key;
	}

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

}
