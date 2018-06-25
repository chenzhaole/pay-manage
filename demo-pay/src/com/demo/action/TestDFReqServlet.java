package com.demo.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.demo.config.ConfigUtil;
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


public class TestDFReqServlet extends HttpServlet {

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
			String PRIVATE_KEY = request.getParameter("publicKey");

			JSONObject data = new JSONObject();

			JSONObject head = getHeadJson(request);
			data.put("head", head);

			//获取body
			Map<String, String> bodyMap = getBodyMap(request);

			//加密body中的detail
			String publicKey = ConfigUtil.publicKey;
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
			map.put("batchOrderNo", req.getParameter("batchOrderNo"));
			map.put("totalNum", req.getParameter("totalNum"));
			map.put("totalAmount", req.getParameter("totalAmount"));
			map.put("notifyUrl", req.getParameter("notifyUrl"));
			map.put("detail", req.getParameter("detail"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
