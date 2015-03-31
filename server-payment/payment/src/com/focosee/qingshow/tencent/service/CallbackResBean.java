package com.focosee.qingshow.tencent.service;

import org.apache.commons.lang.StringUtils;

public class CallbackResBean {
    //协议层
    private String return_code = "";
    private String return_msg = "";
    public String getReturn_code() {
        return return_code;
    }
    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }
    public String getReturn_msg() {
        return return_msg;
    }
    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }    
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        if (StringUtils.isNotEmpty(this.return_code)) {
            sb.append("<return_code><![CDATA[");
            sb.append(this.return_code);
            sb.append("]]></return_code>");
        }
        if (StringUtils.isNotEmpty(this.return_msg)) {
            sb.append("<return_msg><![CDATA[");
            sb.append(this.return_msg);
            sb.append("]]></return_msg>");
        }
        sb.append("</xml>");        
        return sb.toString();
    }
}
