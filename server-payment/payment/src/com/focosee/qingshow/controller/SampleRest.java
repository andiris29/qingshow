package com.focosee.qingshow.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.focosee.qingshow.bean.WeChatNotifyPostData;
import com.wxap.util.XMLUtil;


@RestController
@RequestMapping("/sample")
public class SampleRest {
    private final static Logger logger = Logger.getLogger(SampleRest.class);
    @Value("${setting['weixin.app_id']}")
    private String appid;
    
    @Resource(name="setting")
    private Properties myProperties;

 
    @Value("${jdbc.url}")
    private String jdbcUrl;
    
    
    @RequestMapping(value = "/hello", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> hello(@RequestParam(value="name", defaultValue="World") String name, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        returnMap.put("message", "Hello," + name);
        logger.info(appid);
        logger.info(jdbcUrl);
        logger.info(myProperties.get("weixin.app_id"));
        logger.info(request.getRemoteAddr());
        logger.info(response.getCharacterEncoding());
        
        return new ResponseEntity<>(returnMap, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public String postData(@RequestParam(value="name", defaultValue="World") String name, 
            @RequestBody String xmlData, HttpServletRequest request, HttpServletResponse response) {
        logger.info(name);
        Map m = request.getParameterMap();
        Iterator it = m.keySet().iterator();
        while (it.hasNext()) {
            String k = (String) it.next();
            String v = ((String[]) m.get(k))[0];
            logger.info("Request[" + k + "]=" + v);
        }
        logger.info(xmlData);
        return "ok";
    }
}
