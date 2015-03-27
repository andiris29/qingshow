package com.focosee.qingshow.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * WeChat 通知接口参数
 * 详见：4.3 通知接口参数
 * 
 * @author qusheng
 *
 */
@XmlRootElement
public class WeChatNotifyPostData {

    private String openId;
    private String appId;
    private String isSubscribe;
    private String timeStamp;
    private String nonceStr;
    private String appSignature;
    private String signMethod;
    
    public String getOpenId() {
        return openId;
    }

    @XmlElement(name = "OpenId")
    public void setOpenId(String openId) {
        this.openId = openId;
    }
    public String getAppId() {
        return appId;
    }
    @XmlElement(name = "AppId")
    public void setAppId(String appId) {
        this.appId = appId;
    }
    @XmlElement(name = "IsSubscribe")
    public String getIsSubscribe() {
        return isSubscribe;
    }
    public void setIsSubscribe(String isSubscribe) {
        this.isSubscribe = isSubscribe;
    }
    public String getTimeStamp() {
        return timeStamp;
    }

    @XmlElement(name = "TimeStamp")
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
    public String getNonceStr() {
        return nonceStr;
    }

    @XmlElement(name = "NonceStr")
    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }
    public String getAppSignature() {
        return appSignature;
    }
    @XmlElement(name = "AppSignature")
    public void setAppSignature(String appSignature) {
        this.appSignature = appSignature;
    }
    public String getSignMethod() {
        return signMethod;
    }
    @XmlElement(name = "SignMethod")
    public void setSignMethod(String signMethod) {
        this.signMethod = signMethod;
    }
}
