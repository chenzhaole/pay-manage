import com.sys.common.util.MD5Util;

import java.util.HashMap;
import java.util.Map;
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
        System.out.println("签名后字符串:"+ platSignStr);
        return platSignStr;
    }


    public static void main (String [] args) throws Exception {
        Map<String,String> map = new HashMap();
        map.put("orderId", "6666201808211810005746");
        //yyyyMMddHHmmss
        map.put("orderTime", "20180821180820");
        map.put("amount", "1");
        map.put("currencyType", "CNY");
        map.put("goods", "H5");
        map.put("notifyUrl", "http://kvpaytest.iask.in:8080/gateway/scanPayNotify/notifyLT/b3cc7816a4f6494a99b1f5b3b48b94f7");
        map.put("callBackUrl", "");
        map.put("desc", "");
        map.put("appId", "");
        map.put("appName", "");
        map.put("operator", "");
        map.put("expireTime", "");
        map.put("openId", "");
        map.put("payScene", "");
        map.put("deviceType", "");
        map.put("param", "");
        map.put("ip", "");

        String key = "1bd91dc5af4e400aabd253d938296bcd";

        System.out.println(md5Sign(map, key));
    }

}
