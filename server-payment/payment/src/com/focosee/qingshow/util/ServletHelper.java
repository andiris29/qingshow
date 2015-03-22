package com.focosee.qingshow.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class ServletHelper {
    private static final Logger log = Logger.getLogger(ServletHelper.class);
    
    /**
     * get remote client ip address from http servlet request
     * @param request http servlet request
     * @return remote client's ip address
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = "";
        try {
            ip = request.getHeader("x-forwarded-for");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }

        } catch (Exception ex) {
            log.error(ex);
        }

        log.debug(ip);
        return ip;
    }
}
