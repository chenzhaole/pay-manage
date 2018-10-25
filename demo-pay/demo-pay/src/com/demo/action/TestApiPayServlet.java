package com.demo.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.demo.util.PostUtil;
import com.demo.util.SignUtil;

public class TestApiPayServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static Map<String, String> orderResult; // 用来存储订单的交易状态(key:订单号，value:状态(0:未支付，1：已支付))
	// ---- 这里可以根据需要存储在数据库中
	public static int orderStatus = 0;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		String respStr = "没有正确返回信息";
		try {
			String payUrl = request.getParameter("payUrl");
			String key = request.getParameter("key");
			String sign = SignUtil.md5Sign(getBodyMap(request), key);
			JSONObject data = new JSONObject();
			JSONObject head = getHeadJson(request);
			JSONObject body = (JSONObject) JSONObject.toJSON(getBodyMap(request));
			data.put("head", head);
			data.put("body", body);
			data.put("sign", sign);
			respStr = PostUtil.post(payUrl, data.toJSONString());
			System.out.println(respStr);
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
		Map<String,String> map = new HashMap();
		map.put("orderId", req.getParameter("orderId"));
		map.put("goods", req.getParameter("goods"));
		map.put("amount", req.getParameter("amount"));
		map.put("notifyUrl", req.getParameter("notifyUrl"));
		map.put("callBackUrl", req.getParameter("callBackUrl"));
		map.put("orderTime", req.getParameter("orderTime"));
		map.put("appId", req.getParameter("appId"));
		map.put("appName", req.getParameter("appName"));
		map.put("operator", req.getParameter("operator"));
		map.put("expireTime", req.getParameter("expireTime"));
//		map.put("ip", req.getParameter("ip"));
		return map;
	}

}
