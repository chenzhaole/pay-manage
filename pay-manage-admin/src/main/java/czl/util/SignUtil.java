package czl.util;

import com.sys.common.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

public class SignUtil {

    private static final Logger logger = LoggerFactory.getLogger(SignUtil.class);

    public static String md5Sign(Object platDataMap, String signkey, String moid) throws Exception {
        HashMap<String,String> hashMap = (HashMap<String, String>) platDataMap;
        TreeMap<String,String> treeMap = new TreeMap<String, String>();
        for(String key : hashMap.keySet()){
            treeMap.put(key, hashMap.get(key));
        }
        //开始验签
        String platSignOrigStr = "";
        String platSignStr = "";
        Set<String> keys = treeMap.keySet();
        for (String key : keys){
            String value = (String) treeMap.get(key);
            if(StringUtils.isBlank(value)){
                continue;
            }
            platSignOrigStr = platSignOrigStr + key + "=" + value + "&";
        }
        platSignOrigStr = platSignOrigStr.substring(0, platSignOrigStr.length() -1);
        platSignOrigStr = platSignOrigStr + signkey;

        platSignStr = MD5Util.MD5Encode(platSignOrigStr);
        logger.info(moid+"-->平台签名-原始字符串: "+platSignOrigStr);
        System.out.println(moid + "-->平台签名-原始字符串: " + platSignOrigStr);
        logger.info(moid+"-->平台签名-签名后字符串: "+platSignStr);
        return platSignStr;
    }


    public static boolean checkSign(String platDataMap, String mchtSignStr, String signkey, String moid) throws Exception {
        //返回结果  SUCCESS("0000","成功"),    FAILURE("7777","未知错误"),


        String platSignOrigStr = platDataMap + signkey;
        String platSignStr = MD5Util.MD5Encode(platSignOrigStr);
        logger.info(moid+"-->验签-平台验签原始字符串: "+platSignOrigStr);
        logger.info(moid+"-->验签-平台MD5签名后字符串: "+platSignStr);
        logger.info(moid+"-->验签-商户MD5签名后字符串: "+mchtSignStr);
        if(platSignStr.equalsIgnoreCase(mchtSignStr)){
            logger.info(moid+"-->验签-结果正确！");
            return true;
        }
        logger.info(moid+"-->验签-结果失败！");
        return false;
    }

}
