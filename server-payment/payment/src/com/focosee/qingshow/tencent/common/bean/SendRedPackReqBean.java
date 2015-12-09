package com.focosee.qingshow.tencent.common.bean;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.focosee.qingshow.tencent.common.Configure;
import com.focosee.qingshow.tencent.common.RandomStringGenerator;
import com.focosee.qingshow.tencent.common.Signature;

public class SendRedPackReqBean {
    private String nonce_str;
    private String sign;
    private String mch_billno;
    private String mch_id;
    private String wxappid;
    private String send_name;
    private String re_openid;
    private String total_amount;
    private String total_num;
    private String wishing;
    private String client_ip;
    private String act_name;
    private String remark;
    
    public SendRedPackReqBean() {
        //微信分配的公众号ID（开通公众号之后可以获取到）
        setWxappid(Configure.getAppid());

        //微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
        setMch_id(Configure.getMchid());
        
        //随机字符串，不长于32 位
        setNonce_str(RandomStringGenerator.getRandomStringByLength(32));
    }
    
    public void sign() {
        //根据API给的签名规则进行签名
        String sign = Signature.getSign(toMap());
        //把签名数据设置到Sign这个属性中
        setSign(sign);

    }
    
    public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<String, Object>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object obj;
            try {
                obj = field.get(this);
                if(obj!=null){
                    map.put(field.getName(), obj);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
    
    public String getNonce_str() {
        return nonce_str;
    }
    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }
    public String getSign() {
        return sign;
    }
    public void setSign(String sign) {
        this.sign = sign;
    }
    public String getMch_billno() {
        return mch_billno;
    }
    public void setMch_billno(String mch_billno) {
        this.mch_billno = mch_billno;
    }
    public String getMch_id() {
        return mch_id;
    }
    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }
    public String getWxappid() {
        return wxappid;
    }
    public void setWxappid(String wxappid) {
        this.wxappid = wxappid;
    }
    public String getSend_name() {
        return send_name;
    }
    public void setSend_name(String send_name) {
        this.send_name = send_name;
    }
    public String getRe_openid() {
        return re_openid;
    }
    public void setRe_openid(String re_openid) {
        this.re_openid = re_openid;
    }
    public String getTotal_amount() {
        return total_amount;
    }
    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }
    public String getTotal_num() {
        return total_num;
    }
    public void setTotal_num(String total_num) {
        this.total_num = total_num;
    }
    public String getWishing() {
        return wishing;
    }
    public void setWishing(String wishing) {
        this.wishing = wishing;
    }
    public String getClient_ip() {
        return client_ip;
    }
    public void setClient_ip(String client_ip) {
        this.client_ip = client_ip;
    }
    public String getAct_name() {
        return act_name;
    }
    public void setAct_name(String act_name) {
        this.act_name = act_name;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    
}
