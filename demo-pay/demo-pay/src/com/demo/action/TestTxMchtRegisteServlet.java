package com.demo.action;

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

public class TestTxMchtRegisteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
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
		map.put("name", req.getParameter("name"));
		map.put("nickName", req.getParameter("nickName"));
		map.put("province", req.getParameter("province"));
		map.put("city", req.getParameter("city"));
		map.put("district", req.getParameter("district"));
		map.put("address", req.getParameter("address"));
		map.put("tel", req.getParameter("tel"));
		map.put("email", req.getParameter("email"));
		map.put("mchtType", req.getParameter("mchtType"));
		map.put("businessScope", req.getParameter("businessScope"));
		map.put("businessLicenseType", req.getParameter("businessLicenseType"));
		map.put("businessLicenseCode", req.getParameter("businessLicenseCode"));
		map.put("organizationCode", req.getParameter("organizationCode"));
		map.put("taxRegistCode", req.getParameter("taxRegistCode"));
		map.put("legalName", req.getParameter("legalName"));
		map.put("legalCertType", req.getParameter("legalCertType"));
		map.put("legalCertNo", req.getParameter("legalCertNo"));
		map.put("serviceTel", req.getParameter("serviceTel"));
		map.put("settleBankNo", req.getParameter("settleBankNo"));
		map.put("settleCardType", req.getParameter("settleCardType"));
		map.put("settleCardCvv", req.getParameter("settleCardCvv"));
		map.put("settleCardExpDate", req.getParameter("settleCardExpDate"));
		map.put("bankAccountMobile", req.getParameter("bankAccountMobile"));
		map.put("settleBankAccountNo", req.getParameter("settleBankAccountNo"));
		map.put("settleAccountName", req.getParameter("settleAccountName"));
		map.put("settleBankName", req.getParameter("settleBankName"));
		map.put("settleBankAcctType", req.getParameter("settleBankAcctType"));
		map.put("settleBankProvince", req.getParameter("settleBankProvince"));
		map.put("settleBankCity", req.getParameter("settleBankCity"));
		map.put("settleLineCode", req.getParameter("settleLineCode"));
		map.put("bankDmType", req.getParameter("bankDmType"));
		map.put("bankRateType", req.getParameter("bankRateType"));
		map.put("bankSettleCycle", req.getParameter("bankSettleCycle"));
		map.put("bankRate", req.getParameter("bankRate"));
		map.put("bankFee", req.getParameter("bankFee"));
		map.put("opType", req.getParameter("opType"));
		map.put("userId", req.getParameter("userId"));
		return map;
	}
	
}
