package com.demo.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.demo.config.ConfigUtil;
import com.demo.util.AESUtil;
import com.demo.util.PostUtil;
import com.demo.util.RSAUtils;
import com.demo.util.SignUtil;
import org.apache.commons.lang.RandomStringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;


public class TestDFReqServlet  {



	private static JSONObject getHeadJson(String  mchtId, String version , String biz) {
		JSONObject head = new JSONObject();
		head.put("mchtId", mchtId);
		head.put("version", version);
		head.put("biz", biz);
		return head;
	}

	private static Map<String, String> getBodyMap(String batchOrderNo, String totalNum, String totalAmount, String notifyUrl, String detail) {
		Map<String, String> map = new HashMap();
		try {
			map.put("batchOrderNo", batchOrderNo);
			map.put("totalNum", totalNum);
			map.put("totalAmount", totalAmount);
			map.put("notifyUrl", notifyUrl);
			map.put("detail", detail);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}


	public static void main(String[] args){

		try{
			String payUrl = "http://api.ihengyuan.cn:17082/df/gateway/req/"; //
			String key = "78f648601656492294fad13c58561b0f";			//商户KEY
			String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJ08or33dW8NaeNQGsYlTv69rL/+GM7HVJaly47a7+iyZJT5U1NKZGpQzXJWNUpTxOYmKXceoprLZ3YzdiepLTG3U9a2KqrKBrOKxDzP08k4oKBu8n61F+5F9F2A27F0Ir9scfCJXdYfbmxnmiklBYQgXinJC9qBOCuIJD7C4uxNAgMBAAECgYBHrSN2u36B8Dhr0dFSsCExiN9d6gP/Buitf8iqR22DwwaMzpdIaaoauNjenSPPzR392DmgvotbSwvP2yeqbrlVsIcUK3Sm4mJBYXJnFErZWD6WHiqn/dA2f1xuUmFfDn/56joWKnnMq8UOypIXl9bZTvNOZKndArZu2EvyNUGfAQJBAM2DfaYPtkAA8bW2xlgjLeo/7w4OXfoAN5jh9APgbx6/0JJjue2TuQ4LZOZKea1JKrDS0t6FtnABnX3h6jfh4Y0CQQDD3RNVAq0drKQkoGQ8OKtAmWq0A0IAGOtNWzwFw1nZHAMZQr8o8GGxRS/nGpcbRaQmoAWwwgYsan7d2yv+GKXBAkAM2PiE5hyNmcGCi5+QqPpY15BZP5quY08WdqGrkF9B+9nNHQDlkkOSVjIDl4pNw/IwRa27DX7fN6qh0Pq0baGNAkEApeo0YiLuOuDv+wbTYiAvEX3kYbEQy7xQNys94TPmfH+6MD/WZvuaBeyx83cW4cdQklej+mOhjyZ3acAruAdxAQJBAJNsWEOtxM7MoYD5HhpTlFiOPEYqd4pd4kaTzcRItyVanEJc3sXzwu0KqBbYngPTx48YuI0Tr43cCsbRfVy+wVI=";	//平台下发的私钥  就是页面点击生成的私钥

			JSONObject data = new JSONObject();

			String  mchtId = "2000928000996290";	//商户号
			String version ="20";
			String biz ="df101";

			JSONObject head = getHeadJson(mchtId, version, biz);
			data.put("head", head);



			String batchOrderNo =RandomStringUtils.randomNumeric(7);
			String totalNum= "1";		//总数
			String totalAmount= "500";	//总金额
			String notifyUrl= "http://baidu.com";	//回调地址
			//String detail= "[{\"accType\":\"0\",\"amount\":\"500\",\"bankCode\":\"\",\"bankCardName\":\"--收款人名称----\",\"bankCardNo\":\"---收款人卡号--\",\"bankName\":\"---银行名称--\",\"seq\":\"001\"}]";
			String detail = "[{\"accType\":\"0\",\"amount\":\"500\",\"bankCode\":\"SPDB\",\"bankCardName\":\"王仁东\",\"bankCardNo\":\"6217933135310041\",\"bankName\":\"上海浦东发展银行\",\"seq\":\"001\"}]";

			//获取body
			Map<String, String> bodyMap = getBodyMap(batchOrderNo, totalNum, totalAmount, notifyUrl, detail);
			System.out.println(JSONArray.toJSONString(bodyMap));

			//加密body中的detail   是我们下发的统一公钥
			String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCm++Z9qgw6HxIOKWbAx1hbVU7PokmBBFlomwFhBdU2LInAQWfHEJTGM+2EX9D559J3XXxcPSVanVdM4LcTiJyVJoFSulIg01wR26yk7pzGqy+QJRv1uffL1+otRbgmDLhjeV16148CmwGG3j7xkVOqrv/fsiOViYd1EZdsit+vlQIDAQAB";
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

			System.out.println("请求参数: \r\n" + data.toString());
			String respStr = PostUtil.post(payUrl, data.toJSONString());
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
				System.out.println(respStr);

			}
		}catch (Exception e){

		}

	}
}
