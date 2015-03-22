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
@XmlRootElement(name = "Xml")
public class WeChatNotifyPostData {

    @XmlElement(name = "OpenId")
    private String openId;
    @XmlElement(name = "AppId")
    private String appId;
    @XmlElement(name = "IsSubscribe")
    private String isSubscribe;
    @XmlElement(name = "TimeStamp")
    private String timeStamp;
    @XmlElement(name = "NonceStr")
    private String nonceStr;
    @XmlElement(name = "AppSignature")
    private String appSignature;
    @XmlElement(name = "SignMethod")
    private String signMethod;
    
    public String getOpenId() {
        return openId;
    }
    public void setOpenId(String openId) {
        this.openId = openId;
    }
    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }
    public String getIsSubscribe() {
        return isSubscribe;
    }
    public void setIsSubscribe(String isSubscribe) {
        this.isSubscribe = isSubscribe;
    }
    public String getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
    public String getNonceStr() {
        return nonceStr;
    }
    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }
    public String getAppSignature() {
        return appSignature;
    }
    public void setAppSignature(String appSignature) {
        this.appSignature = appSignature;
    }
    public String getSignMethod() {
        return signMethod;
    }
    public void setSignMethod(String signMethod) {
        this.signMethod = signMethod;
    }
}
