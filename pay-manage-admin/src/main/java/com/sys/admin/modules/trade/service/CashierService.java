package com.sys.admin.modules.trade.service;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.common.util.DesUtil32;
import com.sys.common.util.MD5Util;



@Service
public class CashierService  {
	
	/**
	 * 创建订单
	 * @throws Exception
	 */
	public String createOrder(String biz, String mchtNo, String goods, long amount, String desc) throws Exception{
		JSONObject head = new JSONObject();
		JSONObject body = new JSONObject();
		JSONObject sign = new JSONObject();
		JSONObject json = new JSONObject();
		
		head.put("merchant", mchtNo);//极限网络商户编号
		head.put("version", "10");
		head.put("biz", biz);
		body.put("orderId", "AliOrderId_1701230008");//订单编号不能重复
		body.put("amount", amount);//金额（单位：分）
		body.put("notifyUrl", "http://www.demo.com/notify/ouya/wx");//异步结果通知地址
		body.put("goods", goods);
		body.put("desc", desc);
		body.put("operator", "001");
		body.put("param", "");
		System.out.println(body.toJSONString());
		Map paramMap = JSON.parseObject(body.toString(), Map.class);
		String password = "TBUODONEIDOABVB2";
		String sortedStr = getSignSrc(paramMap, password);
		System.out.println("签名字符串="+sortedStr);
		json.put("head", head);
		json.put("body", DesUtil32.encode(body.toJSONString(), password));
		json.put("sign", MD5Util.MD5Encode(sortedStr));
		System.out.println(json.toJSONString());
		
		//扫码支付请求地址
		String httpUrl = "http://api.monis-pay.com:9092/gateway/api/wxQRCode";
		HttpClient httpClient = new HttpClient(httpUrl, 5000, 120*000);
		String res = httpClient.send(json.toJSONString(), "UTF-8");
		System.out.println("res="+res);
		JSONObject obj = JSON.parseObject(res);
		JSONObject respHead = (JSONObject) obj.get("head");
		String respCode = respHead.getString("respCode");
		
		//如果返回状态码成功，解析body内容
		if("0000".equals(respCode)){
			String respBody = (String)obj.get("body");
			String respSign = (String)obj.get("sign");
			System.out.println("客户端解密后的body->"+DesUtil32.decode(respBody, password));
		}
		
		return "";
	}
	

	//签名原始排序字符串
	public String getSignSrc(Map<String, String> paramMap,String paySecret) {
		SortedMap<String, String> smap = new TreeMap<String, String>(paramMap);
		StringBuffer stringBuffer = new StringBuffer();
		for (Map.Entry<String, String> m : smap.entrySet()) {
			stringBuffer.append(m.getKey()).append("=").append(m.getValue().trim()).append("&");
		}
		stringBuffer.append("key=").append(paySecret.trim()).toString();
		return stringBuffer.toString();
	}
    

   
}
