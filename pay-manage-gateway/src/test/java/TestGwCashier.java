import com.sys.common.util.HttpUtil;
import com.sys.common.util.IdUtil;
import com.sys.common.util.MD5Util;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

public class TestGwCashier {
    public static void main(String[] args) {
        new Thread("test1"){
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    testCashier();
                }
            }
        }.start();

        new Thread("test2"){
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    testCashier();
                }
            }
        }.start();

        new Thread("test3"){
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    testCashier();
                }
            }
        }.start();

        new Thread("test4"){
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    testCashier();
                }
            }
        }.start();

        new Thread("test5"){
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    testCashier();
                }
            }
        }.start();

        new Thread("test6"){
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    testCashier();
                }
            }
        }.start();
        new Thread("test7"){
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    testCashier();
                }
            }
        }.start();
        new Thread("test8"){
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    testCashier();
                }
            }
        }.start();
        new Thread("test9"){
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    testCashier();
                }
            }
        }.start();
        new Thread("test10"){
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    testCashier();
                }
            }
        }.start();
    }

    private static void testCashier() {
        String url = "http://127.0.0.1:12086/gateway/cashier/mchtCall";
        Map<String, String> mapdata = new HashMap<>();
        mapdata.put("orderId", IdUtil.getUUID());
        mapdata.put("orderTime",new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        mapdata.put("amount","1000");
        mapdata.put("currencyType","CNY");
        mapdata.put("goods","游戏充值");
        mapdata.put("notifyUrl","http://www.baidu.com");
        mapdata.put("callBackUrl","");
        mapdata.put("desc","");
        mapdata.put("appId","");
        mapdata.put("appName","");
        mapdata.put("operator","");
        mapdata.put("expireTime","");
        mapdata.put("openId","");
        mapdata.put("ip","");
        mapdata.put("param","");
        String key = "605091ae24f8404086b56d74a20c9812";
        try {
            String sign = md5Sign(mapdata, key);
            mapdata.put("sign",sign);
            mapdata.put("mchtId","1848e6fe");
            mapdata.put("version","20");
            mapdata.put("biz","ca001");
//            String ret = HttpUtil.postConnManager(url, mapdata);
//            System.out.println(ret);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println();
    }


    public static String md5Sign(Object platDataMap, String signkey) throws Exception {
        HashMap<String, String> hashMap = (HashMap)platDataMap;
        TreeMap<String, String> treeMap = new TreeMap();
        Iterator var4 = hashMap.keySet().iterator();

        String platSignStr;
        while(var4.hasNext()) {
            platSignStr = (String)var4.next();
            treeMap.put(platSignStr, hashMap.get(platSignStr));
        }

        String platSignOrigStr = "";
        platSignStr = "";
        Set<String> keys = treeMap.keySet();
        Iterator var7 = keys.iterator();

        while(var7.hasNext()) {
            String key = (String)var7.next();
            String value = (String)treeMap.get(key);
            if (!StringUtils.isBlank(value)) {
                platSignOrigStr = platSignOrigStr + key + "=" + value + "&";
            }
        }

        platSignOrigStr = platSignOrigStr + "key=" + signkey;
        platSignStr = MD5Util.MD5Encode(platSignOrigStr);
        return platSignStr;
    }
}
