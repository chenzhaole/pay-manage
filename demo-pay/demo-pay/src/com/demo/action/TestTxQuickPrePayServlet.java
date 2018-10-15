package com.demo.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.demo.util.PostUtil;
import com.demo.util.SignUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestTxQuickPrePayServlet extends HttpServlet {
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
		String forwordUrl = "/error.jsp";
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

			JSONObject result = JSON.parseObject(respStr);

			JSONObject resultHead = result.getJSONObject("head");
			String respCode = (String) resultHead.get("respCode");
			if("0000".equals(respCode)){
				JSONObject resultBody = result.getJSONObject("body");
				request.setAttribute("tradeId", resultBody.getString("tradeId"));
				forwordUrl = "/index-txQuickPay.jsp";
				request.getRequestDispatcher(forwordUrl).forward(request, response);
			}else{
				response.getWriter().write(respStr);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}

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
		map.put("accountName", req.getParameter("accountName"));
		map.put("certType", req.getParameter("certType"));
		map.put("certificateNo", req.getParameter("certificateNo"));
		map.put("cardType", req.getParameter("cardType"));
		map.put("bankCardNo", req.getParameter("bankCardNo"));
		map.put("mobilePhone", req.getParameter("mobilePhone"));
		map.put("cvv", req.getParameter("cvv"));
		map.put("valid", req.getParameter("valid"));
		map.put("amount", req.getParameter("amount"));
		map.put("orderTime", req.getParameter("orderTime"));
		map.put("desc", req.getParameter("desc"));
		map.put("notifyUrl", req.getParameter("notifyUrl"));
		map.put("callBackUrl", req.getParameter("callBackUrl"));
		map.put("bankBranch", req.getParameter("bankBranch"));
		map.put("province", req.getParameter("province"));
		map.put("city", req.getParameter("city"));
		map.put("ip", req.getParameter("ip"));
		map.put("userId", req.getParameter("userId"));
		map.put("txType", req.getParameter("txType"));
		map.put("dmType", req.getParameter("dmType"));
		map.put("bankCode", req.getParameter("bankCode"));
		return map;
	}
	
}
