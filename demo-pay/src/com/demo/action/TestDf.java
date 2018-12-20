package com.demo.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.demo.util.AESUtil;
import com.demo.util.RSAUtils;
import com.demo.util.SignUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.TreeMap;

public class TestDf {
    public static void main(String[] args) throws Exception {

        String respStr = "{\n" +
                "body: \"Be%2B3KzGaoEHuMNal6jQD%2FJEW5ABy2dRYe2%2FXXvz94YDc3G2aLBP3mNTKGS1rQ3nTwoCInIWURsGj22tR35mIaPxz4iMu13orOlvW7DFmMkZNW42XTtXUEreji4PJ7dGT4LTSsLEmwCicSSlru0yg7rJ5KZ4zNuSa8hCI3nTh20coyhN6NFhYR%2FzCeUUh6iGu\",\n" +
                "encryptKey: \"M25s3%2B8T7xJySES3Ld%2Fuj9mt1fAnOWBmJ51XEiXoV3r3DPr1UL83UIuW%2Bv2keGNNuD5plsevDG8BlsfXnNWLWXvC0a4YSXjtE72KAqDsRbYov55BskoyzmzEGwhcQCUnQtmzjjzHne4iF%2BUDnBgFS9keLWeC3hxKcT8uabRfC0Q%3D\",\n" +
                "head: {\n" +
                "respCode: \"0000\"\n" +
                "},\n" +
                "sign: \"99fe6a97eacfde533a59f401f71f24ab\"\n" +
                "}";

        String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANQzIQVhn76Ohmo1VEoZKHdRQsEuZ1+MHrsoDBxzI6KT4vIdQ0dAnZcRHb8Tfo1YX9mOYrW3hVB2WQfjG5o21WSCYxab8wnK+eWQSr6LXbMUKoBj0EUW/FLyR1K+r85qrvTmn/p5IfVD61icuDS84YygtBa/aLY+sgfkWcWiOkG/AgMBAAECgYA7fT4DqTPEk2ZhfiPsRhNPKAvj5qN6aOjwpTCIBUt3N6iHIecLobTMdjL1r3xqGd19O2q28QVB4nvhNy001ayK5wj4QT3iWEZWv+V04k787FINCF/SKMxZF5/lHcwB45XcbNuX3mSDqDrqq+cl2laN14OKKSX1AFccSgBbXgHdkQJBAPfNbB1Iq00XSIhFUHydwVXQ0nzVstTzI9xM/oq7BZO9Ol9CkTA3fP41+WQ07zozRTzytaz9kmDMMIEUIAQUL3cCQQDbODI0OxoeLjPGRCXJsns4k0LxYA4wDfqD4qjW2dPd32fryIWVfTBSxSVipnFsDBALfs88BbPEiY0UNFYfRGH5AkB6TMdUOJ5a4OfqYZNUvaNC0FpiRDILyahkPRfPrngVL2pUw4zWqDwnVsosO7fqGI3Og9dIdqm83mn9+snGWZQHAkEAjTOMOtJax1fRJ4LbqCgbcFyCQQEzAkbutqZ3RDR4YDA3OsslKB3D03yC0SEOa571csISb8Hogc5rnbj8RBWC2QJAMejrsABt9VG/K4OeDguy6bPJ+nx+1s7xEEFYzusN0JsEU3uYOZU7p3KyTMnYOQ+JbBROsptgV3oIYjDfQqxRqg==";

        respStr = URLDecoder.decode(respStr, "UTF-8");
        JSONObject resJson = JSON.parseObject(respStr);
        JSONObject resHead = resJson.getJSONObject("head");

        //AES解密body
        String aesKeyResp = RSAUtils.decrypt(resJson.getString("encryptKey"), PRIVATE_KEY);
        String bodyResp = AESUtil.Decrypt(JSON.toJSONString(resJson.getString("body")), aesKeyResp);
        System.out.println(bodyResp);

        /*SignUtil.checkSign(JSON.parseObject(bodyResp, new TypeReference<TreeMap<String, String>>() {
        }), resJson.getString("sign"), key);*/

        resJson.put("body", bodyResp);
        respStr = resJson.toString();
        System.out.println(respStr);
    }
}
