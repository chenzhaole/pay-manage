package com.demo.util;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

public class SignUtil {


    /**校验MD5签名**/
    public static boolean checkSign(Object platDataMap, String mchtSignStr, String signkey) throws Exception {
        TreeMap dataMap = (TreeMap) platDataMap;
        //开始验签
        String platSignOrigStr = "";
        String platSignStr = "";
        Set<String> keys = dataMap.keySet();
        for (String key : keys){
        	String value = (String) dataMap.get(key);
        	if(value == null || "".equals(value)){
        		continue;
        	}
            platSignOrigStr = platSignOrigStr + key + "=" + value + "&";
        }
        platSignOrigStr = platSignOrigStr + "key=" + signkey;
        platSignStr = MD5Util.MD5Encode(platSignOrigStr);
        if(platSignStr.equalsIgnoreCase(mchtSignStr)){
            //验签-结果正确
			System.out.println("验签-结果正确");
            return true;
        }
        return false;
    }

    /**MD5签名**/
    public static String md5Sign(Object platDataMap, String signkey) throws Exception {
    	HashMap<String,String> hashMap = (HashMap<String, String>) platDataMap;
        TreeMap<String,String> treeMap = new TreeMap<String, String>();
        for(String key : hashMap.keySet()){
        	treeMap.put(key, hashMap.get(key));
        }
        //开始签名
        String platSignOrigStr = "";
        String platSignStr = "";
        Set<String> keys = treeMap.keySet();
        for (String key : keys){
        	String value = (String) treeMap.get(key);
        	if(value == null || "".equals(value)){
        		continue;
        	}
            platSignOrigStr = platSignOrigStr + key + "=" + value + "&";
        }
        platSignOrigStr = platSignOrigStr + "key=" + signkey;
        System.out.println("签名的字符串为"+platSignOrigStr);
        platSignStr = MD5Util.MD5Encode(platSignOrigStr).toUpperCase();
        return platSignStr;
    }

}
