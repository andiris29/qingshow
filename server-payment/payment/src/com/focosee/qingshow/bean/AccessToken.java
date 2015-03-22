package com.focosee.qingshow.bean;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

public class AccessToken {
    private String accessToken;
    private Date createDateTime;
    private int expires;
    
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public int getExpires() {
        return expires;
    }
    public void setExpires(int expires_in) {
        this.expires = expires_in;
        this.createDateTime = new Date();
    }
    
    public boolean isExprie() {
        Date now = new Date();
        Date expireDate = DateUtils.addSeconds(createDateTime, expires - 2 * 60);
        if (now.compareTo(expireDate) >= 0) {
            return false;
        }
        return true;
    }
    
    
}
