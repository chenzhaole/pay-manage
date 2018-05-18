package com.demo.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.demo.util.AESUtil;
import com.demo.util.PostUtil;
import com.demo.util.RSAUtils;
import com.demo.util.SignUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;


public class TestDFBalanceServlet extends HttpServlet {

    private static final String PRIVATE_KEY =  "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJtQHqwOEDlfE9gryafuEloaYAWC+Wbb7p23XlKyw/IH+mtD+924qqad6wM8OfQkVjINhpBS25y5qQH3MuAmRWdfa958SH8yWSJyx6ugPc7q0az9ZzaKyEVJmiXckLvqJOpX28FscJuz01d0v+bsAP2FnBo+8mtIHwqUnP8ZnjpbAgMBAAECgYBtbYre0N3PflS+B0QCpLObdl/XIkvUk/rQdqTngXqbadGfh/vKYVUjJbqywdlXUc7FX1BDGY4QI6OXdfMLiQt2S9ihZLKN65pMFsjnhSrt6ozEmCqXVHKFG9fwRLRPg0gY9ofTRrvWDwhlgxV0qUnbqcca1/uRYOt89/xUqxKJOQJBAM5LFpO3b3tJwaq+wyl5+dr0aSU8L7/oBF9fG+lgP74eg0FCd5WoSdIUQ75ixoLBry8Yd9EGb+d2IlJFXsYUSM0CQQDAvGGIzDa6bJUBsC2diyu8ddJpdkHv324R4ccfgBj04TeFkM0rshmjLEEjZQzydiYVhY4LC6uPx7HE08FwbC/HAkBw0wpnAaUcFauw+aH9VjO7d37mGXO4DmoNyxOV4Mkb7s40a+jBVggBuImQX69YJhvsswIctNuRCMAepMf/p2plAkEAnQWc7Md7Wvx1lU+EilLFCiBvkW5AH/5G/ZiVEsvZCUCnbFDhZtUN4AuA8iY0myC4vFX3uHYEivolkXb3pPDvJwJBAKjXYfpjgJ7NqWsFaUx+GDIPaOuzCHJ8wUl62IZol1fBe0QMeb+dwVEBfSK/l/443aQO6WcbT2yp2FWkuBlcaFE=";
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        String respStr = "没有正确返回信息";
        try {
            String payUrl = request.getParameter("payUrl");
            String key = request.getParameter("key");

            JSONObject data = new JSONObject();
            JSONObject head = getHeadJson(request);

            //获取body
            Map<String, String> bodyMap = getBodyMap(request);

            //加密body中的detail
            String publicKey = request.getParameter("publicKey");
            String aesKey = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
            String body = JSON.toJSONString(bodyMap);
            System.out.println(body);

            //用AES加密detail
            String bodyAES = AESUtil.Encrypt(body, aesKey);
            //用RSA公钥加密AES的key
            String aesKeyRSA = RSAUtils.encrypt(aesKey, publicKey);

            data.put("encryptKey", URLEncoder.encode(aesKeyRSA, "UTF-8"));
            data.put("body", URLEncoder.encode(bodyAES, "UTF-8"));

            //签名
            String sign = SignUtil.md5Sign(bodyMap, key);
            data.put("sign", sign);

            data.put("head", head);
            respStr = PostUtil.post(payUrl, data.toJSONString());
            System.out.println(respStr);

            respStr = URLDecoder.decode(respStr, "UTF-8");
            JSONObject resJson = JSON.parseObject(respStr);
            JSONObject resHead = resJson.getJSONObject("head");

            //返回成功信息
            if (resHead.getString("respCode").equals("0000")) {

                //AES解密body
                String aesKeyResp = RSAUtils.decrypt(resJson.getString("encryptKey"), PRIVATE_KEY);
                String bodyResp = AESUtil.Decrypt(JSON.toJSONString(resJson.getString("body")), aesKeyResp);
                System.out.println(bodyResp);

                SignUtil.checkSign(JSON.parseObject(bodyResp, new TypeReference<TreeMap<String, String>>() {
                }), resJson.getString("sign"), key);

                resJson.put("body", bodyResp);
                respStr = resJson.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        response.getWriter().write(respStr);
    }


    private JSONObject getHeadJson(HttpServletRequest req) {
        JSONObject head = new JSONObject();
        head.put("mchtId", req.getParameter("mchtId"));
        head.put("version", req.getParameter("version"));
        head.put("biz", req.getParameter("biz"));
        return head;
    }

    private Map<String, String> getBodyMap(HttpServletRequest req) {
        Map<String, String> map = new HashMap();
        try {
            map.put("mchtId", req.getParameter("mchtId"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}