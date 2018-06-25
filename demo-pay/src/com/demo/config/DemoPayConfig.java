package com.demo.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class DemoPayConfig {
    
    /**
     * 交易密钥
     */
    public static String key ;
    
    /**
     * 商户号
     */
    public static String mch_id;
    
    /**
     * 请求url
     */
    public static String req_url;
    
    /**
     * 通知url
     */
    public static String notify_url;
    
    static{
        Properties prop = new Properties();   
        InputStream in = DemoPayConfig.class.getResourceAsStream("/config.properties");
        try {   
            prop.load(in);   
            key = prop.getProperty("key").trim();   
            mch_id = prop.getProperty("mch_id").trim();   
            req_url = prop.getProperty("req_url").trim();   
            notify_url = prop.getProperty("notify_url").trim();   
        } catch (IOException e) {   
            e.printStackTrace();   
        } 
    }
}
