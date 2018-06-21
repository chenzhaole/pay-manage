package com.demo.action;

import com.demo.util.SignUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class TestPayServlet extends HttpServlet {
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
			String mchtId=request.getParameter("mchtId");
			String version=request.getParameter("version");
			String biz=request.getParameter("biz");
			String orderId=request.getParameter("orderId");
			String orderTime=request.getParameter("orderTime");
			String amount=request.getParameter("amount");
			String currencyType=request.getParameter("currencyType");
			String goods=request.getParameter("goods");
			String notifyUrl=request.getParameter("notifyUrl");
			String callBackUrl=request.getParameter("callBackUrl");
			String desc=request.getParameter("desc");
			String appId=request.getParameter("appId");
			String appName=request.getParameter("appName");
			String operator=request.getParameter("operator");
			String openID=request.getParameter("openID");
			String payScene=request.getParameter("payScene");
			String deviceType=request.getParameter("deviceType");
			String ip=request.getParameter("ip");
			String param=request.getParameter("param");
			String expireTime=request.getParameter("expireTime");
			String postUrl="mchtId="+mchtId+"&version="+version+"&biz="+biz+"&orderId="+orderId+"&orderTime="+orderTime
					+"&amount="+amount+"&currencyType="+currencyType+"&goods="+goods+"&notifyUrl="+notifyUrl+"&callBackUrl="+callBackUrl
					+"&desc="+desc+"&appId="+appId+"&appName="+appName+"&operator="+operator+"&expireTime="+expireTime
					+"&openID="+openID+"&payScene="+payScene+"&deviceType="+deviceType+"&ip="+ip
					+"&param="+param+"&sign="+sign;
            PrintWriter out = response.getWriter();
            out.print(sign);
            out.flush();
            out.close();
//			respStr = PostUtil.post(payUrl, postUrl);
//			System.out.println(respStr);
//			System.out.println(postUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
        response.getWriter().write(respStr);
	}

	private Map<String, String> getBodyMap(HttpServletRequest req) {
		Map<String,String> map = new HashMap();
		map.put("orderId", req.getParameter("orderId"));
		map.put("orderTime", req.getParameter("orderTime"));
		map.put("amount", req.getParameter("amount"));
		map.put("currencyType", req.getParameter("currencyType"));
		map.put("goods", req.getParameter("goods"));
		map.put("notifyUrl", req.getParameter("notifyUrl"));
		map.put("callBackUrl", req.getParameter("callBackUrl"));
		map.put("desc", req.getParameter("desc"));
		map.put("appId", req.getParameter("appId"));
		map.put("appName", req.getParameter("appName"));
		map.put("operator", req.getParameter("operator"));
		map.put("expireTime", req.getParameter("expireTime"));
		map.put("openId", req.getParameter("openId"));
		map.put("payScene", req.getParameter("payScene"));
		map.put("deviceType", req.getParameter("deviceType"));
		map.put("param", req.getParameter("param"));
		map.put("ip", req.getParameter("ip"));
		return map;
	}
	
}
