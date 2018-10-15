package com.demo.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ConfigUtil {
    
    /**
     * 公钥
     */
    public static String publicKey ;
    
    static{
        Properties prop = new Properties();   
        InputStream in = ConfigUtil.class.getResourceAsStream("/config.properties");
        try {   
            prop.load(in);
            publicKey = prop.getProperty("publicKey").trim();
        } catch (IOException e) {   
            e.printStackTrace();   
        } 
    }
}
