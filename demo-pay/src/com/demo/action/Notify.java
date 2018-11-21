package com.demo.action;

import com.demo.util.MD5Util;
import com.demo.util.SignUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Notify {
    public static void main(String[] args) throws Exception {
        Map<String, String> treeMap = new HashMap<>();
        treeMap.put("amount", "10000");
        treeMap.put("biz", "al000");
        treeMap.put("chargeTime", "20181015204214");
        treeMap.put("mchtId", "2000611000926889");
        treeMap.put("orderId", "CK1539607261080493");
        treeMap.put("payType", "al");
        treeMap.put("seq", "dbe02576beaf4ec4a128f5f6c5894a3b");
        treeMap.put("status", "SUCCESS");
        treeMap.put("tradeId", "P1810152041218945350");

        System.out.println(md5Sign(treeMap, "37dea58fac314300874ff98bb48f4440"));
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
        System.out.println("签名后字符串:"+ platSignStr);
        return platSignStr;
    }
}
